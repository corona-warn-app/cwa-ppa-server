package app.coronawarn.datadonation.services.ppac.ios.verification.devicetoken;

public interface DeviceTokenRedemptionStrategy {

  /**
   * How to handle exceptions during OTP redemption.
   *
   * @param e {@link Exception} to be handled
   * @throws InternalError in case redemption fails
   */
  void redeem(Exception e) throws InternalError;

}
