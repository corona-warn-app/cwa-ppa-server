package app.coronawarn.datadonation.common.persistence.domain;

/**
 * A key generated for advertising over a window of time.
 */
class AnalyticsBaseData<T> {
  private final int key;
  private final String os;
  private final T value;

  /**
   * Constructor for data object.
   *
   * @param os    - operating system [iOS, android]
   * @param key   - name of the metric
   * @param value - value of that metric
   */
  AnalyticsBaseData(final String os, final int key, final T value) {
    this.os = os;
    this.key = key;
    this.value = value;
  }

  public int getKey() {
    return key;
  }

  public String getOs() {
    return os;
  }

  public T getValue() {
    return value;
  }
}
