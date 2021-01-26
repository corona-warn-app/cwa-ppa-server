package app.coronawarn.datadonation.services.ppac.ios.identification;

import app.coronawarn.datadonation.services.ppac.ios.client.IosDeviceApiClient;
import app.coronawarn.datadonation.services.ppac.ios.client.domain.PerDeviceDataQueryRequest;
import app.coronawarn.datadonation.services.ppac.ios.client.domain.PerDeviceDataResponse;
import app.coronawarn.datadonation.services.ppac.ios.exception.BadDeviceTokenException;
import app.coronawarn.datadonation.services.ppac.ios.exception.InternalErrorException;
import app.coronawarn.datadonation.services.ppac.ios.exception.UnauthorizedException;
import app.coronawarn.datadonation.services.ppac.ios.utils.TimeUtils;
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

  /**
   * This is a comment.
   *
   * @param iosDeviceApiClient a parameter.
   * @param jwtProvider        a parameter.
   * @param deviceTokenService a parameter.
   */
  public PerDeviceDataValidator(IosDeviceApiClient iosDeviceApiClient,
      JwtProvider jwtProvider, DeviceTokenService deviceTokenService) {
    this.iosDeviceApiClient = iosDeviceApiClient;
    this.jwtProvider = jwtProvider;
    this.deviceTokenService = deviceTokenService;
  }

  /**
   * Validates the provided device token against the Device Check API.
   *
   * @param transactionId a valid transaction id for this request.
   * @param deviceToken   the device token as identification.
   * @return the per-device data if available.
   * @throws BadDeviceTokenException if the Device Check API returns {@link FeignException.BadRequest}.
   * @throws InternalErrorException  otherwise.
   */
  public Optional<PerDeviceDataResponse> validateAndStoreDeviceToken(String transactionId,
      String deviceToken) {
    Optional<PerDeviceDataResponse> perDeviceDataResponseOptional;
    Long currentTimeStamp = TimeUtils.getEpochMilliSecondForNow();
    String jwt = jwtProvider.generateJwt();
    try {

      ResponseEntity<String> response = iosDeviceApiClient.queryDeviceData(jwt,
          new PerDeviceDataQueryRequest(
              deviceToken,
              transactionId,
              currentTimeStamp));
      perDeviceDataResponseOptional = parsePerDeviceData(response);
    } catch (FeignException.BadRequest e) {
      throw new BadDeviceTokenException(e.contentUTF8());
    } catch (FeignException e) {
      throw new InternalErrorException(e.contentUTF8());
    }

    if (perDeviceDataResponseOptional.isPresent()) {
      final PerDeviceDataResponse perDeviceDataResponse = perDeviceDataResponseOptional.get();
      deviceTokenService.hashAndStoreDeviceToken(deviceToken, currentTimeStamp);
      if (perDeviceDataResponse.isBit0() && perDeviceDataResponse.isBit1()) {
        throw new UnauthorizedException();
      }
    }
    return perDeviceDataResponseOptional;
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
}
