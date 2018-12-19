package pico.erp.production.plan.detail;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Lazy;
import org.springframework.context.event.EventListener;
import org.springframework.jms.annotation.JmsListener;
import org.springframework.stereotype.Component;
import pico.erp.production.plan.ProductionPlanEvents;
import pico.erp.production.plan.ProductionPlanService;

@SuppressWarnings("unused")
@Component
public class ProductionPlanDetailEventListener {

  private static final String LISTENER_NAME = "listener.production-plan-detail-event-listener";

  @Lazy
  @Autowired
  private ProductionPlanService planService;

  @Lazy
  @Autowired
  private ProductionPlanDetailServiceLogic planDetailService;

  @EventListener
  @JmsListener(destination = LISTENER_NAME + "." + ProductionPlanEvents.CreatedEvent.CHANNEL)
  public void onPlanCreated(ProductionPlanEvents.CreatedEvent event) {
    planDetailService.generate(
      ProductionPlanDetailRequests.GenerateRequest.builder()
        .planId(event.getProductionPlanId())
        .build()
    );
  }

  @EventListener
  @JmsListener(destination = LISTENER_NAME + "."
    + ProductionPlanDetailEvents.RescheduledEvent.CHANNEL)
  public void onPlanDetailRescheduled(ProductionPlanDetailEvents.RescheduledEvent event) {
    planDetailService.rescheduleByDependency(
      ProductionPlanDetailRequests.RescheduleByDependencyRequest.builder()
        .dependencyId(event.getProductionPlanDetailId())
        .beforeStartDate(event.getBeforeStartDate())
        .beforeEndDate(event.getBeforeEndDate())
        .build()
    );
  }

}
