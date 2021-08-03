package app.coronawarn.datadonation.services.ppac.ios.verification.apitoken;

import static app.coronawarn.datadonation.common.utils.TimeUtils.getEpochMilliSecondForNow;

import app.coronawarn.datadonation.common.persistence.domain.ApiToken;
import app.coronawarn.datadonation.common.persistence.repository.ApiTokenRepository;
import app.coronawarn.datadonation.common.protocols.internal.ppdd.PPACIOS;
import app.coronawarn.datadonation.services.ppac.commons.PpacScenario;
import app.coronawarn.datadonation.services.ppac.ios.client.IosDeviceApiClient;
import app.coronawarn.datadonation.services.ppac.ios.client.domain.PerDeviceDataResponse;
import app.coronawarn.datadonation.services.ppac.ios.client.domain.PerDeviceDataUpdateRequest;
import app.coronawarn.datadonation.services.ppac.ios.verification.JwtProvider;
import app.coronawarn.datadonation.services.ppac.ios.verification.PpacIosScenarioRepository;
import app.coronawarn.datadonation.services.ppac.ios.verification.apitoken.authentication.ApiTokenAuthenticationStrategy;
import app.coronawarn.datadonation.services.ppac.ios.verification.errors.ApiTokenAlreadyUsed;
import app.coronawarn.datadonation.services.ppac.ios.verification.errors.ApiTokenExpired;
import app.coronawarn.datadonation.services.ppac.ios.verification.errors.ApiTokenQuotaExceeded;
import app.coronawarn.datadonation.services.ppac.ios.verification.errors.InternalServerError;
import app.coronawarn.datadonation.services.ppac.ios.verification.scenario.ratelimit.PpacIosRateLimitStrategy;
import feign.FeignException;
import java.util.Optional;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.transaction.annotation.Transactional;

public abstract class ApiTokenService {

  private static final Logger logger = LoggerFactory.getLogger(ApiTokenService.class);

  private final ApiTokenRepository apiTokenRepository;
  private final IosDeviceApiClient iosDeviceApiClient;
  private final JwtProvider jwtProvider;
  private final ApiTokenAuthenticationStrategy apiTokenAuthenticationStrategy;
  private final PpacIosRateLimitStrategy iosScenarioValidator;
  private final PpacIosScenarioRepository ppacIosScenarioRepository;

  /**
   * Handles business logic regarding {@link ApiToken}.
   */
  public ApiTokenService(
      ApiTokenRepository apiTokenRepository,
      IosDeviceApiClient iosDeviceApiClient,
      JwtProvider jwtProvider,
      ApiTokenAuthenticationStrategy apiTokenAuthenticationStrategy,
      PpacIosRateLimitStrategy iosScenarioValidator,
      PpacIosScenarioRepository ppacIosScenarioRepository) {
    this.apiTokenRepository = apiTokenRepository;
    this.iosDeviceApiClient = iosDeviceApiClient;
    this.jwtProvider = jwtProvider;
    this.apiTokenAuthenticationStrategy = apiTokenAuthenticationStrategy;
    this.iosScenarioValidator = iosScenarioValidator;
    this.ppacIosScenarioRepository = ppacIosScenarioRepository;
  }

  /**
   * Authenticate an incoming request against the following constraints. If the provided ApiToken {@link ApiToken} does
   * not exist. Check if the corresponding per-Device Data (if exists) and compares when it was last updated. If equals
   * to the same month this means that the ApiToken was already used this month to update the per-device Data. If not it
   * is safe to update the corresponding per-Device Data.
   *
   * @param perDeviceDataResponse       per-device Data associated to the ApiToken.
   * @param transactionId               a valid transaction Id.
   * @param ignoreApiTokenAlreadyIssued flag to indicate whether the ApiToken should be validated against the last
   *                                    updated time from the per-device Data.
   * @throws ApiTokenAlreadyUsed - in case the ApiToken was already issued this month.
   * @throws InternalServerError - in case updating the per-device Data was not successful.
   */
  @Transactional
  public void validate(
      PerDeviceDataResponse perDeviceDataResponse,
      PPACIOS ppacios,
      String transactionId,
      boolean ignoreApiTokenAlreadyIssued,
      PpacScenario ppacScenario) {
    Optional<ApiToken> apiTokenOptional = apiTokenRepository.findById(ppacios.getApiToken());
    if (!apiTokenOptional.isPresent()) {
      this.authenticateNewApiToken(perDeviceDataResponse,
          ppacios,
          transactionId,
          ignoreApiTokenAlreadyIssued,
          ppacScenario);
    } else {
      ppacScenario.update(ppacIosScenarioRepository, apiTokenOptional.get());
    }
  }

  /**
   * Authenticate an incoming request if the provided ApiToken does already exist. Then its expiration data is checked
   * as well as the rate limit.
   *
   * @param ppacios      the client request.
   * @param ppacScenario the scenario we are currently in.
   * @throws ApiTokenExpired       - in case the ApiToken already expired.
   * @throws ApiTokenQuotaExceeded - in case the client has run into the rate limit.
   */
  public void validateLocally(
      PPACIOS ppacios,
      PpacScenario ppacScenario) {
    Optional<ApiToken> apiTokenOptional = apiTokenRepository.findById(ppacios.getApiToken());
    apiTokenOptional.ifPresent(apiToken -> authenticateExistingApiToken(apiToken, ppacScenario));
  }

  private void authenticateExistingApiToken(ApiToken apiToken, PpacScenario scenario) {
    apiTokenAuthenticationStrategy.checkApiTokenNotAlreadyExpired(apiToken);
    scenario.validate(iosScenarioValidator, apiToken);
  }

  private void authenticateNewApiToken(PerDeviceDataResponse perDeviceDataResponse,
      PPACIOS ppacios,
      String transactionId,
      boolean ignoreApiTokenAlreadyIssued,
      PpacScenario scenario) {
    apiTokenAuthenticationStrategy
        .checkApiTokenAlreadyIssued(perDeviceDataResponse, ignoreApiTokenAlreadyIssued);
    final ApiToken emptyApiToken = ApiTokenBuilder.newBuilder().setApiToken(ppacios.getApiToken()).build();
    scenario.save(ppacIosScenarioRepository, emptyApiToken);
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
      logger.debug("Received Ios API client exception: ", e);
      treatApiClientErrors(e);
    }
  }

  protected abstract void treatApiClientErrors(FeignException e);
}
