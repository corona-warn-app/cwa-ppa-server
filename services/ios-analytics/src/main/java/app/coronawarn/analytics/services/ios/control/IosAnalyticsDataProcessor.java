package app.coronawarn.analytics.services.ios.control;


import app.coronawarn.analytics.common.protocols.AnalyticsSubmissionPayloadIOS;
import app.coronawarn.analytics.services.ios.domain.IosDeviceData;
import app.coronawarn.analytics.services.ios.exception.BadDeviceTokenException;
import app.coronawarn.analytics.services.ios.exception.InternalErrorException;
import feign.FeignException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import java.sql.Timestamp;
import java.util.UUID;

@Component
public class IosAnalyticsDataProcessor {


  private final ApiTokenService apiTokenService;
  private final PerDeviceDataValidator perDeviceDataValidator;


  private static final Logger logger = LoggerFactory.getLogger(IosAnalyticsDataProcessor.class);

  public IosAnalyticsDataProcessor(ApiTokenService apiTokenService,
      PerDeviceDataValidator perDeviceDataValidator) {

    this.apiTokenService = apiTokenService;
    this.perDeviceDataValidator = perDeviceDataValidator;
  }

  public void process(AnalyticsSubmissionPayloadIOS submissionPayload) {
    String transactionId = UUID.randomUUID().toString();
    Timestamp timestamp = new Timestamp(System.currentTimeMillis());

    final String deviceToken = submissionPayload.getAuthentication().getDeviceToken();
    final String apiToken = submissionPayload.getAuthentication().getApiToken();
    IosDeviceData perDeviceData = perDeviceDataValidator.validate(transactionId, timestamp, deviceToken);
    apiTokenService.authenticate(perDeviceData, apiToken, deviceToken, transactionId, timestamp);
  }
}
