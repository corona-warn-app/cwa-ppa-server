package app.coronawarn.datadonation.services.ppac.ios.verification.errors;

public class DeviceTokenInvalid extends RuntimeException {

  private static final long serialVersionUID = -3034082672271353058L;

  public DeviceTokenInvalid() {
    super("PPAC failed due to invalid device token!");
  }
}
