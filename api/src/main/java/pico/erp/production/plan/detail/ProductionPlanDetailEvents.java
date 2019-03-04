package pico.erp.production.plan.detail;

import java.time.OffsetDateTime;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import pico.erp.shared.event.Event;

public interface ProductionPlanDetailEvents {

  @Data
  @AllArgsConstructor
  @NoArgsConstructor
  class CreatedEvent implements Event {

    public final static String CHANNEL = "event.production-plan-detail.created";

    private ProductionPlanDetailId id;

    public String channel() {
      return CHANNEL;
    }

  }

  @Data
  @AllArgsConstructor
  @NoArgsConstructor
  class ProgressedEvent implements Event {

    public final static String CHANNEL = "event.production-plan-detail.progressed";

    private ProductionPlanDetailId id;

    public String channel() {
      return CHANNEL;
    }

  }

  @Data
  @AllArgsConstructor
  @NoArgsConstructor
  class DeterminedEvent implements Event {

    public final static String CHANNEL = "event.production-plan-detail.determined";

    private ProductionPlanDetailId id;

    public String channel() {
      return CHANNEL;
    }

  }

  @Data
  @AllArgsConstructor
  @NoArgsConstructor
  class UpdatedEvent implements Event {

    public final static String CHANNEL = "event.production-plan-detail.updated";

    private ProductionPlanDetailId id;

    public String channel() {
      return CHANNEL;
    }

  }

  @Data
  @AllArgsConstructor
  @NoArgsConstructor
  class SplitEvent implements Event {

    public final static String CHANNEL = "event.production-plan-detail.split";

    private ProductionPlanDetailId id;

    public String channel() {
      return CHANNEL;
    }

  }

  @Data
  @AllArgsConstructor
  @NoArgsConstructor
  class CanceledEvent implements Event {

    public final static String CHANNEL = "event.production-plan-detail.canceled";

    private ProductionPlanDetailId id;

    public String channel() {
      return CHANNEL;
    }

  }

  @Data
  @AllArgsConstructor
  @NoArgsConstructor
  class CompletedEvent implements Event {

    public final static String CHANNEL = "event.production-plan-detail.completed";

    private ProductionPlanDetailId id;

    public String channel() {
      return CHANNEL;
    }

  }

  @Data
  @AllArgsConstructor
  @NoArgsConstructor
  class DeletedEvent implements Event {

    public final static String CHANNEL = "event.production-plan-detail.deleted";

    private ProductionPlanDetailId id;

    public String channel() {
      return CHANNEL;
    }

  }

  @Data
  @AllArgsConstructor
  @NoArgsConstructor
  class RescheduledEvent implements Event {

    public final static String CHANNEL = "event.production-plan-detail.rescheduled";

    OffsetDateTime beforeStartDate;

    OffsetDateTime beforeEndDate;

    private ProductionPlanDetailId id;

    public String channel() {
      return CHANNEL;
    }

  }
}
