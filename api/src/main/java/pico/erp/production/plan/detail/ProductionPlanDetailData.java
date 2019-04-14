package pico.erp.production.plan.detail;

import java.math.BigDecimal;
import java.time.OffsetDateTime;
import java.util.List;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import pico.erp.company.CompanyId;
import pico.erp.item.ItemId;
import pico.erp.item.spec.ItemSpecCode;
import pico.erp.item.spec.ItemSpecId;
import pico.erp.process.ProcessId;
import pico.erp.process.preparation.ProcessPreparationId;
import pico.erp.production.plan.ProductionPlanId;
import pico.erp.shared.data.UnitKind;

@Builder
@AllArgsConstructor
@NoArgsConstructor
@Data
public class ProductionPlanDetailData {

  public boolean cancelable;

  public boolean completable;

  public boolean determinable;

  public boolean progressable;

  public boolean reschedulable;

  public boolean splittable;

  public boolean updatable;

  public boolean deletable;

  ProductionPlanDetailId id;

  ProductionPlanDetailGroupId groupId;

  ProductionPlanId planId;

  ItemId itemId;

  ItemSpecCode itemSpecCode;

  ProcessId processId;

  ProcessPreparationId processPreparationId;

  ItemSpecId itemSpecId;

  BigDecimal quantity;

  BigDecimal spareQuantity;

  BigDecimal plannedQuantity;

  BigDecimal progressedQuantity;

  BigDecimal progressRate;

  OffsetDateTime startDate;

  OffsetDateTime endDate;

  CompanyId actorId;

  CompanyId receiverId;

  OffsetDateTime completedDate;

  ProductionPlanDetailProgressTypeKind progressType;

  ProductionPlanDetailStatusKind status;

  OffsetDateTime determinedDate;

  OffsetDateTime canceledDate;

  int order;

  UnitKind unit;

  List<ProductionPlanDetailId> dependencies;

}
