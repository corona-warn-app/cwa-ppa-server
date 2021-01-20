package app.coronawarn.analytics.services.ios.control;

import app.coronawarn.analytics.common.persistence.domain.ApiToken;
import app.coronawarn.analytics.common.persistence.repository.ApiTokenRepository;
import app.coronawarn.analytics.services.ios.controller.DeviceApiClient;
import app.coronawarn.analytics.services.ios.domain.DeviceData;
import app.coronawarn.analytics.services.ios.domain.DeviceDataUpdateRequest;
import app.coronawarn.analytics.services.ios.exception.ApiTokenAlreadyUsedException;
import app.coronawarn.analytics.services.ios.exception.ApiTokenExpiredException;
import java.sql.Timestamp;
import java.time.LocalDateTime;
import java.time.OffsetDateTime;
import java.time.ZoneOffset;
import java.time.temporal.ChronoUnit;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;


@Component
public class ApiTokenService {

  private final ApiTokenRepository apiTokenRepository;
  private final TimeUtils timeUtils;
  private final DeviceApiClient deviceApiClient;

  private static final Logger logger = LoggerFactory.getLogger(ApiTokenService.class);

  /**
   * TODO FR.
   *
   * @param apiTokenRepository a comment.
   * @param timeUtils          a comment.
   * @param deviceApiClient    a comment.
   */
  public ApiTokenService(
      ApiTokenRepository apiTokenRepository,
      TimeUtils timeUtils,
      DeviceApiClient deviceApiClient) {
    this.apiTokenRepository = apiTokenRepository;
    this.timeUtils = timeUtils;
    this.deviceApiClient = deviceApiClient;
  }

  /**
   * Authenticate TODO FR.
   *
   * @param perDeviceData a comment.
   * @param apiToken      a comment.
   * @param deviceToken   a comment.
   * @param transactionId a comment.
   * @param timestamp     a comment.
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

  private void authenticateExistingApiToken(ApiToken apiToken) {
    LocalDateTime now = LocalDateTime.now();
    if (now.isAfter(apiToken.getExpirationDate())) {
      throw new ApiTokenExpiredException();
    }
    // TODO FR: check rate limit
  }

  private void updatePerDeviceData(String deviceToken, String transactionId, Timestamp timestamp) {
    DeviceDataUpdateRequest updateRequest = new DeviceDataUpdateRequest(
        deviceToken,
        transactionId,
        timestamp.getTime(),
        false,
        false);
    deviceApiClient.updatePerDeviceData(updateRequest);
  }

  private void createApiToken(String apiToken) {
    OffsetDateTime now = OffsetDateTime.now();
    Long timestamp = now.toInstant().getEpochSecond();
    OffsetDateTime expirationDate = timeUtils.getLastDayOfMonthFor(now, ZoneOffset.UTC).truncatedTo(ChronoUnit.DAYS);

    apiTokenRepository.insert(apiToken,
        expirationDate.toLocalDateTime(),
        timestamp,
        timestamp);
  }
}
