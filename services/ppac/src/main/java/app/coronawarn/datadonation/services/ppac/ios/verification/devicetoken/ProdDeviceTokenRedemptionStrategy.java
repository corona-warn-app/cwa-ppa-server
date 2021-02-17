package app.coronawarn.datadonation.services.ppac.ios.verification.devicetoken;

import app.coronawarn.datadonation.services.ppac.ios.verification.errors.DeviceTokenRedeemed;
import app.coronawarn.datadonation.services.ppac.ios.verification.errors.InternalError;
import org.springframework.context.annotation.Profile;
import org.springframework.dao.DuplicateKeyException;
import org.springframework.stereotype.Component;

@Component
@Profile("!loadtest")
public class ProdDeviceTokenRedemptionStrategy implements DeviceTokenRedemptionStrategy {

  @Override
  public void redeem(Exception e) {
    if (e.getCause() instanceof DuplicateKeyException) {
      throw new DeviceTokenRedeemed(e);
    }
    throw new InternalError(e);
  }
}
