package app.coronawarn.datadonation.services.ppac.ios.verification.apitoken.authentication;

import static app.coronawarn.datadonation.common.utils.TimeUtils.getLocalDateFor;
import static app.coronawarn.datadonation.common.utils.TimeUtils.getLocalDateForNow;

import app.coronawarn.datadonation.common.persistence.domain.ApiTokenData;
import app.coronawarn.datadonation.services.ppac.ios.client.domain.PerDeviceDataResponse;
import app.coronawarn.datadonation.services.ppac.ios.verification.errors.ApiTokenExpired;
import java.time.LocalDate;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Component;

@Component
@Profile("!test && !loadtest")
public class ProdApiTokenAuthenticationStrategy implements ApiTokenAuthenticationStrategy {

  @Override
  public void checkApiTokenAlreadyIssued(PerDeviceDataResponse perDeviceDataResponse,
      boolean ignoreApiTokenAlreadyIssued) {
    perDeviceDataResponse.getLastUpdated().ifPresent(this::validateApiTokenNotAlreadyUsed);
  }

  @Override
  public void checkApiTokenNotAlreadyExpired(ApiTokenData apiTokenData) {
    LocalDate expirationDate = getLocalDateFor(apiTokenData.getExpirationDate());
    LocalDate now = getLocalDateForNow();
    if (now.isAfter(expirationDate)) {
      throw new ApiTokenExpired();
    }
  }
}
