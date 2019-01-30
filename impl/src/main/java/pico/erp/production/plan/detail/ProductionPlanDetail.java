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
import pico.erp.item.spec.ItemSpecData;
import pico.erp.process.ProcessData;
import pico.erp.process.preparation.ProcessPreparationData;
import pico.erp.production.plan.ProductionPlan;
import pico.erp.shared.event.Event;

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

  ProductionPlanDetailGroupId groupId;

  ProductionPlan plan;

  ItemData item;

  ProcessData process;

  ProcessPreparationData processPreparation;

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

  ProductionPlanDetailProgressTypeKind progressType;

  ProductionPlanDetailStatusKind status;

  Set<ProductionPlanDetail> dependencies;

  int order;

  public ProductionPlanDetail() {

  }

  public ProductionPlanDetailMessages.Create.Response apply(
    ProductionPlanDetailMessages.Create.Request request) {
    this.id = request.getId();
    this.groupId = ProductionPlanDetailGroupId.generate();
    this.plan = request.getPlan();
    this.item = request.getItem();
    this.process = request.getProcess();
    this.processPreparation = request.getProcessPreparation();
    this.itemSpec = request.getItemSpec();
    this.quantity = request.getQuantity();
    this.spareQuantity = request.getSpareQuantity();
    this.progressedQuantity = BigDecimal.ZERO;
    this.startDate = request.getStartDate();
    this.endDate = request.getEndDate();
    this.status = ProductionPlanDetailStatusKind.CREATED;
    this.dependencies = new HashSet<>();
    this.order = 0;
    return new ProductionPlanDetailMessages.Create.Response(
      Arrays.asList(new ProductionPlanDetailEvents.CreatedEvent(this.id))
    );
  }

  public ProductionPlanDetailMessages.Split.Response apply(
    ProductionPlanDetailMessages.Split.Request request) {
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
    split.groupId = this.groupId;
    split.plan = this.plan;
    split.item = this.item;
    split.process = this.process;
    split.processPreparation = this.processPreparation;
    split.itemSpec = this.itemSpec;
    split.quantity = request.getQuantity();
    split.spareQuantity = request.getSpareQuantity();
    split.progressedQuantity = BigDecimal.ZERO;
    split.startDate = this.startDate;
    split.endDate = this.endDate;
    split.status = ProductionPlanDetailStatusKind.CREATED;
    split.dependencies = new HashSet<>(this.dependencies);

    return new ProductionPlanDetailMessages.Split.Response(
      Arrays.asList(new ProductionPlanDetailEvents.SplitEvent(this.id)),
      split
    );
  }

  public ProductionPlanDetailMessages.Update.Response apply(
    ProductionPlanDetailMessages.Update.Request request) {
    if (!isUpdatable()) {
      throw new ProductionPlanDetailExceptions.CannotUpdateException();
    }
    this.quantity = request.getQuantity();
    this.spareQuantity = request.getSpareQuantity();
    this.startDate = request.getStartDate();
    this.endDate = request.getEndDate();
    this.progressCompany = request.getProgressCompany();
    this.progressType = request.getProgressType();
    return new ProductionPlanDetailMessages.Update.Response(
      Arrays.asList(new ProductionPlanDetailEvents.UpdatedEvent(this.id))
    );
  }

  public ProductionPlanDetailMessages.Determine.Response apply(
    ProductionPlanDetailMessages.Determine.Request request) {
    if (!isDeterminable()) {
      throw new ProductionPlanDetailExceptions.CannotDetermineException();
    }
    this.status = ProductionPlanDetailStatusKind.DETERMINED;
    this.determinedDate = OffsetDateTime.now();
    return new ProductionPlanDetailMessages.Determine.Response(
      Arrays.asList(new ProductionPlanDetailEvents.DeterminedEvent(this.id))
    );
  }

  public ProductionPlanDetailMessages.Progress.Response apply(
    ProductionPlanDetailMessages.Progress.Request request) {
    if (!isProgressable()) {
      throw new ProductionPlanDetailExceptions.CannotProgressException();
    }
    this.progressedQuantity = this.progressedQuantity.add(request.getProgressedQuantity());
    this.status = ProductionPlanDetailStatusKind.IN_PROGRESS;
    return new ProductionPlanDetailMessages.Progress.Response(
      Arrays.asList(new ProductionPlanDetailEvents.ProgressedEvent(this.id))
    );
  }

  public ProductionPlanDetailMessages.AddDependency.Response apply(
    ProductionPlanDetailMessages.AddDependency.Request request) {
    if (!isUpdatable()) {
      throw new ProductionPlanDetailExceptions.CannotUpdateException();
    }

    val dependency = request.getDependency();

    if (isDeepDependOn(dependency) || this.dependencies.contains(dependency)) {
      throw new ProductionPlanDetailExceptions.CannotUpdateException();
    }
    this.dependencies.add(dependency);
    this.order = this.dependencies.stream()
      .map(ProductionPlanDetail::getOrder)
      .max(Comparator.comparing(i -> i))
      .orElse(0) + 1;
    return new ProductionPlanDetailMessages.AddDependency.Response(
      Arrays.asList(new ProductionPlanDetailEvents.UpdatedEvent(this.id))
    );
  }

  public ProductionPlanDetailMessages.Reschedule.Response apply(
    ProductionPlanDetailMessages.Reschedule.Request request) {
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
    return new ProductionPlanDetailMessages.Reschedule.Response(
      Arrays.asList(
        new ProductionPlanDetailEvents.RescheduledEvent(
          beforeStartDate,
          beforeEndDate,
          this.id
        )
      )
    );
  }

  public ProductionPlanDetailMessages.RescheduleByDependency.Response apply(
    ProductionPlanDetailMessages.RescheduleByDependency.Request request) {
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
    return new ProductionPlanDetailMessages.RescheduleByDependency.Response(events);
  }

  public ProductionPlanDetailMessages.Cancel.Response apply(
    ProductionPlanDetailMessages.Cancel.Request request) {
    if (!isCancelable()) {
      throw new ProductionPlanDetailExceptions.CannotCancelException();
    }
    this.status = ProductionPlanDetailStatusKind.CANCELED;
    this.canceledDate = OffsetDateTime.now();
    return new ProductionPlanDetailMessages.Cancel.Response(
      Arrays.asList(new ProductionPlanDetailEvents.CanceledEvent(this.id))
    );
  }

  public ProductionPlanDetailMessages.Complete.Response apply(
    ProductionPlanDetailMessages.Complete.Request request) {
    if (!isCompletable()) {
      throw new ProductionPlanDetailExceptions.CannotCompleteException();
    }
    this.status = ProductionPlanDetailStatusKind.COMPLETED;
    this.completedDate = OffsetDateTime.now();
    return new ProductionPlanDetailMessages.Complete.Response(
      Arrays.asList(new ProductionPlanDetailEvents.CompletedEvent(this.id))
    );
  }

  public ProductionPlanDetailMessages.Delete.Response apply(
    ProductionPlanDetailMessages.Delete.Request request) {
    if (!isUpdatable()) {
      throw new ProductionPlanDetailExceptions.CannotDeleteException();
    }
    return new ProductionPlanDetailMessages.Delete.Response(
      Arrays.asList(new ProductionPlanDetailEvents.DeletedEvent(this.id))
    );
  }

  public ProductionPlanDetailMessages.RemoveDependency.Response apply(
    ProductionPlanDetailMessages.RemoveDependency.Request request) {
    if (!isUpdatable()) {
      throw new ProductionPlanDetailExceptions.CannotUpdateException();
    }
    if (!this.dependencies.contains(request.getDependency())) {
      throw new ProductionPlanDetailExceptions.CannotUpdateException();
    }
    this.dependencies.remove(request.getDependency());
    return new ProductionPlanDetailMessages.RemoveDependency.Response(
      Arrays.asList(new ProductionPlanDetailEvents.UpdatedEvent(this.id))
    );
  }

  public String getName() {
    if (processPreparation != null) {
      return "사전공정 " + processPreparation.getName();
    } else if (process != null) {
      return "공정 " + process.getName() + " : " + item.getName();
    } else {
      return item.getName();
    }
  }

  public BigDecimal getPlannedQuantity() {
    return quantity.add(spareQuantity);
  }

  protected OffsetDateTime getStartableDate() {
    val endDates = new HashMap<ProductionPlanDetailGroupId, OffsetDateTime>();
    for (ProductionPlanDetail dependency : dependencies) {
      val groupId = dependency.getGroupId();
      val endDate = dependency.getEndDate();
      if (!endDates.containsKey(groupId)) {
        endDates.put(groupId, OffsetDateTime.MAX);
      }
      val mappedDate = endDates.get(groupId);
      if (endDate.isBefore(mappedDate)) {
        endDates.put(groupId, endDate);
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
    return status.isDeterminable() && this.progressCompany != null && this.progressType != null;
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
