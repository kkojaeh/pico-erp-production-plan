package pico.erp.production.plan.detail;

import java.math.BigDecimal;
import java.time.OffsetDateTime;
import java.util.Set;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import pico.erp.company.CompanyId;
import pico.erp.item.ItemId;
import pico.erp.item.spec.ItemSpecId;
import pico.erp.project.ProjectId;
import pico.erp.shared.data.Auditor;
import pico.erp.user.UserId;

@Data
public class ProductionPlanDetailView {

  ProductionPlanDetailId id;

  ProductionPlanDetailId parentId;

  ItemId itemId;

  ItemSpecId itemSpecId;

  BigDecimal plannedQuantity;

  BigDecimal progressedQuantity;

  OffsetDateTime startDate;

  OffsetDateTime endDate;

  CompanyId progressCompanyId;

  boolean completed;

  OffsetDateTime completedDate;

  UserId chargerId;

  ProductionPlanDetailProgressTypeKind progressType;

  Auditor createdBy;

  OffsetDateTime createdDate;

  @Data
  @NoArgsConstructor
  @AllArgsConstructor
  @Builder
  public static class Filter {

    String code;

    CompanyId relatedCompanyId;

    ProjectId projectId;

    ItemId itemId;

    Set<ProductionPlanDetailStatusKind> statuses;

    OffsetDateTime startDueDate;

    OffsetDateTime endDueDate;

  }

}
