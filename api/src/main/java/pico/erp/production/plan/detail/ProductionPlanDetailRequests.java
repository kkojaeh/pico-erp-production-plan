package pico.erp.production.plan.detail;

import java.math.BigDecimal;
import java.time.OffsetDateTime;
import javax.validation.Valid;
import javax.validation.constraints.Future;
import javax.validation.constraints.Min;
import javax.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.Getter;
import lombok.NoArgsConstructor;
import pico.erp.company.CompanyId;
import pico.erp.item.ItemId;
import pico.erp.item.spec.ItemSpecId;
import pico.erp.process.ProcessId;
import pico.erp.process.preparation.ProcessPreparationId;
import pico.erp.production.plan.ProductionPlanId;
import pico.erp.user.UserId;

public interface ProductionPlanDetailRequests {

  @Data
  @NoArgsConstructor
  @AllArgsConstructor
  @Builder
  class CreateRequest {

    @Valid
    @NotNull
    ProductionPlanDetailId id;

    @Valid
    ProductionPlanId planId;

    @Valid
    @NotNull
    ItemId itemId;

    @Valid
    ProcessId processId;

    @Valid
    ProcessPreparationId processPreparationId;

    @Valid
    ItemSpecId itemSpecId;

    @NotNull
    @Min(0)
    BigDecimal quantity;

    @NotNull
    @Min(0)
    BigDecimal spareQuantity;

    @Future
    @NotNull
    OffsetDateTime startDate;

    @Future
    @NotNull
    OffsetDateTime endDate;

  }

  @Data
  @NoArgsConstructor
  @AllArgsConstructor
  @Builder
  class UpdateRequest {

    @Valid
    @NotNull
    ProductionPlanDetailId id;

    @NotNull
    @Min(0)
    BigDecimal quantity;

    @NotNull
    @Min(0)
    BigDecimal spareQuantity;

    @Future
    @NotNull
    OffsetDateTime startDate;

    @Future
    @NotNull
    OffsetDateTime endDate;

    UserId chargerId;

    CompanyId progressCompanyId;

    ProductionPlanDetailProgressTypeKind progressType;

  }

  @Data
  @NoArgsConstructor
  @AllArgsConstructor
  @Builder
  class DetermineRequest {

    @Valid
    @NotNull
    ProductionPlanDetailId id;

  }

  @Data
  @NoArgsConstructor
  @AllArgsConstructor
  @Builder
  class CancelRequest {

    @Valid
    @NotNull
    ProductionPlanDetailId id;

  }

  @Data
  @NoArgsConstructor
  @AllArgsConstructor
  @Builder
  class CompleteRequest {

    @Valid
    @NotNull
    ProductionPlanDetailId id;

  }

  @Data
  @NoArgsConstructor
  @AllArgsConstructor
  @Builder
  class DeleteRequest {

    @Valid
    @NotNull
    ProductionPlanDetailId id;

  }

  @Data
  @NoArgsConstructor
  @AllArgsConstructor
  @Builder
  class RescheduleRequest {

    @Valid
    @NotNull
    ProductionPlanDetailId id;

    @NotNull
    OffsetDateTime startDate;

    @Future
    @NotNull
    OffsetDateTime endDate;

  }

  @Data
  @NoArgsConstructor
  @AllArgsConstructor
  @Builder
  class ProgressRequest {

    @Valid
    @NotNull
    ProductionPlanDetailId id;

    @NotNull
    @Min(0)
    BigDecimal progressedQuantity;

    boolean completed;

  }

  @Data
  @NoArgsConstructor
  @AllArgsConstructor
  @Builder
  class SplitRequest {

    @Valid
    @NotNull
    ProductionPlanDetailId id;

    @NotNull
    @Min(0)
    BigDecimal quantity;

    @NotNull
    @Min(0)
    BigDecimal spareQuantity;

  }

  @Data
  @NoArgsConstructor
  @AllArgsConstructor
  @Builder
  class AddDependencyRequest {

    @Valid
    @NotNull
    ProductionPlanDetailId id;

    @Valid
    @NotNull
    ProductionPlanDetailId dependencyId;

  }

  @Data
  @NoArgsConstructor
  @AllArgsConstructor
  @Builder
  class RemoveDependencyRequest {

    @Valid
    @NotNull
    ProductionPlanDetailId id;

    @Valid
    @NotNull
    ProductionPlanDetailId dependencyId;

  }

  @Getter
  @Builder
  class GenerateRequest {

    @Valid
    @NotNull
    ProductionPlanId planId;

  }

  @Getter
  @Builder
  class RescheduleByDependencyRequest {

    @Valid
    @NotNull
    ProductionPlanDetailId dependencyId;

    @NotNull
    OffsetDateTime beforeStartDate;

    @NotNull
    OffsetDateTime beforeEndDate;

  }
}
