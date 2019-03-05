package pico.erp.production.plan;

import java.math.BigDecimal;
import lombok.val;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Lazy;
import org.springframework.context.event.EventListener;
import org.springframework.jms.annotation.JmsListener;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;
import pico.erp.production.plan.detail.ProductionPlanDetailData;
import pico.erp.production.plan.detail.ProductionPlanDetailEvents;
import pico.erp.production.plan.detail.ProductionPlanDetailServiceLogic;
import pico.erp.production.plan.detail.ProductionPlanDetailStatusKind;

@SuppressWarnings("unused")
@Component
@Transactional
public class ProductionPlanEventListener {

  private static final String LISTENER_NAME = "listener.production-plan-event-listener";

  @Lazy
  @Autowired
  private ProductionPlanService planService;

  @Lazy
  @Autowired
  private ProductionPlanDetailServiceLogic planDetailService;

  /**
   * 상세 계획이 모두 확정되면 계획을 준비 완료로 변경 시킴
   */
  @EventListener
  @JmsListener(destination = LISTENER_NAME + "."
    + ProductionPlanDetailEvents.DeterminedEvent.CHANNEL)
  public void onPlanDetailDetermined(ProductionPlanDetailEvents.DeterminedEvent event) {
    val detail = planDetailService.get(event.getId());
    val planId = detail.getPlanId();
    val determined = planDetailService.getAll(planId).stream()
      .allMatch(d -> d.getStatus() == ProductionPlanDetailStatusKind.DETERMINED);
    if (determined) {
      planService.prepare(
        ProductionPlanRequests.PrepareRequest.builder()
          .id(planId)
          .build()
      );
    }

  }

  @EventListener
  @JmsListener(destination = LISTENER_NAME + "."
    + ProductionPlanDetailEvents.ProgressedEvent.CHANNEL)
  public void onPlanDetailCompleted(ProductionPlanDetailEvents.CompletedEvent event) {
    val detail = planDetailService.get(event.getId());
    val plan = planService.get(detail.getPlanId());
    if (plan.isCompletable()) {
      val details = planDetailService.getAll(plan.getId());
      val completed = details.stream()
        .allMatch(d -> d.getStatus() == ProductionPlanDetailStatusKind.COMPLETED);

      if (completed) {
        val last = details.get(details.size() - 1);
        val completedQuantity = details.stream()
          .filter(d -> d.getGroupId().equals(last.getGroupId()))
          .map(ProductionPlanDetailData::getProgressedQuantity)
          .reduce(BigDecimal.ZERO, BigDecimal::add);
        planService.complete(
          ProductionPlanRequests.CompleteRequest.builder()
            .id(plan.getId())
            .completedQuantity(completedQuantity)
            .build()
        );
      }
    }

  }

  @EventListener
  @JmsListener(destination = LISTENER_NAME + "."
    + ProductionPlanDetailEvents.ProgressedEvent.CHANNEL)
  public void onPlanDetailProgressed(ProductionPlanDetailEvents.ProgressedEvent event) {
    val detail = planDetailService.get(event.getId());
    val plan = planService.get(detail.getPlanId());
    if (plan.isProgressable()) {
      val details = planDetailService.getAll(plan.getId());
      val progressRate = details.stream()
        .map(ProductionPlanDetailData::getProgressRate)
        .reduce(BigDecimal.ZERO, BigDecimal::add)
        .divide(new BigDecimal(details.size()));
      planService.progress(
        ProductionPlanRequests.ProgressRequest.builder()
          .id(plan.getId())
          .progressRate(progressRate)
          .build()
      );
    }

  }

}
