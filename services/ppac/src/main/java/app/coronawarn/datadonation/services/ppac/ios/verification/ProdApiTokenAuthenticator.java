package app.coronawarn.datadonation.services.ppac.ios.verification;

import app.coronawarn.datadonation.services.ppac.ios.client.domain.PerDeviceDataResponse;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Component;

@Component
@Profile("!test")
public class ProdApiTokenAuthenticator implements ApiTokenAuthenticator {

  @Override
  public void checkApiTokenAlreadyIssued(PerDeviceDataResponse perDeviceDataResponse,
      boolean ignoreApiTokenAlreadyIssued) {
    perDeviceDataResponse.getLastUpdated().ifPresent(this::validateApiTokenNotAlreadyUsed);
  }
}
