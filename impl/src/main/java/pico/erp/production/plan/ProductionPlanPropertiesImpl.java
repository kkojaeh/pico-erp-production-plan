package pico.erp.production.plan;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;
import pico.erp.shared.Public;
import pico.erp.user.group.GroupData;

@Public
@Data
@Configuration
@ConfigurationProperties("production-plan")
public class ProductionPlanPropertiesImpl implements ProductionPlanProperties {

  GroupData chargerGroup;

  DetailGenerationPolicy detailGenerationPolicy;

}
