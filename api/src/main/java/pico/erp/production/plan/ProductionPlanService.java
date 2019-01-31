package pico.erp.production.plan;

import javax.validation.Valid;
import javax.validation.constraints.NotNull;

public interface ProductionPlanService {

  void cancel(@Valid @NotNull ProductionPlanRequests.CancelRequest request);

  void complete(@Valid @NotNull ProductionPlanRequests.CompleteRequest request);

  ProductionPlanData create(@Valid @NotNull ProductionPlanRequests.CreateRequest request);

  void determine(@Valid @NotNull ProductionPlanRequests.DetermineRequest request);

  boolean exists(@Valid @NotNull ProductionPlanId id);

  ProductionPlanData get(@Valid @NotNull ProductionPlanId id);

  void progress(@Valid @NotNull ProductionPlanRequests.ProgressRequest request);

  void prepare(@Valid @NotNull ProductionPlanRequests.PrepareRequest request);

  void update(@Valid @NotNull ProductionPlanRequests.UpdateRequest request);


}
