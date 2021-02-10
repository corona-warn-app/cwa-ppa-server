package app.coronawarn.datadonation.services.ppac.ios.verification;

import static app.coronawarn.datadonation.common.utils.TimeUtils.getLocalDateFor;

import app.coronawarn.datadonation.common.persistence.domain.ApiToken;
import app.coronawarn.datadonation.services.ppac.ios.verification.errors.ApiTokenQuotaExceeded;
import java.time.YearMonth;
import org.springframework.stereotype.Component;

@Component
public class PpacIosScenarioValidator {

  void validateForEdus(ApiToken apiToken) {
    YearMonth currentMonth = YearMonth.now();
    YearMonth lastUsedForEdusMonth = YearMonth.from(getLocalDateFor(apiToken.getLastUsedEdus()));
    if (currentMonth.equals(lastUsedForEdusMonth)) {
      throw new ApiTokenQuotaExceeded();
    }
  }

  void validateForPpa(ApiToken apiToken) {

  }
}
