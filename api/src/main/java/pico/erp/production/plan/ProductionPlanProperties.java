package pico.erp.production.plan;

import java.time.LocalTime;
import lombok.Data;
import pico.erp.user.group.GroupData;

public interface ProductionPlanProperties {

  GroupData getChargerGroup();

  DetailGenerationPolicy getDetailGenerationPolicy();

  @Data
  class DetailGenerationPolicy {

    LocalTime startTime;

    LocalTime endTime;

  }

}
