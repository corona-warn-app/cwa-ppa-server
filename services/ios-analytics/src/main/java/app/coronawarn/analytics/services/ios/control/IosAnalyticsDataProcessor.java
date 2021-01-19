package app.coronawarn.analytics.services.ios.control;


import app.coronawarn.analytics.common.persistence.domain.ApiToken;
import app.coronawarn.analytics.common.persistence.repository.AnalyticsDataRepository;
import app.coronawarn.analytics.common.persistence.repository.ApiTokenRepository;
import app.coronawarn.analytics.common.protocols.AnalyticsSubmissionPayloadIOS;
import app.coronawarn.analytics.services.ios.controller.AppleDeviceApiClient;
import app.coronawarn.analytics.services.ios.domain.IosDeviceData;
import app.coronawarn.analytics.services.ios.domain.IosDeviceDataQueryRequest;
import app.coronawarn.analytics.services.ios.domain.IosDeviceDataUpdateRequest;
import app.coronawarn.analytics.services.ios.exception.BlockedDeviceException;
import feign.FeignException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import java.sql.Timestamp;
import java.time.*;
import java.time.format.DateTimeFormatter;
import java.time.temporal.ChronoUnit;
import java.time.temporal.TemporalAdjusters;
import java.util.Optional;
import java.util.UUID;

@Component
public class IosAnalyticsDataProcessor {

    AppleDeviceApiClient appleDeviceApiClient;
    ApiTokenService apiTokenService;
    AnalyticsDataRepository analyticsDataRepository;
    ApiTokenRepository apiTokenRepository;


    private static final Logger logger = LoggerFactory.getLogger(IosAnalyticsDataProcessor.class);

    public IosAnalyticsDataProcessor(AppleDeviceApiClient appleDeviceApiClient,
                                     ApiTokenService apiTokenService,
                                     AnalyticsDataRepository analyticsDataRepository,
                                     ApiTokenRepository apiTokenRepository) {
        this.appleDeviceApiClient = appleDeviceApiClient;
        this.apiTokenService = apiTokenService;
        this.analyticsDataRepository = analyticsDataRepository;
        this.apiTokenRepository = apiTokenRepository;
    }

    public void process(AnalyticsSubmissionPayloadIOS submissionPayload) {
        //1. Generate Transaction Id
        String transactionId = UUID.randomUUID().toString();
        Timestamp timestamp = new Timestamp(System.currentTimeMillis());
        LocalDateTime now = LocalDateTime.now();
        try {
            // 1. STEP: Retrieve per-device data
            final String deviceToken = submissionPayload.getAuthentication().getDeviceToken();
            final String apiToken = submissionPayload.getAuthentication().getApiToken();


            // Assumption: Feign will only continue if the status code is 2xx
            IosDeviceData perDeviceData = appleDeviceApiClient.queryDeviceData(
                    new IosDeviceDataQueryRequest(
                            deviceToken,
                            transactionId,
                            timestamp.getTime()));
            // ---------------- Device Token is valid (Otherwise Feign throws an error) ---------------

            // 2. STEP: Check per-device data
            if (perDeviceData.isBit0() && perDeviceData.isBit1()) {
                // if bit0 and bit1 are in state 1 the device is blocked
                // return 404 and log as warning
                String msg = "PPAC failed due to blocked device";
                logger.warn(msg);
                throw new BlockedDeviceException(msg);
            }

            // 3. STEP: Read API Token from DB
            Optional<ApiToken> apiTokenOptional = apiTokenRepository.findById(apiToken);
            if (apiTokenOptional.isEmpty()) {
                // 3.1 if API Token does NOT exist

                // 3.1.1 Determine year-month of the server
                String yearMonth = OffsetDateTime
                        .now()
                        .atZoneSameInstant(ZoneOffset.UTC)
                        .format(DateTimeFormatter
                                .ofPattern("yyyy-MM"));
                // 3.1.2 Check last Update Time of Per-Device Data
                String lastUpdated = perDeviceData.getLast_update_time();
                if (yearMonth.equals(lastUpdated)) {
                    // 3.1.2.1 In this case the API-Token was already been issued in the current month
                    // return 403
                    String msg = "PPAC failed due to API Token already issued this month";
                    logger.warn(msg);
                    throw new RuntimeException(msg);
                }
                // 3.1.3 Store API Token
                OffsetDateTime expirationDate = OffsetDateTime.now()
                        .withOffsetSameLocal(ZoneOffset.UTC)
                        .with(TemporalAdjusters.lastDayOfMonth());
                apiTokenRepository.insert(apiToken,
                        expirationDate.toLocalDateTime(),
                        expirationDate.toLocalDate(),
                        expirationDate.toLocalDate());

                // 3.1.4 Update per-device Data
                // TODO:
            } else {
                // 3.2 If the API Token exists
                ApiToken existingApiToken = apiTokenOptional.get();
                // 3.2.1 Check expiration Date
                if (now.isAfter(existingApiToken.getExpirationDate())) {
                    String msg = "PPAC failed du to expiration date";
                    logger.error(msg);
                    throw new RuntimeException(msg);
                }
                // 3.2.2 Check Rate Limit
                // TODO:
            }
        } catch (FeignException e) {
            e.printStackTrace();
        }

    }
}
