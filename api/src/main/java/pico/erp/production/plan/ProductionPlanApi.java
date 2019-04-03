package pico.erp.production.plan;

import javax.persistence.Id;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import pico.erp.shared.data.Role;

public final class ProductionPlanApi {

  @RequiredArgsConstructor
  public enum Roles implements Role {

    PRODUCTION_PLAN_MANAGER,

    PRODUCTION_PLAN_CHARGER;

    @Id
    @Getter
    private final String id = name();

  }
}
