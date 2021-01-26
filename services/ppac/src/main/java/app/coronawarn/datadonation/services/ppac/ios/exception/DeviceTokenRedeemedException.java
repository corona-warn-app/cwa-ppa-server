package app.coronawarn.datadonation.services.ppac.ios.exception;

public class DeviceTokenRedeemedException extends RuntimeException {

  public DeviceTokenRedeemedException() {
    super("PPAC failed due to redeemed device token");
  }
}
