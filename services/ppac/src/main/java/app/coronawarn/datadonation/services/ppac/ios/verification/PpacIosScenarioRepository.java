package app.coronawarn.datadonation.services.ppac.ios.verification;

import app.coronawarn.datadonation.common.persistence.repository.ApiTokenRepository;
import app.coronawarn.datadonation.services.ppac.ios.verification.errors.InternalError;
import app.coronawarn.datadonation.services.ppac.utils.TimeUtils;
import org.springframework.stereotype.Component;

@Component
public class PpacIosScenarioRepository {

  private final ApiTokenRepository apiTokenRepository;

  public PpacIosScenarioRepository(ApiTokenRepository apiTokenRepository) {
    this.apiTokenRepository = apiTokenRepository;
  }

  public void saveForEdus(String apiToken) {
    Long currentTimeStamp = TimeUtils.getEpochSecondForNow();
    Long expirationDate = TimeUtils.getLastDayOfMonthForNow();

    try {
      apiTokenRepository.insert(apiToken,
          expirationDate,
          currentTimeStamp,
          currentTimeStamp,
          null);
    } catch (Exception e) {
      throw new InternalError(e);
    }
  }

  public void saveForPpa(String apiToken) {
    Long currentTimeStamp = TimeUtils.getEpochSecondForNow();
    Long expirationDate = TimeUtils.getLastDayOfMonthForNow();

    try {
      apiTokenRepository.insert(apiToken,
          expirationDate,
          currentTimeStamp,
          null,
          currentTimeStamp);
    } catch (Exception e) {
      throw new InternalError(e);
    }
  }
}
