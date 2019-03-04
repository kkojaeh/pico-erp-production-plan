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

  /**
   * 계획이 취소되면 상세 계획 모두 취소 됨
   */
  @EventListener
  @JmsListener(destination = LISTENER_NAME + "." + ProductionPlanEvents.CanceledEvent.CHANNEL)
  public void onPlanCanceled(ProductionPlanEvents.CanceledEvent event) {
    planDetailService.getAll(event.getId()).forEach(detail -> {
      planDetailService.cancel(
        ProductionPlanDetailRequests.CancelRequest.builder()
          .id(detail.getId())
          .build()
      );
    });
  }

  @EventListener
  @JmsListener(destination = LISTENER_NAME + "." + ProductionPlanEvents.CreatedEvent.CHANNEL)
  public void onPlanCreated(ProductionPlanEvents.CreatedEvent event) {
    planDetailService.generate(
      ProductionPlanDetailRequests.GenerateRequest.builder()
        .planId(event.getId())
        .build()
    );
  }

  @EventListener
  @JmsListener(destination = LISTENER_NAME + "."
    + ProductionPlanDetailEvents.RescheduledEvent.CHANNEL)
  public void onPlanDetailRescheduled(ProductionPlanDetailEvents.RescheduledEvent event) {
    planDetailService.rescheduleByDependency(
      ProductionPlanDetailRequests.RescheduleByDependencyRequest.builder()
        .dependencyId(event.getId())
        .beforeStartDate(event.getBeforeStartDate())
        .beforeEndDate(event.getBeforeEndDate())
        .build()
    );
  }

}
