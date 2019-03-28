package pico.erp.production.plan;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.Set;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import pico.erp.item.ItemId;
import pico.erp.project.ProjectId;
import pico.erp.shared.data.Auditor;
import pico.erp.shared.data.UnitKind;

@Data
public class ProductionPlanView {

  ProductionPlanId id;

  ProductionPlanCode code;

  ItemId itemId;

  BigDecimal quantity;

  BigDecimal spareQuantity;

  BigDecimal completedQuantity;

  BigDecimal progressRate;

  ProjectId projectId;

  LocalDateTime dueDate;

  LocalDateTime completedDate;

  LocalDateTime determinedDate;

  LocalDateTime canceledDate;

  ProductionPlanStatusKind status;

  Auditor createdBy;

  LocalDateTime createdDate;

  UnitKind unit;

  @Data
  @NoArgsConstructor
  @AllArgsConstructor
  @Builder
  public static class Filter {

    String code;

    ProjectId projectId;

    ItemId itemId;

    Set<ProductionPlanStatusKind> statuses;

    LocalDateTime startDueDate;

    LocalDateTime endDueDate;

  }

}
