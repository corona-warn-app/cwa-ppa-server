package app.coronawarn.datadonation.services.ppac.ios.verification;

import app.coronawarn.datadonation.common.persistence.domain.ApiToken;
import app.coronawarn.datadonation.services.ppac.ios.verification.errors.ApiTokenQuotaExceeded;
import app.coronawarn.datadonation.services.ppac.utils.TimeUtils;
import java.time.YearMonth;
import org.springframework.stereotype.Component;

@Component
public class PpacIosScenarioValidator {

  void validateForEdus(ApiToken apiToken) {
    YearMonth currentMonth = YearMonth.now();
    YearMonth lastUsedForEdusMonth = YearMonth.from(TimeUtils.getLocalDateFor(apiToken.getLastUsedEdus()));
    if (currentMonth.equals(lastUsedForEdusMonth)) {
      throw new ApiTokenQuotaExceeded();
    }
  }

  void validateForPpa(ApiToken apiToken) {

  }
}
