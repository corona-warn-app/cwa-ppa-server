package app.coronawarn.analytics.services.ios;

import app.coronawarn.analytics.common.persistence.repository.ApiTokenRepository;
import app.coronawarn.analytics.common.protocols.AnalyticsSubmissionPayloadIOS;
import app.coronawarn.analytics.common.protocols.AuthIOS;
import app.coronawarn.analytics.common.protocols.Metrics;
import app.coronawarn.analytics.services.ios.controller.AppleDeviceApiClient;
import app.coronawarn.analytics.services.ios.domain.IosDeviceData;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.http.*;

import java.time.OffsetDateTime;
import java.time.format.DateTimeFormatter;
import java.time.temporal.ChronoUnit;
import java.time.temporal.TemporalAdjusters;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
public class IosAuthenticationIntegrationTest {

    private static final String IOS_SERVICE_URL = "/version/v1/iOS/data";
    private static final String IOS_SERVICE_PING_URL = "/version/v1/ping/";
    private static final String API_TOKEN = "API_TOKEN";
    private static final String DEVICE_TOKEN = "DEVICE_TOKEN";

    @Autowired
    TestRestTemplate testRestTemplate;

    @Autowired
    ApiTokenRepository apiTokenRepository;

    @MockBean
    private AppleDeviceApiClient appleDeviceApiClient;

    private static final OffsetDateTime NOW = OffsetDateTime.parse("2020-10-01T10:00:00+01:00");


    @Test
    public void test_submitData_blockedDevice() {

        // Create Mock Data
        IosDeviceData data = new IosDeviceData();
        data.setBit0(true);
        data.setBit1(true);
        data.setLast_update_time(NOW.toString());

        Mockito.when(appleDeviceApiClient.queryDeviceData(Mockito.any())).thenReturn(data);

        AuthIOS authIOS = AuthIOS
                .newBuilder()
                .setApiToken(API_TOKEN)
                .setDeviceToken(DEVICE_TOKEN)
                .build();
        Metrics metrics = Metrics.newBuilder()
                .build();
        AnalyticsSubmissionPayloadIOS submissionPayloadIOS = AnalyticsSubmissionPayloadIOS
                .newBuilder()
                .setAuthentication(authIOS)
                .setMetrics(metrics)
                .build();

        HttpHeaders httpHeaders = new HttpHeaders();
        httpHeaders.setContentType(MediaType.valueOf("application/x-protobuf"));

        ResponseEntity<Void> response = testRestTemplate.exchange(IOS_SERVICE_URL, HttpMethod.POST,
                new HttpEntity<>(submissionPayloadIOS), Void.class,
                httpHeaders);
        Assertions.assertThat(response.getStatusCode()).isEqualTo(HttpStatus.UNAUTHORIZED);
    }
}
