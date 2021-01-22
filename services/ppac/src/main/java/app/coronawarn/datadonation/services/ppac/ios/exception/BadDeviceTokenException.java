package app.coronawarn.datadonation.services.ppac.ios.exception;

public class BadDeviceTokenException extends RuntimeException {

  public BadDeviceTokenException() {
    super("PPAC failed due to bad device token");
  }
}
