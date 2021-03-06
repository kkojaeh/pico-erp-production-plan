package pico.erp.production.plan.detail;

import java.math.BigDecimal;
import java.time.OffsetDateTime;
import java.time.temporal.ChronoUnit;
import java.util.Arrays;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import kkojaeh.spring.boot.component.ComponentAutowired;
import kkojaeh.spring.boot.component.ComponentBean;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;
import lombok.val;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.validation.annotation.Validated;
import pico.erp.bom.BomData;
import pico.erp.bom.BomHierarchyData;
import pico.erp.bom.BomId;
import pico.erp.bom.BomService;
import pico.erp.item.ItemService;
import pico.erp.item.spec.ItemSpecCode;
import pico.erp.item.spec.ItemSpecService;
import pico.erp.process.ProcessData;
import pico.erp.process.ProcessService;
import pico.erp.process.preparation.ProcessPreparationData;
import pico.erp.process.preparation.ProcessPreparationService;
import pico.erp.production.plan.ProductionPlanData;
import pico.erp.production.plan.ProductionPlanId;
import pico.erp.production.plan.ProductionPlanProperties;
import pico.erp.production.plan.ProductionPlanService;
import pico.erp.production.plan.detail.ProductionPlanDetailRequests.GenerateRequest;
import pico.erp.production.plan.detail.ProductionPlanDetailRequests.RescheduleByDependencyRequest;
import pico.erp.shared.data.UnitKind;
import pico.erp.shared.event.Event;
import pico.erp.shared.event.EventPublisher;

@SuppressWarnings("Duplicates")
@Service
@ComponentBean
@Transactional
@Validated
@Slf4j
public class ProductionPlanDetailServiceLogic implements ProductionPlanDetailService {

  @Autowired
  private ProductionPlanDetailRepository planDetailRepository;

  @Autowired
  private EventPublisher eventPublisher;

  @Autowired
  private ProductionPlanDetailMapper mapper;

  @ComponentAutowired
  private BomService bomService;

  @ComponentAutowired
  private ItemSpecService itemSpecService;

  @ComponentAutowired
  private ItemService itemService;

  @Autowired
  private ProductionPlanProperties properties;

  @Autowired
  private ProductionPlanService productionPlanService;

  @ComponentAutowired
  private ProcessService processService;

  @ComponentAutowired
  private ProcessPreparationService processPreparationService;

  @Override
  public void addDependency(ProductionPlanDetailRequests.AddDependencyRequest request) {
    val planDetail = planDetailRepository.findBy(request.getId())
      .orElseThrow(ProductionPlanDetailExceptions.NotFoundException::new);
    val response = planDetail.apply(mapper.map(request));
    planDetailRepository.update(planDetail);
    eventPublisher.publishEvents(response.getEvents());
  }

  @Override
  public void cancel(ProductionPlanDetailRequests.CancelRequest request) {
    val planDetail = planDetailRepository.findBy(request.getId())
      .orElseThrow(ProductionPlanDetailExceptions.NotFoundException::new);
    val response = planDetail.apply(mapper.map(request));
    planDetailRepository.update(planDetail);
    eventPublisher.publishEvents(response.getEvents());
  }

  @Override
  public void complete(ProductionPlanDetailRequests.CompleteRequest request) {
    val events = new LinkedList<Event>();
    val planDetail = planDetailRepository.findBy(request.getId())
      .orElseThrow(ProductionPlanDetailExceptions.NotFoundException::new);
    val response = planDetail.apply(mapper.map(request));
    events.addAll(response.getEvents());
    planDetailRepository.update(planDetail);
    planDetailRepository.findAllDependedOn(planDetail.getId()).forEach(depended -> {
      if (log.isDebugEnabled()) {
        log.debug("plan detail complete depended : {}", depended);
      }
      val dependenciesCompleted = depended.getDependencies().stream()
        .allMatch(dependency -> ProductionPlanDetailStatusKind.COMPLETED == dependency.getStatus());
      if (log.isDebugEnabled()) {
        log.debug("plan detail complete dependenciesCompleted : {}", dependenciesCompleted);
      }
      if (dependenciesCompleted) {
        events.add(
          new ProductionPlanDetailEvents.DependenciesCompletedEvent(depended.getId())
        );
      }
    });
    eventPublisher.publishEvents(events);
  }

  @Override
  public ProductionPlanDetailData create(ProductionPlanDetailRequests.CreateRequest request) {
    val planDetail = new ProductionPlanDetail();
    val response = planDetail.apply(mapper.map(request));
    if (planDetailRepository.exists(planDetail.getId())) {
      throw new ProductionPlanDetailExceptions.AlreadyExistsException();
    }
    val created = planDetailRepository.create(planDetail);
    eventPublisher.publishEvents(response.getEvents());
    return mapper.map(created);
  }

  @Override
  public void delete(ProductionPlanDetailRequests.DeleteRequest request) {
    val planDetail = planDetailRepository.findBy(request.getId())
      .orElseThrow(ProductionPlanDetailExceptions.NotFoundException::new);
    val response = planDetail.apply(mapper.map(request));
    planDetailRepository.findAllDependedOn(planDetail.getId()).forEach(detail -> {
      removeDependency(
        ProductionPlanDetailRequests.RemoveDependencyRequest.builder()
          .id(detail.getId())
          .dependencyId(planDetail.getId())
          .build()
      );
    });
    planDetailRepository.deleteBy(planDetail.getId());
    eventPublisher.publishEvents(response.getEvents());

  }

  @Override
  public void determine(ProductionPlanDetailRequests.DetermineRequest request) {
    val planDetail = planDetailRepository.findBy(request.getId())
      .orElseThrow(ProductionPlanDetailExceptions.NotFoundException::new);
    val response = planDetail.apply(mapper.map(request));
    planDetailRepository.update(planDetail);
    eventPublisher.publishEvents(response.getEvents());
  }

  @Override
  public boolean exists(ProductionPlanDetailId id) {
    return planDetailRepository.exists(id);
  }

  private Stream<ProductionPlanDetailData> filter(List<ProductionPlanDetailData> list,
    ProductionPlanDetailData target) {
    return list.stream()
      .filter(data -> data.getDependencies().contains(target.getId()));
  }

  private ProductionPlanDetailData generate(ProcessData process, BigDecimal spareRatio, BomData bom,
    long depth,
    GenerateContext context) {

    val quantity = context.getQuantity(bom);
    val spareQuantity = context.getSpareQuantity(bom);
    val unit = itemService.get(bom.getItemId()).getUnit();
    val adjustedQuantity = quantity.add(quantity.multiply(spareRatio))
      .setScale(unit.getPrecision(), BigDecimal.ROUND_HALF_UP);
    val adjustedSpareQuantity = spareQuantity.add(spareQuantity.multiply(spareRatio))
      .setScale(unit.getPrecision(), BigDecimal.ROUND_HALF_UP);

    val detail = ProductionPlanDetailData.builder()
      .id(ProductionPlanDetailId.generate())
      .planId(context.getPlan().getId())
      .itemId(bom.getItemId())
      .itemSpecCode(process.getItemSpecCode())
      .processId(process.getId())
      .processPreparationId(null)
      .quantity(adjustedQuantity)
      .spareQuantity(adjustedSpareQuantity)
      .startDate(context.getStartDate(depth))
      .endDate(context.getEndDate(depth))
      .unit(unit)
      .build();

    val preparations = processPreparationService.getAll(process.getId()).stream()
      .filter(preparation -> !preparation.isDone())
      .collect(Collectors.toList());

    if (!preparations.isEmpty()) {
      val generated = preparations.stream()
        .map(preparation -> generate(preparation, bom, depth + 1, context))
        .collect(Collectors.toList());
      detail.setDependencies(
        generated.stream().map(ProductionPlanDetailData::getId).collect(Collectors.toList())
      );
    }
    context.add(detail);
    return detail;
  }

  private ProductionPlanDetailData generate(ProcessPreparationData preparation,
    BomData bom, long depth, GenerateContext context) {
    val detail = ProductionPlanDetailData.builder()
      .id(ProductionPlanDetailId.generate())
      .planId(context.getPlan().getId())
      .itemId(bom.getItemId())
      .processId(null)
      .processPreparationId(preparation.getId())
      .quantity(BigDecimal.ONE)
      .spareQuantity(BigDecimal.ZERO)
      .startDate(context.getStartDate(depth))
      .endDate(context.getEndDate(depth))
      .unit(UnitKind.EA)
      .build();
    context.add(detail);
    return detail;
  }

  public void generate(GenerateRequest request) {
    val plan = productionPlanService.get(request.getPlanId());
    val startDate = OffsetDateTime.now().plusDays(1)
      .with(properties.getDetailGenerationPolicy().getStartTime());
    val endDate = plan.getDueDate().minusDays(1)
      .with(properties.getDetailGenerationPolicy().getEndTime());
    val results = new LinkedList<ProductionPlanDetailData>();
    if (!bomService.exists(plan.getItemId())) {
      results.add(
        ProductionPlanDetailData.builder()
          .id(ProductionPlanDetailId.generate())
          .planId(plan.getId())
          .itemId(plan.getItemId())
          .itemSpecCode(ItemSpecCode.NOT_APPLICABLE)
          .quantity(plan.getQuantity())
          .spareQuantity(plan.getSpareQuantity())
          .startDate(startDate)
          .endDate(endDate)
          .unit(plan.getUnit())
          .build()
      );
    } else {
      val bomHierarchy = bomService.getHierarchy(plan.getItemId());
      val bomDependencies = new HashMap<BomId, ProductionPlanDetailId>();
      val context = new GenerateContext(plan, bomHierarchy);
      bomHierarchy.visitPostOrder((bom, parents) -> {
        val level = parents.size();
        val generated = generate(bom, level, context);
        val first = generated.get(0);
        val last = generated.get(generated.size() - 1);

        bomDependencies.put(bom.getId(), last.getId());

        val dependencies = bom.getMaterials().stream()
          .map(BomData::getId)
          .map(bomId -> bomDependencies.get(bomId))
          .collect(Collectors.toList());
        first.setDependencies(dependencies);
      });
      results.addAll(context.getGenerated());
    }
    results.getLast().setReceiverId(plan.getReceiverId());
    results.stream()
      .map(data -> ProductionPlanDetailRequests.CreateRequest.from(data))
      .forEach(this::create);
    results.stream()
      .flatMap(data -> ProductionPlanDetailRequests.AddDependencyRequest.from(data).stream())
      .forEach(this::addDependency);
  }

  private List<ProductionPlanDetailData> generate(BomData bom, int level, GenerateContext context) {
    val result = new LinkedList<ProductionPlanDetailData>();
    val processes = processService.getAll(bom.getItemId());
    val depth = context.levelToDepth(level);
    if (processes.isEmpty()) {
      val itemSpecCode =
        bom.getItemSpecId() != null ? itemSpecService.get(bom.getItemSpecId()).getCode()
          : ItemSpecCode.NOT_APPLICABLE;
      val quantity = context.getQuantity(bom);
      val spareQuantity = context.getSpareQuantity(bom);
      val unit = itemService.get(bom.getItemId()).getUnit();
      val adjustedQuantity = quantity.setScale(unit.getPrecision(), BigDecimal.ROUND_HALF_UP);
      val adjustedSpareQuantity = spareQuantity
        .setScale(unit.getPrecision(), BigDecimal.ROUND_HALF_UP);
      val detail = ProductionPlanDetailData.builder()
        .id(ProductionPlanDetailId.generate())
        .planId(context.getPlan().getId())
        .itemId(bom.getItemId())
        .itemSpecId(bom.getItemSpecId())
        .itemSpecCode(itemSpecCode)
        .processId(null)
        .processPreparationId(null)
        .unit(unit)
        .quantity(adjustedQuantity)
        .spareQuantity(adjustedSpareQuantity)
        .startDate(context.getStartDate(depth))
        .endDate(context.getEndDate(depth))
        .build();
      result.add(detail);
      context.add(detail);
    } else {
      ProductionPlanDetailData previous = null;
      int length = processes.size();
      for (int i = 0; i < length; i++) {
        val process = processes.get(i);
        val spareRatio = processes.subList(i, length).stream()
          .map(ProcessData::getLossRate)
          .reduce(BigDecimal.ONE, (acc, curr) -> curr.add(BigDecimal.ONE).multiply(acc))
          .subtract(BigDecimal.ONE)
          .setScale(5, BigDecimal.ROUND_HALF_UP);
        // 이전 lossRate 계산하여 전
        val processDepth = depth + context.levelToGap(level) - i - 1;
        ProductionPlanDetailData generated = generate(process, spareRatio, bom, processDepth,
          context);
        if (previous != null) {
          generated.setDependencies(Arrays.asList(previous.getId()));
        }
        result.add(generated);
        previous = generated;
      }
    }
    return result;
  }

  @Override
  public ProductionPlanDetailData get(ProductionPlanDetailId id) {
    return planDetailRepository.findBy(id)
      .map(mapper::map)
      .orElseThrow(ProductionPlanDetailExceptions.NotFoundException::new);
  }

  @Override
  public List<ProductionPlanDetailData> getAll(ProductionPlanId planId) {
    return planDetailRepository.findAllBy(planId)
      .map(mapper::map)
      .collect(Collectors.toList());
  }

  @Override
  public void progress(ProductionPlanDetailRequests.ProgressRequest request) {
    val planDetail = planDetailRepository.findBy(request.getId())
      .orElseThrow(ProductionPlanDetailExceptions.NotFoundException::new);
    val response = planDetail.apply(mapper.map(request));
    planDetailRepository.update(planDetail);
    eventPublisher.publishEvents(response.getEvents());
  }

  @Override
  public void removeDependency(ProductionPlanDetailRequests.RemoveDependencyRequest request) {
    val planDetail = planDetailRepository.findBy(request.getId())
      .orElseThrow(ProductionPlanDetailExceptions.NotFoundException::new);
    val response = planDetail.apply(mapper.map(request));
    planDetailRepository.update(planDetail);
    eventPublisher.publishEvents(response.getEvents());
  }

  @Override
  public void reschedule(ProductionPlanDetailRequests.RescheduleRequest request) {
    val planDetail = planDetailRepository.findBy(request.getId())
      .orElseThrow(ProductionPlanDetailExceptions.NotFoundException::new);
    val response = planDetail.apply(mapper.map(request));
    planDetailRepository.update(planDetail);
    eventPublisher.publishEvents(response.getEvents());
  }

  public void rescheduleByDependency(RescheduleByDependencyRequest request) {
    val message = mapper.map(request);
    planDetailRepository.findAllDependedOn(request.getDependencyId()).forEach(detail -> {
      val response = detail.apply(message);
      planDetailRepository.update(detail);
      eventPublisher.publishEvents(response.getEvents());
    });
  }

  @Override
  public ProductionPlanDetailData split(ProductionPlanDetailRequests.SplitRequest request) {
    val planDetail = planDetailRepository.findBy(request.getId())
      .orElseThrow(ProductionPlanDetailExceptions.NotFoundException::new);
    val response = planDetail.apply(mapper.map(request));
    planDetailRepository.update(planDetail);
    val split = planDetailRepository.create(response.getSplit());
    eventPublisher.publishEvents(response.getEvents());
    return mapper.map(split);
  }

  @Override
  public void update(ProductionPlanDetailRequests.UpdateRequest request) {
    val planDetail = planDetailRepository.findBy(request.getId())
      .orElseThrow(ProductionPlanDetailExceptions.NotFoundException::new);
    val events = new LinkedList<Event>();
    val response = planDetail.apply(mapper.map(request));
    planDetailRepository.update(planDetail);
    events.addAll(response.getEvents());
    planDetail.getDependencies().forEach(dependency -> {
      if (dependency.isUpdatable()) {
        val dependedOns = planDetailRepository.findAllDependedOn(dependency.getId())
          .collect(Collectors.toList());
        val dependencyResponse = dependency.apply(
          new ProductionPlanDetailMessages.RevalidateByDependedOns.Request(dependedOns)
        );
        planDetailRepository.update(dependency);
        events.addAll(dependencyResponse.getEvents());
      }
    });
    eventPublisher.publishEvents(events);
  }

  @Getter
  private class GenerateContext {

    private final Map<Long, Long> levelGaps;

    private final Map<Long, Long> levelStarts = new HashMap<>();

    private final long maxDepth;

    private final List<ProductionPlanDetailData> generated = new LinkedList<>();

    private final ProductionPlanData plan;

    private final BomHierarchyData root;

    private final OffsetDateTime startDate;

    private final OffsetDateTime endDate;

    private final long intervalHours;

    private final long gapHours = 1;

    private GenerateContext(ProductionPlanData plan, BomHierarchyData bomHierarchy) {
      this.plan = plan;
      this.startDate = OffsetDateTime.now().plusDays(1)
        .with(properties.getDetailGenerationPolicy().getStartTime());
      this.endDate = plan.getDueDate().minusDays(1)
        .with(properties.getDetailGenerationPolicy().getEndTime());
      this.root = bomHierarchy;
      this.levelGaps = new HashMap<>();
      bomHierarchy.visitPostOrder((bom, parents) -> {
        val level = new Long(parents.size());
        if (!levelGaps.containsKey(level)) {
          levelGaps.put(level, 0L);
        }
        val processes = processService.getAll(bom.getItemId());
        if (processes.isEmpty()) {
          levelGaps.put(level, Math.max(1L, levelGaps.get(level)));
        } else {
          val preparationCount = processes.stream()
            .map(ProcessData::getId)
            .map(processId -> processPreparationService.getAll(processId)
              .stream()
              .filter(preparation -> !preparation.isDone())
              .count())
            .reduce(0l, Long::sum);
          levelGaps.put(level, Math.max(preparationCount + processes.size(), levelGaps.get(level)));
        }
      });
      this.maxDepth = levelGaps.values().stream().reduce(Long::sum).orElse(0L) + 1L;
      this.intervalHours = Math.max(ChronoUnit.HOURS.between(startDate, endDate) / maxDepth, 3);
      val levels = levelGaps.keySet();
      levels.forEach(level -> {
        val start = levels.stream().filter(l -> l < level)
          .map(levelGaps::get)
          .reduce(0l, Long::sum);
        levelStarts.put(level, start);
      });
    }

    void add(ProductionPlanDetailData data) {
      generated.add(data);
    }

    long gapMinutes() {
      return intervalHours * 60 / 10;
    }

    OffsetDateTime getEndDate(long depth) {
      return startDate.plusHours((maxDepth - depth) * intervalHours)
        .minusMinutes(gapMinutes());
    }

    BigDecimal getQuantity(BomData bom) {
      val isRoot = bom.getId().equals(root.getId());
      return isRoot ? plan.getQuantity()
        : bom.getQuantityRatio().multiply(plan.getPlannedQuantity())
          .setScale(5, BigDecimal.ROUND_HALF_UP);
    }

    BigDecimal getSpareQuantity(BomData bom) {
      val isRoot = bom.getId().equals(root.getId());
      return isRoot ? plan.getSpareQuantity()
        : bom.getSpareRatio().multiply(bom.getQuantityRatio())
          .multiply(plan.getPlannedQuantity()).setScale(5, BigDecimal.ROUND_HALF_UP);
    }

    OffsetDateTime getStartDate(long depth) {
      return startDate.plusHours((maxDepth - depth - 1) * intervalHours)
        .plusMinutes(gapMinutes());
    }

    long levelToDepth(long level) {
      return levelStarts.get(level);
    }

    long levelToGap(long level) {
      return levelGaps.get(level);
    }

  }

}
