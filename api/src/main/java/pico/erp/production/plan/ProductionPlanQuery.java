package pico.erp.production.plan;

import javax.validation.constraints.NotNull;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface ProductionPlanQuery {

  Page<ProductionPlanView> retrieve(@NotNull ProductionPlanView.Filter filter,
    @NotNull Pageable pageable);

}
