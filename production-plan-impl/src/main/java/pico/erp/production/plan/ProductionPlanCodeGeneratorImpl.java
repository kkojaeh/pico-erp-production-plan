package pico.erp.production.plan;

import java.time.LocalTime;
import java.time.OffsetDateTime;
import lombok.val;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Component;

@Component
public class ProductionPlanCodeGeneratorImpl implements ProductionPlanCodeGenerator {

  @Lazy
  @Autowired
  private ProductionPlanRepository productionPlanRepository;

  @Override
  public ProductionPlanCode generate(ProductionPlan productionPlan) {
    val now = OffsetDateTime.now();
    val begin = now.with(LocalTime.MIN);
    val end = now.with(LocalTime.MAX);
    val count = productionPlanRepository.countCreatedBetween(begin, end);
    val date =
      Integer.toString(now.getYear() - 1900, 36) + Integer.toString(now.getMonthValue(), 16)
        + Integer.toString(now.getDayOfMonth(), 36);
    val code = String.format("%s-%04d", date, count + 1).toUpperCase();
    return ProductionPlanCode.from(code);
  }
}
