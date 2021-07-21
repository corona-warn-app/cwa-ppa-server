package app.coronawarn.datadonation.services.ppac.ios.verification;

import app.coronawarn.datadonation.common.persistence.domain.ApiToken;
import app.coronawarn.datadonation.common.persistence.domain.DeviceToken;
import app.coronawarn.datadonation.common.protocols.internal.ppdd.PPACIOS;
import app.coronawarn.datadonation.services.ppac.commons.PpacScenario;
import app.coronawarn.datadonation.services.ppac.ios.client.domain.PerDeviceDataResponse;
import app.coronawarn.datadonation.services.ppac.ios.verification.apitoken.ApiTokenService;
import app.coronawarn.datadonation.services.ppac.ios.verification.devicedata.PerDeviceDataValidator;
import java.util.UUID;
import org.springframework.stereotype.Component;

@Component
public class PpacProcessor {

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
   * that the request was indeed coming from a valid CWA client. The first step is to validate the provided ApiToken
   * {@link ApiToken}. Second step is to validate the DeviceToken {@link DeviceToken} against the Apple DeviceCheck API.
   * Valid DeviceToken's are then stored to prevent replay attacks. Last step is to update the corresponding per-Device
   * Data (if existing or creating a new one).
   *
   * @param authentication              authentication object that contains the device token and the API token.
   * @param ignoreApiTokenAlreadyIssued flag to indicate whether the ApiToken should be validated
   *                                    against the last updated time from the per-device Data.
   * @param scenario                    enum that specifies whether validation happens in a EDUS or PPA scenario.
   */
  public void validate(PPACIOS authentication, final boolean ignoreApiTokenAlreadyIssued,
      PpacScenario scenario) {
    apiTokenService.validateLocally(authentication, scenario);
    String transactionId = UUID.randomUUID().toString();
    PerDeviceDataResponse perDeviceDataResponse = perDeviceDataValidator
        .validateAndStoreDeviceToken(transactionId, authentication.getDeviceToken());
    apiTokenService
        .validate(perDeviceDataResponse, authentication, transactionId, ignoreApiTokenAlreadyIssued,
            scenario);
  }
}
