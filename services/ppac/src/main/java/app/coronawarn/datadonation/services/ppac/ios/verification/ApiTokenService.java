package app.coronawarn.datadonation.services.ppac.ios.verification;

import app.coronawarn.datadonation.common.persistence.domain.ApiToken;
import app.coronawarn.datadonation.common.persistence.repository.ApiTokenRepository;
import app.coronawarn.datadonation.services.ppac.ios.client.IosDeviceApiClient;
import app.coronawarn.datadonation.services.ppac.ios.client.domain.PerDeviceDataResponse;
import app.coronawarn.datadonation.services.ppac.ios.client.domain.PerDeviceDataUpdateRequest;
import app.coronawarn.datadonation.services.ppac.ios.verification.errors.ApiTokenAlreadyUsed;
import app.coronawarn.datadonation.services.ppac.ios.verification.errors.ApiTokenExpired;
import app.coronawarn.datadonation.services.ppac.ios.verification.errors.InternalError;
import app.coronawarn.datadonation.services.ppac.utils.TimeUtils;
import feign.FeignException;
import java.time.LocalDate;
import java.time.YearMonth;
import java.time.format.DateTimeFormatter;
import java.util.Optional;
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

  /**
   * Handles business logic regarding {@link ApiToken}.
   */
  public ApiTokenService(
      ApiTokenRepository apiTokenRepository,
      IosDeviceApiClient iosDeviceApiClient,
      JwtProvider jwtProvider) {
    this.apiTokenRepository = apiTokenRepository;
    this.iosDeviceApiClient = iosDeviceApiClient;
    this.jwtProvider = jwtProvider;
  }

  /**
   * Authenticate an incoming requests against the following constraints. If the provided ApiToken {@link ApiToken} does
   * not exist. Check if the corresponding per-Device Data (if exists) and compares when it was last updated. If equals
   * to the same month this means that the ApiToken was already used this month to update the per-device Data. If not it
   * is safe to update the corresponding per-Device Data. If the provided ApiToken does already exist its expiration
   * data is checked.
   *
   * @param perDeviceDataResponse per-device Data associated to the ApiToken.
   * @param apiToken              the ApiToken to authenticate
   * @param deviceToken           the deviceToken associated with the per-device Data.
   * @param transactionId         a valid transaction Id.
   * @throws ApiTokenExpired     - in case the ApiToken already expired.
   * @throws ApiTokenAlreadyUsed - in case the ApiToken was already issued this month.
   * @throws InternalError       - in case updating the per-device Data was not successful.
   */
  @Transactional
  public void validate(
      Optional<PerDeviceDataResponse> perDeviceDataResponse,
      String apiToken,
      String deviceToken,
      String transactionId) {
    apiTokenRepository.findById(apiToken).ifPresentOrElse(
        this::authenticateExistingApiToken,
        () -> authenticateNewApiToken(perDeviceDataResponse,
            apiToken,
            deviceToken,
            transactionId));
  }

  private void authenticateExistingApiToken(ApiToken apiToken) {
    LocalDate expirationDate = TimeUtils.getLocalDateFor(apiToken.getExpirationDate());
    LocalDate now = TimeUtils.getLocalDateForNow();
    if (now.isAfter(expirationDate)) {
      throw new ApiTokenExpired();
    }
  }

  private void authenticateNewApiToken(
      Optional<PerDeviceDataResponse> perDeviceDataResponseOptional,
      String apiToken,
      String deviceToken,
      String transactionId) {
    perDeviceDataResponseOptional.ifPresent(this::checkApiTokenAlreadyUsed);
    createApiToken(apiToken);
    updatePerDeviceData(deviceToken, transactionId);
  }

  private void checkApiTokenAlreadyUsed(PerDeviceDataResponse perDeviceDataResponse) {
    final YearMonth lastUpdated = YearMonth.parse(
        perDeviceDataResponse.getLastUpdated(),
        DateTimeFormatter.ofPattern("yyyy-MM"));
    if (YearMonth.now().equals(lastUpdated)) {
      throw new ApiTokenAlreadyUsed();
    }
  }

  private void updatePerDeviceData(String deviceToken, String transactionId) {
    PerDeviceDataUpdateRequest updateRequest = new PerDeviceDataUpdateRequest(
        deviceToken,
        transactionId,
        TimeUtils.getEpochMilliSecondForNow(),
        false,
        false);
    try {
      iosDeviceApiClient.updatePerDeviceData(jwtProvider.generateJwt(), updateRequest);
    } catch (FeignException e) {
      throw new InternalError(e.contentUTF8(), e);
    }
  }

  private void createApiToken(String apiToken) {
    Long currentTimeStamp = TimeUtils.getEpochSecondForNow();
    Long expirationDate = TimeUtils.getLastDayOfMonthForNow();

    try {
      apiTokenRepository.insert(apiToken,
          expirationDate,
          currentTimeStamp,
          null,
          currentTimeStamp);
    } catch (Exception e) {
      throw new InternalError("Internal error occurred while inserting the api token", e);
    }
  }
}
