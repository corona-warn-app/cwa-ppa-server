package app.coronawarn.datadonation.services.ppac.ios.verification.apitoken.authentication;

import app.coronawarn.datadonation.common.persistence.domain.ApiToken;
import app.coronawarn.datadonation.services.ppac.ios.client.domain.PerDeviceDataResponse;
import app.coronawarn.datadonation.services.ppac.ios.verification.apitoken.authentication.ApiTokenAuthenticationStrategy;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Component;

@Component
@Profile("loadtest")
public class LoadTestApiTokenAuthenticationStrategy implements ApiTokenAuthenticationStrategy {

  @Override
  public void checkApiTokenAlreadyIssued(PerDeviceDataResponse perDeviceDataResponse,
      boolean ignoreApiTokenAlreadyIssued) {
  }

  @Override
  public void checkApiTokenNotAlreadyExpired(ApiToken apiToken) {

  }
}
