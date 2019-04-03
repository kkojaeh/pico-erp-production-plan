package pico.erp.production.plan;

import java.time.LocalDateTime;
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
  long countCreatedBetween(@Param("begin") LocalDateTime begin, @Param("end") LocalDateTime end);

}

@Repository
@Transactional
public class ProductionPlanRepositoryJpa implements ProductionPlanRepository {

  @Autowired
  private ProductionPlanEntityRepository repository;

  @Autowired
  private ProductionPlanMapper mapper;

  @Override
  public long countCreatedBetween(LocalDateTime begin, LocalDateTime end) {
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
    repository.deleteById(id);
  }

  @Override
  public boolean exists(ProductionPlanId id) {
    return repository.existsById(id);
  }

  @Override
  public Optional<ProductionPlan> findBy(ProductionPlanId id) {
    return repository.findById(id)
      .map(mapper::jpa);
  }

  @Override
  public void update(ProductionPlan plan) {
    val entity = repository.findById(plan.getId()).get();
    mapper.pass(mapper.jpa(plan), entity);
    repository.save(entity);
  }
}
