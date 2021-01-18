package app.coronawarn.analytics.services.ios.control;


import app.coronawarn.analytics.common.persistence.domain.AnalyticsData;
import app.coronawarn.analytics.common.persistence.domain.ApiToken;
import app.coronawarn.analytics.common.persistence.repository.AnalyticsDataRepository;
import app.coronawarn.analytics.services.ios.controller.AppleDeviceApiClient;
import app.coronawarn.analytics.services.ios.domain.IosDeviceData;
import app.coronawarn.analytics.services.ios.domain.IosDeviceDataQueryRequest;
import app.coronawarn.analytics.services.ios.domain.IosDeviceDataUpdateRequest;
import app.coronawarn.analytics.services.ios.domain.IosDeviceValidationRequest;
import feign.FeignException;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Isolation;
import org.springframework.transaction.annotation.Transactional;

import java.sql.Timestamp;
import java.time.LocalDate;
import java.time.Month;
import java.time.OffsetDateTime;
import java.time.format.DateTimeFormatter;
import java.time.temporal.TemporalAdjuster;
import java.time.temporal.TemporalAdjusters;
import java.util.Optional;
import java.util.UUID;

@Component
public class IosAnalyticsDataProcessor {

    AppleDeviceApiClient appleDeviceApiClient;
    ApiTokenService apiTokenService;
    AnalyticsDataRepository analyticsDataRepository;

    public IosAnalyticsDataProcessor(AppleDeviceApiClient appleDeviceApiClient, ApiTokenService apiTokenService, AnalyticsDataRepository analyticsDataRepository) {
        this.appleDeviceApiClient = appleDeviceApiClient;
        this.apiTokenService = apiTokenService;
        this.analyticsDataRepository = analyticsDataRepository;
    }

    @Transactional(isolation = Isolation.READ_UNCOMMITTED) // TODO FR TBD
    public void process(AnalyticsData submissionPayload, String apiToken) {
        //1. Generate Transaction Id
        String transactionId = UUID.randomUUID().toString();
        Timestamp timestamp = new Timestamp(System.currentTimeMillis());
        try {
            //2. Validate device or get per-device data
            appleDeviceApiClient.validateDevice(new IosDeviceValidationRequest(submissionPayload.getDeviceToken(),
                    transactionId,
                    timestamp.getTime()));

            IosDeviceData perDeviceData = appleDeviceApiClient.getPerDeviceData(
                    new IosDeviceDataQueryRequest(
                            submissionPayload.getDeviceToken(),
                            transactionId,
                            timestamp.getTime()));
            // ---------------- Device Token is valid (Otherwise Feign throws an error) ---------------

            //3. Retrieve API Token metadata TODO FR apiToken = apiTOkenRepo.getApiToken(submissionPayload.getApiToken())
            Optional<ApiToken> apiTokenOptional = apiTokenService.retrieveApiToken(apiToken);

            Month currentMonth = OffsetDateTime.now().getMonth();
            LocalDate lastUpdated = LocalDate.parse(perDeviceData.getLast_update_time(), DateTimeFormatter.ofPattern("yyyy-MM"));
            if (!apiTokenOptional.isPresent()) {
                if( lastUpdated.getMonth() != currentMonth){
                    // if new API TOKEN and Per-Device DATA not UPDATED this month
                    //4. create API Token and metadata
                    ApiToken newApiToken = new ApiToken();
                    newApiToken.setApiToken(apiToken);
                    //4.1 set expiration date to end of month
                    newApiToken.setExpirationDate(OffsetDateTime.now().with(TemporalAdjusters.lastDayOfMonth()));
                    apiTokenService.create(newApiToken);
                    //5. Generate Transaction Id
                    transactionId = UUID.randomUUID().toString();

                    //6. Update per-device Data
                    appleDeviceApiClient.updatePerDeviceData(
                            new IosDeviceDataUpdateRequest(
                                    submissionPayload.getDeviceToken(),
                                    transactionId,
                                    timestamp.getTime(),
                                    perDeviceData.isBit0(),
                                    perDeviceData.isBit1()));

                    //7. Store Analytical Data
                    analyticsDataRepository.save(submissionPayload);
                }else{
                    // NEW API TOKEN AND per-Device-Data updated this month
                    // ------------------ REJECT ------------------------
                }

            } else {

            }


            // if new API TOKEN and NOT expired or USED currentMonth
            //8. Update API token metadata
            //9. Store analytical Data

            // if INVALID API Token
            // Do error processing
        } catch (FeignException e) {
            e.printStackTrace();
        }


        // then error handling
    }
}
