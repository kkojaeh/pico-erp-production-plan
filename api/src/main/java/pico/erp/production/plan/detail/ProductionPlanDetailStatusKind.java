package pico.erp.production.plan.detail;

import lombok.AllArgsConstructor;
import lombok.Getter;
import pico.erp.shared.data.LocalizedNameable;

@AllArgsConstructor
public enum ProductionPlanDetailStatusKind implements LocalizedNameable {

  /**
   * 상세 계획 생성
   */
  CREATED(true, true, true, false, false, true, false),

  /**
   * 확정 함
   */
  DETERMINED(false, true, false, true, true, true, false),

  /**
   * 취소 됨
   */
  CANCELED(false, false, false, false, false, false, false),

  /**
   * 진행중
   */
  IN_PROGRESS(false, true, false, true, true, false, true),

  /**
   * 생산완료
   */
  COMPLETED(false, false, false, false, false, false, false);

  @Getter
  private final boolean updatable;

  @Getter
  private final boolean splittable;

  @Getter
  private final boolean determinable;

  @Getter
  private final boolean reschedulable;

  @Getter
  private final boolean progressable;

  @Getter
  private final boolean cancelable;

  @Getter
  private final boolean completable;

}
