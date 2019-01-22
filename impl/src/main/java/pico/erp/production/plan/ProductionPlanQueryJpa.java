package pico.erp.production.plan;

import static org.springframework.util.StringUtils.isEmpty;

import com.querydsl.core.BooleanBuilder;
import com.querydsl.core.types.Projections;
import com.querydsl.jpa.impl.JPAQuery;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import lombok.val;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.validation.annotation.Validated;
import pico.erp.production.plan.ProductionPlanView.Filter;
import pico.erp.shared.Public;
import pico.erp.shared.jpa.QueryDslJpaSupport;

@Service
@Public
@Transactional(readOnly = true)
@Validated
public class ProductionPlanQueryJpa implements ProductionPlanQuery {

  private final QProductionPlanEntity productionPlan = QProductionPlanEntity.productionPlanEntity;

  @PersistenceContext
  private EntityManager entityManager;

  @Autowired
  private QueryDslJpaSupport queryDslJpaSupport;

  @Override
  public Page<ProductionPlanView> retrieve(Filter filter, Pageable pageable) {
    val query = new JPAQuery<ProductionPlanView>(entityManager);
    val select = Projections.bean(ProductionPlanView.class,
      productionPlan.id,
      productionPlan.code,
      productionPlan.itemId,
      productionPlan.quantity,
      productionPlan.spareQuantity,
      productionPlan.progressedQuantity,
      productionPlan.projectId,
      productionPlan.dueDate,
      productionPlan.completedDate,
      productionPlan.determinedDate,
      productionPlan.canceledDate,
      productionPlan.status,
      productionPlan.createdBy,
      productionPlan.createdDate
    );
    query.select(select);
    query.from(productionPlan);

    val builder = new BooleanBuilder();

    if (!isEmpty(filter.getCode())) {
      builder.and(productionPlan.code.value
        .likeIgnoreCase(queryDslJpaSupport.toLikeKeyword("%", filter.getCode(), "%")));
    }

    if (filter.getProjectId() != null) {
      builder.and(productionPlan.projectId.eq(filter.getProjectId()));
    }

    if (filter.getStartDueDate() != null) {
      builder.and(productionPlan.dueDate.goe(filter.getStartDueDate()));
    }
    if (filter.getEndDueDate() != null) {
      builder.and(productionPlan.dueDate.loe(filter.getEndDueDate()));
    }

    if (filter.getStatuses() != null && !filter.getStatuses().isEmpty()) {
      builder.and(productionPlan.status.in(filter.getStatuses()));
    }

    if (filter.getItemId() != null) {
      builder.and(productionPlan.itemId.eq(filter.getItemId()));
    }

    query.where(builder);
    return queryDslJpaSupport.paging(query, pageable, select);
  }
}
