package app.coronawarn.analytics.services.ios.control;

import app.coronawarn.analytics.services.ios.controller.DeviceApiClient;
import app.coronawarn.analytics.services.ios.domain.DeviceData;
import app.coronawarn.analytics.services.ios.domain.DeviceDataUpdateRequest;
import app.coronawarn.analytics.services.ios.exception.ApiTokenAlreadyUsedException;
import app.coronawarn.analytics.services.ios.exception.ApiTokenExpiredException;
import app.coronawarn.analytics.services.ios.exception.EdusAlreadyAccessedException;
import app.coronawarn.analytics.services.ios.utils.TimeUtils;
import app.coronawarn.datadonation.common.persistence.domain.ApiToken;
import app.coronawarn.datadonation.common.persistence.repository.ApiTokenRepository;
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
  private final TimeUtils timeUtils;
  private final DeviceApiClient deviceApiClient;
  private final JwtProvider jwtProvider;

  /**
   * Handles business logic regarding {@link ApiToken}.
   */
  public ApiTokenService(
      ApiTokenRepository apiTokenRepository,
      TimeUtils timeUtils,
      DeviceApiClient deviceApiClient, JwtProvider jwtProvider) {
    this.apiTokenRepository = apiTokenRepository;
    this.timeUtils = timeUtils;
    this.deviceApiClient = deviceApiClient;
    this.jwtProvider = jwtProvider;
  }

  /**
   * Authenticate an incoming requests against the following constraints If the provided ApiToken already exists: -
   * check if the ApiToken is not expired If the provided ApiToken does not exist: - check if the ApiToken was already
   * used this month.
   *
   * @param perDeviceData per-device Data associated to the ApiToken.
   * @param apiToken      the ApiToken to authenticate
   * @param deviceToken   the deviceToken associated with the per-evice Data.
   * @param transactionId a valid transaction Id.
   * @param timestamp     a valid timestamp.
   */
  @Transactional
  public void authenticate(DeviceData perDeviceData, String apiToken, String deviceToken, String transactionId,
      Timestamp timestamp) {
    apiTokenRepository
        .findById(apiToken)
        .ifPresentOrElse(
            this::authenticateExistingApiToken,
            () -> authenticateNewApiToken(perDeviceData,
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

    LocalDate lastDayOfMonth = timeUtils.getLastDayOfMonthFor(OffsetDateTime.now(), ZoneOffset.UTC);
    LocalDate lastUsedEdus = timeUtils.getLocalDateFor(apiToken.getLastUsedEdus(), ZoneOffset.UTC);
    if (lastDayOfMonth.getMonth().equals(lastUsedEdus.getMonth())) {
      throw new EdusAlreadyAccessedException();
    }
  }

  private void authenticateNewApiToken(DeviceData deviceData,
      String apiToken,
      String deviceToken,
      String transactionId,
      Timestamp timestamp) {
    String yearMonth = timeUtils.getCurrentTimeFor(ZoneOffset.UTC, "yyyy-MM");
    String lastUpdated = deviceData.getLastUpdated();

    if (yearMonth.equals(lastUpdated)) {
      throw new ApiTokenAlreadyUsedException();
    }
    createApiToken(apiToken);
    updatePerDeviceData(deviceToken, transactionId, timestamp);
  }

  private void updatePerDeviceData(String deviceToken, String transactionId, Timestamp timestamp) {
    DeviceDataUpdateRequest updateRequest = new DeviceDataUpdateRequest(
        deviceToken,
        transactionId,
        timestamp.getTime(),
        false,
        false);
    deviceApiClient.updatePerDeviceData(jwtProvider.generateJwt(), updateRequest);
  }

  private void createApiToken(String apiToken) {
    OffsetDateTime now = OffsetDateTime.now();
    Long timestamp = timeUtils.getEpochSecondFor(now);
    LocalDate expirationDate = timeUtils.getLastDayOfMonthFor(now, ZoneOffset.UTC);

    apiTokenRepository.insert(apiToken,
        expirationDate,
        timestamp,
        timestamp);
  }
}
