package pico.erp.production.plan;

import kkojaeh.spring.boot.component.Give;
import lombok.val;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.validation.annotation.Validated;
import pico.erp.production.plan.ProductionPlanRequests.CancelRequest;
import pico.erp.production.plan.ProductionPlanRequests.CompleteRequest;
import pico.erp.production.plan.ProductionPlanRequests.DetermineRequest;
import pico.erp.shared.event.EventPublisher;

@SuppressWarnings("Duplicates")
@Service
@Give
@Transactional
@Validated
public class ProductionPlanServiceLogic implements ProductionPlanService {

  @Autowired
  private ProductionPlanRepository productionPlanRepository;

  @Autowired
  private EventPublisher eventPublisher;

  @Autowired
  private ProductionPlanMapper mapper;

  @Override
  public void cancel(CancelRequest request) {
    val plan = productionPlanRepository.findBy(request.getId())
      .orElseThrow(ProductionPlanExceptions.NotFoundException::new);
    val response = plan.apply(mapper.map(request));
    productionPlanRepository.update(plan);
    eventPublisher.publishEvents(response.getEvents());
  }

  @Override
  public void complete(CompleteRequest request) {
    val plan = productionPlanRepository.findBy(request.getId())
      .orElseThrow(ProductionPlanExceptions.NotFoundException::new);
    val response = plan.apply(mapper.map(request));
    productionPlanRepository.update(plan);
    eventPublisher.publishEvents(response.getEvents());
  }

  @Override
  public ProductionPlanData create(ProductionPlanRequests.CreateRequest request) {
    val plan = new ProductionPlan();
    val response = plan.apply(mapper.map(request));
    if (productionPlanRepository.exists(plan.getId())) {
      throw new ProductionPlanExceptions.AlreadyExistsException();
    }
    val created = productionPlanRepository.create(plan);
    eventPublisher.publishEvents(response.getEvents());
    return mapper.map(created);
  }

  @Override
  public void determine(DetermineRequest request) {
    val plan = productionPlanRepository.findBy(request.getId())
      .orElseThrow(ProductionPlanExceptions.NotFoundException::new);
    val response = plan.apply(mapper.map(request));
    productionPlanRepository.update(plan);
    eventPublisher.publishEvents(response.getEvents());
  }

  @Override
  public boolean exists(ProductionPlanId id) {
    return productionPlanRepository.exists(id);
  }

  @Override
  public ProductionPlanData get(ProductionPlanId id) {
    return productionPlanRepository.findBy(id)
      .map(mapper::map)
      .orElseThrow(ProductionPlanExceptions.NotFoundException::new);
  }

  @Override
  public void prepare(ProductionPlanRequests.PrepareRequest request) {
    val plan = productionPlanRepository.findBy(request.getId())
      .orElseThrow(ProductionPlanExceptions.NotFoundException::new);
    val response = plan.apply(mapper.map(request));
    productionPlanRepository.update(plan);
    eventPublisher.publishEvents(response.getEvents());
  }

  @Override
  public void progress(ProductionPlanRequests.ProgressRequest request) {
    val plan = productionPlanRepository.findBy(request.getId())
      .orElseThrow(ProductionPlanExceptions.NotFoundException::new);
    val response = plan.apply(mapper.map(request));
    productionPlanRepository.update(plan);
    eventPublisher.publishEvents(response.getEvents());
  }

  @Override
  public void update(ProductionPlanRequests.UpdateRequest request) {
    val plan = productionPlanRepository.findBy(request.getId())
      .orElseThrow(ProductionPlanExceptions.NotFoundException::new);
    val response = plan.apply(mapper.map(request));
    productionPlanRepository.update(plan);
    eventPublisher.publishEvents(response.getEvents());
  }
}
