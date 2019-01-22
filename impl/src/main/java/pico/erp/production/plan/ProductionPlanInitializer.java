package pico.erp.production.plan;

import lombok.val;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Lazy;
import pico.erp.shared.ApplicationInitializer;
import pico.erp.user.group.GroupRequests;
import pico.erp.user.group.GroupService;

@SuppressWarnings("SpringJavaInjectionPointsAutowiringInspection")
@Configuration
public class ProductionPlanInitializer implements ApplicationInitializer {

  @Lazy
  @Autowired
  GroupService groupService;

  @Autowired
  ProductionPlanProperties properties;

  @Override
  public void initialize() {
    val chargerGroup = properties.getChargerGroup();
    if (!groupService.exists(chargerGroup.getId())) {
      groupService.create(
        GroupRequests.CreateRequest.builder()
          .id(chargerGroup.getId())
          .name(chargerGroup.getName())
          .build()
      );
    }
  }
}
