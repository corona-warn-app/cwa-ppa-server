package app.coronawarn.datadonation.services.ppac.ios.verification.devicetoken;

public interface DeviceTokenRedemptionStrategy {

  /**
   * @param e
   * @throws InternalError
   */
  void redeem(Exception e) throws InternalError;

}
