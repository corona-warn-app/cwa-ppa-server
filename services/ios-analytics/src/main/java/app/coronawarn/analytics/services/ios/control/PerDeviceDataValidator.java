package app.coronawarn.analytics.services.ios.control;

import app.coronawarn.analytics.services.ios.controller.DeviceApiClient;
import app.coronawarn.analytics.services.ios.domain.DeviceData;
import app.coronawarn.analytics.services.ios.domain.DeviceDataQueryRequest;
import app.coronawarn.analytics.services.ios.exception.BadDeviceTokenException;
import app.coronawarn.analytics.services.ios.exception.InternalErrorException;
import app.coronawarn.analytics.services.ios.exception.UnauthorizedException;
import feign.FeignException;
import java.sql.Timestamp;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

@Component
public class PerDeviceDataValidator {

  private final DeviceApiClient deviceApiClient;

  private static final Logger logger = LoggerFactory.getLogger(PerDeviceDataValidator.class);

  public PerDeviceDataValidator(DeviceApiClient deviceApiClient) {
    this.deviceApiClient = deviceApiClient;
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
  public DeviceData validate(String transactionId, Timestamp timestamp, String deviceToken) {
    try {
      DeviceData perDeviceData = deviceApiClient.queryDeviceData(
          new DeviceDataQueryRequest(
              deviceToken,
              transactionId,
              timestamp.getTime()));
      if (perDeviceData.isBit0() && perDeviceData.isBit1()) {
        throw new UnauthorizedException();
      }
      return perDeviceData;
    } catch (FeignException.BadRequest e) {
      throw new BadDeviceTokenException();
    } catch (FeignException e) {
      // APPLE API (401,401,403) so need to compare error message
      // TODO FR need to clarify
      throw new InternalErrorException();
    }
  }
}
