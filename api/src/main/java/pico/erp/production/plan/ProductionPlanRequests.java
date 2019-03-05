package pico.erp.production.plan;

import java.math.BigDecimal;
import java.time.OffsetDateTime;
import javax.validation.Valid;
import javax.validation.constraints.Future;
import javax.validation.constraints.Max;
import javax.validation.constraints.Min;
import javax.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import pico.erp.company.CompanyId;
import pico.erp.item.ItemId;
import pico.erp.project.ProjectId;
import pico.erp.shared.data.UnitKind;
import pico.erp.user.UserId;

public interface ProductionPlanRequests {

  @Data
  @NoArgsConstructor
  @AllArgsConstructor
  @Builder
  class CreateRequest {

    @Valid
    @NotNull
    ProductionPlanId id;

    @Valid
    @NotNull
    ItemId itemId;

    @Valid
    @NotNull
    ProjectId projectId;

    @NotNull
    @Min(0)
    BigDecimal quantity;

    @NotNull
    @Min(0)
    BigDecimal spareQuantity;

    @Future
    @NotNull
    OffsetDateTime dueDate;

    @NotNull
    UnitKind unit;

    @Valid
    @NotNull
    UserId plannerId;

    @Valid
    @NotNull
    CompanyId receiverId;

  }

  @Data
  @NoArgsConstructor
  @AllArgsConstructor
  @Builder
  class UpdateRequest {

    @Valid
    @NotNull
    ProductionPlanId id;

    @NotNull
    @Min(0)
    BigDecimal quantity;

    @NotNull
    @Min(0)
    BigDecimal spareQuantity;

    @Future
    @NotNull
    OffsetDateTime dueDate;

  }

  @Data
  @NoArgsConstructor
  @AllArgsConstructor
  @Builder
  class CancelRequest {

    @Valid
    @NotNull
    ProductionPlanId id;

  }

  @Data
  @NoArgsConstructor
  @AllArgsConstructor
  @Builder
  class PrepareRequest {

    @Valid
    @NotNull
    ProductionPlanId id;

  }

  @Data
  @NoArgsConstructor
  @AllArgsConstructor
  @Builder
  class DetermineRequest {

    @Valid
    @NotNull
    ProductionPlanId id;

  }

  @Data
  @NoArgsConstructor
  @AllArgsConstructor
  @Builder
  class CompleteRequest {

    @Valid
    @NotNull
    ProductionPlanId id;

    @NotNull
    @Min(0)
    BigDecimal completedQuantity;

  }

  @Data
  @NoArgsConstructor
  @AllArgsConstructor
  @Builder
  class ProgressRequest {

    @Valid
    @NotNull
    ProductionPlanId id;

    @NotNull
    @Min(0)
    @Max(1)
    BigDecimal progressRate;

  }


}
