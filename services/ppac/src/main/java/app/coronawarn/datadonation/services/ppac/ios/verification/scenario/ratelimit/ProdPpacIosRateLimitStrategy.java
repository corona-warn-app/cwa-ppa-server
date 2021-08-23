package app.coronawarn.datadonation.services.ppac.ios.verification.scenario.ratelimit;

import static app.coronawarn.datadonation.common.utils.TimeUtils.getLocalDateFor;
import static app.coronawarn.datadonation.common.utils.TimeUtils.getLocalDateTimeForNow;
import static java.time.Instant.ofEpochSecond;
import static java.time.ZoneOffset.UTC;
import static java.time.temporal.ChronoUnit.HOURS;

import app.coronawarn.datadonation.common.persistence.domain.ApiToken;
import app.coronawarn.datadonation.services.ppac.ios.verification.errors.ApiTokenQuotaExceeded;
import java.time.LocalDateTime;
import java.time.YearMonth;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Component;

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
   * Check Rate Limit for PPA Scenario. ApiToken in a PPA Scenario can only be used once every 23 hours.
   *
   * @param apiToken the ApiToken that needs to be validated.
   */
  public void validateForPpa(ApiToken apiToken) {
    apiToken.getLastUsedPpac().ifPresent(getLastUsedEpochSecond -> {
      LocalDateTime currentDateUtc = getLocalDateTimeForNow();
      LocalDateTime lastUsedForPpaUtc = ofEpochSecond(getLastUsedEpochSecond).atOffset(UTC).toLocalDateTime();
      long hours = HOURS.between(lastUsedForPpaUtc, currentDateUtc);
      if ( hours > VALIDITY_IN_HOURS ) {
        logger.info("Api Token was updated {} hours ago. Api Token can only be used once every 23 hours.", hours);
        throw new ApiTokenQuotaExceeded();
      }
    });
  }
}
