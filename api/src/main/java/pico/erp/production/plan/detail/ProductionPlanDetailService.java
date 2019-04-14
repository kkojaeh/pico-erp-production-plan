package pico.erp.production.plan.detail;

import java.util.List;
import javax.validation.Valid;
import javax.validation.constraints.NotNull;
import pico.erp.production.plan.ProductionPlanId;

public interface ProductionPlanDetailService {


  void addDependency(@Valid @NotNull ProductionPlanDetailRequests.AddDependencyRequest request);

  void cancel(@Valid @NotNull ProductionPlanDetailRequests.CancelRequest request);

  void complete(@Valid @NotNull ProductionPlanDetailRequests.CompleteRequest request);

  ProductionPlanDetailData create(
    @Valid @NotNull ProductionPlanDetailRequests.CreateRequest request);

  void delete(@Valid @NotNull ProductionPlanDetailRequests.DeleteRequest request);

  void determine(@Valid @NotNull ProductionPlanDetailRequests.DetermineRequest request);

  boolean exists(@Valid @NotNull ProductionPlanDetailId id);

  ProductionPlanDetailData get(@Valid @NotNull ProductionPlanDetailId id);

  List<ProductionPlanDetailData> getAll(ProductionPlanId planId);

  void progress(@Valid @NotNull ProductionPlanDetailRequests.ProgressRequest request);

  void removeDependency(
    @Valid @NotNull ProductionPlanDetailRequests.RemoveDependencyRequest request);

  void reschedule(@Valid @NotNull ProductionPlanDetailRequests.RescheduleRequest request);

  ProductionPlanDetailData split(@Valid @NotNull ProductionPlanDetailRequests.SplitRequest request);

  void update(@Valid @NotNull ProductionPlanDetailRequests.UpdateRequest request);


}
