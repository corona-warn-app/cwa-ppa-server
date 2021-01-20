package app.coronawarn.analytics.services.ios.control;

import app.coronawarn.analytics.services.ios.controller.IosDeviceApiClient;
import app.coronawarn.analytics.services.ios.domain.IosDeviceData;
import app.coronawarn.analytics.services.ios.domain.IosDeviceDataQueryRequest;
import app.coronawarn.analytics.services.ios.exception.BadDeviceTokenException;
import app.coronawarn.analytics.services.ios.exception.InternalErrorException;
import app.coronawarn.analytics.services.ios.exception.UnauthorizedException;
import feign.FeignException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import java.sql.Timestamp;

@Component
public class PerDeviceDataValidator {

  private final IosDeviceApiClient iosDeviceApiClient;

  private static final Logger logger = LoggerFactory.getLogger(PerDeviceDataValidator.class);

  public PerDeviceDataValidator(IosDeviceApiClient iosDeviceApiClient) {
    this.iosDeviceApiClient = iosDeviceApiClient;
  }

  public IosDeviceData validate(String transactionId, Timestamp timestamp, String deviceToken) {
    try {
      IosDeviceData perDeviceData = iosDeviceApiClient.queryDeviceData(
          new IosDeviceDataQueryRequest(
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
