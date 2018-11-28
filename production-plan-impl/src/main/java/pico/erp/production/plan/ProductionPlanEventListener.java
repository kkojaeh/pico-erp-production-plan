package pico.erp.production.plan;

import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

@SuppressWarnings("unused")
@Component
@Transactional
public class ProductionPlanEventListener {

  private static final String LISTENER_NAME = "listener.production-plan-event-listener";

}
