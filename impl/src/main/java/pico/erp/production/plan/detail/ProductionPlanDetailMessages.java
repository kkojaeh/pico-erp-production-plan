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
import pico.erp.item.spec.ItemSpecCode;
import pico.erp.item.spec.ItemSpecData;
import pico.erp.process.ProcessData;
import pico.erp.process.preparation.ProcessPreparationData;
import pico.erp.production.plan.ProductionPlan;
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
      ItemData item;

      @Valid
      @NotNull
      ItemSpecCode itemSpecCode;

      ProcessData process;

      ProcessPreparationData processPreparation;

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
      OffsetDateTime startDate;

      @Future
      @NotNull
      OffsetDateTime endDate;

      CompanyData progressCompany;

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
      OffsetDateTime startDate;

      @Future
      @NotNull
      OffsetDateTime endDate;

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
      OffsetDateTime beforeStartDate;

      @NotNull
      OffsetDateTime beforeEndDate;

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


}
