package app.coronawarn.datadonation.services.ppac.ios.verification.errors;

public class DeviceTokenSyntaxError extends RuntimeException {

  public DeviceTokenSyntaxError(final Throwable cause) {
    super("Device token is badly formatted ", cause);
  }
}
