package pico.erp.production.plan.detail;

import java.util.Optional;
import java.util.stream.Stream;
import lombok.val;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;
import pico.erp.production.plan.ProductionPlanId;

@Repository
interface ProductionPlanDetailEntityRepository extends
  CrudRepository<ProductionPlanDetailEntity, ProductionPlanDetailId> {

  @Query("SELECT d FROM ProductionPlanDetail d WHERE d.planId = :planId ORDER BY order")
  Stream<ProductionPlanDetailEntity> findAllBy(@Param("planId") ProductionPlanId planId);

  @Query("SELECT d FROM ProductionPlanDetail d WHERE :planDetailId MEMBER OF d.dependencies ORDER BY order")
  Stream<ProductionPlanDetailEntity> findAllDependedOn(
    @Param("planDetailId") ProductionPlanDetailId planDetailId);


}

@Repository
@Transactional
public class ProductionPlanDetailRepositoryJpa implements ProductionPlanDetailRepository {

  @Autowired
  private ProductionPlanDetailEntityRepository repository;

  @Autowired
  private ProductionPlanDetailMapper mapper;

  @Override
  public ProductionPlanDetail create(ProductionPlanDetail planItem) {
    val entity = mapper.jpa(planItem);
    val created = repository.save(entity);
    return mapper.jpa(created);
  }

  @Override
  public void deleteBy(ProductionPlanDetailId id) {
    repository.deleteById(id);
  }

  @Override
  public boolean exists(ProductionPlanDetailId id) {
    return repository.existsById(id);
  }

  @Override
  public Stream<ProductionPlanDetail> findAllBy(ProductionPlanId planId) {
    return repository.findAllBy(planId)
      .map(mapper::jpa);
  }

  @Override
  public Stream<ProductionPlanDetail> findAllDependedOn(ProductionPlanDetailId planDetailId) {
    return repository.findAllDependedOn(planDetailId)
      .map(mapper::jpa);
  }

  @Override
  public Optional<ProductionPlanDetail> findBy(ProductionPlanDetailId id) {
    return repository.findById(id)
      .map(mapper::jpa);
  }

  @Override
  public void update(ProductionPlanDetail planItem) {
    val entity = repository.findById(planItem.getId()).get();
    mapper.pass(mapper.jpa(planItem), entity);
    repository.save(entity);
  }
}
