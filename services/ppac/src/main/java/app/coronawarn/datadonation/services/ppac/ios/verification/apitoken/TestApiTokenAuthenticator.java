package app.coronawarn.datadonation.services.ppac.ios.verification.apitoken;

import app.coronawarn.datadonation.services.ppac.ios.client.domain.PerDeviceDataResponse;
import app.coronawarn.datadonation.services.ppac.ios.verification.apitoken.ApiTokenAuthenticator;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Component;

@Component
@Profile("test")
public class TestApiTokenAuthenticator implements ApiTokenAuthenticator {

  @Override
  public void checkApiTokenAlreadyIssued(PerDeviceDataResponse perDeviceDataResponse,
      boolean ignoreApiTokenAlreadyIssued) {
    if (!ignoreApiTokenAlreadyIssued) {
      perDeviceDataResponse.getLastUpdated()
          .ifPresent(this::validateApiTokenNotAlreadyUsed);
    }
  }
}
