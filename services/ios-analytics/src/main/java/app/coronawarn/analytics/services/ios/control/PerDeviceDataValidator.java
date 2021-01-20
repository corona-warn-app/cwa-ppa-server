package app.coronawarn.analytics.services.ios.control;

import app.coronawarn.analytics.services.ios.controller.IosDeviceApiClient;
import app.coronawarn.analytics.services.ios.domain.IosDeviceData;
import app.coronawarn.analytics.services.ios.domain.IosDeviceDataQueryRequest;
import app.coronawarn.analytics.services.ios.exception.UnauthorizedException;
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
        IosDeviceData perDeviceData = iosDeviceApiClient.queryDeviceData(
                new IosDeviceDataQueryRequest(
                        deviceToken,
                        transactionId,
                        timestamp.getTime()));
        if (perDeviceData.isBit0() && perDeviceData.isBit1()) {
            String msg = "PPAC failed due to blocked device";
            logger.warn(msg);
            throw new UnauthorizedException(msg);
        }
        return perDeviceData;
    }
}
