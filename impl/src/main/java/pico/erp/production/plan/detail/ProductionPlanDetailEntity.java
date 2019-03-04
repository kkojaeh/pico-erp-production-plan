package pico.erp.production.plan.detail;


import java.io.Serializable;
import java.math.BigDecimal;
import java.time.OffsetDateTime;
import java.util.Set;
import javax.persistence.AttributeOverride;
import javax.persistence.AttributeOverrides;
import javax.persistence.CollectionTable;
import javax.persistence.Column;
import javax.persistence.ElementCollection;
import javax.persistence.Embedded;
import javax.persistence.EmbeddedId;
import javax.persistence.Entity;
import javax.persistence.EntityListeners;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.FetchType;
import javax.persistence.Index;
import javax.persistence.JoinColumn;
import javax.persistence.Table;
import javax.persistence.UniqueConstraint;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.hibernate.annotations.CacheConcurrencyStrategy;
import org.springframework.data.annotation.CreatedBy;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedBy;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;
import pico.erp.company.CompanyId;
import pico.erp.item.ItemId;
import pico.erp.item.spec.ItemSpecCode;
import pico.erp.item.spec.ItemSpecId;
import pico.erp.process.ProcessId;
import pico.erp.process.preparation.ProcessPreparationId;
import pico.erp.production.plan.ProductionPlanId;
import pico.erp.shared.TypeDefinitions;
import pico.erp.shared.data.Auditor;

@Entity(name = "ProductionPlanDetail")
@Table(name = "PRP_PRODUCTION_PLAN_DETAIL", indexes = @Index(columnList = "PLAN_ID"))
@Data
@EqualsAndHashCode(of = "id")
@FieldDefaults(level = AccessLevel.PRIVATE)
@EntityListeners(AuditingEntityListener.class)
@org.hibernate.annotations.Cache(usage = CacheConcurrencyStrategy.READ_WRITE)
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class ProductionPlanDetailEntity implements Serializable {

  private static final long serialVersionUID = 1L;

  @EmbeddedId
  @AttributeOverrides({
    @AttributeOverride(name = "value", column = @Column(name = "ID", length = TypeDefinitions.UUID_BINARY_LENGTH))
  })
  ProductionPlanDetailId id;

  @AttributeOverrides({
    @AttributeOverride(name = "value", column = @Column(name = "PLAN_ID", length = TypeDefinitions.UUID_BINARY_LENGTH))
  })
  ProductionPlanId planId;

  @AttributeOverrides({
    @AttributeOverride(name = "value", column = @Column(name = "ITEM_ID", length = TypeDefinitions.UUID_BINARY_LENGTH))
  })
  ItemId itemId;

  @AttributeOverrides({
    @AttributeOverride(name = "value", column = @Column(name = "ITEM_SPEC_ID", length = TypeDefinitions.UUID_BINARY_LENGTH))
  })
  ItemSpecId itemSpecId;

  @AttributeOverrides({
    @AttributeOverride(name = "value", column = @Column(name = "ITEM_SPEC_CODE", length = TypeDefinitions.CODE_LENGTH))
  })
  ItemSpecCode itemSpecCode;

  @Column(precision = 19, scale = 2)
  BigDecimal quantity;

  @Column(precision = 19, scale = 2)
  BigDecimal spareQuantity;

  @Column(precision = 19, scale = 2)
  BigDecimal progressedQuantity;

  OffsetDateTime startDate;

  OffsetDateTime endDate;

  @AttributeOverrides({
    @AttributeOverride(name = "value", column = @Column(name = "ACTOR_ID", length = TypeDefinitions.ID_LENGTH))
  })
  CompanyId actorId;

  @AttributeOverrides({
    @AttributeOverride(name = "value", column = @Column(name = "RECEIVER_ID", length = TypeDefinitions.ID_LENGTH))
  })
  CompanyId receiverId;

  OffsetDateTime completedDate;

  OffsetDateTime canceledDate;

  OffsetDateTime determinedDate;

  @Column(length = TypeDefinitions.ENUM_LENGTH)
  @Enumerated(EnumType.STRING)
  ProductionPlanDetailProgressTypeKind progressType;

  @Column(length = TypeDefinitions.ENUM_LENGTH)
  @Enumerated(EnumType.STRING)
  ProductionPlanDetailStatusKind status;

  @Embedded
  @AttributeOverrides({
    @AttributeOverride(name = "id", column = @Column(name = "CREATED_BY_ID", updatable = false, length = TypeDefinitions.ID_LENGTH)),
    @AttributeOverride(name = "name", column = @Column(name = "CREATED_BY_NAME", updatable = false, length = TypeDefinitions.NAME_LENGTH))
  })
  @CreatedBy
  Auditor createdBy;

  @CreatedDate
  @Column(updatable = false)
  OffsetDateTime createdDate;

  @Embedded
  @AttributeOverrides({
    @AttributeOverride(name = "id", column = @Column(name = "LAST_MODIFIED_BY_ID", length = TypeDefinitions.ID_LENGTH)),
    @AttributeOverride(name = "name", column = @Column(name = "LAST_MODIFIED_BY_NAME", length = TypeDefinitions.NAME_LENGTH))
  })
  @LastModifiedBy
  Auditor lastModifiedBy;

  @LastModifiedDate
  OffsetDateTime lastModifiedDate;

  @ElementCollection(fetch = FetchType.LAZY)
  @org.hibernate.annotations.Cache(usage = CacheConcurrencyStrategy.NONSTRICT_READ_WRITE)
  @AttributeOverrides({
    @AttributeOverride(name = "value", column = @Column(name = "PLAN_DETAIL_DEPENDENCY_ID", length = TypeDefinitions.ID_LENGTH, nullable = false))
  })
  @CollectionTable(name = "PRP_PRODUCTION_PLAN_DETAIL_DEPENDENCY", joinColumns = @JoinColumn(name = "PLAN_DETAIL_ID"), uniqueConstraints = {
    @UniqueConstraint(columnNames = {"PLAN_DETAIL_ID", "PLAN_DETAIL_DEPENDENCY_ID"})
  })
  Set<ProductionPlanDetailId> dependencies;

  @AttributeOverrides({
    @AttributeOverride(name = "value", column = @Column(name = "PROCESS_ID", length = TypeDefinitions.UUID_BINARY_LENGTH))
  })
  ProcessId processId;

  @AttributeOverrides({
    @AttributeOverride(name = "value", column = @Column(name = "PROCESS_PREPARATION_ID", length = TypeDefinitions.UUID_BINARY_LENGTH))
  })
  ProcessPreparationId processPreparationId;

  @AttributeOverrides({
    @AttributeOverride(name = "value", column = @Column(name = "GROUP_ID", length = TypeDefinitions.UUID_BINARY_LENGTH))
  })
  ProductionPlanDetailGroupId groupId;

  @Column(name = "DEPENDENCY_ORDER")
  int order;

  boolean split;

  /*
  @OneToMany(cascade = CascadeType.ALL, orphanRemoval = true)
  @JoinColumn(name = "PROJECT_ID")
  @OrderBy("createdDate DESC")
  List<ProjectChargeEntity> charges;

  @OneToMany(cascade = CascadeType.ALL, orphanRemoval = true)
  @JoinColumn(name = "PROJECT_ID")
  @OrderBy("createdDate DESC")
  List<ProjectSaleItemEntity> saleItems;
  */

}
