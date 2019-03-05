package pico.erp.production.plan;

import java.util.Optional;
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
import pico.erp.project.ProjectData;
import pico.erp.project.ProjectId;
import pico.erp.project.ProjectService;
import pico.erp.shared.data.Auditor;
import pico.erp.user.UserData;
import pico.erp.user.UserId;
import pico.erp.user.UserService;

@Mapper
public abstract class ProductionPlanMapper {

  @Autowired
  protected AuditorAware<Auditor> auditorAware;

  @Lazy
  @Autowired
  protected ItemService itemService;

  @Lazy
  @Autowired
  protected ItemSpecService itemSpecService;

  @Autowired
  protected ProductionPlanCodeGenerator productionPlanCodeGenerator;

  @Lazy
  @Autowired
  private CompanyService companyService;

  @Lazy
  @Autowired
  private UserService userService;

  @Lazy
  @Autowired
  private ProductionPlanRepository productionPlanRepository;

  @Lazy
  @Autowired
  private ProjectService projectService;

  @Mappings({
    @Mapping(target = "createdBy", ignore = true),
    @Mapping(target = "createdDate", ignore = true),
    @Mapping(target = "lastModifiedBy", ignore = true),
    @Mapping(target = "lastModifiedDate", ignore = true)
  })
  public abstract ProductionPlanEntity jpa(ProductionPlan data);

  public ProductionPlan jpa(ProductionPlanEntity entity) {
    return ProductionPlan.builder()
      .id(entity.getId())
      .code(entity.getCode())
      .itemId(entity.getItemId())
      .quantity(entity.getQuantity())
      .spareQuantity(entity.getSpareQuantity())
      .completedQuantity(entity.getCompletedQuantity())
      .dueDate(entity.getDueDate())
      .determinedDate(entity.getDeterminedDate())
      .completedDate(entity.getCompletedDate())
      .canceledDate(entity.getCanceledDate())
      .status(entity.getStatus())
      .progressRate(entity.getProgressRate())
      .projectId(entity.getProjectId())
      .unit(entity.getUnit())
      .plannerId(entity.getPlannerId())
      .receiverId(entity.getReceiverId())
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

  public ProductionPlan map(ProductionPlanId productionPlanId) {
    return Optional.ofNullable(productionPlanId)
      .map(id -> productionPlanRepository.findBy(id)
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

  @Mappings({
  })
  public abstract ProductionPlanData map(ProductionPlan productionPlan);

  @Mappings({
    @Mapping(target = "codeGenerator", expression = "java(productionPlanCodeGenerator)")
  })
  public abstract ProductionPlanMessages.Create.Request map(
    ProductionPlanRequests.CreateRequest request);

  @Mappings({
  })
  public abstract ProductionPlanMessages.Update.Request map(
    ProductionPlanRequests.UpdateRequest request);


  @Mappings({
  })
  public abstract ProductionPlanMessages.Cancel.Request map(
    ProductionPlanRequests.CancelRequest request);

  @Mappings({
  })
  public abstract ProductionPlanMessages.Determine.Request map(
    ProductionPlanRequests.DetermineRequest request);


  @Mappings({
  })
  public abstract ProductionPlanMessages.Prepare.Request map(
    ProductionPlanRequests.PrepareRequest request);

  @Mappings({
  })
  public abstract ProductionPlanMessages.Complete.Request map(
    ProductionPlanRequests.CompleteRequest request);

  @Mappings({
  })
  public abstract ProductionPlanMessages.Progress.Request map(
    ProductionPlanRequests.ProgressRequest request);

  public abstract void pass(ProductionPlanEntity from, @MappingTarget ProductionPlanEntity to);


}


