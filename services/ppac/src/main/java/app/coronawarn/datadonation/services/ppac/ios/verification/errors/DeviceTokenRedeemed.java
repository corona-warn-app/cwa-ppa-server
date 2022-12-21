package app.coronawarn.datadonation.services.ppac.ios.verification.errors;

public class DeviceTokenRedeemed extends RuntimeException {

  private static final long serialVersionUID = -9084625478944741624L;

  public DeviceTokenRedeemed(final Throwable cause) {
    super("PPAC failed due to redeemed device token", cause);
  }
}
