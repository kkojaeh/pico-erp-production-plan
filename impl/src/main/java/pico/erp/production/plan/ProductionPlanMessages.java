package pico.erp.production.plan;

import java.math.BigDecimal;
import java.time.OffsetDateTime;
import java.util.Collection;
import javax.validation.Valid;
import javax.validation.constraints.Future;
import javax.validation.constraints.Min;
import javax.validation.constraints.NotNull;
import lombok.Data;
import lombok.Value;
import pico.erp.item.ItemData;
import pico.erp.project.ProjectData;
import pico.erp.shared.event.Event;

public interface ProductionPlanMessages {

  @Data
  class CreateRequest {

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

  @Data
  class UpdateRequest {

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
  class DetermineRequest {

  }


  @Data
  class CompleteRequest {


  }

  @Data
  class CancelRequest {


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


}
