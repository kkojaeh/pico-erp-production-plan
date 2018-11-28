package pico.erp.production.plan.detail;

import java.math.BigDecimal;
import java.time.OffsetDateTime;
import java.util.List;
import lombok.Data;
import pico.erp.company.CompanyId;
import pico.erp.item.ItemId;
import pico.erp.item.spec.ItemSpecId;
import pico.erp.production.plan.ProductionPlanId;
import pico.erp.user.UserId;

@Data
public class ProductionPlanDetailData {

  ProductionPlanDetailId id;

  ProductionPlanId planId;

  ItemId itemId;

  ItemSpecId itemSpecId;

  BigDecimal quantity;

  BigDecimal spareQuantity;

  BigDecimal plannedQuantity;

  BigDecimal progressedQuantity;

  OffsetDateTime startDate;

  OffsetDateTime endDate;

  CompanyId progressCompanyId;

  OffsetDateTime completedDate;

  UserId chargerId;

  ProductionPlanDetailProgressTypeKind progressType;

  ProductionPlanDetailStatusKind status;

  OffsetDateTime determinedDate;

  OffsetDateTime canceledDate;

  List<ProductionPlanDetailId> dependencies;

}
