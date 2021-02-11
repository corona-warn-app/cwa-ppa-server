package app.coronawarn.datadonation.services.ppac.ios.verification;

import static app.coronawarn.datadonation.common.utils.TimeUtils.getEpochMilliSecondForNow;
import static app.coronawarn.datadonation.common.utils.TimeUtils.getLocalDateFor;
import static app.coronawarn.datadonation.common.utils.TimeUtils.getLocalDateForNow;

import app.coronawarn.datadonation.common.persistence.domain.ApiToken;
import app.coronawarn.datadonation.common.persistence.repository.ApiTokenRepository;
import app.coronawarn.datadonation.common.protocols.internal.ppdd.PpacIos.PPACIOS;
import app.coronawarn.datadonation.common.utils.TimeUtils;
import app.coronawarn.datadonation.services.ppac.ios.client.IosDeviceApiClient;
import app.coronawarn.datadonation.services.ppac.ios.client.domain.PerDeviceDataResponse;
import app.coronawarn.datadonation.services.ppac.ios.client.domain.PerDeviceDataUpdateRequest;
import app.coronawarn.datadonation.services.ppac.ios.verification.errors.ApiTokenAlreadyUsed;
import app.coronawarn.datadonation.services.ppac.ios.verification.errors.ApiTokenExpired;
import app.coronawarn.datadonation.services.ppac.ios.verification.errors.InternalError;
import feign.FeignException;
import java.time.LocalDate;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

@Component
public class ApiTokenService {

  private static final Logger logger = LoggerFactory.getLogger(ApiTokenService.class);
  private final ApiTokenRepository apiTokenRepository;
  private final IosDeviceApiClient iosDeviceApiClient;
  private final JwtProvider jwtProvider;
  private final ApiTokenAuthenticator apiTokenAuthenticator;
  private final PpacIosScenarioValidator ppacIosScenarioValidator;
  private final PpacIosScenarioRepository ppacIosScenarioRepository;

  /**
   * Handles business logic regarding {@link ApiToken}.
   */
  public ApiTokenService(
      ApiTokenRepository apiTokenRepository,
      IosDeviceApiClient iosDeviceApiClient,
      JwtProvider jwtProvider,
      ApiTokenAuthenticator apiTokenAuthenticator,
      PpacIosScenarioValidator ppacIosScenarioValidator,
      PpacIosScenarioRepository ppacIosScenarioRepository) {
    this.apiTokenRepository = apiTokenRepository;
    this.iosDeviceApiClient = iosDeviceApiClient;
    this.jwtProvider = jwtProvider;
    this.apiTokenAuthenticator = apiTokenAuthenticator;
    this.ppacIosScenarioValidator = ppacIosScenarioValidator;
    this.ppacIosScenarioRepository = ppacIosScenarioRepository;
  }

  /**
   * Authenticate an incoming requests against the following constraints. If the provided ApiToken
   * {@link ApiToken} does not exist. Check if the corresponding per-Device Data (if exists) and
   * compares when it was last updated. If equals to the same month this means that the ApiToken was
   * already used this month to update the per-device Data. If not it is safe to update the
   * corresponding per-Device Data. If the provided ApiToken does already exist its expiration data
   * is checked.
   *
   * @param perDeviceDataResponse       per-device Data associated to the ApiToken.
   * @param transactionId               a valid transaction Id.
   * @param ignoreApiTokenAlreadyIssued flag to indicate whether the ApiToken should be validated
   *                                    against the last updated time from the per-device Data.
   * @throws ApiTokenExpired     - in case the ApiToken already expired.
   * @throws ApiTokenAlreadyUsed - in case the ApiToken was already issued this month.
   * @throws InternalError       - in case updating the per-device Data was not successful.
   */
  @Transactional
  public void validate(
      PerDeviceDataResponse perDeviceDataResponse,
      PPACIOS ppacios,
      String transactionId,
      boolean ignoreApiTokenAlreadyIssued,
      PpacIosScenario ppacIosScenario) {
    apiTokenRepository.findById(ppacios.getApiToken()).ifPresentOrElse(
        apiToken -> this.authenticateExistingApiToken(apiToken, ppacIosScenario),
        () -> authenticateNewApiToken(perDeviceDataResponse,
            ppacios,
            transactionId,
            ignoreApiTokenAlreadyIssued,
            ppacIosScenario));
  }

  private void authenticateExistingApiToken(ApiToken apiToken, PpacIosScenario scenario) {
    LocalDate expirationDate = getLocalDateFor(apiToken.getExpirationDate());
    LocalDate now = getLocalDateForNow();
    if (now.isAfter(expirationDate)) {
      throw new ApiTokenExpired();
    }
    scenario.validate(ppacIosScenarioValidator, apiToken);
  }

  private void authenticateNewApiToken(PerDeviceDataResponse perDeviceDataResponse,
      PPACIOS ppacios,
      String transactionId,
      boolean ignoreApiTokenAlreadyIssued,
      PpacIosScenario scenario) {
    apiTokenAuthenticator
        .checkApiTokenAlreadyIssued(perDeviceDataResponse, ignoreApiTokenAlreadyIssued);
    scenario.save(ppacIosScenarioRepository, ppacios.getApiToken());
    updatePerDeviceData(ppacios.getDeviceToken(), transactionId);
  }

  private void updatePerDeviceData(String deviceToken, String transactionId) {
    PerDeviceDataUpdateRequest updateRequest = new PerDeviceDataUpdateRequest(
        deviceToken,
        transactionId,
        getEpochMilliSecondForNow(),
        false,
        false);
    try {
      iosDeviceApiClient.updatePerDeviceData(jwtProvider.generateJwt(), updateRequest);
    } catch (FeignException e) {
      throw new InternalError(e);
    }
  }

}
