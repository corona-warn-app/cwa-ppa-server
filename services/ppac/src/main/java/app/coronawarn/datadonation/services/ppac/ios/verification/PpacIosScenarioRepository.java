package app.coronawarn.datadonation.services.ppac.ios.verification;

import static app.coronawarn.datadonation.common.utils.TimeUtils.getEpochSecondsForNow;
import static app.coronawarn.datadonation.common.utils.TimeUtils.getLastDayOfMonthForNow;

import app.coronawarn.datadonation.common.persistence.domain.ApiTokenData;
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
   * @param apiTokenData {@link String} Key of the API Token.
   */
  public void saveForEdus(ApiTokenData apiTokenData) {
    Long currentTimeStamp = getEpochSecondsForNow();
    Long expirationDate = getLastDayOfMonthForNow();

    try {
      apiTokenRepository.insert(apiTokenData.getApiToken(),
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
   * @param apiTokenData {@link String} Key of the API Token.
   */
  public void saveForPpa(ApiTokenData apiTokenData) {
    Long currentTimeStamp = getEpochSecondsForNow();
    Long expirationDate = getLastDayOfMonthForNow();

    try {
      apiTokenRepository.insert(apiTokenData.getApiToken(),
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
   * @param apiTokenData the apitoken to update.
   */
  public void updateForEdus(ApiTokenData apiTokenData) {
    Long currentTimeStamp = getEpochSecondsForNow();
    apiTokenData.setLastUsedEdus(currentTimeStamp);
    apiTokenRepository.save(apiTokenData);
  }

  /**
   * Update an existing ApiToken in case of PPA. Set the lastUsedForPpac property.
   *
   * @param apiTokenData the apitoken to update.
   */
  public void updateForPpa(ApiTokenData apiTokenData) {
    Long currentTimeStamp = getEpochSecondsForNow();
    apiTokenData.setLastUsedPpac(currentTimeStamp);
    apiTokenRepository.save(apiTokenData);
  }
}
