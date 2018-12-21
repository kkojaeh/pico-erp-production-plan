package pico.erp.production.plan;

import java.time.OffsetDateTime;
import java.util.Optional;
import lombok.val;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

@Repository
interface ProductionPlanEntityRepository extends
  CrudRepository<ProductionPlanEntity, ProductionPlanId> {

  @Query("SELECT COUNT(r) FROM ProductionPlan r WHERE r.createdDate >= :begin AND r.createdDate <= :end")
  long countCreatedBetween(@Param("begin") OffsetDateTime begin, @Param("end") OffsetDateTime end);

}

@Repository
@Transactional
public class ProductionPlanRepositoryJpa implements ProductionPlanRepository {

  @Autowired
  private ProductionPlanEntityRepository repository;

  @Autowired
  private ProductionPlanMapper mapper;

  @Override
  public long countCreatedBetween(OffsetDateTime begin, OffsetDateTime end) {
    return repository.countCreatedBetween(begin, end);
  }

  @Override
  public ProductionPlan create(ProductionPlan plan) {
    val entity = mapper.jpa(plan);
    val created = repository.save(entity);
    return mapper.jpa(created);
  }

  @Override
  public void deleteBy(ProductionPlanId id) {
    repository.delete(id);
  }

  @Override
  public boolean exists(ProductionPlanId id) {
    return repository.exists(id);
  }

  @Override
  public Optional<ProductionPlan> findBy(ProductionPlanId id) {
    return Optional.ofNullable(repository.findOne(id))
      .map(mapper::jpa);
  }

  @Override
  public void update(ProductionPlan plan) {
    val entity = repository.findOne(plan.getId());
    mapper.pass(mapper.jpa(plan), entity);
    repository.save(entity);
  }
}
