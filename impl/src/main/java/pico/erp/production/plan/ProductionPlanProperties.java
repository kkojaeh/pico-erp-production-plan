package pico.erp.production.plan;

import java.time.LocalTime;
import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

@Data
@Configuration
@ConfigurationProperties("production-plan")
public class ProductionPlanProperties {

  DetailGenerationPolicy detailGenerationPolicy;

  @Data
  public static class DetailGenerationPolicy {

    LocalTime startTime;

    LocalTime endTime;

  }

}
