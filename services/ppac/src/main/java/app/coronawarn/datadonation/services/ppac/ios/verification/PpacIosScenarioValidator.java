package app.coronawarn.datadonation.services.ppac.ios.verification;

import static app.coronawarn.datadonation.common.utils.TimeUtils.getLocalDateFor;

import app.coronawarn.datadonation.common.persistence.domain.ApiToken;
import app.coronawarn.datadonation.common.utils.TimeUtils;
import app.coronawarn.datadonation.services.ppac.ios.verification.errors.ApiTokenQuotaExceeded;
import java.time.LocalDate;
import java.time.YearMonth;
import org.springframework.stereotype.Component;

@Component
public class PpacIosScenarioValidator {

  /**
   * Check Rate Limit for EDUS Scenario. ApiToken in a EDUS Scenario can only be used once a month.
   *
   * @param apiToken the ApiToken that needs to be validated.
   */
  public void validateForEdus(ApiToken apiToken) {
    YearMonth currentMonth = YearMonth.now();
    YearMonth lastUsedForEdusMonth = YearMonth.from(getLocalDateFor(apiToken.getLastUsedEdus()));
    if (currentMonth.equals(lastUsedForEdusMonth)) {
      throw new ApiTokenQuotaExceeded();
    }
  }

  /**
   * Check Rate Limit for PPA Scenario. ApiToken in a PPA Scenario can only be used once a day.
   *
   * @param apiToken the ApiToken that needs to be validated.
   */
  public void validateForPpa(ApiToken apiToken) {
    LocalDate currentDate = TimeUtils.getLocalDateForNow();
    LocalDate lastUsedForPpa = getLocalDateFor(apiToken.getLastUsedPpac());
    if (currentDate.getDayOfWeek().equals(lastUsedForPpa.getDayOfWeek())) {
      throw new ApiTokenQuotaExceeded();
    }
  }
}
