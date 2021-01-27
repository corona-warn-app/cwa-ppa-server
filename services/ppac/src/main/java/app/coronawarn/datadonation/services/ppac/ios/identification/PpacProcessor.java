package app.coronawarn.datadonation.services.ppac.ios.identification;

import app.coronawarn.datadonation.common.persistence.domain.ApiToken;
import app.coronawarn.datadonation.common.persistence.domain.DeviceToken;
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
   * Incoming data submission requests must be validated before further processing. This means that it must be ensured
   * that the request was indeed coming from a valid CWA client.
   * <p>
   * The first step is to validate the DeviceToken {@link DeviceToken} against the Apple DeviceCheck API. Valid
   * DeviceToken's are then stored to prevent replay attacks. Second step is to validate the provided ApiToken {@link
   * ApiToken} and to update the corresponding per-Device Data (if existing or creating a new one).
   *
   * @param submissionPayload the data that is donated for statistical usage..
   */
  public void validate(SubmissionPayloadIos submissionPayload) {
    String transactionId = UUID.randomUUID().toString();
    final String deviceToken = submissionPayload.getAuthentication().getDeviceToken();
    final String apiToken = submissionPayload.getAuthentication().getApiToken();
    Optional<PerDeviceDataResponse> perDeviceDataResponse = perDeviceDataValidator
        .validateAndStoreDeviceToken(transactionId, deviceToken);
    apiTokenService.validate(perDeviceDataResponse, apiToken, deviceToken, transactionId);
  }
}
