package app.coronawarn.analytics.common.persistence.domain;

public class AnalyticsFloatData extends AnalyticsBaseData<Double> {

  public AnalyticsFloatData(final String os, final int key, final Double value) {
    super(os, key, value);
  }

}
