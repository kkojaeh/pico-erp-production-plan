package pico.erp.production.plan.detail;

import java.util.Optional;
import java.util.stream.Collectors;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;
import org.mapstruct.Mappings;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Lazy;
import org.springframework.data.domain.AuditorAware;
import pico.erp.company.CompanyData;
import pico.erp.company.CompanyId;
import pico.erp.company.CompanyService;
import pico.erp.item.ItemData;
import pico.erp.item.ItemId;
import pico.erp.item.ItemService;
import pico.erp.item.spec.ItemSpecData;
import pico.erp.item.spec.ItemSpecId;
import pico.erp.item.spec.ItemSpecService;
import pico.erp.process.ProcessData;
import pico.erp.process.ProcessId;
import pico.erp.process.ProcessService;
import pico.erp.process.preparation.ProcessPreparationData;
import pico.erp.process.preparation.ProcessPreparationId;
import pico.erp.process.preparation.ProcessPreparationService;
import pico.erp.production.plan.ProductionPlan;
import pico.erp.production.plan.ProductionPlanExceptions;
import pico.erp.production.plan.ProductionPlanId;
import pico.erp.production.plan.ProductionPlanMapper;
import pico.erp.production.plan.detail.ProductionPlanDetailRequests.RescheduleByDependencyRequest;
import pico.erp.project.ProjectData;
import pico.erp.project.ProjectId;
import pico.erp.project.ProjectService;
import pico.erp.shared.data.Auditor;
import pico.erp.user.UserData;
import pico.erp.user.UserId;
import pico.erp.user.UserService;

@Mapper
public abstract class ProductionPlanDetailMapper {

  @Autowired
  protected AuditorAware<Auditor> auditorAware;

  @Lazy
  @Autowired
  protected ItemService itemService;

  @Lazy
  @Autowired
  protected ItemSpecService itemSpecService;

  @Lazy
  @Autowired
  private CompanyService companyService;

  @Lazy
  @Autowired
  private UserService userService;

  @Lazy
  @Autowired
  private ProductionPlanDetailRepository productionPlanDetailRepository;

  @Lazy
  @Autowired
  private ProjectService projectService;

  @Lazy
  @Autowired
  private ProcessService processService;

  @Lazy
  @Autowired
  private ProcessPreparationService processPreparationService;

  @Autowired
  private ProductionPlanMapper planMapper;

  protected ProductionPlanDetailId id(ProductionPlanDetail productionPlanDetail) {
    return productionPlanDetail != null ? productionPlanDetail.getId() : null;
  }

  @Mappings({
    @Mapping(target = "planId", source = "plan.id"),
    @Mapping(target = "createdBy", ignore = true),
    @Mapping(target = "createdDate", ignore = true),
    @Mapping(target = "lastModifiedBy", ignore = true),
    @Mapping(target = "lastModifiedDate", ignore = true)
  })
  public abstract ProductionPlanDetailEntity jpa(ProductionPlanDetail data);

  public ProductionPlanDetail jpa(ProductionPlanDetailEntity entity) {
    return ProductionPlanDetail.builder()
      .id(entity.getId())
      .plan(map(entity.getPlanId()))
      .itemId(entity.getItemId())
      .processId(entity.getProcessId())
      .processPreparationId(entity.getProcessPreparationId())
      .itemSpecId(entity.getItemSpecId())
      .quantity(entity.getQuantity())
      .spareQuantity(entity.getSpareQuantity())
      .progressedQuantity(entity.getProgressedQuantity())
      .startDate(entity.getStartDate())
      .endDate(entity.getEndDate())
      .actorId(entity.getActorId())
      .receiverId(entity.getReceiverId())
      .status(entity.getStatus())
      .completedDate(entity.getCompletedDate())
      .canceledDate(entity.getCanceledDate())
      .determinedDate(entity.getDeterminedDate())
      .progressType(entity.getProgressType())
      .order(entity.getOrder())
      .dependencies(entity.getDependencies().stream().map(this::map).collect(Collectors.toSet()))
      .groupId(entity.getGroupId())
      .split(entity.isSplit())
      .build();
  }

  protected UserData map(UserId userId) {
    return Optional.ofNullable(userId)
      .map(userService::get)
      .orElse(null);
  }

  protected CompanyData map(CompanyId companyId) {
    return Optional.ofNullable(companyId)
      .map(companyService::get)
      .orElse(null);
  }

  protected ProjectData map(ProjectId projectId) {
    return Optional.ofNullable(projectId)
      .map(projectService::get)
      .orElse(null);
  }

  public ProductionPlanDetail map(ProductionPlanDetailId productionPlanDetailId) {
    return Optional.ofNullable(productionPlanDetailId)
      .map(id -> productionPlanDetailRepository.findBy(id)
        .orElseThrow(ProductionPlanExceptions.NotFoundException::new)
      )
      .orElse(null);
  }

  protected ItemData map(ItemId itemId) {
    return Optional.ofNullable(itemId)
      .map(itemService::get)
      .orElse(null);
  }

  protected ItemSpecData map(ItemSpecId itemSpecId) {
    return Optional.ofNullable(itemSpecId)
      .map(itemSpecService::get)
      .orElse(null);
  }

  protected ProcessData map(ProcessId processId) {
    return Optional.ofNullable(processId)
      .map(processService::get)
      .orElse(null);
  }

  protected ProcessPreparationData map(ProcessPreparationId processPreparationId) {
    return Optional.ofNullable(processPreparationId)
      .map(processPreparationService::get)
      .orElse(null);
  }

  protected ProductionPlan map(ProductionPlanId productionPlanId) {
    return planMapper.map(productionPlanId);
  }

  @Mappings({
    @Mapping(target = "planId", source = "plan.id")
  })
  public abstract ProductionPlanDetailData map(ProductionPlanDetail item);

  @Mappings({
    @Mapping(target = "plan", source = "planId")
  })
  public abstract ProductionPlanDetailMessages.Create.Request map(
    ProductionPlanDetailRequests.CreateRequest request);

  public abstract ProductionPlanDetailMessages.Update.Request map(
    ProductionPlanDetailRequests.UpdateRequest request);

  @Mappings({
  })
  public abstract ProductionPlanDetailMessages.Progress.Request map(
    ProductionPlanDetailRequests.ProgressRequest request);

  @Mappings({
  })
  public abstract ProductionPlanDetailMessages.Reschedule.Request map(
    ProductionPlanDetailRequests.RescheduleRequest request);

  @Mappings({
  })
  public abstract ProductionPlanDetailMessages.Cancel.Request map(
    ProductionPlanDetailRequests.CancelRequest request);

  @Mappings({
  })
  public abstract ProductionPlanDetailMessages.Complete.Request map(
    ProductionPlanDetailRequests.CompleteRequest request);

  @Mappings({
  })
  public abstract ProductionPlanDetailMessages.Determine.Request map(
    ProductionPlanDetailRequests.DetermineRequest request);

  @Mappings({
  })
  public abstract ProductionPlanDetailMessages.Delete.Request map(
    ProductionPlanDetailRequests.DeleteRequest request);

  @Mappings({
  })
  public abstract ProductionPlanDetailMessages.Split.Request map(
    ProductionPlanDetailRequests.SplitRequest request);

  @Mappings({
    @Mapping(target = "dependency", source = "dependencyId")
  })
  public abstract ProductionPlanDetailMessages.AddDependency.Request map(
    ProductionPlanDetailRequests.AddDependencyRequest request);

  @Mappings({
    @Mapping(target = "dependency", source = "dependencyId")
  })
  public abstract ProductionPlanDetailMessages.RemoveDependency.Request map(
    ProductionPlanDetailRequests.RemoveDependencyRequest request);

  @Mappings({
    @Mapping(target = "dependency", source = "dependencyId")
  })
  public abstract ProductionPlanDetailMessages.RescheduleByDependency.Request map(
    RescheduleByDependencyRequest request);

  public abstract void pass(
    ProductionPlanDetailEntity from, @MappingTarget ProductionPlanDetailEntity to);


}



