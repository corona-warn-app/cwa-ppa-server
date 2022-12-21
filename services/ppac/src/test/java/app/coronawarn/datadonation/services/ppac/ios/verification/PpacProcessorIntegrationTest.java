package app.coronawarn.datadonation.services.ppac.ios.verification;

import static app.coronawarn.datadonation.common.config.UrlConstants.DATA;
import static app.coronawarn.datadonation.common.config.UrlConstants.IOS;
import static app.coronawarn.datadonation.common.utils.TimeUtils.getEpochSecondFor;
import static app.coronawarn.datadonation.common.utils.TimeUtils.getLastDayOfMonthFor;
import static app.coronawarn.datadonation.common.utils.TimeUtils.getLastDayOfMonthForNow;
import static app.coronawarn.datadonation.services.ppac.ios.testdata.TestData.buildBase64String;
import static app.coronawarn.datadonation.services.ppac.ios.testdata.TestData.buildIosDeviceData;
import static app.coronawarn.datadonation.services.ppac.ios.testdata.TestData.buildPPADataRequestIosPayload;
import static app.coronawarn.datadonation.services.ppac.ios.testdata.TestData.buildUuid;
import static app.coronawarn.datadonation.services.ppac.ios.testdata.TestData.jsonify;
import static app.coronawarn.datadonation.services.ppac.ios.testdata.TestData.postErrSubmission;
import static app.coronawarn.datadonation.services.ppac.ios.testdata.TestData.postSubmission;
import static app.coronawarn.datadonation.services.ppac.logging.PpacErrorCode.API_TOKEN_EXPIRED;
import static app.coronawarn.datadonation.services.ppac.logging.PpacErrorCode.API_TOKEN_QUOTA_EXCEEDED;
import static java.time.OffsetDateTime.now;
import static java.time.OffsetDateTime.parse;
import static java.time.ZoneOffset.UTC;
import static java.time.temporal.ChronoUnit.DAYS;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.within;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.boot.test.context.SpringBootTest.WebEnvironment.RANDOM_PORT;
import static org.springframework.http.HttpStatus.FORBIDDEN;
import static org.springframework.http.HttpStatus.TOO_MANY_REQUESTS;

import app.coronawarn.datadonation.common.persistence.domain.ApiTokenData;
import app.coronawarn.datadonation.common.persistence.repository.ApiTokenRepository;
import app.coronawarn.datadonation.common.protocols.internal.ppdd.PPADataRequestIOS;
import app.coronawarn.datadonation.services.ppac.commons.web.DataSubmissionResponse;
import app.coronawarn.datadonation.services.ppac.config.PpacConfiguration;
import app.coronawarn.datadonation.services.ppac.ios.client.IosDeviceApiClient;
import app.coronawarn.datadonation.services.ppac.ios.client.domain.PerDeviceDataResponse;
import feign.FeignException;
import java.time.OffsetDateTime;
import java.util.Optional;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.http.ResponseEntity;

@SpringBootTest(webEnvironment = RANDOM_PORT)
class PpacProcessorIntegrationTest {

  private static final OffsetDateTime OFFSET_DATE_TIME = parse("2021-10-01T10:00:00+01:00");

  @Autowired
  private TestRestTemplate rest;

  @Autowired
  private PpacConfiguration config;

  @MockBean
  private IosDeviceApiClient iosDeviceApiClient;

  @MockBean
  private JwtProvider jwtProvider;

  @Autowired
  ApiTokenRepository apiTokenRepo;

  @BeforeEach
  void clearDatabase() throws Exception {
    apiTokenRepo.deleteAll();
    when(jwtProvider.generateJwt()).thenReturn("jwt");
    final PerDeviceDataResponse data = buildIosDeviceData(OFFSET_DATE_TIME, true);
    when(iosDeviceApiClient.queryDeviceData(anyString(), any())).thenReturn(ResponseEntity.ok(jsonify(data)));
  }

  @Test
  void testApiTokenExpiredShouldNotTriggerAppleCall() {
    final String deviceToken = buildBase64String(config.getIos().getMinDeviceTokenLength() + 1);
    final String apiToken = buildUuid();
    final OffsetDateTime now = now();
    final Long expirationDate = getLastDayOfMonthFor(now.minusMonths(1));
    final long timestamp = getEpochSecondFor(now);

    apiTokenRepo.insert(apiToken, expirationDate, expirationDate, timestamp, timestamp, timestamp);
    final PPADataRequestIOS payload = buildPPADataRequestIosPayload(apiToken, deviceToken, false);

    final ResponseEntity<DataSubmissionResponse> response = postErrSubmission(payload, rest, IOS + DATA, false);

    assertThat(response.getStatusCode()).isEqualTo(FORBIDDEN);
    assertThat(response.getBody()).isNotNull();
    assertThat(response.getBody().getErrorCode()).isEqualTo(API_TOKEN_EXPIRED);
    verify(iosDeviceApiClient, times(0)).queryDeviceData(anyString(), any());
  }

  @Test
  void testApiTokenQuotaExceededShouldNotTriggerAppleCall() {
    final String deviceToken = buildBase64String(config.getIos().getMinDeviceTokenLength() + 1);
    final String apiToken = buildUuid();
    final OffsetDateTime now = now();
    buildIosDeviceData(now, true);
    final PPADataRequestIOS payload = buildPPADataRequestIosPayload(apiToken, deviceToken, false);
    final Long expirationDate = getLastDayOfMonthFor(now);
    final long timestamp = getEpochSecondFor(now);
    apiTokenRepo.insert(apiToken, expirationDate, expirationDate, timestamp, timestamp, timestamp);

    final ResponseEntity<DataSubmissionResponse> response = postErrSubmission(payload, rest, IOS + DATA, false);

    assertThat(response.getStatusCode()).isEqualTo(TOO_MANY_REQUESTS);
    assertThat(response.getBody()).isNotNull();
    assertThat(response.getBody().getErrorCode()).isEqualTo(API_TOKEN_QUOTA_EXCEEDED);
    verify(iosDeviceApiClient, times(0)).queryDeviceData(anyString(), any());
  }

  @Test
  void testExistingApiTokenRollbackAppliedWhenFurtherProcessingFails() throws Exception {
    // given
    // - a valid ApiToken that was created now and was last used for PPA the day before. The Api Token is valid until
    // the end of the current Month.
    final String apiToken = buildUuid();
    final OffsetDateTime now = now(UTC);
    final Long lastDayOfMonthForNow = getLastDayOfMonthForNow();
    final Long createdAt = now.toEpochSecond();
    final long lastUsedForPpa = now.minus(1, DAYS).toEpochSecond();
    apiTokenRepo.insert(apiToken, lastDayOfMonthForNow, createdAt, null, lastUsedForPpa, null);
    // - a device token
    final String deviceToken = buildBase64String(config.getIos().getMinDeviceTokenLength() + 1);
    // - a data submission payload
    final PPADataRequestIOS submissionPayloadIos = buildPPADataRequestIosPayload(apiToken, deviceToken, false);

    // when
    // - checking the device first throw an exception and second return some valid mock data.
    final PerDeviceDataResponse perDeviceDataResponse = buildIosDeviceData(now.minusMonths(1), true);
    when(iosDeviceApiClient.queryDeviceData(anyString(), any())).thenThrow(new RuntimeException())
        .thenReturn(ResponseEntity.ok(jsonify(perDeviceDataResponse)));
    // then
    // - failed submission
    postSubmission(submissionPayloadIos, rest, IOS + DATA, true);
    final Optional<ApiTokenData> optionalApiToken = apiTokenRepo.findById(apiToken);
    // - the api token's last updated timestamp is still the old one
    assertThat(optionalApiToken).isNotEmpty();
    assertThat(optionalApiToken.get().getLastUsedPpac()).isPresent();
    final long value = optionalApiToken.get().getLastUsedPpac().get();
    assertThat(value).isEqualTo(lastUsedForPpa);
    // then
    // - successful submission
    postSubmission(submissionPayloadIos, rest, IOS + DATA, true);
    final Optional<ApiTokenData> optionalApiToken1 = apiTokenRepo.findById(apiToken);
    // - the api token's last updated timestamp is somewhere near the createdAt timestamp.
    assertThat(optionalApiToken1).isNotEmpty();
    assertThat(optionalApiToken1.get().getLastUsedPpac()).isPresent();
    assertThat(optionalApiToken1.get().getLastUsedPpac().get()).isCloseTo(createdAt,
        within(createdAt - lastUsedForPpa));
  }

  @Test
  void testNewApiTokenNotSavedWhenFurtherProcessingFails() throws Exception {
    // given
    // - a valid ApiToken that was created now.
    final String apiToken = buildUuid();
    final OffsetDateTime now = now(UTC);

    // - a device token
    final String deviceToken = buildBase64String(config.getIos().getMinDeviceTokenLength() + 1);
    // - a data submission payload
    final PPADataRequestIOS submissionPayloadIos = buildPPADataRequestIosPayload(apiToken, deviceToken, false);

    // when
    // - checking the device token then return some valid mock data.
    final PerDeviceDataResponse perDeviceDataResponse = buildIosDeviceData(now.minusMonths(1), true);
    when(iosDeviceApiClient.queryDeviceData(anyString(), any()))
        .thenReturn(ResponseEntity.ok(jsonify(perDeviceDataResponse)));
    // - when updating the device data then fail
    when(iosDeviceApiClient.updatePerDeviceData(anyString(), any())).thenThrow(FeignException.class);
    // then
    // - failed submission
    postSubmission(submissionPayloadIos, rest, IOS + DATA, true);
    final Optional<ApiTokenData> optionalApiToken = apiTokenRepo.findById(apiToken);
    // - the api token was not created.
    assertThat(optionalApiToken).isEmpty();
  }
}
