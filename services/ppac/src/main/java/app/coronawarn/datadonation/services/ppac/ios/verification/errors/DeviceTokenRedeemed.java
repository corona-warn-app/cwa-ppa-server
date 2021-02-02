package app.coronawarn.datadonation.services.ppac.ios.verification.errors;

public class DeviceTokenRedeemed extends RuntimeException {

  public DeviceTokenRedeemed(Exception e) {
    super("PPAC failed due to redeemed device token", e.getCause());
  }
}
