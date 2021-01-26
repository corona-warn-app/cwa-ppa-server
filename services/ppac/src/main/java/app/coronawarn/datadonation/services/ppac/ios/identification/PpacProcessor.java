package app.coronawarn.datadonation.services.ppac.ios.identification;

import app.coronawarn.datadonation.common.protocols.SubmissionPayloadIos;
import app.coronawarn.datadonation.services.ppac.ios.client.domain.PerDeviceDataResponse;
import java.util.Optional;
import java.util.UUID;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

@Component
public class PpacProcessor {

  private static final Logger logger = LoggerFactory.getLogger(PpacProcessor.class);
  private final ApiTokenService apiTokenService;
  private final PerDeviceDataValidator perDeviceDataValidator;

  /**
   * Constructor for DataDonationProcessor.
   *
   * @param apiTokenService        apiTokenService for processing Api Tokens.
   * @param perDeviceDataValidator Per-Device Data Validator.
   */
  public PpacProcessor(
      ApiTokenService apiTokenService,
      PerDeviceDataValidator perDeviceDataValidator) {
    this.apiTokenService = apiTokenService;
    this.perDeviceDataValidator = perDeviceDataValidator;
  }

  /**
   * Process a data donation sample.
   *
   * @param submissionPayload the data that is donated for statistical usage..
   */
  public void process(SubmissionPayloadIos submissionPayload) {
    String transactionId = UUID.randomUUID().toString();
    final String deviceToken = submissionPayload.getAuthentication().getDeviceToken();
    final String apiToken = submissionPayload.getAuthentication().getApiToken();
    Optional<PerDeviceDataResponse> perDeviceDataResponse = perDeviceDataValidator
        .validateAndStoreDeviceToken(transactionId, deviceToken);
    apiTokenService.authenticate(perDeviceDataResponse, apiToken, deviceToken, transactionId);
  }
}
