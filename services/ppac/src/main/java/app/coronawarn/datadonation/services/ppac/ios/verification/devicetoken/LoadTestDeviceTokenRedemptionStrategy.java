package app.coronawarn.datadonation.services.ppac.ios.verification.devicetoken;

import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Component;

@Component
@Profile("loadtest")
public class LoadTestDeviceTokenRedemptionStrategy implements DeviceTokenRedemptionStrategy {

  @Override
  public void redeem(Exception e) {
    // do nothing here
  }
}
