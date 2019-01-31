package pico.erp.production.plan;

import java.io.Serializable;
import java.math.BigDecimal;
import java.time.OffsetDateTime;
import java.util.Arrays;
import javax.persistence.Id;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.ToString;
import lombok.experimental.FieldDefaults;
import pico.erp.audit.annotation.Audit;
import pico.erp.item.ItemData;
import pico.erp.production.plan.ProductionPlanExceptions.CannotUpdateException;
import pico.erp.project.ProjectData;

/**
 * 주문 접수
 */
@Getter
@ToString
@AllArgsConstructor
@EqualsAndHashCode(of = "id")
@Builder(toBuilder = true)
@FieldDefaults(level = AccessLevel.PRIVATE)
@Audit(alias = "production-plan")
public class ProductionPlan implements Serializable {

  private static final long serialVersionUID = 1L;

  @Id
  ProductionPlanId id;

  ProductionPlanCode code;

  ItemData item;

  BigDecimal quantity;

  BigDecimal spareQuantity;

  BigDecimal completedQuantity;

  BigDecimal progressRate;

  OffsetDateTime dueDate;

  OffsetDateTime determinedDate;

  OffsetDateTime completedDate;

  OffsetDateTime canceledDate;

  ProductionPlanStatusKind status;

  ProjectData project;

  public ProductionPlan() {

  }

  public ProductionPlanMessages.Create.Response apply(
    ProductionPlanMessages.Create.Request request) {
    this.id = request.getId();
    this.item = request.getItem();
    this.project = request.getProject();
    this.quantity = request.getQuantity();
    this.spareQuantity = request.getSpareQuantity();
    this.dueDate = request.getDueDate();
    this.status = ProductionPlanStatusKind.CREATED;
    this.progressRate = BigDecimal.ZERO;
    this.completedQuantity = BigDecimal.ZERO;
    this.code = request.getCodeGenerator().generate(this);
    return new ProductionPlanMessages.Create.Response(
      Arrays.asList(new ProductionPlanEvents.CreatedEvent(this.id))
    );
  }

  public ProductionPlanMessages.Update.Response apply(
    ProductionPlanMessages.Update.Request request) {
    if (!isUpdatable()) {
      throw new CannotUpdateException();
    }
    this.quantity = request.getQuantity();
    this.spareQuantity = request.getSpareQuantity();
    this.dueDate = request.getDueDate();
    return new ProductionPlanMessages.Update.Response(
      Arrays.asList(new ProductionPlanEvents.UpdatedEvent(this.id))
    );
  }

  public ProductionPlanMessages.Determine.Response apply(
    ProductionPlanMessages.Determine.Request request) {
    if (!isDeterminable()) {
      throw new ProductionPlanExceptions.CannotDetermineException();
    }
    this.status = ProductionPlanStatusKind.DETERMINED;
    this.determinedDate = OffsetDateTime.now();
    return new ProductionPlanMessages.Determine.Response(
      Arrays.asList(new ProductionPlanEvents.DeterminedEvent(this.id))
    );
  }

  public ProductionPlanMessages.Cancel.Response apply(
    ProductionPlanMessages.Cancel.Request request) {
    if (!isCancelable()) {
      throw new ProductionPlanExceptions.CannotCancelException();
    }
    this.status = ProductionPlanStatusKind.CANCELED;
    this.canceledDate = OffsetDateTime.now();
    return new ProductionPlanMessages.Cancel.Response(
      Arrays.asList(new ProductionPlanEvents.CanceledEvent(this.id))
    );
  }

  public ProductionPlanMessages.Prepare.Response apply(
    ProductionPlanMessages.Prepare.Request request) {
    if (!isPreparable()) {
      throw new ProductionPlanExceptions.CannotPrepareException();
    }
    this.status = ProductionPlanStatusKind.PREPARED;
    return new ProductionPlanMessages.Prepare.Response(
      Arrays.asList(new ProductionPlanEvents.PreparedEvent(this.id))
    );
  }

  public ProductionPlanMessages.Complete.Response apply(
    ProductionPlanMessages.Complete.Request request) {
    if (!isCompletable()) {
      throw new ProductionPlanExceptions.CannotCompleteException();
    }
    this.status = ProductionPlanStatusKind.COMPLETED;
    this.completedQuantity = request.getCompletedQuantity();
    this.completedDate = OffsetDateTime.now();
    return new ProductionPlanMessages.Complete.Response(
      Arrays.asList(new ProductionPlanEvents.CompletedEvent(this.id))
    );
  }

  public ProductionPlanMessages.Progress.Response apply(
    ProductionPlanMessages.Progress.Request request) {
    if (!isProgressable()) {
      throw new ProductionPlanExceptions.CannotProgressException();
    }
    this.status = ProductionPlanStatusKind.IN_PROGRESS;
    this.progressRate = request.getProgressRate();
    return new ProductionPlanMessages.Progress.Response(
      Arrays.asList(new ProductionPlanEvents.ProgressedEvent(this.id))
    );
  }

  public BigDecimal getPlannedQuantity() {
    return quantity.add(spareQuantity);
  }

  public boolean isCancelable() {
    return status.isCancelable();
  }

  public boolean isCompletable() {
    return status.isCompletable();
  }

  public boolean isDeterminable() {
    return status.isDeterminable();
  }

  public boolean isProgressable() {
    return status.isProgressable();
  }

  public boolean isUpdatable() {
    return status.isUpdatable();
  }

  public boolean isPreparable() {
    return status.isPreparable();
  }


}
