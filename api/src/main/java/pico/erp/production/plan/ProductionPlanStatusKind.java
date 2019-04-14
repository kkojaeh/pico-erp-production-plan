package pico.erp.production.plan;

import lombok.AllArgsConstructor;
import pico.erp.shared.data.LocalizedNameable;

@AllArgsConstructor
public enum ProductionPlanStatusKind implements LocalizedNameable {

  /**
   * 작성중
   */
  CREATED,

  /**
   * 준비됨
   */
  PREPARED,

  /**
   * 확정 함
   */
  DETERMINED,

  /**
   * 취소 됨
   */
  CANCELED,

  /**
   * 진행중
   */
  IN_PROGRESS,

  /**
   * 생산완료
   */
  COMPLETED;

  public boolean isCancelable() {
    return this == CREATED || this == PREPARED;
  }

  public boolean isCompletable() {
    return this == DETERMINED || this == IN_PROGRESS;
  }

  public boolean isDeterminable() {
    return this == PREPARED;
  }

  public boolean isPreparable() {
    return this == CREATED;
  }

  public boolean isProgressable() {
    return this == DETERMINED || this == IN_PROGRESS;
  }

  public boolean isUpdatable() {
    return this == CREATED;
  }

}
