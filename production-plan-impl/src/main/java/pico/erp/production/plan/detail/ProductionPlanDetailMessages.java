package pico.erp.production.plan.detail;

import java.math.BigDecimal;
import java.time.OffsetDateTime;
import java.util.Collection;
import javax.validation.Valid;
import javax.validation.constraints.Future;
import javax.validation.constraints.Min;
import javax.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.Value;
import org.hibernate.validator.constraints.ScriptAssert;
import pico.erp.company.CompanyData;
import pico.erp.item.ItemData;
import pico.erp.item.spec.ItemSpecData;
import pico.erp.production.plan.ProductionPlan;
import pico.erp.production.plan.ProductionPlanId;
import pico.erp.shared.event.Event;
import pico.erp.user.UserData;

public interface ProductionPlanDetailMessages {

  @Builder
  @NoArgsConstructor
  @AllArgsConstructor
  @Data
  @ScriptAssert.List({
    @ScriptAssert(lang = "javascript", alias = "_", script = "_.startDate.isBefore(_.endDate)", message = "{start-date.after.than.end-date")
  })
  class CreateRequest {

    @Valid
    @NotNull
    ProductionPlanDetailId id;

    @Valid
    @NotNull
    ProductionPlan plan;

    @Valid
    @NotNull
    ItemData item;

    @Valid
    ItemSpecData itemSpec;

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
  @ScriptAssert.List({
    @ScriptAssert(lang = "javascript", alias = "_", script = "_.startDate.isBefore(_.endDate)", message = "{start-date.after.than.end-date")
  })
  class UpdateRequest {

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

    UserData charger;

    CompanyData progressCompany;

    ProductionPlanDetailProgressTypeKind progressType;

  }

  @Data
  class ProgressRequest {

    @NotNull
    @Min(0)
    BigDecimal progressedQuantity;

  }

  @Data
  class DetermineRequest {

  }

  @Data
  @ScriptAssert.List({
    @ScriptAssert(lang = "javascript", alias = "_", script = "_.startDate.isBefore(_.endDate)", message = "{start-date.after.than.end-date")
  })
  class RescheduleRequest {

    @NotNull
    OffsetDateTime startDate;

    @Future
    @NotNull
    OffsetDateTime endDate;

  }

  @Data
  @ScriptAssert.List({
    @ScriptAssert(lang = "javascript", alias = "_", script = "_.beforeStartDate.isBefore(_.beforeEndDate)", message = "{start-date.after.than.end-date")
  })
  class RescheduleByDependencyRequest {

    @NotNull
    ProductionPlanDetail dependency;

    @NotNull
    OffsetDateTime beforeStartDate;

    @NotNull
    OffsetDateTime beforeEndDate;

  }

  @Data
  class CompleteRequest {

  }

  @Data
  class CancelRequest {


  }

  @Data
  class SplitRequest {

    @NotNull
    @Min(0)
    BigDecimal quantity;

    @NotNull
    @Min(0)
    BigDecimal spareQuantity;

  }

  @Data
  class AddDependencyRequest {

    ProductionPlanDetail dependency;

  }

  @Data
  class RemoveDependencyRequest {

    ProductionPlanDetail dependency;

  }

  @Value
  class CreateResponse {

    Collection<Event> events;

  }

  @Value
  class UpdateResponse {

    Collection<Event> events;

  }

  @Value
  class SplitResponse {

    Collection<Event> events;

    ProductionPlanDetail splitPlan;

  }

  @Value
  class ProgressResponse {

    Collection<Event> events;

  }

  @Value
  class DetermineResponse {

    Collection<Event> events;

  }

  @Value
  class CompleteResponse {

    Collection<Event> events;

  }

  @Value
  class CancelResponse {

    Collection<Event> events;

  }

  @Value
  class RescheduleResponse {

    Collection<Event> events;

  }

  @Value
  class AddDependencyResponse {

    Collection<Event> events;

  }

  @Value
  class RemoveDependencyResponse {

    Collection<Event> events;

  }

  @Value
  class RescheduleByDependencyResponse {

    Collection<Event> events;

  }


}
