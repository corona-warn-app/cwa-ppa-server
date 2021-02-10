package app.coronawarn.datadonation.services.ppac.ios.verification.errors;

public class DeviceTokenInvalid extends RuntimeException {

  public DeviceTokenInvalid() {
    super("PPAC failed due to invalid device token!");
  }
}
