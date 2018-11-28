package pico.erp.production.plan.detail;

import java.io.Serializable;
import java.math.BigDecimal;
import java.time.OffsetDateTime;
import java.time.temporal.ChronoUnit;
import java.util.Arrays;
import java.util.Comparator;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.Set;
import javax.persistence.Id;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.ToString;
import lombok.experimental.FieldDefaults;
import lombok.val;
import pico.erp.audit.annotation.Audit;
import pico.erp.company.CompanyData;
import pico.erp.item.ItemData;
import pico.erp.item.ItemId;
import pico.erp.item.spec.ItemSpecData;
import pico.erp.production.plan.ProductionPlan;
import pico.erp.production.plan.ProductionPlanId;
import pico.erp.shared.event.Event;
import pico.erp.user.UserData;

/**
 * 주문 접수
 */
@Getter
@ToString
@AllArgsConstructor
@EqualsAndHashCode(of = "id")
@Builder(toBuilder = true)
@FieldDefaults(level = AccessLevel.PRIVATE)
@Audit(alias = "production-plan-detail")
public class ProductionPlanDetail implements Serializable {

  private static final long serialVersionUID = 1L;

  @Id
  ProductionPlanDetailId id;

  ProductionPlan plan;

  ItemData item;

  ItemSpecData itemSpec;

  BigDecimal quantity;

  BigDecimal spareQuantity;

  BigDecimal progressedQuantity;

  OffsetDateTime startDate;

  OffsetDateTime endDate;

  CompanyData progressCompany;

  OffsetDateTime completedDate;

  OffsetDateTime determinedDate;

  OffsetDateTime canceledDate;

  UserData charger;

  ProductionPlanDetailProgressTypeKind progressType;

  ProductionPlanDetailStatusKind status;

  Set<ProductionPlanDetail> dependencies;

/*
  public OffsetDateTime calculateParentProgressibleDate() {

    OffsetDateTime date = endDate;

    for (ProductionPlanSchedule schedule : schedules) {
      if (schedule.getScheduledDate().isBefore(date)) {
        date = schedule.getScheduledDate();
      }
    }
    return date;
  }*/


  public ProductionPlanDetail() {

  }

  public ProductionPlanDetailMessages.CreateResponse apply(
    ProductionPlanDetailMessages.CreateRequest request) {
    this.id = request.getId();
    this.plan = request.getPlan();
    this.item = request.getItem();
    this.itemSpec = request.getItemSpec();
    this.quantity = request.getQuantity();
    this.spareQuantity = request.getSpareQuantity();
    this.progressedQuantity = BigDecimal.ZERO;
    this.startDate = request.getStartDate();
    this.endDate = request.getEndDate();
    this.status = ProductionPlanDetailStatusKind.CREATED;
    this.dependencies = new HashSet<>();
    return new ProductionPlanDetailMessages.CreateResponse(
      Arrays.asList(new ProductionPlanDetailEvents.CreatedEvent(this.id))
    );
  }

  public ProductionPlanDetailMessages.UpdateResponse apply(
    ProductionPlanDetailMessages.UpdateRequest request) {
    if (!isUpdatable()) {
      throw new ProductionPlanDetailExceptions.CannotUpdateException();
    }
    this.quantity = request.getQuantity();
    this.spareQuantity = request.getSpareQuantity();
    this.startDate = request.getStartDate();
    this.endDate = request.getEndDate();
    this.charger = request.getCharger();
    this.progressCompany = request.getProgressCompany();
    this.progressType = request.getProgressType();
    return new ProductionPlanDetailMessages.UpdateResponse(
      Arrays.asList(new ProductionPlanDetailEvents.UpdatedEvent(this.id))
    );
  }

  public ProductionPlanDetailMessages.DetermineResponse apply(
    ProductionPlanDetailMessages.DetermineRequest request) {
    if (!isDeterminable()) {
      throw new ProductionPlanDetailExceptions.CannotDetermineException();
    }
    this.status = ProductionPlanDetailStatusKind.DETERMINED;
    this.determinedDate = OffsetDateTime.now();
    return new ProductionPlanDetailMessages.DetermineResponse(
      Arrays.asList(new ProductionPlanDetailEvents.DeterminedEvent(this.id))
    );
  }

  public ProductionPlanDetailMessages.ProgressResponse apply(
    ProductionPlanDetailMessages.ProgressRequest request) {
    if (!isProgressable()) {
      throw new ProductionPlanDetailExceptions.CannotProgressException();
    }
    this.progressedQuantity = this.progressedQuantity.add(request.getProgressedQuantity());
    this.status = ProductionPlanDetailStatusKind.IN_PROGRESS;
    return new ProductionPlanDetailMessages.ProgressResponse(
      Arrays.asList(new ProductionPlanDetailEvents.ProgressedEvent(this.id))
    );
  }

  public ProductionPlanDetailMessages.SplitResponse apply(
    ProductionPlanDetailMessages.SplitRequest request) {
    if (!isSplittable()) {
      throw new ProductionPlanDetailExceptions.CannotSplitException();
    }
    val remainedQuantity = this.getPlannedQuantity().subtract(this.progressedQuantity);
    val splitQuantity = request.getQuantity().add(request.getSpareQuantity());
    if (splitQuantity.compareTo(remainedQuantity) > 0) {
      throw new ProductionPlanDetailExceptions.CannotSplitException();
    }
    this.quantity = this.quantity.subtract(request.getQuantity());
    this.spareQuantity = this.spareQuantity.subtract(request.getSpareQuantity());
    val split = new ProductionPlanDetail();
    split.id = ProductionPlanDetailId.generate();
    split.plan = this.plan;
    split.item = this.item;
    split.itemSpec = this.itemSpec;
    split.quantity = request.getQuantity();
    split.spareQuantity = request.getSpareQuantity();
    split.progressedQuantity = BigDecimal.ZERO;
    split.startDate = this.startDate;
    split.endDate = this.endDate;
    split.status = ProductionPlanDetailStatusKind.CREATED;
    split.dependencies = new HashSet<>(this.dependencies);

    return new ProductionPlanDetailMessages.SplitResponse(
      Arrays.asList(new ProductionPlanDetailEvents.SplitEvent(this.id)),
      split
    );
  }

  public ProductionPlanDetailMessages.RescheduleResponse apply(
    ProductionPlanDetailMessages.RescheduleRequest request) {
    if (!isReschedulable()) {
      throw new ProductionPlanDetailExceptions.CannotRescheduleException();
    }
    val beforeStartDate = this.startDate;
    val beforeEndDate = this.endDate;
    if (!beforeStartDate.isEqual(request.getStartDate())) {
      val startableDate = getStartableDate();
      if (startableDate.isAfter(request.getStartDate())) {
        throw new ProductionPlanDetailExceptions.CannotRescheduleException();
      }
    }
    this.startDate = request.getStartDate();
    this.endDate = request.getEndDate();
    return new ProductionPlanDetailMessages.RescheduleResponse(
      Arrays.asList(
        new ProductionPlanDetailEvents.RescheduledEvent(
          beforeStartDate,
          beforeEndDate,
          this.id
        )
      )
    );
  }

  public ProductionPlanDetailMessages.RescheduleByDependencyResponse apply(
    ProductionPlanDetailMessages.RescheduleByDependencyRequest request) {
    if (!isReschedulable()) {
      throw new ProductionPlanDetailExceptions.CannotRescheduleException();
    }
    val events = new LinkedList<Event>();
    val startableDate = getStartableDate();
    if (startableDate.isAfter(this.startDate)) {
      val beforeStartDate = this.startDate;
      val beforeEndDate = this.endDate;
      val hours = ChronoUnit.HOURS.between(this.startDate, this.endDate);
      this.startDate = startableDate.plusHours(1);
      this.endDate = this.startDate.plusHours(hours);
      events.add(
        new ProductionPlanDetailEvents.RescheduledEvent(
          beforeStartDate,
          beforeEndDate,
          this.id
        )
      );
    }
    return new ProductionPlanDetailMessages.RescheduleByDependencyResponse(events);
  }

  public ProductionPlanDetailMessages.CancelResponse apply(
    ProductionPlanDetailMessages.CancelRequest request) {
    if (!isCancelable()) {
      throw new ProductionPlanDetailExceptions.CannotCancelException();
    }
    this.status = ProductionPlanDetailStatusKind.CANCELED;
    this.canceledDate = OffsetDateTime.now();
    return new ProductionPlanDetailMessages.CancelResponse(
      Arrays.asList(new ProductionPlanDetailEvents.CanceledEvent(this.id))
    );
  }

  public ProductionPlanDetailMessages.CompleteResponse apply(
    ProductionPlanDetailMessages.CompleteRequest request) {
    if (!isCompletable()) {
      throw new ProductionPlanDetailExceptions.CannotCompleteException();
    }
    this.status = ProductionPlanDetailStatusKind.COMPLETED;
    this.completedDate = OffsetDateTime.now();
    return new ProductionPlanDetailMessages.CompleteResponse(
      Arrays.asList(new ProductionPlanDetailEvents.CompletedEvent(this.id))
    );
  }

  public ProductionPlanDetailMessages.AddDependencyResponse apply(
    ProductionPlanDetailMessages.AddDependencyRequest request) {
    if (!isUpdatable()) {
      throw new ProductionPlanDetailExceptions.CannotUpdateException();
    }

    val dependency = request.getDependency();

    if (isDeepDependOn(dependency) || this.dependencies.contains(dependency)) {
      throw new ProductionPlanDetailExceptions.CannotUpdateException();
    }
    this.dependencies.add(dependency);
    return new ProductionPlanDetailMessages.AddDependencyResponse(
      Arrays.asList(new ProductionPlanDetailEvents.UpdatedEvent(this.id))
    );
  }

  public ProductionPlanDetailMessages.RemoveDependencyResponse apply(
    ProductionPlanDetailMessages.RemoveDependencyRequest request) {
    if (!isUpdatable()) {
      throw new ProductionPlanDetailExceptions.CannotUpdateException();
    }
    if (!this.dependencies.contains(request.getDependency())) {
      throw new ProductionPlanDetailExceptions.CannotUpdateException();
    }
    this.dependencies.remove(request.getDependency());
    return new ProductionPlanDetailMessages.RemoveDependencyResponse(
      Arrays.asList(new ProductionPlanDetailEvents.UpdatedEvent(this.id))
    );
  }

  public BigDecimal getPlannedQuantity() {
    return quantity.add(spareQuantity);
  }

  protected OffsetDateTime getStartableDate() {
    val endDates = new HashMap<ItemId, OffsetDateTime>();
    for (ProductionPlanDetail dependency : dependencies) {
      val itemId = dependency.getItem().getId();
      val endDate = dependency.getEndDate();
      if (!endDates.containsKey(itemId)) {
        endDates.put(itemId, OffsetDateTime.MAX);
      }
      val mappedDate = endDates.get(itemId);
      if (endDate.isBefore(mappedDate)) {
        endDates.put(itemId, endDate);
      }
    }
    return endDates.values().stream()
      .max(Comparator.comparing(date -> date))
      .orElse(OffsetDateTime.now());
  }

  public boolean isCancelable() {
    return status.isCancelable();
  }

  public boolean isCompletable() {
    return status.isCompletable();
  }

  private boolean isDeepDependOn(ProductionPlanDetail dependency) {
    if (dependency.equals(this)) {
      return true;
    }
    for (ProductionPlanDetail deep : dependency.getDependencies()) {
      if (isDeepDependOn(deep)) {
        return true;
      }
    }
    return false;
  }

  public boolean isDeterminable() {
    return status.isDeterminable() && this.progressCompany != null && this.progressType != null
      && this.charger != null;
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
