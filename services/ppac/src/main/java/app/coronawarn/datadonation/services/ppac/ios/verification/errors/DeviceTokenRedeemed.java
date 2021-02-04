package app.coronawarn.datadonation.services.ppac.ios.verification.errors;

public class DeviceTokenRedeemed extends RuntimeException {

  public DeviceTokenRedeemed(final Throwable cause) {
    super("PPAC failed due to redeemed device token", cause);
  }
}
