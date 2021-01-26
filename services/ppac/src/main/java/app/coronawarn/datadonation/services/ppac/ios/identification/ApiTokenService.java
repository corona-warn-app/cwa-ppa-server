package app.coronawarn.datadonation.services.ppac.ios.identification;

import app.coronawarn.datadonation.common.persistence.domain.ApiToken;
import app.coronawarn.datadonation.common.persistence.repository.ApiTokenRepository;
import app.coronawarn.datadonation.services.ppac.ios.client.IosDeviceApiClient;
import app.coronawarn.datadonation.services.ppac.ios.client.domain.PerDeviceDataResponse;
import app.coronawarn.datadonation.services.ppac.ios.client.domain.PerDeviceDataUpdateRequest;
import app.coronawarn.datadonation.services.ppac.ios.exception.ApiTokenAlreadyUsedException;
import app.coronawarn.datadonation.services.ppac.ios.exception.ApiTokenExpiredException;
import app.coronawarn.datadonation.services.ppac.ios.exception.EdusAlreadyAccessedException;
import app.coronawarn.datadonation.services.ppac.ios.exception.InternalErrorException;
import app.coronawarn.datadonation.services.ppac.ios.utils.TimeUtils;
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
   * Authenticate an incoming requests against the following constraints If the provided ApiToken already exists: -
   * check if the ApiToken is not expired If the provided ApiToken does not exist: - check if the ApiToken was already
   * used this month.
   *
   * @param perDeviceDataResponse per-device Data associated to the ApiToken.
   * @param apiToken              the ApiToken to authenticate
   * @param deviceToken           the deviceToken associated with the per-device Data.
   * @param transactionId         a valid transaction Id.
   */
  @Transactional
  public void authenticate(
      Optional<PerDeviceDataResponse> perDeviceDataResponse,
      String apiToken,
      String deviceToken,
      String transactionId) {
    apiTokenRepository
        .findById(apiToken)
        .ifPresentOrElse(this::authenticateExistingApiToken,
            () -> authenticateNewApiToken(perDeviceDataResponse,
                apiToken,
                deviceToken,
                transactionId));
  }

  private void authenticateExistingApiToken(ApiToken apiToken) {
    LocalDate expirationDate = TimeUtils.getLocalDateFor(apiToken.getExpirationDate());
    LocalDate now = TimeUtils.getLocalDateForNow();
    if (now.isAfter(expirationDate)) {
      throw new ApiTokenExpiredException();
    }
    LocalDate lastUsedEdus = TimeUtils.getLocalDateFor(apiToken.getLastUsedEdus());
    if (YearMonth.now().equals(YearMonth.from(lastUsedEdus))) {
      throw new EdusAlreadyAccessedException();
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
      throw new ApiTokenAlreadyUsedException();
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
      throw new InternalErrorException(e.contentUTF8());
    }

  }

  private void createApiToken(String apiToken) {

    Long currentTimeStamp = TimeUtils.getEpochSecondForNow();
    Long expirationDate = TimeUtils.getLastDayOfMonthForNow();

    // TODO FR if called in an EDUS then use timstamp otherwise null
    // equivalent the other for PPA
    apiTokenRepository.insert(apiToken,
        expirationDate,
        currentTimeStamp,
        currentTimeStamp,
        currentTimeStamp);
  }
}
