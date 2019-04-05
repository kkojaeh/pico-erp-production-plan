package pico.erp.production.plan;

import java.math.BigDecimal;
import java.time.OffsetDateTime;
import java.util.Collection;
import javax.validation.Valid;
import javax.validation.constraints.Future;
import javax.validation.constraints.Max;
import javax.validation.constraints.Min;
import javax.validation.constraints.NotNull;
import lombok.Data;
import lombok.Value;
import pico.erp.company.CompanyId;
import pico.erp.item.ItemId;
import pico.erp.project.ProjectId;
import pico.erp.shared.data.UnitKind;
import pico.erp.shared.event.Event;
import pico.erp.user.UserId;

public interface ProductionPlanMessages {

  interface Create {

    @Data
    class Request {

      @Valid
      @NotNull
      ProductionPlanId id;

      @Valid
      @NotNull
      ItemId itemId;

      @NotNull
      @Min(0)
      BigDecimal quantity;

      @NotNull
      @Min(0)
      BigDecimal spareQuantity;

      @Valid
      @NotNull
      ProjectId projectId;

      @Future
      @NotNull
      OffsetDateTime dueDate;

      @NotNull
      ProductionPlanCodeGenerator codeGenerator;

      @Valid
      @NotNull
      UnitKind unit;

      @Valid
      @NotNull
      UserId plannerId;

      @Valid
      @NotNull
      CompanyId receiverId;

    }

    @Value
    class Response {

      Collection<Event> events;

    }

  }

  interface Update {

    @Data
    class Request {

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

  interface Prepare {

    @Data
    class Request {

    }

    @Value
    class Response {

      Collection<Event> events;

    }

  }

  interface Complete {

    @Data
    class Request {

      @NotNull
      @Min(0)
      BigDecimal completedQuantity;

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

  interface Progress {

    @Data
    class Request {

      @NotNull
      @Min(0)
      @Max(1)
      BigDecimal progressRate;


    }

    @Value
    class Response {

      Collection<Event> events;

    }
  }


}
