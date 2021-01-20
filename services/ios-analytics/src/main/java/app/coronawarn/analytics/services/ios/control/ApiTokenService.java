package app.coronawarn.analytics.services.ios.control;

import app.coronawarn.analytics.common.persistence.domain.ApiToken;
import app.coronawarn.analytics.common.persistence.repository.ApiTokenRepository;
import app.coronawarn.analytics.services.ios.controller.IosDeviceApiClient;
import app.coronawarn.analytics.services.ios.domain.IosDeviceData;
import app.coronawarn.analytics.services.ios.domain.IosDeviceDataUpdateRequest;
import app.coronawarn.analytics.services.ios.exception.ApiTokenAlreadyUsedException;
import app.coronawarn.analytics.services.ios.exception.ApiTokenExpiredException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.transaction.TransactionManager;
import org.springframework.transaction.annotation.Transactional;

import java.sql.Timestamp;
import java.time.Instant;
import java.time.LocalDateTime;
import java.time.OffsetDateTime;
import java.time.ZoneOffset;
import java.time.temporal.ChronoUnit;


@Component
public class ApiTokenService {

  private final ApiTokenRepository apiTokenRepository;
  private final TimeUtils timeUtils;
  private final IosDeviceApiClient iosDeviceApiClient;

  private static final Logger logger = LoggerFactory.getLogger(ApiTokenService.class);


  public ApiTokenService(ApiTokenRepository apiTokenRepository, TimeUtils timeUtils,
      IosDeviceApiClient iosDeviceApiClient) {
    this.apiTokenRepository = apiTokenRepository;
    this.timeUtils = timeUtils;
    this.iosDeviceApiClient = iosDeviceApiClient;
  }


  @Transactional
  public void authenticate(IosDeviceData perDeviceData, String apiToken, String deviceToken, String transactionId,
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

  private void authenticateNewApiToken(IosDeviceData iosDeviceData,
      String apiToken,
      String deviceToken,
      String transactionId,
      Timestamp timestamp) {
    String yearMonth = timeUtils.getCurrentTimeFor(ZoneOffset.UTC, "yyyy-MM");
    String lastUpdated = iosDeviceData.getLastUpdated();

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
    IosDeviceDataUpdateRequest updateRequest = new IosDeviceDataUpdateRequest(
        deviceToken,
        transactionId,
        timestamp.getTime(),
        false,
        false);
    iosDeviceApiClient.updatePerDeviceData(updateRequest);
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
