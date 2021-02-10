package app.coronawarn.datadonation.common.persistence.errors;

public class MetricsDataCouldNotBeStored extends RuntimeException {

  private static final long serialVersionUID = 2136916612923338677L;

  public MetricsDataCouldNotBeStored(String message) {
    super(message);
  }
}
