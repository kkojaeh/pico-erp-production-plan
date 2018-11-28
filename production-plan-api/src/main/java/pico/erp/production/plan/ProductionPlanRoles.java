package pico.erp.production.plan;

import javax.persistence.Id;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import pico.erp.shared.data.Role;

@RequiredArgsConstructor
public enum ProductionPlanRoles implements Role {

  PRODUCTION_PLAN_MANAGER,
  PRODUCTION_PLAN_CHARGER;

  @Id
  @Getter
  private final String id = name();

}
