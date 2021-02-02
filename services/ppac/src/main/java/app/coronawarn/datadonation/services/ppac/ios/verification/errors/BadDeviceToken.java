package app.coronawarn.datadonation.services.ppac.ios.verification.errors;

public class BadDeviceToken extends RuntimeException {

  public BadDeviceToken(Exception e) {
    super(e.getMessage(), e.getCause());
  }
}
