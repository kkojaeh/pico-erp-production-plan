package pico.erp.production.plan.detail;

import java.math.BigDecimal;
import java.time.OffsetDateTime;
import java.util.List;
import java.util.stream.Collectors;
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
import pico.erp.item.spec.ItemSpecCode;
import pico.erp.item.spec.ItemSpecId;
import pico.erp.process.ProcessId;
import pico.erp.process.preparation.ProcessPreparationId;
import pico.erp.production.plan.ProductionPlanId;
import pico.erp.shared.data.UnitKind;

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

    @Valid
    ItemSpecCode itemSpecCode;

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

    @NotNull
    UnitKind unit;

    public static CreateRequest from(ProductionPlanDetailData data) {
      return CreateRequest.builder()
        .id(data.getId())
        .planId(data.getPlanId())
        .itemId(data.getItemId())
        .itemSpecCode(data.getItemSpecCode())
        .itemSpecId(data.getItemSpecId())
        .processId(data.getProcessId())
        .processPreparationId(data.getProcessPreparationId())
        .quantity(data.getQuantity())
        .spareQuantity(data.getSpareQuantity())
        .startDate(data.getStartDate())
        .endDate(data.getEndDate())
        .unit(data.getUnit())
        .build();
    }

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

    CompanyId actorId;

    CompanyId receiverId;

    ProductionPlanDetailProgressTypeKind progressType;

    public static UpdateRequest from(ProductionPlanDetailData data) {
      return UpdateRequest.builder()
        .id(data.getId())
        .quantity(data.getQuantity())
        .spareQuantity(data.getSpareQuantity())
        .startDate(data.getStartDate())
        .endDate(data.getEndDate())
        .actorId(data.getActorId())
        .receiverId(data.getReceiverId())
        .progressType(data.getProgressType())
        .build();
    }

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

    public static List<AddDependencyRequest> from(ProductionPlanDetailData data) {
      return data.getDependencies().stream()
        .map(dependencyId -> ProductionPlanDetailRequests.AddDependencyRequest.builder()
          .id(data.getId())
          .dependencyId(dependencyId)
          .build()
        ).collect(Collectors.toList());
    }

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
