package app.coronawarn.datadonation.services.ppac.ios.verification;

import static app.coronawarn.datadonation.common.utils.TimeUtils.getEpochMilliSecondForNow;

import app.coronawarn.datadonation.services.ppac.config.PpacConfiguration;
import app.coronawarn.datadonation.services.ppac.ios.client.IosDeviceApiClient;
import app.coronawarn.datadonation.services.ppac.ios.client.domain.PerDeviceDataQueryRequest;
import app.coronawarn.datadonation.services.ppac.ios.client.domain.PerDeviceDataResponse;
import app.coronawarn.datadonation.services.ppac.ios.verification.devicetoken.DeviceTokenService;
import app.coronawarn.datadonation.services.ppac.ios.verification.errors.DeviceBlocked;
import app.coronawarn.datadonation.services.ppac.ios.verification.errors.DeviceTokenInvalid;
import app.coronawarn.datadonation.services.ppac.ios.verification.errors.DeviceTokenSyntaxError;
import app.coronawarn.datadonation.services.ppac.ios.verification.errors.InternalError;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import feign.FeignException;
import java.util.Optional;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;

@Component
public class PerDeviceDataValidator {

  private static final Logger logger = LoggerFactory.getLogger(PerDeviceDataValidator.class);
  private final IosDeviceApiClient iosDeviceApiClient;
  private final JwtProvider jwtProvider;
  private final DeviceTokenService deviceTokenService;
  private final PpacConfiguration ppacConfiguration;

  /**
   * Constructor for per-device Data validator.
   *
   * @param iosDeviceApiClient instance of the ios device check api client.
   * @param jwtProvider        instance of the bean that generates and signs a valid jwt for the request.
   * @param deviceTokenService instance of the service class to handle device token logic..
   */
  public PerDeviceDataValidator(IosDeviceApiClient iosDeviceApiClient,
      JwtProvider jwtProvider, DeviceTokenService deviceTokenService, PpacConfiguration ppacConfiguration) {
    this.iosDeviceApiClient = iosDeviceApiClient;
    this.jwtProvider = jwtProvider;
    this.deviceTokenService = deviceTokenService;
    this.ppacConfiguration = ppacConfiguration;
  }

  /**
   * Validates the provided device token against the Device Check API.
   *
   * @param transactionId a valid transaction id for this request.
   * @param deviceToken   the device token as identification.
   * @return the per-device data if available.
   * @throws DeviceTokenSyntaxError - in case the DeviceToken is badly formatted or missing
   * @throws InternalError          - in case device validation fails with any different code than 200/400
   * @throws DeviceBlocked          - in case the Device is blocked (which means both bits are in state 1
   * @see <a href="https://developer.apple.com/documentation/devicecheck">DeviceCheck API</a>
   */
  public PerDeviceDataResponse validateAndStoreDeviceToken(String transactionId, String deviceToken) {
    Optional<PerDeviceDataResponse> perDeviceDataResponseOptional;
    Long currentTimeStamp = getEpochMilliSecondForNow();
    String jwt = jwtProvider.generateJwt();
    try {
      ResponseEntity<String> response = iosDeviceApiClient.queryDeviceData(jwt,
          new PerDeviceDataQueryRequest(
              deviceToken,
              transactionId,
              currentTimeStamp));
      perDeviceDataResponseOptional = parsePerDeviceData(response);
    } catch (FeignException.BadRequest e) {
      if (isBadDeviceToken(e.contentUTF8())) {
        throw new DeviceTokenInvalid();
      }
      throw new InternalError(e);
    } catch (FeignException e) {
      throw new InternalError(e);
    }
    deviceTokenService.hashAndStoreDeviceToken(deviceToken, currentTimeStamp);
    perDeviceDataResponseOptional.ifPresent(this::validateDeviceNotBlocked);

    return perDeviceDataResponseOptional.orElse(new PerDeviceDataResponse());
  }

  private boolean isBadDeviceToken(String message) {
    final String missingOrIncorrectlyFormattedDeviceTokenPayload = ppacConfiguration.getIos()
        .getMissingOrIncorrectlyFormattedDeviceTokenPayload();
    return missingOrIncorrectlyFormattedDeviceTokenPayload.toLowerCase().contains(message.toLowerCase());
  }

  private Optional<PerDeviceDataResponse> parsePerDeviceData(ResponseEntity<String> response) {
    ObjectMapper objectMapper = new ObjectMapper();
    Optional<PerDeviceDataResponse> perDeviceDataResponse;
    try {
      perDeviceDataResponse = Optional.of(objectMapper.readValue(response.getBody(), PerDeviceDataResponse.class));
    } catch (JsonProcessingException e) {
      perDeviceDataResponse = Optional.empty();
    }
    return perDeviceDataResponse;
  }

  private void validateDeviceNotBlocked(PerDeviceDataResponse it) {
    if (it.isBit0() && it.isBit1()) {
      throw new DeviceBlocked();
    }
  }
}
