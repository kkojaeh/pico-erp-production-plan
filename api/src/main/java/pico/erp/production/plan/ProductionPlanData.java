package pico.erp.production.plan;

import java.math.BigDecimal;
import java.time.OffsetDateTime;
import lombok.Data;
import pico.erp.item.ItemId;
import pico.erp.project.ProjectId;
import pico.erp.shared.data.UnitKind;
import pico.erp.user.UserId;

@Data
public class ProductionPlanData {

  ProductionPlanId id;

  ProductionPlanCode code;

  ItemId itemId;

  BigDecimal quantity;

  BigDecimal spareQuantity;

  BigDecimal plannedQuantity;

  BigDecimal completedQuantity;

  ProjectId projectId;

  OffsetDateTime dueDate;

  OffsetDateTime completedDate;

  OffsetDateTime determinedDate;

  OffsetDateTime canceledDate;

  ProductionPlanStatusKind status;

  BigDecimal progressRate;

  UnitKind unit;

  UserId plannerId;

  boolean updatable;

  boolean determinable;

  boolean progressable;

  boolean cancelable;

  boolean completable;

  boolean preparable;

}
