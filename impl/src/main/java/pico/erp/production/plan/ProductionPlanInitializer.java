package pico.erp.production.plan;

import kkojaeh.spring.boot.component.SpringBootComponentReadyEvent;
import lombok.val;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationListener;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Lazy;
import pico.erp.user.group.GroupRequests;
import pico.erp.user.group.GroupService;

@SuppressWarnings("SpringJavaInjectionPointsAutowiringInspection")
@Configuration
public class ProductionPlanInitializer implements
  ApplicationListener<SpringBootComponentReadyEvent> {

  @Lazy
  @Autowired
  GroupService groupService;

  @Autowired
  ProductionPlanProperties properties;

  @Override
  public void onApplicationEvent(SpringBootComponentReadyEvent event) {
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
