package app.coronawarn.analytics.services.ios.control;


import app.coronawarn.analytics.common.protocols.AnalyticsSubmissionPayloadIOS;
import app.coronawarn.analytics.services.ios.domain.DeviceData;
import java.sql.Timestamp;
import java.util.UUID;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

@Component
public class DataDonationProcessor {


  private final ApiTokenService apiTokenService;
  private final PerDeviceDataValidator perDeviceDataValidator;


  private static final Logger logger = LoggerFactory.getLogger(DataDonationProcessor.class);

  public DataDonationProcessor(ApiTokenService apiTokenService,
      PerDeviceDataValidator perDeviceDataValidator) {
    this.apiTokenService = apiTokenService;
    this.perDeviceDataValidator = perDeviceDataValidator;
  }

  /**
   * Process a data donation sample.
   *
   * @param submissionPayload the data that is donated for statistical usage..
   */
  public void process(AnalyticsSubmissionPayloadIOS submissionPayload) {
    String transactionId = UUID.randomUUID().toString();
    Timestamp timestamp = new Timestamp(System.currentTimeMillis());

    final String deviceToken = submissionPayload.getAuthentication().getDeviceToken();
    final String apiToken = submissionPayload.getAuthentication().getApiToken();
    DeviceData perDeviceData = perDeviceDataValidator.validate(transactionId, timestamp, deviceToken);
    apiTokenService.authenticate(perDeviceData, apiToken, deviceToken, transactionId, timestamp);
  }
}
