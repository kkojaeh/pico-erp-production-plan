package pico.erp.production.plan;

import javax.validation.Valid;
import javax.validation.constraints.NotNull;

public interface ProductionPlanService {

  void cancel(@Valid @NotNull ProductionPlanRequests.CancelRequest request);

  ProductionPlanData create(@Valid @NotNull ProductionPlanRequests.CreateRequest request);

  boolean exists(@Valid @NotNull ProductionPlanId id);

  ProductionPlanData get(@Valid @NotNull ProductionPlanId id);

  void update(@Valid @NotNull ProductionPlanRequests.UpdateRequest request);


}
