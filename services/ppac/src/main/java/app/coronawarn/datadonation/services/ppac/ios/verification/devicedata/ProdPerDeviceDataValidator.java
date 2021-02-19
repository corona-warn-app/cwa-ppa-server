package app.coronawarn.datadonation.services.ppac.ios.verification.devicedata;

import app.coronawarn.datadonation.services.ppac.config.PpacConfiguration;
import app.coronawarn.datadonation.services.ppac.ios.client.IosDeviceApiClient;
import app.coronawarn.datadonation.services.ppac.ios.verification.JwtProvider;
import app.coronawarn.datadonation.services.ppac.ios.verification.devicetoken.DeviceTokenService;
import app.coronawarn.datadonation.services.ppac.ios.verification.errors.DeviceTokenInvalid;
import app.coronawarn.datadonation.services.ppac.ios.verification.errors.InternalError;
import feign.FeignException;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Component;

@Component
@Profile("!loadtest")
public class ProdPerDeviceDataValidator extends PerDeviceDataValidator {

  public ProdPerDeviceDataValidator(IosDeviceApiClient iosDeviceApiClient, JwtProvider jwtProvider,
      DeviceTokenService deviceTokenService, PpacConfiguration ppacConfiguration) {
    super(iosDeviceApiClient, jwtProvider, deviceTokenService, ppacConfiguration);
  }

  @Override
  protected void treatBadRequest(FeignException.BadRequest e) {
    if (isBadDeviceToken(e.contentUTF8())) {
      throw new DeviceTokenInvalid();
    }
    throw new InternalError(e);
  }

  private boolean isBadDeviceToken(String message) {
    final String missingOrIncorrectlyFormattedDeviceTokenPayload =
        ppacConfiguration.getIos().getMissingOrIncorrectlyFormattedDeviceTokenPayload();
    return missingOrIncorrectlyFormattedDeviceTokenPayload.toLowerCase()
        .contains(message.toLowerCase());
  }

  @Override
  protected void treatGeneralRequestError(FeignException e) {
    throw new InternalError(e);
  }
}
