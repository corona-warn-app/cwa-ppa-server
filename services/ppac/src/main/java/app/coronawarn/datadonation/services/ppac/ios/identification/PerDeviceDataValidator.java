package app.coronawarn.datadonation.services.ppac.ios.identification;

import app.coronawarn.datadonation.services.ppac.ios.client.IosDeviceApiClient;
import app.coronawarn.datadonation.services.ppac.ios.client.domain.PerDeviceDataQueryRequest;
import app.coronawarn.datadonation.services.ppac.ios.client.domain.PerDeviceDataResponse;
import app.coronawarn.datadonation.services.ppac.ios.exception.BadDeviceTokenException;
import app.coronawarn.datadonation.services.ppac.ios.exception.InternalErrorException;
import app.coronawarn.datadonation.services.ppac.ios.exception.UnauthorizedException;
import feign.FeignException;
import java.sql.Timestamp;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

@Component
public class PerDeviceDataValidator {

  private static final Logger logger = LoggerFactory.getLogger(PerDeviceDataValidator.class);
  private final IosDeviceApiClient iosDeviceApiClient;
  private final JwtProvider jwtProvider;

  public PerDeviceDataValidator(IosDeviceApiClient iosDeviceApiClient,
      JwtProvider jwtProvider) {
    this.iosDeviceApiClient = iosDeviceApiClient;
    this.jwtProvider = jwtProvider;
  }

  /**
   * Validates the provided device token against the Device Check API.
   *
   * @param transactionId a valid transaction id for this request.
   * @param timestamp     a valid timestamp for this request.
   * @param deviceToken   the device token as identification.
   * @return the per-device data if available.
   * @throws BadDeviceTokenException if the Device Check API returns {@link FeignException.BadRequest}.
   * @throws InternalErrorException  otherwise.
   */
  public PerDeviceDataResponse validate(String transactionId, Timestamp timestamp, String deviceToken) {
    try {
      PerDeviceDataResponse perDeviceDataResponse = iosDeviceApiClient.queryDeviceData(jwtProvider.generateJwt(),
          new PerDeviceDataQueryRequest(
              deviceToken,
              transactionId,
              timestamp.getTime()));
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
