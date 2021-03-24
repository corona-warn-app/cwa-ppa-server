package app.coronawarn.datadonation.services.ppac.ios.verification;

import static app.coronawarn.datadonation.common.utils.TimeUtils.getEpochSecondFor;
import static app.coronawarn.datadonation.common.utils.TimeUtils.getLastDayOfMonthFor;
import static app.coronawarn.datadonation.services.ppac.ios.testdata.TestData.*;
import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import app.coronawarn.datadonation.common.config.UrlConstants;
import app.coronawarn.datadonation.common.persistence.domain.ApiToken;
import app.coronawarn.datadonation.common.persistence.repository.ApiTokenRepository;
import app.coronawarn.datadonation.common.protocols.internal.ppdd.PPADataRequestIOS;
import app.coronawarn.datadonation.services.ppac.commons.web.DataSubmissionResponse;
import app.coronawarn.datadonation.services.ppac.config.PpacConfiguration;
import app.coronawarn.datadonation.services.ppac.ios.client.IosDeviceApiClient;
import app.coronawarn.datadonation.services.ppac.ios.client.domain.PerDeviceDataResponse;
import app.coronawarn.datadonation.services.ppac.logging.PpacErrorCode;
import java.time.OffsetDateTime;
import java.util.Optional;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.boot.test.mock.mockito.SpyBean;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.test.context.ActiveProfiles;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
public class PpacProcessorIntegrationTest {

  private static final String IOS_SERVICE_URL = UrlConstants.IOS + UrlConstants.DATA;
  private static final OffsetDateTime OFFSET_DATE_TIME = OffsetDateTime.parse("2021-10-01T10:00:00+01:00");

  @Autowired
  private TestRestTemplate testRestTemplate;

  @Autowired
  private PpacConfiguration configuration;

  @MockBean
  private IosDeviceApiClient iosDeviceApiClient;

  @MockBean
  private JwtProvider jwtProvider;

  @Autowired
  ApiTokenRepository apiTokenRepository;

  @BeforeEach
  void clearDatabase() {
    apiTokenRepository.deleteAll();
    when(jwtProvider.generateJwt()).thenReturn("jwt");
    PerDeviceDataResponse data = buildIosDeviceData(OFFSET_DATE_TIME, true);
    when(iosDeviceApiClient.queryDeviceData(anyString(), any())).thenReturn(ResponseEntity.ok(jsonify(data)));
  }

  @Test
  public void testApiTokenQuotaExceededShouldNotTriggerAppleCall() {
    String deviceToken = buildBase64String(this.configuration.getIos().getMinDeviceTokenLength() + 1);
    String apiToken = buildUuid();
    OffsetDateTime now = OffsetDateTime.now();
    PerDeviceDataResponse data = buildIosDeviceData(now, true);
    PPADataRequestIOS submissionPayloadIos = buildPPADataRequestIosPayload(apiToken, deviceToken, false);
    Long expirationDate = getLastDayOfMonthFor(now);
    long timestamp = getEpochSecondFor(now);
    apiTokenRepository.insert(apiToken, expirationDate, expirationDate, timestamp, timestamp);

    ResponseEntity<DataSubmissionResponse> response = postSubmission(submissionPayloadIos, testRestTemplate,
        IOS_SERVICE_URL, false);

    assertThat(response.getStatusCode()).isEqualTo(HttpStatus.TOO_MANY_REQUESTS);
    assertThat(response.getBody().getErrorCode()).isEqualTo(PpacErrorCode.API_TOKEN_QUOTA_EXCEEDED);
    verify(iosDeviceApiClient, times(0)).queryDeviceData(anyString(), any());
  }

  @Test
  public void testApiTokenExpiredShouldNotTriggerAppleCall() {
      String deviceToken = buildBase64String(this.configuration.getIos().getMinDeviceTokenLength() + 1);
      String apiToken = buildUuid();
      OffsetDateTime now = OffsetDateTime.now();
      Long expirationDate = getLastDayOfMonthFor(now.minusMonths(1));
      long timestamp = getEpochSecondFor(now);

      apiTokenRepository.insert(apiToken, expirationDate, expirationDate, timestamp, timestamp);
      PPADataRequestIOS submissionPayloadIos = buildPPADataRequestIosPayload(apiToken, deviceToken, false);

      ResponseEntity<DataSubmissionResponse> response = postSubmission(submissionPayloadIos, testRestTemplate,
          IOS_SERVICE_URL, false);

      assertThat(response.getStatusCode()).isEqualTo(HttpStatus.FORBIDDEN);
      assertThat(response.getBody().getErrorCode()).isEqualTo(PpacErrorCode.API_TOKEN_EXPIRED);
      verify(iosDeviceApiClient, times(0)).queryDeviceData(anyString(), any());
    }
}
