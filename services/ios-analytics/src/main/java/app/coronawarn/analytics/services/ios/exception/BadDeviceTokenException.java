package app.coronawarn.analytics.services.ios.exception;

public class BadDeviceTokenException extends RuntimeException {

  private static final String ERROR_MESSAGE = "PPAC failed due to bad device token";

  public BadDeviceTokenException() {
    super(ERROR_MESSAGE);
  }
}
