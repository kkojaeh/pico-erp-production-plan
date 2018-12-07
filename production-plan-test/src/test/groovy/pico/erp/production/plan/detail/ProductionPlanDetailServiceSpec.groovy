package pico.erp.production.plan.detail


import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.context.annotation.ComponentScan
import org.springframework.context.annotation.Configuration
import org.springframework.test.annotation.Rollback
import org.springframework.test.context.ActiveProfiles
import org.springframework.transaction.annotation.Transactional
import pico.erp.company.CompanyId
import pico.erp.item.ItemId
import pico.erp.production.plan.ProductionPlanId
import pico.erp.production.plan.ProductionPlanRequests
import pico.erp.production.plan.ProductionPlanService
import pico.erp.project.ProjectId
import pico.erp.shared.IntegrationConfiguration
import pico.erp.user.UserId
import spock.lang.Specification

import java.time.OffsetDateTime

@SpringBootTest(classes = [IntegrationConfiguration])
@Transactional
@Rollback
@ActiveProfiles("test")
@Configuration
@ComponentScan("pico.erp.config")
class ProductionPlanDetailServiceSpec extends Specification {

  @Autowired
  ProductionPlanService planService

  @Autowired
  ProductionPlanDetailService planDetailService

  def planId = ProductionPlanId.from("plan-1")

  def planDetailId = ProductionPlanDetailId.from("plan-detail-1")

  def planDetailId2 = ProductionPlanDetailId.from("plan-detail-2")

  def unknownPlanId = ProductionPlanDetailId.from("unknown")

  def itemId = ItemId.from("item-1")

  def chargerId = UserId.from("kjh")

  def progressCompanyId = CompanyId.from("CUST1")

  def projectId = ProjectId.from("sample-project1")

  def planStartDate = OffsetDateTime.now().plusDays(2)
  def planEndDate = planStartDate.plusDays(1)


  def setup() {
    planService.create(
      new ProductionPlanRequests.CreateRequest(
        id: planId,
        itemId: itemId,
        quantity: 100,
        spareQuantity: 10,
        projectId: projectId,
        dueDate: OffsetDateTime.now().plusDays(2)
      )
    )
    planDetailService.create(
      new ProductionPlanDetailRequests.CreateRequest(
        id: planDetailId,
        planId: planId,
        itemId: itemId,
        quantity: 100,
        spareQuantity: 10,
        startDate: planStartDate,
        endDate: planEndDate
      )
    )
  }

  def createPlan2() {
    planDetailService.create(
      new ProductionPlanDetailRequests.CreateRequest(
        id: planDetailId2,
        planId: planId,
        itemId: itemId,
        quantity: 100,
        spareQuantity: 10,
        startDate: planEndDate.plusDays(1),
        endDate: planEndDate.plusDays(2)
      )
    )
  }

  def addDependency() {
    planDetailService.addDependency(
      new ProductionPlanDetailRequests.AddDependencyRequest(
        id: planDetailId2,
        dependencyId: planDetailId
      )
    )
  }

  def addDependency2() {
    planDetailService.addDependency(
      new ProductionPlanDetailRequests.AddDependencyRequest(
        id: planDetailId,
        dependencyId: planDetailId2
      )
    )
  }

  def cancelPlan() {
    planDetailService.cancel(
      new ProductionPlanDetailRequests.CancelRequest(
        id: planDetailId
      )
    )
  }

  def updatePlan() {
    planDetailService.update(
      new ProductionPlanDetailRequests.UpdateRequest(
        id: planDetailId,
        quantity: 100,
        spareQuantity: 10,
        startDate: planStartDate,
        endDate: planEndDate,
        chargerId: chargerId,
        progressCompanyId: progressCompanyId,
        progressType: ProductionPlanDetailProgressTypeKind.PRODUCE
      )
    )
  }

  def updatePlan2() {
    planDetailService.update(
      new ProductionPlanDetailRequests.UpdateRequest(
        id: planDetailId2,
        quantity: 100,
        spareQuantity: 10,
        startDate: planEndDate.plusDays(1),
        endDate: planEndDate.plusDays(2),
        chargerId: chargerId,
        progressCompanyId: progressCompanyId,
        progressType: ProductionPlanDetailProgressTypeKind.PRODUCE
      )
    )
  }

  def updatePlanNotEnough() {
    planDetailService.update(
      new ProductionPlanDetailRequests.UpdateRequest(
        id: planDetailId,
        quantity: 100,
        spareQuantity: 10,
        startDate: planStartDate,
        endDate: planEndDate,
        progressCompanyId: progressCompanyId,
        progressType: ProductionPlanDetailProgressTypeKind.PRODUCE
      )
    )
  }

  def determinePlan() {
    planDetailService.determine(
      new ProductionPlanDetailRequests.DetermineRequest(
        id: planDetailId
      )
    )
  }

  def determinePlan2() {
    planDetailService.determine(
      new ProductionPlanDetailRequests.DetermineRequest(
        id: planDetailId2
      )
    )
  }

  def completePlan() {
    planDetailService.complete(
      new ProductionPlanDetailRequests.CompleteRequest(
        id: planDetailId
      )
    )
  }

  def splitPlan() {
    return planDetailService.split(
      new ProductionPlanDetailRequests.SplitRequest(
        id: planDetailId,
        quantity: 50,
        spareQuantity: 5
      )
    )
  }

  def progressPlan(quantity) {
    planDetailService.progress(
      new ProductionPlanDetailRequests.ProgressRequest(
        id: planDetailId,
        progressedQuantity: quantity
      )
    )
  }

  def reschedulePlan() {
    planDetailService.reschedule(
      new ProductionPlanDetailRequests.RescheduleRequest(
        id: planDetailId,
        startDate: planStartDate,
        endDate: planEndDate
      )
    )
  }

  def "자동생성 - 생산 계획이 생성되면 BOM 에 따라 상세계획이 생성된다"() {
    when:
    def details = planDetailService.getAll(planId)
    then:
    details.size() > 0
  }

  def "존재 - 아이디로 확인"() {
    when:
    def exists = planDetailService.exists(planDetailId)

    then:
    exists == true
  }

  def "존재 - 존재하지 않는 아이디로 확인"() {
    when:
    def exists = planDetailService.exists(unknownPlanId)

    then:
    exists == false
  }

  def "조회 - 아이디로 조회"() {
    when:
    def detail = planDetailService.get(planDetailId)

    println detail
    then:
    detail.plannedQuantity == 110
    detail.itemId == itemId
  }

  def "조회 - 존재하지 않는 아이디로 조회"() {
    when:
    planDetailService.get(unknownPlanId)

    then:
    thrown(ProductionPlanDetailExceptions.NotFoundException)
  }

  def "수정 - 확정 후 수정 불가"() {
    when:
    updatePlan()
    determinePlan()
    updatePlan()

    then:
    thrown(ProductionPlanDetailExceptions.CannotUpdateException)
  }

  def "수정 - 취소 후 수정 불가"() {
    when:
    cancelPlan()
    updatePlan()
    then:
    thrown(ProductionPlanDetailExceptions.CannotUpdateException)
  }

  def "수정 - 진행 중 수정 불가"() {
    when:
    updatePlan()
    determinePlan()
    progressPlan(20)
    updatePlan()
    then:
    thrown(ProductionPlanDetailExceptions.CannotUpdateException)
  }

  def "수정 - 완료 후 수정 불가"() {
    when:
    updatePlan()
    determinePlan()
    progressPlan(20)
    completePlan()
    updatePlan()
    then:
    thrown(ProductionPlanDetailExceptions.CannotUpdateException)

  }

  def "수정 - 생성 후 수정"() {
    when:
    updatePlan()
    def plan = planDetailService.get(planDetailId)

    then:
    plan.plannedQuantity == 110
    plan.chargerId == chargerId
    plan.progressCompanyId == progressCompanyId
    plan.progressType == ProductionPlanDetailProgressTypeKind.PRODUCE
  }

  def "취소 - 취소 후에는 취소 불가"() {
    when:
    cancelPlan()
    cancelPlan()
    then:
    thrown(ProductionPlanDetailExceptions.CannotCancelException)
  }

  def "취소 - 완료 후에는 취소 할 수 없다"() {
    when:
    updatePlan()
    determinePlan()
    progressPlan(20)
    completePlan()
    cancelPlan()
    then:
    thrown(ProductionPlanDetailExceptions.CannotCancelException)
  }

  def "취소 - 진행 중에는 취소 할 수 없다"() {
    when:
    updatePlan()
    determinePlan()
    progressPlan(20)
    cancelPlan()
    then:
    thrown(ProductionPlanDetailExceptions.CannotCancelException)
  }

  def "확정 - 확정 요건을 만족하지 않으면 확정 할 수 없다"() {
    when:
    updatePlanNotEnough()
    determinePlan()
    then:
    thrown(ProductionPlanDetailExceptions.CannotDetermineException)
  }

  def "확정 - 취소 후에는 확정 할 수 없다"() {
    when:
    cancelPlan()
    determinePlan()
    then:
    thrown(ProductionPlanDetailExceptions.CannotDetermineException)
  }

  def "확정 - 완료 후에는 확정 할 수 없다"() {
    when:
    updatePlan()
    determinePlan()
    progressPlan(20)
    completePlan()
    determinePlan()
    then:
    thrown(ProductionPlanDetailExceptions.CannotDetermineException)
  }

  def "확정 - 진행중에는 후에는 확정 할 수 없다"() {
    when:
    updatePlan()
    determinePlan()
    progressPlan(20)
    determinePlan()
    then:
    thrown(ProductionPlanDetailExceptions.CannotDetermineException)
  }

  def "진행 - 확정 전에는 진행 할 수 없다"() {
    when:
    updatePlan()
    progressPlan(20)
    then:
    thrown(ProductionPlanDetailExceptions.CannotProgressException)
  }

  def "진행 - 취소한 후에는 진행 할 수 없다"() {
    when:
    cancelPlan()
    progressPlan(20)
    then:
    thrown(ProductionPlanDetailExceptions.CannotProgressException)
  }

  def "진행 - 진행한다"() {
    when:
    updatePlan()
    determinePlan()
    progressPlan(20)
    progressPlan(30)

    def plan = planDetailService.get(planDetailId)

    then:
    plan.progressedQuantity == 50
    plan.status == ProductionPlanDetailStatusKind.IN_PROGRESS
  }

  def "일정변경 - 확정 전에는 일정을 변경 할 수 없다"() {
    when:
    reschedulePlan()
    then:
    thrown(ProductionPlanDetailExceptions.CannotRescheduleException)
  }

  def "일정변경 - 취소 후에는 일정을 변경 할 수 없다"() {
    when:
    cancelPlan()
    reschedulePlan()
    then:
    thrown(ProductionPlanDetailExceptions.CannotRescheduleException)
  }

  def "일정변경 - 완료 후에는 일정을 변경 할 수 없다"() {
    when:
    updatePlan()
    determinePlan()
    progressPlan(20)
    completePlan()
    reschedulePlan()
    then:
    thrown(ProductionPlanDetailExceptions.CannotRescheduleException)
  }

  def "일정변경 - 일정을 변경되어도 종속성 있는 일정이 허용 범위면 변경되지 않는다"() {
    when:
    createPlan2()
    updatePlan()
    updatePlan2()
    addDependency()
    determinePlan()
    determinePlan2()
    def before = planDetailService.get(planDetailId2)
    planDetailService.reschedule(
      new ProductionPlanDetailRequests.RescheduleRequest(
        id: planDetailId,
        startDate: planStartDate.plusDays(1),
        endDate: planEndDate.plusDays(1)
      )
    )
    def after = planDetailService.get(planDetailId2)
    then:
    before.startDate.isEqual(after.startDate)
    before.endDate.isEqual(after.endDate)
  }

  def "일정변경 - 일정을 변경하면 종속성 있는 일정이 허용 범위가 아니면 같이 변경된다"() {
    when:
    createPlan2()
    updatePlan()
    updatePlan2()
    addDependency()
    determinePlan()
    determinePlan2()
    def before = planDetailService.get(planDetailId2)
    planDetailService.reschedule(
      new ProductionPlanDetailRequests.RescheduleRequest(
        id: planDetailId,
        startDate: planStartDate.plusDays(1),
        endDate: planEndDate.plusDays(2)
      )
    )
    def after = planDetailService.get(planDetailId2)
    println before
    println after
    then:
    before.startDate.isBefore(after.startDate)
    before.endDate.isBefore(after.endDate)
  }

  def "분리 - 취소 후에는 일정을 분리 할 수 없다"() {
    when:
    cancelPlan()
    splitPlan()
    then:
    thrown(ProductionPlanDetailExceptions.CannotSplitException)

  }

  def "분리 - 완료 후에는 일정을 분리 할 수 없다"() {

    when:
    updatePlan()
    determinePlan()
    progressPlan(20)
    completePlan()
    splitPlan()
    then:
    thrown(ProductionPlanDetailExceptions.CannotSplitException)

  }

  def "분리 - 일정을 분리하면 새 일정의 상태는 작성중이며 기존의 수량이 새일정의 수량 만큼 줄어 든다"() {
    when:
    def split = splitPlan()
    def after = planDetailService.get(planDetailId)
    then:
    split.quantity == 50
    split.spareQuantity == 5
    after.quantity == 50
    after.spareQuantity == 5
  }

  def "의존일정 추가 - 재귀적으로 의존일정을 추가할 수 없다"() {
    when:
    createPlan2()
    updatePlan()
    updatePlan2()
    addDependency()
    addDependency2()
    then:
    thrown(ProductionPlanDetailExceptions.CannotUpdateException)
  }
}
