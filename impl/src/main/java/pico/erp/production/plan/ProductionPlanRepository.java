package pico.erp.production.plan;

import java.time.OffsetDateTime;
import java.util.Optional;
import javax.validation.constraints.NotNull;
import org.springframework.stereotype.Repository;

@Repository
public interface ProductionPlanRepository {

  long countCreatedBetween(OffsetDateTime begin, OffsetDateTime end);

  ProductionPlan create(@NotNull ProductionPlan orderAcceptance);

  void deleteBy(@NotNull ProductionPlanId id);

  boolean exists(@NotNull ProductionPlanId id);

  Optional<ProductionPlan> findBy(@NotNull ProductionPlanId id);

  void update(@NotNull ProductionPlan orderAcceptance);

}
