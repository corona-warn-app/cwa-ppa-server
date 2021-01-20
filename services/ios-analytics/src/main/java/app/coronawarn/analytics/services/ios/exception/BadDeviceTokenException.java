package app.coronawarn.analytics.services.ios.exception;

public class BadDeviceTokenException extends RuntimeException {


  public BadDeviceTokenException() {
    super("PPAC failed due to bad device token");
  }
}
