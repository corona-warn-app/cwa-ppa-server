package app.coronawarn.datadonation.services.ppac.ios.identification;

import app.coronawarn.datadonation.services.ppac.ios.client.IosDeviceApiClient;
import app.coronawarn.datadonation.services.ppac.ios.client.domain.PerDeviceDataQueryRequest;
import app.coronawarn.datadonation.services.ppac.ios.client.domain.PerDeviceDataResponse;
import app.coronawarn.datadonation.services.ppac.ios.exception.BadDeviceTokenException;
import app.coronawarn.datadonation.services.ppac.ios.exception.InternalErrorException;
import app.coronawarn.datadonation.services.ppac.ios.exception.UnauthorizedException;
import app.coronawarn.datadonation.services.ppac.ios.utils.TimeUtils;
import feign.FeignException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

@Component
public class PerDeviceDataValidator {

  private static final Logger logger = LoggerFactory.getLogger(PerDeviceDataValidator.class);
  private final IosDeviceApiClient iosDeviceApiClient;
  private final JwtProvider jwtProvider;
  private final DeviceTokenService deviceTokenService;
  private final TimeUtils timeUtils;

  /**
   * This is a comment.
   *
   * @param iosDeviceApiClient a parameter.
   * @param jwtProvider        a parameter.
   * @param deviceTokenService a parameter.
   */
  public PerDeviceDataValidator(IosDeviceApiClient iosDeviceApiClient,
      JwtProvider jwtProvider, DeviceTokenService deviceTokenService, TimeUtils timeUtils) {
    this.iosDeviceApiClient = iosDeviceApiClient;
    this.jwtProvider = jwtProvider;
    this.deviceTokenService = deviceTokenService;
    this.timeUtils = timeUtils;
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
  public PerDeviceDataResponse validateAndStoreDeviceToken(String transactionId,
      String deviceToken) {
    try {
      Long currentTimeStamp = timeUtils.getEpochSecondForNow();
      PerDeviceDataResponse perDeviceDataResponse = iosDeviceApiClient.queryDeviceData(jwtProvider.generateJwt(),
          new PerDeviceDataQueryRequest(
              deviceToken,
              transactionId,
              currentTimeStamp));
      deviceTokenService.store(deviceToken,currentTimeStamp);
      if (perDeviceDataResponse.isBit0() && perDeviceDataResponse.isBit1()) {
        throw new UnauthorizedException();
      }
      return perDeviceDataResponse;
    } catch (FeignException.BadRequest e) {
      throw new BadDeviceTokenException();
    } catch (FeignException e) {
      // APPLE API (401,401,403) so need to compare error message
      // TODO FR need to clarify
      throw new InternalErrorException();
    }
  }
}
