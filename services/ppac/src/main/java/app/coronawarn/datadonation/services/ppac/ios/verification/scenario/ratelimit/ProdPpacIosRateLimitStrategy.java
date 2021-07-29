package app.coronawarn.datadonation.services.ppac.ios.verification.scenario.ratelimit;

import app.coronawarn.datadonation.common.persistence.domain.ApiToken;
import app.coronawarn.datadonation.common.utils.TimeUtils;
import app.coronawarn.datadonation.services.ppac.ios.verification.errors.ApiTokenQuotaExceeded;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Component;

import java.time.Instant;
import java.time.LocalDate;
import java.time.YearMonth;
import java.time.temporal.ChronoUnit;

import static app.coronawarn.datadonation.common.utils.TimeUtils.getLocalDateFor;

@Component
@Profile("!loadtest")
public class ProdPpacIosRateLimitStrategy implements PpacIosRateLimitStrategy {

  private static final Logger logger = LoggerFactory.getLogger(ProdPpacIosRateLimitStrategy.class);
  static final int VALIDITY_IN_HOURS = 23;

  /**
   * Check Rate Limit for EDUS Scenario. ApiToken in a EDUS Scenario can only be used once a month.
   *
   * @param apiToken the ApiToken that needs to be validated.
   */
  public void validateForEdus(ApiToken apiToken) {
    apiToken.getLastUsedEdus().ifPresent(it -> {
      YearMonth currentMonth = YearMonth.now();
      YearMonth lastUsedForEdusMonth = YearMonth.from(getLocalDateFor(it));
      if (currentMonth.equals(lastUsedForEdusMonth)) {
        throw new ApiTokenQuotaExceeded();
      }
    });
  }

  /**
   * Check Rate Limit for PPA Scenario. ApiToken in a PPA Scenario can only be used once a day.
   *
   * @param apiToken the ApiToken that needs to be validated.
   */
  public void validateForPpa(ApiToken apiToken) {
    apiToken.getLastUsedPpac().ifPresent(getLastUsedEpochSecond -> {
      LocalDate currentDateUtc = TimeUtils.getLocalDateForNow();
      LocalDate lastUsedForPpaUtc = getLocalDateFor(getLastUsedEpochSecond);
      logLastUpdate(getLastUsedEpochSecond, currentDateUtc, lastUsedForPpaUtc, Instant.now());

      if (currentDateUtc.isEqual(lastUsedForPpaUtc)) {
        throw new ApiTokenQuotaExceeded();
      }
    });
  }

  static long logLastUpdate(Long getLastUsedEpochSecond, final LocalDate currentDateUtc,
      final LocalDate lastUsedForPpaUtc, Instant currentTimeStamp) {
    Instant lastUsedTimeStamp = Instant.ofEpochSecond(getLastUsedEpochSecond);
    final long diff = ChronoUnit.HOURS.between(lastUsedTimeStamp, currentTimeStamp);
    if (diff == VALIDITY_IN_HOURS && currentDateUtc.equals(lastUsedForPpaUtc)) {
      logger.info("Api Token was updated {} hours ago on the same day. Api Token can only be used once a day {}.", diff,
          currentTimeStamp.truncatedTo(ChronoUnit.MINUTES));
    }
    return diff;
  }
}
