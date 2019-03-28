package pico.erp.production.plan.detail;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.Collection;
import java.util.List;
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
import pico.erp.company.CompanyId;
import pico.erp.item.ItemId;
import pico.erp.item.spec.ItemSpecCode;
import pico.erp.item.spec.ItemSpecId;
import pico.erp.process.ProcessId;
import pico.erp.process.preparation.ProcessPreparationId;
import pico.erp.production.plan.ProductionPlan;
import pico.erp.shared.data.UnitKind;
import pico.erp.shared.event.Event;

public interface ProductionPlanDetailMessages {

  interface Create {

    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    @Data
    @ScriptAssert.List({
      @ScriptAssert(lang = "javascript", alias = "_", script = "_.startDate.isBefore(_.endDate)", message = "{start-date.after.than.end-date")
    })
    class Request {

      @Valid
      @NotNull
      ProductionPlanDetailId id;

      @Valid
      @NotNull
      ProductionPlan plan;

      @Valid
      @NotNull
      ItemId itemId;

      @Valid
      @NotNull
      ItemSpecCode itemSpecCode;

      ProcessId processId;

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
      LocalDateTime startDate;

      @Future
      @NotNull
      LocalDateTime endDate;

      @NotNull
      UnitKind unit;

      @Valid
      CompanyId receiverId;

    }

    @Value
    class Response {

      Collection<Event> events;

    }
  }

  interface Update {

    @Data
    @ScriptAssert.List({
      @ScriptAssert(lang = "javascript", alias = "_", script = "_.startDate.isBefore(_.endDate)", message = "{start-date.after.than.end-date")
    })
    class Request {

      @NotNull
      @Min(0)
      BigDecimal quantity;

      @NotNull
      @Min(0)
      BigDecimal spareQuantity;

      @Future
      @NotNull
      LocalDateTime startDate;

      @Future
      @NotNull
      LocalDateTime endDate;

      CompanyId actorId;

      CompanyId receiverId;

      ProductionPlanDetailProgressTypeKind progressType;

    }

    @Value
    class Response {

      Collection<Event> events;

    }

  }

  interface Progress {

    @Data
    class Request {

      @NotNull
      @Min(0)
      BigDecimal progressedQuantity;

    }

    @Value
    class Response {

      Collection<Event> events;

    }
  }

  interface Determine {

    @Data
    class Request {

    }

    @Value
    class Response {

      Collection<Event> events;

    }

  }

  interface Reschedule {

    @Data
    @ScriptAssert.List({
      @ScriptAssert(lang = "javascript", alias = "_", script = "_.startDate.isBefore(_.endDate)", message = "{start-date.after.than.end-date")
    })
    class Request {

      @NotNull
      LocalDateTime startDate;

      @Future
      @NotNull
      LocalDateTime endDate;

    }

    @Value
    class Response {

      Collection<Event> events;

    }

  }

  interface RescheduleByDependency {

    @Data
    @ScriptAssert.List({
      @ScriptAssert(lang = "javascript", alias = "_", script = "_.beforeStartDate.isBefore(_.beforeEndDate)", message = "{start-date.after.than.end-date")
    })
    class Request {

      @NotNull
      ProductionPlanDetail dependency;

      @NotNull
      LocalDateTime beforeStartDate;

      @NotNull
      LocalDateTime beforeEndDate;

    }

    @Value
    class Response {

      Collection<Event> events;

    }

  }

  interface Complete {

    @Data
    class Request {

    }

    @Value
    class Response {

      Collection<Event> events;

    }

  }

  interface Cancel {

    @Data
    class Request {


    }

    @Value
    class Response {

      Collection<Event> events;

    }

  }

  interface Delete {

    @Data
    class Request {

    }

    @Value
    class Response {

      Collection<Event> events;

    }

  }

  interface Split {

    @Data
    class Request {

      @NotNull
      @Min(0)
      BigDecimal quantity;

      @NotNull
      @Min(0)
      BigDecimal spareQuantity;

    }

    @Value
    class Response {

      Collection<Event> events;

      ProductionPlanDetail split;

    }

  }

  interface AddDependency {

    @Data
    class Request {

      ProductionPlanDetail dependency;

    }

    @Value
    class Response {

      Collection<Event> events;

    }
  }

  interface RemoveDependency {

    @Data
    class Request {

      ProductionPlanDetail dependency;

    }

    @Value
    class Response {

      Collection<Event> events;

    }

  }

  interface RevalidateByDependedOns {

    @Data
    @AllArgsConstructor
    @NoArgsConstructor
    class Request {

      @NotNull
      List<ProductionPlanDetail> dependedOns;

    }

    @Value
    class Response {

      Collection<Event> events;

    }

  }


}
