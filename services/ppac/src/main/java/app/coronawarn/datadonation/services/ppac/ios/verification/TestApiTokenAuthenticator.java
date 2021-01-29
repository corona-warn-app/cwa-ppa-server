package app.coronawarn.datadonation.services.ppac.ios.verification;

import app.coronawarn.datadonation.services.ppac.ios.client.domain.PerDeviceDataResponse;
import app.coronawarn.datadonation.services.ppac.ios.verification.errors.ApiTokenAlreadyUsed;
import java.time.YearMonth;
import java.time.format.DateTimeFormatter;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Component;

@Component
@Profile("test")
public class TestApiTokenAuthenticator implements ApiTokenAuthenticator {

  @Override
  public void checkApiTokenAlreadyIssued(PerDeviceDataResponse perDeviceDataResponse,
      boolean ignoreApiTokenAlreadyIssued) {
    perDeviceDataResponse.getLastUpdated()
        .ifPresent(lastUpdated -> this.validateApiTokenNotAlreadyUsed(lastUpdated, ignoreApiTokenAlreadyIssued));
  }

  private void validateApiTokenNotAlreadyUsed(String perDeviceDataLastUpdated, boolean ignoreApiTokenExpiration) {
    final YearMonth lastUpdated = YearMonth.parse(
        perDeviceDataLastUpdated,
        DateTimeFormatter.ofPattern("yyyy-MM"));
    if (!ignoreApiTokenExpiration && YearMonth.now().equals(lastUpdated)) {
      throw new ApiTokenAlreadyUsed();
    }
  }
}
