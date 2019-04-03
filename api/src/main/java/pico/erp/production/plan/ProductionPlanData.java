package pico.erp.production.plan;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import lombok.Data;
import pico.erp.company.CompanyId;
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

  LocalDateTime dueDate;

  LocalDateTime completedDate;

  LocalDateTime determinedDate;

  LocalDateTime canceledDate;

  ProductionPlanStatusKind status;

  BigDecimal progressRate;

  UnitKind unit;

  UserId plannerId;

  CompanyId receiverId;

  boolean updatable;

  boolean determinable;

  boolean progressable;

  boolean cancelable;

  boolean completable;

  boolean preparable;

}
