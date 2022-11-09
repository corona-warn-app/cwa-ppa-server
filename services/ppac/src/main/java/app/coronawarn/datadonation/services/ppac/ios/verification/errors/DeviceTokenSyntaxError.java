package app.coronawarn.datadonation.services.ppac.ios.verification.errors;

public class DeviceTokenSyntaxError extends RuntimeException {

  private static final long serialVersionUID = -6225267971601640961L;

  public DeviceTokenSyntaxError(final Throwable cause) {
    super("Device token is badly formatted ", cause);
  }
}
