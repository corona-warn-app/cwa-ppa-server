package app.coronawarn.datadonation.services.ppac.ios.verification.apitoken.authentication;

import app.coronawarn.datadonation.common.persistence.domain.ApiTokenData;
import app.coronawarn.datadonation.services.ppac.ios.client.domain.PerDeviceDataResponse;
import app.coronawarn.datadonation.services.ppac.ios.verification.errors.ApiTokenAlreadyUsed;
import java.time.YearMonth;
import java.time.ZoneOffset;
import java.time.format.DateTimeFormatter;

public interface ApiTokenAuthenticationStrategy {

  void checkApiTokenAlreadyIssued(PerDeviceDataResponse perDeviceDataResponse,
      boolean ignoreApiTokenAlreadyIssued);

  void checkApiTokenNotAlreadyExpired(ApiTokenData apiTokenData);

  /**
   * Default implementation to check whether the provided per-device Data was updated this Month. If so then this
   * methods throws {@link ApiTokenAlreadyUsed}.
   *
   * @param perDeviceDataLastUpdated the per-device Data to check against (Format yyyy-MM).
   * @throws ApiTokenAlreadyUsed if the provided per-device data was already used this month.
   */
  default void validateApiTokenNotAlreadyUsed(String perDeviceDataLastUpdated) {
    final YearMonth lastUpdated = YearMonth.parse(
        perDeviceDataLastUpdated,
        DateTimeFormatter.ofPattern("yyyy-MM"));
    if (YearMonth.now(ZoneOffset.UTC).equals(lastUpdated)) {
      throw new ApiTokenAlreadyUsed(perDeviceDataLastUpdated);
    }
  }
}
