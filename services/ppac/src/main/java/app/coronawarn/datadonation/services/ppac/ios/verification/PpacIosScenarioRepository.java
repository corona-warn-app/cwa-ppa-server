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
   * EDU-specific save method. Stores the provided API Token and sets its expirationDate on the last day of the month.
   *
   * @param apiToken {@link String} Key of the API Token.
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
   * PPAC-specific save method. Stores the provided API Token and sets its expirationDate on the last day of the month.
   *
   * @param apiToken {@link String} Key of the API Token.
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
