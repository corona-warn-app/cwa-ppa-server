package app.coronawarn.datadonation.services.ppac.ios.identification;

import app.coronawarn.datadonation.common.persistence.domain.ApiToken;
import app.coronawarn.datadonation.common.persistence.repository.ApiTokenRepository;
import app.coronawarn.datadonation.services.ppac.ios.client.IosDeviceApiClient;
import app.coronawarn.datadonation.services.ppac.ios.client.domain.PerDeviceDataResponse;
import app.coronawarn.datadonation.services.ppac.ios.client.domain.PerDeviceDataUpdateRequest;
import app.coronawarn.datadonation.services.ppac.ios.exception.ApiTokenAlreadyUsedException;
import app.coronawarn.datadonation.services.ppac.ios.exception.ApiTokenExpiredException;
import app.coronawarn.datadonation.services.ppac.ios.exception.EdusAlreadyAccessedException;
import app.coronawarn.datadonation.services.ppac.utils.TimeUtils;
import java.sql.Timestamp;
import java.time.LocalDate;
import java.time.OffsetDateTime;
import java.time.ZoneOffset;
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
      IosDeviceApiClient iosDeviceApiClient, JwtProvider jwtProvider) {
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
   * @param deviceToken           the deviceToken associated with the per-evice Data.
   * @param transactionId         a valid transaction Id.
   * @param timestamp             a valid timestamp.
   */
  @Transactional
  public void authenticate(PerDeviceDataResponse perDeviceDataResponse, String apiToken, String deviceToken,
      String transactionId,
      Timestamp timestamp) {
    apiTokenRepository
        .findById(apiToken)
        .ifPresentOrElse(
            this::authenticateExistingApiToken,
            () -> authenticateNewApiToken(perDeviceDataResponse,
                apiToken,
                deviceToken,
                transactionId,
                timestamp));
  }

  private void authenticateExistingApiToken(ApiToken apiToken) {
    LocalDate now = LocalDate.now();
    if (now.isAfter(apiToken.getExpirationDate())) {
      throw new ApiTokenExpiredException();
    }

    LocalDate lastDayOfMonth = TimeUtils.getLastDayOfMonthFor(OffsetDateTime.now(), ZoneOffset.UTC);
    LocalDate lastUsedEdus = TimeUtils.getLocalDateFor(apiToken.getLastUsedEdus(), ZoneOffset.UTC);
    if (lastDayOfMonth.getMonth().equals(lastUsedEdus.getMonth())) {
      throw new EdusAlreadyAccessedException();
    }
  }

  private void authenticateNewApiToken(PerDeviceDataResponse perDeviceDataResponse,
      String apiToken,
      String deviceToken,
      String transactionId,
      Timestamp timestamp) {
    String yearMonth = TimeUtils.getCurrentTimeFor(ZoneOffset.UTC, "yyyy-MM");
    String lastUpdated = perDeviceDataResponse.getLastUpdated();

    if (yearMonth.equals(lastUpdated)) {
      throw new ApiTokenAlreadyUsedException();
    }
    createApiToken(apiToken);
    updatePerDeviceData(deviceToken, transactionId, timestamp);
  }

  private void updatePerDeviceData(String deviceToken, String transactionId, Timestamp timestamp) {
    PerDeviceDataUpdateRequest updateRequest = new PerDeviceDataUpdateRequest(
        deviceToken,
        transactionId,
        timestamp.getTime(),
        false,
        false);
    iosDeviceApiClient.updatePerDeviceData(jwtProvider.generateJwt(), updateRequest);
  }

  private void createApiToken(String apiToken) {
    OffsetDateTime now = OffsetDateTime.now();
    Long timestamp = TimeUtils.getEpochSecondFor(now);
    LocalDate expirationDate = TimeUtils.getLastDayOfMonthFor(now, ZoneOffset.UTC);

    apiTokenRepository.insert(apiToken,
        expirationDate,
        timestamp,
        timestamp);
  }
}
