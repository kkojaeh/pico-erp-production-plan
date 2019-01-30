package pico.erp.production.plan.detail;

import java.math.BigDecimal;
import java.time.OffsetDateTime;
import java.util.List;
import java.util.stream.Collectors;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import pico.erp.company.CompanyId;
import pico.erp.item.ItemId;
import pico.erp.item.spec.ItemSpecId;
import pico.erp.process.ProcessId;
import pico.erp.process.preparation.ProcessPreparationId;
import pico.erp.production.plan.ProductionPlanId;

@Builder
@AllArgsConstructor
@NoArgsConstructor
@Data
public class ProductionPlanDetailData {

  ProductionPlanDetailId id;

  ProductionPlanDetailGroupId groupId;

  ProductionPlanId planId;

  String name;

  ItemId itemId;

  ProcessId processId;

  ProcessPreparationId processPreparationId;

  ItemSpecId itemSpecId;

  BigDecimal quantity;

  BigDecimal spareQuantity;

  BigDecimal plannedQuantity;

  BigDecimal progressedQuantity;

  OffsetDateTime startDate;

  OffsetDateTime endDate;

  CompanyId progressCompanyId;

  OffsetDateTime completedDate;

  ProductionPlanDetailProgressTypeKind progressType;

  ProductionPlanDetailStatusKind status;

  OffsetDateTime determinedDate;

  OffsetDateTime canceledDate;

  int order;

  List<ProductionPlanDetailId> dependencies;


  List<ProductionPlanDetailRequests.AddDependencyRequest> toAddDependencyRequests() {
    return dependencies.stream()
      .map(dependencyId -> ProductionPlanDetailRequests.AddDependencyRequest.builder()
        .id(id)
        .dependencyId(dependencyId)
        .build()
      ).collect(Collectors.toList());
  }

  ProductionPlanDetailRequests.CreateRequest toCreateRequest() {
    return ProductionPlanDetailRequests.CreateRequest.builder()
      .id(id)
      .planId(planId)
      .itemId(itemId)
      .processId(processId)
      .processPreparationId(processPreparationId)
      .quantity(quantity)
      .spareQuantity(spareQuantity)
      .startDate(startDate)
      .endDate(endDate)
      .build();
  }

}
