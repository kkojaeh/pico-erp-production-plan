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
import pico.erp.item.ItemData;
import pico.erp.project.ProjectData;
import pico.erp.shared.event.Event;

public interface ProductionPlanMessages {

  interface Create {

    @Data
    class Request {

      @Valid
      @NotNull
      ProductionPlanId id;

      @Valid
      @NotNull
      ItemData item;

      @NotNull
      @Min(0)
      BigDecimal quantity;

      @NotNull
      @Min(0)
      BigDecimal spareQuantity;

      @Valid
      @NotNull
      ProjectData project;

      @Future
      @NotNull
      OffsetDateTime dueDate;

      @NotNull
      ProductionPlanCodeGenerator codeGenerator;

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
