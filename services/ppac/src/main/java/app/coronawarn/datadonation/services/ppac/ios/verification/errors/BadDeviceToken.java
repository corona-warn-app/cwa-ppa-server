package app.coronawarn.datadonation.services.ppac.ios.verification.errors;

public class BadDeviceToken extends RuntimeException {

  public BadDeviceToken(String msg) {
    super(msg);
  }
}
