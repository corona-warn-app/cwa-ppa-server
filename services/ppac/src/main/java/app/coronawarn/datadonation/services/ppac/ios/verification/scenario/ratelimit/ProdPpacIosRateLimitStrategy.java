package app.coronawarn.datadonation.services.ppac.ios.verification.scenario.ratelimit;

import static app.coronawarn.datadonation.common.utils.TimeUtils.formatToDays;
import static app.coronawarn.datadonation.common.utils.TimeUtils.formatToHours;
import static app.coronawarn.datadonation.common.utils.TimeUtils.getLocalDateFor;
import static app.coronawarn.datadonation.common.utils.TimeUtils.getLocalDateTimeForNow;
import static java.time.Instant.ofEpochSecond;
import static java.time.ZoneOffset.UTC;
import static java.time.temporal.ChronoUnit.SECONDS;

import app.coronawarn.datadonation.common.persistence.domain.ApiTokenData;
import app.coronawarn.datadonation.common.utils.TimeUtils;
import app.coronawarn.datadonation.services.ppac.config.PpacConfiguration;
import app.coronawarn.datadonation.services.ppac.ios.verification.errors.ApiTokenQuotaExceeded;
import java.time.LocalDateTime;
import java.time.YearMonth;
import java.util.concurrent.TimeUnit;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Component;

@Component
@Profile("!loadtest")
public class ProdPpacIosRateLimitStrategy implements PpacIosRateLimitStrategy {

  private static final Logger LOGGER = LoggerFactory.getLogger(ProdPpacIosRateLimitStrategy.class);

  private final int validityInSeconds;
  private final int srsTimeBetweenSubmissionInDays;

  /**
   * Constructs a validator instance.
   */
  public ProdPpacIosRateLimitStrategy(PpacConfiguration ppacConfiguration) {
    this.validityInSeconds = ppacConfiguration.getIos().getApiTokenRateLimitSeconds();
    this.srsTimeBetweenSubmissionInDays = ppacConfiguration.getSrsTimeBetweenSubmissionsInDays();
  }

  /**
   * Check Rate Limit for EDUS Scenario. ApiToken in a EDUS Scenario can only be used once a month.
   *
   * @param apiTokenData the ApiToken that needs to be validated.
   */
  public void validateForEdus(ApiTokenData apiTokenData) {
    apiTokenData.getLastUsedEdus().ifPresent(it -> {
      YearMonth currentMonth = TimeUtils.getYearMonthNow();
      YearMonth lastUsedForEdusMonth = YearMonth.from(getLocalDateFor(it));
      if (currentMonth.equals(lastUsedForEdusMonth)) {
        throw new ApiTokenQuotaExceeded();
      }
    });
  }

  /**
   * Check Rate Limit for PPA Scenario. ApiToken in a PPA Scenario can only be used once every 23 hours.
   *
   * @param apiTokenData the ApiToken that needs to be validated.
   */
  public void validateForPpa(ApiTokenData apiTokenData) {
    apiTokenData.getLastUsedPpac().ifPresent(getLastUsedEpochSecond -> {
      LocalDateTime currentTimeUtc = getLocalDateTimeForNow();
      LocalDateTime lastUsedForPpaUtc = ofEpochSecond(getLastUsedEpochSecond).atOffset(UTC).toLocalDateTime();
      long seconds = SECONDS.between(lastUsedForPpaUtc, currentTimeUtc);
      if (seconds < validityInSeconds) {
        LOGGER.info("Api Token was updated {} hours ago. Api Token can only be used once every {} hours.",
            formatToHours(seconds), formatToHours(validityInSeconds));
        throw new ApiTokenQuotaExceeded();
      }
    });
  }

  /**
   * Check Rate Limit for SRS Scenario. ApiToken in an SRS Scenario can only be used once a week.
   *
   * @param apiTokenData the ApiToken that needs to be validated.
   */
  public void validateForSrs(ApiTokenData apiTokenData) {
    apiTokenData.getLastUsedSrs().ifPresent(getLastUsedEpochSecond -> {
      LocalDateTime currentTimeUtc = getLocalDateTimeForNow();
      LocalDateTime lastUsedForSrsUtc = ofEpochSecond(getLastUsedEpochSecond).atOffset(UTC).toLocalDateTime();
      long seconds = SECONDS.between(lastUsedForSrsUtc, currentTimeUtc);
      if (seconds < TimeUnit.DAYS.toSeconds(srsTimeBetweenSubmissionInDays)) {
        LOGGER.info("Api Token was updated {} days ago. Api Token can only be used once every {} days.",
            formatToDays(seconds), srsTimeBetweenSubmissionInDays);
        throw new ApiTokenQuotaExceeded();
      }
    });
  }
}
