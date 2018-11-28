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

  BigDecimal progressedQuantity;

  OffsetDateTime dueDate;

  OffsetDateTime determinedDate;

  OffsetDateTime completedDate;

  OffsetDateTime canceledDate;

  ProductionPlanStatusKind status;

  ProjectData project;

  public ProductionPlan() {

  }

  public ProductionPlanMessages.CreateResponse apply(
    ProductionPlanMessages.CreateRequest request) {
    this.id = request.getId();
    this.item = request.getItem();
    this.project = request.getProject();
    this.quantity = request.getQuantity();
    this.spareQuantity = request.getSpareQuantity();
    this.progressedQuantity = BigDecimal.ZERO;
    this.dueDate = request.getDueDate();
    this.status = ProductionPlanStatusKind.CREATED;
    this.code = request.getCodeGenerator().generate(this);
    // TODO: plan 품목 생성
    return new ProductionPlanMessages.CreateResponse(
      Arrays.asList(new ProductionPlanEvents.CreatedEvent(this.id))
    );
  }

  public ProductionPlanMessages.UpdateResponse apply(
    ProductionPlanMessages.UpdateRequest request) {
    if (!isUpdatable()) {
      throw new CannotUpdateException();
    }
    this.quantity = request.getQuantity();
    this.spareQuantity = request.getSpareQuantity();
    this.dueDate = request.getDueDate();
    return new ProductionPlanMessages.UpdateResponse(
      Arrays.asList(new ProductionPlanEvents.UpdatedEvent(this.id))
    );
  }

  public ProductionPlanMessages.DetermineResponse apply(
    ProductionPlanMessages.DetermineRequest request) {
    if (!isDeterminable()) {
      throw new ProductionPlanExceptions.CannotDetermineException();
    }
    this.status = ProductionPlanStatusKind.DETERMINED;
    this.determinedDate = OffsetDateTime.now();
    return new ProductionPlanMessages.DetermineResponse(
      Arrays.asList(new ProductionPlanEvents.DeterminedEvent(this.id))
    );
  }

  public ProductionPlanMessages.CancelResponse apply(
    ProductionPlanMessages.CancelRequest request) {
    if (!isCancelable()) {
      throw new ProductionPlanExceptions.CannotCancelException();
    }
    this.status = ProductionPlanStatusKind.CANCELED;
    this.canceledDate = OffsetDateTime.now();
    return new ProductionPlanMessages.CancelResponse(
      Arrays.asList(new ProductionPlanEvents.CanceledEvent(this.id))
    );
  }

  public ProductionPlanMessages.CompleteResponse apply(
    ProductionPlanMessages.CompleteRequest request) {
    if (!isCompletable()) {
      throw new ProductionPlanExceptions.CannotCompleteException();
    }
    this.status = ProductionPlanStatusKind.COMPLETED;
    this.completedDate = OffsetDateTime.now();
    return new ProductionPlanMessages.CompleteResponse(
      Arrays.asList(new ProductionPlanEvents.CompletedEvent(this.id))
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

  public boolean isReschedulable() {
    return status.isReschedulable();
  }

  public boolean isSplittable() {
    return status.isSplittable();
  }

  public boolean isUpdatable() {
    return status.isUpdatable();
  }


}
