package pico.erp.production.plan

import kkojaeh.spring.boot.component.SpringBootTestComponent
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.context.MessageSource
import org.springframework.context.i18n.LocaleContextHolder
import org.springframework.test.annotation.Rollback
import org.springframework.test.context.ActiveProfiles
import org.springframework.transaction.annotation.Transactional
import pico.erp.production.plan.detail.ProductionPlanDetailProgressTypeKind
import pico.erp.production.plan.detail.ProductionPlanDetailStatusKind
import pico.erp.shared.ComponentDefinitionServiceLoaderTestComponentSiblingsSupplier
import pico.erp.shared.TestParentApplication
import spock.lang.Specification

import java.util.stream.Collectors
import java.util.stream.Stream

@SpringBootTest(classes = [ProductionPlanApplication, TestConfig])
@SpringBootTestComponent(parent = TestParentApplication, siblingsSupplier = ComponentDefinitionServiceLoaderTestComponentSiblingsSupplier.class)
@Transactional
@Rollback
@ActiveProfiles("test")
class MessageSourceSpec extends Specification {

  @Autowired
  MessageSource messageSource

  def locale = LocaleContextHolder.locale

  def "생산 계획 상태"() {
    when:
    def messages = Stream.of(ProductionPlanStatusKind.values())
      .map({
      kind -> messageSource.getMessage(kind.nameCode, null, locale)
    }).collect(Collectors.toList())

    println messages

    then:
    messages.size() == 6
  }

  def "생산 상세 계획 상태"() {
    when:
    def messages = Stream.of(ProductionPlanDetailStatusKind.values())
      .map({
      kind -> messageSource.getMessage(kind.nameCode, null, locale)
    }).collect(Collectors.toList())

    println messages

    then:
    messages.size() == 5
  }

  def "생산 계획 진행 유형"() {
    when:
    def messages = Stream.of(ProductionPlanDetailProgressTypeKind.values())
      .map({
      kind -> messageSource.getMessage(kind.nameCode, null, locale)
    }).collect(Collectors.toList())

    println messages

    then:
    messages.size() == 4
  }

}
