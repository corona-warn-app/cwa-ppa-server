package app.coronawarn.datadonation.services.ppac.ios.verification.devicedata;

import app.coronawarn.datadonation.services.ppac.config.PpacConfiguration;
import app.coronawarn.datadonation.services.ppac.ios.client.IosDeviceApiClient;
import app.coronawarn.datadonation.services.ppac.ios.verification.JwtProvider;
import app.coronawarn.datadonation.services.ppac.ios.verification.devicetoken.DeviceTokenService;
import feign.FeignException;
import feign.FeignException.BadRequest;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Component;

@Component
@Profile("loadtest")
public class LoadtestPerDeviceDataValidator extends PerDeviceDataValidator {

  public LoadtestPerDeviceDataValidator(IosDeviceApiClient iosDeviceApiClient,
      JwtProvider jwtProvider, DeviceTokenService deviceTokenService,
      PpacConfiguration ppacConfiguration) {
    super(iosDeviceApiClient, jwtProvider, deviceTokenService, ppacConfiguration);
  }

  @Override
  protected void treatGeneralRequestError(FeignException e) {
    // Do nothing during load tests
  }

  @Override
  protected void treatBadRequest(BadRequest e) {
    // Do nothing during load tests
  }
}
