package app.coronawarn.datadonation.services.ppac.ios.verification.devicetoken;

import app.coronawarn.datadonation.services.ppac.ios.verification.errors.DeviceTokenRedeemed;
import app.coronawarn.datadonation.services.ppac.ios.verification.errors.InternalServerError;
import org.springframework.context.annotation.Profile;
import org.springframework.dao.DuplicateKeyException;
import org.springframework.stereotype.Component;

@Component
@Profile("!loadtest")
public class ProdDeviceTokenRedemptionStrategy implements DeviceTokenRedemptionStrategy {

  /**
   * How to handle exceptions during OTP redemption.
   *
   * @param e in case of {@link DuplicateKeyException} a {@link DeviceTokenRedeemed} is thrown.
   * @throws InternalServerError if given {@link Exception} is <code>not</code> an instance of {@link DuplicateKeyException}.
   */
  @Override
  public void redeem(Exception e) throws InternalServerError {
    if (e.getCause() instanceof DuplicateKeyException) {
      throw new DeviceTokenRedeemed(e);
    }
    throw new InternalServerError(e);
  }
}
