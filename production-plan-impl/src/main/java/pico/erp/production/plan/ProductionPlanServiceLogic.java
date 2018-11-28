package pico.erp.production.plan;

import lombok.val;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.validation.annotation.Validated;
import pico.erp.audit.AuditService;
import pico.erp.production.plan.ProductionPlanRequests.CancelRequest;
import pico.erp.shared.Public;
import pico.erp.shared.event.EventPublisher;

@SuppressWarnings("Duplicates")
@Service
@Public
@Transactional
@Validated
public class ProductionPlanServiceLogic implements ProductionPlanService {

  @Autowired
  private ProductionPlanRepository productionPlanRepository;

  @Autowired
  private EventPublisher eventPublisher;

  @Autowired
  private ProductionPlanMapper mapper;

  @Lazy
  @Autowired
  private AuditService auditService;

  @Override
  public void cancel(CancelRequest request) {
    val plan = productionPlanRepository.findBy(request.getId())
      .orElseThrow(ProductionPlanExceptions.NotFoundException::new);
    val response = plan.apply(mapper.map(request));
    productionPlanRepository.update(plan);
    auditService.commit(plan);
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
    auditService.commit(created);
    eventPublisher.publishEvents(response.getEvents());
    return mapper.map(created);
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
  public void update(ProductionPlanRequests.UpdateRequest request) {
    val plan = productionPlanRepository.findBy(request.getId())
      .orElseThrow(ProductionPlanExceptions.NotFoundException::new);
    val response = plan.apply(mapper.map(request));
    productionPlanRepository.update(plan);
    auditService.commit(plan);
    eventPublisher.publishEvents(response.getEvents());
  }
}
