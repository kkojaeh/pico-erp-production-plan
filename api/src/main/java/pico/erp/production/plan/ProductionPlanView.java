package pico.erp.production.plan;

import java.math.BigDecimal;
import java.time.OffsetDateTime;
import java.util.Set;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import pico.erp.item.ItemId;
import pico.erp.project.ProjectId;
import pico.erp.shared.data.Auditor;

@Data
public class ProductionPlanView {

  ProductionPlanId id;

  ProductionPlanCode code;

  ItemId itemId;

  BigDecimal quantity;

  BigDecimal spareQuantity;

  BigDecimal progressedQuantity;

  ProjectId projectId;

  OffsetDateTime dueDate;

  OffsetDateTime completedDate;

  OffsetDateTime determinedDate;

  OffsetDateTime canceledDate;

  ProductionPlanStatusKind status;

  Auditor createdBy;

  OffsetDateTime createdDate;

  @Data
  @NoArgsConstructor
  @AllArgsConstructor
  @Builder
  public static class Filter {

    String code;

    ProjectId projectId;

    ItemId itemId;

    Set<ProductionPlanStatusKind> statuses;

    OffsetDateTime startDueDate;

    OffsetDateTime endDueDate;

  }

}
