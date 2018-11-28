package pico.erp.production.plan.detail;

import java.util.Optional;
import java.util.stream.Stream;
import javax.validation.constraints.NotNull;
import org.springframework.stereotype.Repository;
import pico.erp.production.plan.ProductionPlanId;

@Repository
public interface ProductionPlanDetailRepository {

  ProductionPlanDetail create(@NotNull ProductionPlanDetail item);

  void deleteBy(@NotNull ProductionPlanDetailId id);

  boolean exists(@NotNull ProductionPlanDetailId id);

  Stream<ProductionPlanDetail> findAllBy(@NotNull ProductionPlanId planId);

  Stream<ProductionPlanDetail> findAllDependedOn(@NotNull ProductionPlanDetailId planDetailId);

  Optional<ProductionPlanDetail> findBy(@NotNull ProductionPlanDetailId id);

  void update(@NotNull ProductionPlanDetail item);

}
