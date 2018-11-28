package pico.erp.production.plan.detail;

import java.time.OffsetDateTime;
import java.time.temporal.ChronoUnit;
import java.util.HashMap;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.Collectors;
import javax.validation.Valid;
import javax.validation.constraints.NotNull;
import lombok.Builder;
import lombok.Getter;
import lombok.val;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.validation.annotation.Validated;
import pico.erp.audit.AuditService;
import pico.erp.bom.BomData;
import pico.erp.bom.BomId;
import pico.erp.bom.BomService;
import pico.erp.production.plan.ProductionPlanData;
import pico.erp.production.plan.ProductionPlanId;
import pico.erp.production.plan.ProductionPlanProperties;
import pico.erp.shared.Public;
import pico.erp.shared.event.EventPublisher;

@SuppressWarnings("Duplicates")
@Service
@Public
@Transactional
@Validated
public class ProductionPlanDetailServiceLogic implements ProductionPlanDetailService {

  @Autowired
  private ProductionPlanDetailRepository planDetailRepository;

  @Autowired
  private EventPublisher eventPublisher;

  @Autowired
  private ProductionPlanDetailMapper mapper;

  @Lazy
  @Autowired
  private AuditService auditService;

  @Lazy
  @Autowired
  private BomService bomService;

  @Autowired
  private ProductionPlanProperties properties;

  @Override
  public void addDependency(ProductionPlanDetailRequests.AddDependencyRequest request) {
    val plan = planDetailRepository.findBy(request.getId())
      .orElseThrow(ProductionPlanDetailExceptions.NotFoundException::new);
    val response = plan.apply(mapper.map(request));
    planDetailRepository.update(plan);
    auditService.commit(plan);
    eventPublisher.publishEvents(response.getEvents());
  }

  @Override
  public void cancel(ProductionPlanDetailRequests.CancelRequest request) {
    val plan = planDetailRepository.findBy(request.getId())
      .orElseThrow(ProductionPlanDetailExceptions.NotFoundException::new);
    val response = plan.apply(mapper.map(request));
    planDetailRepository.update(plan);
    auditService.commit(plan);
    eventPublisher.publishEvents(response.getEvents());
  }

  @Override
  public void complete(ProductionPlanDetailRequests.CompleteRequest request) {
    val plan = planDetailRepository.findBy(request.getId())
      .orElseThrow(ProductionPlanDetailExceptions.NotFoundException::new);
    val response = plan.apply(mapper.map(request));
    planDetailRepository.update(plan);
    auditService.commit(plan);
    eventPublisher.publishEvents(response.getEvents());
  }

  @Override
  public ProductionPlanDetailData create(ProductionPlanDetailRequests.CreateRequest request) {
    val plan = new ProductionPlanDetail();
    val response = plan.apply(mapper.map(request));
    if (planDetailRepository.exists(plan.getId())) {
      throw new ProductionPlanDetailExceptions.AlreadyExistsException();
    }
    val created = planDetailRepository.create(plan);
    auditService.commit(created);
    eventPublisher.publishEvents(response.getEvents());
    return mapper.map(created);
  }

  @Override
  public void determine(ProductionPlanDetailRequests.DetermineRequest request) {
    val plan = planDetailRepository.findBy(request.getId())
      .orElseThrow(ProductionPlanDetailExceptions.NotFoundException::new);
    val response = plan.apply(mapper.map(request));
    planDetailRepository.update(plan);
    auditService.commit(plan);
    eventPublisher.publishEvents(response.getEvents());
  }

  @Override
  public boolean exists(ProductionPlanDetailId id) {
    return planDetailRepository.exists(id);
  }

  public void generate(GenerateRequest request) {
    val plan = request.getPlan();
    val startDate = OffsetDateTime.now().plusDays(1)
      .with(properties.getDetailGenerationPolicy().getStartTime());
    val endDate = plan.getDueDate().minusDays(1)
      .with(properties.getDetailGenerationPolicy().getEndTime());
    if (bomService.exists(plan.getItemId())) {
      create(
        ProductionPlanDetailRequests.CreateRequest.builder()
          .id(ProductionPlanDetailId.generate())
          .planId(plan.getId())
          .itemId(plan.getItemId())
          .quantity(plan.getQuantity())
          .spareQuantity(plan.getSpareQuantity())
          .startDate(startDate)
          .endDate(endDate)
          .build()
      );
    } else {
      val bomHierarchy = bomService.getHierarchy(plan.getItemId());
      val mappings = new HashMap<BomId, ProductionPlanDetailId>();
      val maxLevel = new AtomicInteger(0);
      bomHierarchy.visitPostOrder((bom, parents) -> {
        maxLevel.set(Math.max(maxLevel.get(), parents.size()));
      });
      val hours = ChronoUnit.HOURS.between(startDate, endDate);
      val maxDepth = maxLevel.get() + 1;
      val interval = hours / maxDepth;

      bomHierarchy.visitPostOrder((bom, parents) -> {
        val detailId = ProductionPlanDetailId.generate();
        val root = bom.equals(bomHierarchy);
        val depth = parents.size();
        val quantity =
          root ? plan.getQuantity() : bom.getQuantityRatio().multiply(plan.getPlannedQuantity());
        val spareQuantity = root ? plan.getSpareQuantity()
          : bom.getSpareRatio().multiply(bom.getQuantityRatio())
            .multiply(plan.getPlannedQuantity());
        mappings.put(bom.getId(), detailId);
        create(
          ProductionPlanDetailRequests.CreateRequest.builder()
            .id(detailId)
            .planId(plan.getId())
            .itemId(bom.getItemId())
            .quantity(quantity)
            .spareQuantity(spareQuantity)
            .startDate(startDate.plusHours((maxDepth - depth) * interval))
            .endDate(startDate.plusHours((maxDepth - depth + 1) * interval))
            .build()
        );
        // materials -> dependencies
        bom.getMaterials().stream()
          .map(BomData::getId)
          .map(bomId -> mappings.get(bomId))
          .map(dependencyId ->
            ProductionPlanDetailRequests.AddDependencyRequest.builder()
              .id(detailId)
              .dependencyId(dependencyId)
              .build()
          )
          .forEach(this::addDependency);
      });
    }
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
    val plan = planDetailRepository.findBy(request.getId())
      .orElseThrow(ProductionPlanDetailExceptions.NotFoundException::new);
    val response = plan.apply(mapper.map(request));
    planDetailRepository.update(plan);
    auditService.commit(plan);
    eventPublisher.publishEvents(response.getEvents());
  }

  @Override
  public void removeDependency(ProductionPlanDetailRequests.RemoveDependencyRequest request) {
    val plan = planDetailRepository.findBy(request.getId())
      .orElseThrow(ProductionPlanDetailExceptions.NotFoundException::new);
    val response = plan.apply(mapper.map(request));
    planDetailRepository.update(plan);
    auditService.commit(plan);
    eventPublisher.publishEvents(response.getEvents());
  }

  @Override
  public void reschedule(ProductionPlanDetailRequests.RescheduleRequest request) {
    val plan = planDetailRepository.findBy(request.getId())
      .orElseThrow(ProductionPlanDetailExceptions.NotFoundException::new);
    val response = plan.apply(mapper.map(request));
    planDetailRepository.update(plan);
    auditService.commit(plan);
    eventPublisher.publishEvents(response.getEvents());
  }

  public void rescheduleByDependency(RescheduleByDependencyRequest request) {
    val message = mapper.map(request);
    planDetailRepository.findAllDependedOn(request.getDependencyId()).forEach(detail -> {
      val response = detail.apply(message);
      planDetailRepository.update(detail);
      auditService.commit(detail);
      eventPublisher.publishEvents(response.getEvents());
    });
  }

  @Override
  public ProductionPlanDetailData split(ProductionPlanDetailRequests.SplitRequest request) {
    val plan = planDetailRepository.findBy(request.getId())
      .orElseThrow(ProductionPlanDetailExceptions.NotFoundException::new);
    val response = plan.apply(mapper.map(request));
    planDetailRepository.update(plan);
    auditService.commit(plan);
    val splitPlan = planDetailRepository.create(response.getSplitPlan());
    eventPublisher.publishEvents(response.getEvents());
    return mapper.map(splitPlan);
  }

  @Override
  public void update(ProductionPlanDetailRequests.UpdateRequest request) {
    val plan = planDetailRepository.findBy(request.getId())
      .orElseThrow(ProductionPlanDetailExceptions.NotFoundException::new);
    val response = plan.apply(mapper.map(request));
    planDetailRepository.update(plan);
    auditService.commit(plan);
    eventPublisher.publishEvents(response.getEvents());
  }

  @Getter
  @Builder
  public static class GenerateRequest {

    @Valid
    @NotNull
    ProductionPlanData plan;

  }

  @Getter
  @Builder
  public static class RescheduleByDependencyRequest {

    @Valid
    @NotNull
    ProductionPlanDetailId dependencyId;

    @NotNull
    OffsetDateTime beforeStartDate;

    @NotNull
    OffsetDateTime beforeEndDate;

  }

}
