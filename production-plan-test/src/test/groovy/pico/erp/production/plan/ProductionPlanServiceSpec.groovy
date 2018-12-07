package pico.erp.production.plan

import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.context.annotation.ComponentScan
import org.springframework.context.annotation.Configuration
import org.springframework.test.annotation.Rollback
import org.springframework.test.context.ActiveProfiles
import org.springframework.transaction.annotation.Transactional
import pico.erp.company.CompanyId
import pico.erp.item.ItemId
import pico.erp.project.ProjectId
import pico.erp.shared.IntegrationConfiguration
import spock.lang.Specification

import java.time.OffsetDateTime

@SpringBootTest(classes = [IntegrationConfiguration])
@Transactional
@Rollback
@ActiveProfiles("test")
@Configuration
@ComponentScan("pico.erp.config")
class ProductionPlanServiceSpec extends Specification {

  @Autowired
  ProductionPlanService planService

  def planId = ProductionPlanId.from("plan-1")

  def unknownPlanId = ProductionPlanId.from("unknown")

  def itemId = ItemId.from("item-1")

  def projectId = ProjectId.from("sample-project1")

  def progressCompanyId = CompanyId.from("CUST1")

  def planDueDate = OffsetDateTime.now().plusDays(2)


  def setup() {
    planService.create(
      new ProductionPlanRequests.CreateRequest(
        id: planId,
        itemId: itemId,
        quantity: 100,
        spareQuantity: 10,
        projectId: projectId,
        dueDate: planDueDate
      )
    )
  }

  def cancelPlan() {
    planService.cancel(
      new ProductionPlanRequests.CancelRequest(
        id: planId
      )
    )
  }

  def updatePlan() {
    planService.update(
      new ProductionPlanRequests.UpdateRequest(
        id: planId,
        quantity: 100,
        spareQuantity: 10,
        dueDate: planDueDate
      )
    )
  }

  def updatePlanNotEnough() {
    planService.update(
      new ProductionPlanRequests.UpdateRequest(
        id: planId,
        quantity: 100,
        spareQuantity: 10,
        dueDate: planDueDate,
        progressCompanyId: progressCompanyId,
        progressType: ProductionPlanProgressTypeKind.PRODUCE
      )
    )
  }

  def "존재 - 아이디로 존재 확인"() {
    when:
    def exists = planService.exists(planId)

    then:
    exists == true
  }

  def "존재 - 존재하지 않는 아이디로 확인"() {
    when:
    def exists = planService.exists(unknownPlanId)

    then:
    exists == false
  }

  def "조회 - 아이디로 조회"() {
    when:
    def ProductionPlanItem = planService.get(planId)

    println ProductionPlanItem
    then:
    ProductionPlanItem.plannedQuantity == 110
    ProductionPlanItem.itemId == itemId
  }

  def "조회 - 존재하지 않는 아이디로 조회"() {
    when:
    planService.get(unknownPlanId)

    then:
    thrown(ProductionPlanExceptions.NotFoundException)
  }


  def "수정 - 취소 후 수정할 수 없다"() {
    when:
    cancelPlan()
    updatePlan()
    then:
    thrown(ProductionPlanExceptions.CannotUpdateException)
  }


  def "수정 - 수정한다"() {
    when:
    updatePlan()
    def plan = planService.get(planId)

    then:
    plan.plannedQuantity == 110
    plan.itemId == itemId
  }

  def "취소 - 취소 후에는 취소 할 수 없다"() {
    when:
    cancelPlan()
    cancelPlan()
    then:
    thrown(ProductionPlanExceptions.CannotCancelException)
  }

}
