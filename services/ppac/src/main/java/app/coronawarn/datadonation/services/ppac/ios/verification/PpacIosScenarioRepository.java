package app.coronawarn.datadonation.services.ppac.ios.verification;

import static app.coronawarn.datadonation.common.utils.TimeUtils.getEpochSecondsForNow;
import static app.coronawarn.datadonation.common.utils.TimeUtils.getLastDayOfMonthForNow;

import app.coronawarn.datadonation.common.persistence.domain.ApiToken;
import app.coronawarn.datadonation.common.persistence.repository.ApiTokenRepository;
import app.coronawarn.datadonation.services.ppac.ios.verification.errors.InternalServerError;
import org.springframework.stereotype.Component;

@Component
public class PpacIosScenarioRepository {

  private final ApiTokenRepository apiTokenRepository;

  public PpacIosScenarioRepository(ApiTokenRepository apiTokenRepository) {
    this.apiTokenRepository = apiTokenRepository;
  }

  /**
   * EDUS-specific save method. Stores the provided API Token and sets its expirationDate on the last day of the month.
   *
   * @param apiToken {@link String} Key of the API Token.
   */
  public void saveForEdus(ApiToken apiToken) {
    Long currentTimeStamp = getEpochSecondsForNow();
    Long expirationDate = getLastDayOfMonthForNow();

    try {
      apiTokenRepository.insert(apiToken.getApiToken(),
          expirationDate,
          currentTimeStamp,
          currentTimeStamp,
          null);
    } catch (Exception e) {
      throw new InternalServerError(e);
    }
  }

  /**
   * PPAC-specific save method. Stores the provided API Token and sets its expirationDate on the last day of the month.
   *
   * @param apiToken {@link String} Key of the API Token.
   */
  public void saveForPpa(ApiToken apiToken) {
    Long currentTimeStamp = getEpochSecondsForNow();
    Long expirationDate = getLastDayOfMonthForNow();

    try {
      apiTokenRepository.insert(apiToken.getApiToken(),
          expirationDate,
          currentTimeStamp,
          null,
          currentTimeStamp);
    } catch (Exception e) {
      throw new InternalServerError(e);
    }
  }

  /**
   * Update an existing ApiToken in case of EDUS. Set the lastUsedForEdus property.
   *
   * @param apiToken the apitoken to update.
   */
  public void updateForEdus(ApiToken apiToken) {
    Long currentTimeStamp = getEpochSecondsForNow();
    apiToken.setLastUsedEdus(currentTimeStamp);
    apiTokenRepository.save(apiToken);
  }

  /**
   * Update an existing ApiToken in case of PPA. Set the lastUsedForPpac property.
   *
   * @param apiToken the apitoken to update.
   */
  public void updateForPpa(ApiToken apiToken) {
    Long currentTimeStamp = getEpochSecondsForNow();
    apiToken.setLastUsedPpac(currentTimeStamp);
    apiTokenRepository.save(apiToken);
  }
}
