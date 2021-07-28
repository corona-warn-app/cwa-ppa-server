package app.coronawarn.datadonation.services.ppac.ios.verification;

import app.coronawarn.datadonation.common.config.UrlConstants;
import app.coronawarn.datadonation.common.persistence.domain.ApiToken;
import app.coronawarn.datadonation.common.persistence.repository.ApiTokenRepository;
import app.coronawarn.datadonation.common.protocols.internal.ppdd.PPADataRequestIOS;
import app.coronawarn.datadonation.common.utils.TimeUtils;
import app.coronawarn.datadonation.services.ppac.commons.web.DataSubmissionResponse;
import app.coronawarn.datadonation.services.ppac.config.PpacConfiguration;
import app.coronawarn.datadonation.services.ppac.ios.client.IosDeviceApiClient;
import app.coronawarn.datadonation.services.ppac.ios.client.domain.PerDeviceDataResponse;
import app.coronawarn.datadonation.services.ppac.logging.PpacErrorCode;
import feign.FeignException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import java.time.OffsetDateTime;
import java.time.ZoneOffset;
import java.time.temporal.ChronoUnit;
import java.util.Optional;

import static app.coronawarn.datadonation.common.utils.TimeUtils.getEpochSecondFor;
import static app.coronawarn.datadonation.common.utils.TimeUtils.getLastDayOfMonthFor;
import static app.coronawarn.datadonation.services.ppac.ios.testdata.TestData.*;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.within;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;

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
  public void testNewApiTokenNotSavedWhenFurtherProcessingFails() {
    // given
    // - a valid ApiToken that was created now.
    String apiToken = buildUuid();
    final OffsetDateTime now = OffsetDateTime.now(ZoneOffset.UTC);

    // - a device token
    String deviceToken = buildBase64String(this.configuration.getIos().getMinDeviceTokenLength() + 1);
    // - a data submission payload
    PPADataRequestIOS submissionPayloadIos = buildPPADataRequestIosPayload(apiToken, deviceToken, false);

    // when
    // - checking the device token then return some valid mock data.
    final PerDeviceDataResponse perDeviceDataResponse = buildIosDeviceData(now.minusMonths(1), true);
    when(iosDeviceApiClient.queryDeviceData(anyString(), any()))
        .thenReturn(ResponseEntity.ok(jsonify(perDeviceDataResponse)));
    // - when updating the device data then fail
    when(iosDeviceApiClient.updatePerDeviceData(anyString(), any()))
        .thenThrow(FeignException.class);
    // then
    // - failed submission
    final ResponseEntity<DataSubmissionResponse> dataSubmissionResponseResponseEntity = postSubmission(
        submissionPayloadIos, testRestTemplate,
        IOS_SERVICE_URL, true);
    Optional<ApiToken> optionalApiToken = apiTokenRepository.findById(apiToken);
    // - the api token was not created.
    assertThat(optionalApiToken).isEmpty();
  }

  @Test
  public void testExistingApiTokenRollbackAppliedWhenFurtherProcessingFails() {
    // given
    // - a valid ApiToken that was created now and was last used for PPA the day before. The Api Token is valid until the end of the current Month.
    String apiToken = buildUuid();
    final OffsetDateTime now = OffsetDateTime.now(ZoneOffset.UTC);
    final Long lastDayOfMonthForNow = TimeUtils.getLastDayOfMonthForNow();
    final Long createdAt = now.toEpochSecond();
    final long lastUsedForPpa = now.minus(1, ChronoUnit.DAYS).toEpochSecond();
    apiTokenRepository.insert(apiToken, lastDayOfMonthForNow, createdAt, null, lastUsedForPpa);
    // - a device token
    String deviceToken = buildBase64String(this.configuration.getIos().getMinDeviceTokenLength() + 1);
    // - a data submission payload
    PPADataRequestIOS submissionPayloadIos = buildPPADataRequestIosPayload(apiToken, deviceToken, false);

    // when
    // - checking the device first throw an exception and second return some valid mock data.
    final PerDeviceDataResponse perDeviceDataResponse = buildIosDeviceData(now.minusMonths(1), true);
    when(iosDeviceApiClient.queryDeviceData(anyString(), any()))
        .thenThrow(new RuntimeException()).thenReturn(ResponseEntity.ok(jsonify(perDeviceDataResponse)));
    // then
    // - failed submission
    postSubmission(submissionPayloadIos, testRestTemplate,
        IOS_SERVICE_URL, true);
    Optional<ApiToken> optionalApiToken = apiTokenRepository.findById(apiToken);
    // - the api token's last updated timestamp is still the old one
    assertThat(optionalApiToken).isNotEmpty();
    assertThat(optionalApiToken.get().getLastUsedPpac()).isPresent();
    assertThat(optionalApiToken.get().getLastUsedPpac().get()).isEqualTo(lastUsedForPpa);
    // then
    // - successful submission
    postSubmission(submissionPayloadIos, testRestTemplate,
        IOS_SERVICE_URL, true);
    Optional<ApiToken> optionalApiToken1 = apiTokenRepository.findById(apiToken);
    // - the api token's last updated timestamp is somewhere near the createdAt timestamp.
    assertThat(optionalApiToken1).isNotEmpty();
    assertThat(optionalApiToken1.get().getLastUsedPpac()).isPresent();
    assertThat(optionalApiToken1.get().getLastUsedPpac().get())
        .isCloseTo(createdAt, within(createdAt - lastUsedForPpa));
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
