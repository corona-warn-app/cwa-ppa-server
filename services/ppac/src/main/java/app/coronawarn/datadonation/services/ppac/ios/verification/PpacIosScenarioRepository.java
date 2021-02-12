package app.coronawarn.datadonation.services.ppac.ios.verification;

import static app.coronawarn.datadonation.common.utils.TimeUtils.getEpochSecondForNow;
import static app.coronawarn.datadonation.common.utils.TimeUtils.getLastDayOfMonthForNow;

import app.coronawarn.datadonation.common.persistence.repository.ApiTokenRepository;
import app.coronawarn.datadonation.services.ppac.ios.verification.errors.InternalError;
import org.springframework.stereotype.Component;

@Component
public class PpacIosScenarioRepository {

  private final ApiTokenRepository apiTokenRepository;

  public PpacIosScenarioRepository(ApiTokenRepository apiTokenRepository) {
    this.apiTokenRepository = apiTokenRepository;
  }

  /**
   * TODO.
   * @param apiToken TODO.
   */
  public void saveForEdus(String apiToken) {
    Long currentTimeStamp = getEpochSecondForNow();
    Long expirationDate = getLastDayOfMonthForNow();

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

  /**
   * TODO.
   * @param apiToken TODO.
   */
  public void saveForPpa(String apiToken) {
    Long currentTimeStamp = getEpochSecondForNow();
    Long expirationDate = getLastDayOfMonthForNow();

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
