package pico.erp.production.plan.detail;

import pico.erp.shared.data.LocalizedNameable;

public enum ProductionPlanDetailProgressTypeKind implements LocalizedNameable {

  /**
   * 입고
   */
  WAREHOUSING,

  /**
   * 외주
   */
  OUTSOURCING,

  /**
   * 구매
   */
  PURCHASE,

  /**
   * 생산
   */
  PRODUCE

}
