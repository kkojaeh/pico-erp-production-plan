package pico.erp.production.plan;

import pico.erp.shared.data.LocalizedNameable;

public enum ProductionPlanProgressTypeKind implements LocalizedNameable {

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
