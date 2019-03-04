package pico.erp.production.plan;

import java.util.Properties;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.builder.SpringApplicationBuilder;
import org.springframework.context.annotation.Bean;
import pico.erp.audit.AuditApi;
import pico.erp.bom.BomApi;
import pico.erp.company.CompanyApi;
import pico.erp.item.ItemApi;
import pico.erp.process.ProcessApi;
import pico.erp.production.plan.ProductionPlanApi.Roles;
import pico.erp.project.ProjectApi;
import pico.erp.shared.ApplicationId;
import pico.erp.shared.ApplicationStarter;
import pico.erp.shared.Public;
import pico.erp.shared.SpringBootConfigs;
import pico.erp.shared.data.Role;
import pico.erp.shared.impl.ApplicationImpl;
import pico.erp.user.UserApi;

@Slf4j
@SpringBootConfigs
public class ProductionPlanApplication implements ApplicationStarter {

  public static final String CONFIG_NAME = "production-plan/application";

  public static final String CONFIG_NAME_PROPERTY = "spring.config.name=production-plan/application";

  public static final Properties DEFAULT_PROPERTIES = new Properties();

  static {
    DEFAULT_PROPERTIES.put("spring.config.name", CONFIG_NAME);
  }

  public static SpringApplication application() {
    return new SpringApplicationBuilder(ProductionPlanApplication.class)
      .properties(DEFAULT_PROPERTIES)
      .web(false)
      .build();
  }

  public static void main(String[] args) {
    application().run(args);
  }

  @Override
  public Set<ApplicationId> getDependencies() {
    return Stream.of(
      UserApi.ID,
      CompanyApi.ID,
      ItemApi.ID,
      AuditApi.ID,
      ProjectApi.ID,
      BomApi.ID,
      ProcessApi.ID
    ).collect(Collectors.toSet());
  }

  @Override
  public ApplicationId getId() {
    return ProductionPlanApi.ID;
  }

  @Override
  public boolean isWeb() {
    return false;
  }

  @Bean
  @Public
  public Role productionPlanChargerRole() {
    return Roles.PRODUCTION_PLAN_CHARGER;
  }

  @Bean
  @Public
  public Role productionPlanManagerRole() {
    return Roles.PRODUCTION_PLAN_MANAGER;
  }

  @Override
  public pico.erp.shared.Application start(String... args) {
    return new ApplicationImpl(application().run(args));
  }


}
