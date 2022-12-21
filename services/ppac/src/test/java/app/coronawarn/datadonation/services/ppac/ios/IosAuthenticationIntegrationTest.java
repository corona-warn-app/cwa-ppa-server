package app.coronawarn.datadonation.services.ppac.ios;

import static app.coronawarn.datadonation.common.config.UrlConstants.DATA;
import static app.coronawarn.datadonation.common.config.UrlConstants.IOS;
import static app.coronawarn.datadonation.common.config.UrlConstants.OTP;
import static app.coronawarn.datadonation.common.utils.TimeUtils.getEpochSecondFor;
import static app.coronawarn.datadonation.common.utils.TimeUtils.getEpochSecondsForNow;
import static app.coronawarn.datadonation.common.utils.TimeUtils.getLastDayOfMonthFor;
import static app.coronawarn.datadonation.common.utils.TimeUtils.getLastDayOfMonthForNow;
import static app.coronawarn.datadonation.common.utils.TimeUtils.getLocalDateFor;
import static app.coronawarn.datadonation.services.ppac.ios.testdata.TestData.buildBase64String;
import static app.coronawarn.datadonation.services.ppac.ios.testdata.TestData.buildDeviceToken;
import static app.coronawarn.datadonation.services.ppac.ios.testdata.TestData.buildEdusOneTimePasswordPayload;
import static app.coronawarn.datadonation.services.ppac.ios.testdata.TestData.buildInvalidPPADataRequestIosPayload;
import static app.coronawarn.datadonation.services.ppac.ios.testdata.TestData.buildIosDeviceData;
import static app.coronawarn.datadonation.services.ppac.ios.testdata.TestData.buildPPADataRequestIosPayload;
import static app.coronawarn.datadonation.services.ppac.ios.testdata.TestData.buildUuid;
import static app.coronawarn.datadonation.services.ppac.ios.testdata.TestData.jsonify;
import static app.coronawarn.datadonation.services.ppac.ios.testdata.TestData.postErrSubmission;
import static app.coronawarn.datadonation.services.ppac.ios.testdata.TestData.postErrSurvey;
import static app.coronawarn.datadonation.services.ppac.ios.testdata.TestData.postSubmission;
import static app.coronawarn.datadonation.services.ppac.ios.testdata.TestData.postSurvey;
import static app.coronawarn.datadonation.services.ppac.logging.PpacErrorCode.API_TOKEN_ALREADY_ISSUED;
import static app.coronawarn.datadonation.services.ppac.logging.PpacErrorCode.API_TOKEN_EXPIRED;
import static app.coronawarn.datadonation.services.ppac.logging.PpacErrorCode.API_TOKEN_QUOTA_EXCEEDED;
import static app.coronawarn.datadonation.services.ppac.logging.PpacErrorCode.DEVICE_BLOCKED;
import static app.coronawarn.datadonation.services.ppac.logging.PpacErrorCode.DEVICE_TOKEN_INVALID;
import static app.coronawarn.datadonation.services.ppac.logging.PpacErrorCode.DEVICE_TOKEN_REDEEMED;
import static app.coronawarn.datadonation.services.ppac.logging.PpacErrorCode.DEVICE_TOKEN_SYNTAX_ERROR;
import static java.time.OffsetDateTime.now;
import static java.time.OffsetDateTime.parse;
import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.mockito.ArgumentCaptor.forClass;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.boot.test.context.SpringBootTest.WebEnvironment.RANDOM_PORT;
import static org.springframework.http.HttpStatus.BAD_REQUEST;
import static org.springframework.http.HttpStatus.FORBIDDEN;
import static org.springframework.http.HttpStatus.INTERNAL_SERVER_ERROR;
import static org.springframework.http.HttpStatus.OK;
import static org.springframework.http.HttpStatus.TOO_MANY_REQUESTS;
import static org.springframework.http.HttpStatus.UNAUTHORIZED;
import static org.springframework.http.ResponseEntity.ok;

import app.coronawarn.datadonation.common.persistence.domain.ApiTokenData;
import app.coronawarn.datadonation.common.persistence.domain.DeviceToken;
import app.coronawarn.datadonation.common.persistence.repository.ApiTokenRepository;
import app.coronawarn.datadonation.common.persistence.repository.DeviceTokenRepository;
import app.coronawarn.datadonation.common.persistence.service.OtpCreationResponse;
import app.coronawarn.datadonation.common.protocols.internal.ppdd.EDUSOneTimePasswordRequestIOS;
import app.coronawarn.datadonation.common.protocols.internal.ppdd.PPADataRequestIOS;
import app.coronawarn.datadonation.services.ppac.commons.web.DataSubmissionResponse;
import app.coronawarn.datadonation.services.ppac.config.PpacConfiguration;
import app.coronawarn.datadonation.services.ppac.ios.client.IosDeviceApiClient;
import app.coronawarn.datadonation.services.ppac.ios.client.domain.PerDeviceDataQueryRequest;
import app.coronawarn.datadonation.services.ppac.ios.client.domain.PerDeviceDataResponse;
import app.coronawarn.datadonation.services.ppac.ios.client.domain.PerDeviceDataUpdateRequest;
import app.coronawarn.datadonation.services.ppac.ios.verification.JwtProvider;
import app.coronawarn.datadonation.services.ppac.ios.verification.PpacIosScenarioRepository;
import feign.FeignException;
import feign.Request;
import feign.Request.Body;
import feign.Request.HttpMethod;
import java.time.LocalDate;
import java.time.OffsetDateTime;
import java.time.temporal.TemporalAdjusters;
import java.util.HashMap;
import java.util.Optional;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.boot.test.mock.mockito.SpyBean;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.http.ResponseEntity;

@SpringBootTest(webEnvironment = RANDOM_PORT)
class IosAuthenticationIntegrationTest {

  private static final OffsetDateTime OFFSET_DATE_TIME = parse("2021-10-01T10:00:00+01:00");

  @Autowired
  private TestRestTemplate rest;

  @Autowired
  private DeviceTokenRepository deviceTokenRepo;

  @Autowired
  private PpacConfiguration config;

  @MockBean
  private IosDeviceApiClient iosDeviceApiClient;

  @MockBean
  private JwtProvider jwtProvider;

  @SpyBean
  PpacIosScenarioRepository scenarioRepo;

  @Autowired
  ApiTokenRepository apiTokenRepo;

  private FeignException.BadRequest buildFakeException() {
    return new FeignException.BadRequest("Bad Device Token", buildFakeFeignRequest(), null, null);
  }

  private Request buildFakeFeignRequest() {
    return Request.create(HttpMethod.POST, "", new HashMap<>(), Body.create(""), null);
  }

  @BeforeEach
  void clearDatabase() {
    deviceTokenRepo.deleteAll();
    apiTokenRepo.deleteAll();
    when(jwtProvider.generateJwt()).thenReturn("jwt");
  }

  @Test
  void testSubmitData_apiTokenAlreadyUsed() throws Exception {
    // Toy ios device data that has last update NOW - this will be compared against current server time
    // so this means that someone altered the per device data already this month with an api token.

    // given
    final String deviceToken = buildBase64String(config.getIos().getMinDeviceTokenLength() + 1);
    final String apiToken = buildUuid();
    final PerDeviceDataResponse data = buildIosDeviceData(now(), true);
    final PPADataRequestIOS payload = buildPPADataRequestIosPayload(apiToken, deviceToken, false);

    // when
    when(iosDeviceApiClient.queryDeviceData(anyString(), any())).thenReturn(ok(jsonify(data)));
    final ResponseEntity<DataSubmissionResponse> response = postErrSubmission(payload, rest, IOS + DATA, false);

    // then
    final Optional<ApiTokenData> optionalApiToken = apiTokenRepo.findById(apiToken);
    assertThat(response.getStatusCode()).isEqualTo(FORBIDDEN);
    assertThat(optionalApiToken).isEmpty();
    assertThat(response.getBody()).isNotNull();
    assertThat(response.getBody()).isInstanceOf(DataSubmissionResponse.class);
    assertThat(response.getBody().getErrorCode()).isEqualTo(API_TOKEN_ALREADY_ISSUED);
  }

  @Test
  void testSubmitData_apiTokenExpired() throws Exception {
    // Existing API Token that expired LAST month is compared against current timestamp
    // submission will fail because the API Token expired last month.

    // given
    final String deviceToken = buildBase64String(config.getIos().getMinDeviceTokenLength() + 1);
    final String apiToken = buildUuid();
    final OffsetDateTime now = now();
    final Long expirationDate = getLastDayOfMonthFor(now.minusMonths(1));
    final long timestamp = getEpochSecondFor(now);

    apiTokenRepo.insert(apiToken, expirationDate, expirationDate, timestamp, timestamp, timestamp);
    final PerDeviceDataResponse data = buildIosDeviceData(OFFSET_DATE_TIME, true);
    final PPADataRequestIOS payload = buildPPADataRequestIosPayload(apiToken, deviceToken, false);

    // when
    when(iosDeviceApiClient.queryDeviceData(anyString(), any())).thenReturn(ok(jsonify(data)));
    final ResponseEntity<DataSubmissionResponse> response = postErrSubmission(payload, rest, IOS + DATA, false);

    // then
    final Optional<ApiTokenData> apiTokenOptional = apiTokenRepo.findById(apiToken);
    assertThat(response.getStatusCode()).isEqualTo(FORBIDDEN);
    assertThat(apiTokenOptional).isPresent();
    assertThat(apiTokenOptional.get().getExpirationDate()).isEqualTo(expirationDate);
    assertThat(response.getBody()).isNotNull();
    assertThat(response.getBody()).isInstanceOf(DataSubmissionResponse.class);
    assertThat(response.getBody().getErrorCode()).isEqualTo(API_TOKEN_EXPIRED);
  }

  @Test
  void testSubmitData_authenticateExistingApiToken_successfulPpac() throws Exception {
    final String deviceToken = buildBase64String(config.getIos().getMinDeviceTokenLength() + 1);
    final String apiToken = buildUuid();
    final OffsetDateTime now = now();
    final PerDeviceDataResponse perDeviceDataResponse = buildIosDeviceData(now.minusMonths(1), true);
    final PPADataRequestIOS payload = buildPPADataRequestIosPayload(apiToken, deviceToken, false);
    apiTokenRepo.insert(apiToken, getLastDayOfMonthForNow(), getEpochSecondsForNow(), null, null, null);

    when(iosDeviceApiClient.queryDeviceData(any(), any())).thenReturn(ok(jsonify(perDeviceDataResponse)));

    final ResponseEntity<OtpCreationResponse> responseEntity = postSubmission(payload, rest, IOS + DATA, false);
    final DeviceToken newDeviceToken = buildDeviceToken(payload.getAuthentication().getDeviceToken());
    final Optional<DeviceToken> byDeviceTokenHash = deviceTokenRepo
        .findByDeviceTokenHash(newDeviceToken.getDeviceTokenHash());

    assertThat(responseEntity.getStatusCode()).isEqualTo(OK);
    assertThat(byDeviceTokenHash).isPresent();
  }

  @Test
  void testSubmitData_errorUpdatingPerDevicedata_rollback() throws Exception {
    // Have no API Token YET
    // and a submission that corresponds to per-device data that was last updated last month.
    // Per-Device Data should be updated and a new API Token should be created with expiration set to end of the current
    // month.
    // After exception is thrown the db insertion should be rollbacked. So no API Token will be found.
    final OffsetDateTime now = now();
    final String deviceToken = buildBase64String(config.getIos().getMinDeviceTokenLength() + 1);
    final String apiToken = buildUuid();
    final PerDeviceDataResponse data = buildIosDeviceData(now.minusMonths(1), true);
    final PPADataRequestIOS submissionPayloadIos = buildPPADataRequestIosPayload(apiToken, deviceToken, false);

    // when
    when(iosDeviceApiClient.queryDeviceData(anyString(), any())).thenReturn(ok(jsonify(data)));
    doThrow(FeignException.class).when(iosDeviceApiClient).updatePerDeviceData(anyString(), any());
    postSubmission(submissionPayloadIos, rest, IOS + DATA, false);

    // then
    final Optional<ApiTokenData> optionalApiToken = apiTokenRepo.findById(apiToken);
    assertFalse(optionalApiToken.isPresent());
  }

  @Test
  void testSubmitData_failRetrievingPerDeviceData_internalServerError() {
    // Querying the apple device api returns a statuscode that is not 400 nor 200

    // given
    final String deviceToken = buildBase64String(config.getIos().getMinDeviceTokenLength() + 1);
    final String apiToken = buildUuid();
    final PPADataRequestIOS payload = buildPPADataRequestIosPayload(apiToken, deviceToken, false);

    // when
    when(iosDeviceApiClient.queryDeviceData(anyString(), any())).thenThrow(FeignException.class);
    final ResponseEntity<DataSubmissionResponse> response = postErrSubmission(payload, rest, IOS + DATA, false);

    // then
    assertThat(response.getStatusCode()).isEqualTo(INTERNAL_SERVER_ERROR);
    assertThat(response.getBody()).isNull();
  }

  @Test
  void testSubmitData_failRetrievingPerDeviceData_invalidDeviceToken() {
    // given
    final String deviceToken = buildBase64String(config.getIos().getMinDeviceTokenLength() + 1);
    final String apiToken = buildUuid();
    final FeignException feignException = buildFakeException();

    // when
    when(iosDeviceApiClient.queryDeviceData(anyString(), any())).thenThrow(feignException);
    final PPADataRequestIOS payload = buildPPADataRequestIosPayload(apiToken, deviceToken, false);
    final ResponseEntity<DataSubmissionResponse> response = postErrSubmission(payload, rest, IOS + DATA, false);
    // then
    assertThat(response.getStatusCode()).isEqualTo(BAD_REQUEST);
    assertThat(response.getBody()).isNotNull();
    assertThat(response.getBody()).isInstanceOf(DataSubmissionResponse.class);
    assertThat(response.getBody().getErrorCode()).isEqualTo(DEVICE_TOKEN_INVALID);
  }

  @Test
  void testSubmitData_invalidPayload() {
    final PPADataRequestIOS payload = buildInvalidPPADataRequestIosPayload();
    final ResponseEntity<DataSubmissionResponse> response = postErrSubmission(payload, rest, IOS + DATA, false);

    assertThat(response.getStatusCode()).isEqualTo(BAD_REQUEST);
    assertThat(response.getBody()).isNotNull();
    assertThat(response.getBody()).isInstanceOf(DataSubmissionResponse.class);
    assertThat(response.getBody().getErrorCode()).isEqualTo(DEVICE_TOKEN_SYNTAX_ERROR);
  }

  @Test
  void testSubmitData_invalidPerDeviceData() throws Exception {
    // Toy data contains invalid values for bot0 and bit1 (both have state 1)

    // given
    final String deviceToken = buildBase64String(config.getIos().getMinDeviceTokenLength() + 1);
    final String apiToken = buildUuid();
    final PerDeviceDataResponse data = buildIosDeviceData(OFFSET_DATE_TIME, false);
    final PPADataRequestIOS payload = buildPPADataRequestIosPayload(apiToken, deviceToken, false);

    // when
    when(iosDeviceApiClient.queryDeviceData(anyString(), any())).thenReturn(ok(jsonify(data)));
    when(iosDeviceApiClient.updatePerDeviceData(anyString(), any())).thenReturn(ok().build());
    final ResponseEntity<DataSubmissionResponse> response = postErrSubmission(payload, rest, IOS + DATA, false);

    // when
    assertThat(response.getStatusCode()).isEqualTo(UNAUTHORIZED);
    assertThat(response.getBody()).isNotNull();
    assertThat(response.getBody()).isInstanceOf(DataSubmissionResponse.class);
    assertThat(response.getBody().getErrorCode()).isEqualTo(DEVICE_BLOCKED);
  }

  @Test
  void testSubmitData_storeDeviceTokenHash_uniqueKeyViolation() throws Exception {
    // given
    // Per Device Data that was updated last month
    final String deviceToken = buildBase64String(config.getIos().getMinDeviceTokenLength() + 1);
    final String apiToken = buildUuid();
    final OffsetDateTime now = now();
    final PerDeviceDataResponse data = buildIosDeviceData(now.minusMonths(1), true);
    // And a valid payload
    final PPADataRequestIOS payload = buildPPADataRequestIosPayload(apiToken, deviceToken, false);
    // And an already existing device token
    final DeviceToken newDeviceToken = buildDeviceToken(payload.getAuthentication().getDeviceToken());
    deviceTokenRepo.save(newDeviceToken);

    // when the device api returns per-device data
    when(iosDeviceApiClient.queryDeviceData(anyString(), any())).thenReturn(ok(jsonify(data)));
    when(iosDeviceApiClient.updatePerDeviceData(anyString(), any())).thenReturn(ok().build());
    // And a new payload is sent to the server
    final ResponseEntity<DataSubmissionResponse> response = postErrSubmission(payload, rest, IOS + DATA, false);

    // then
    // The request fails because the device token already exists in the device token hash table
    assertThat(response.getStatusCode()).isEqualTo(FORBIDDEN);
    assertThat(response.getBody()).isNotNull();
    assertThat(response.getBody()).isInstanceOf(DataSubmissionResponse.class);
    assertThat(response.getBody().getErrorCode()).isEqualTo(DEVICE_TOKEN_REDEEMED);
  }

  @Test
  void testSubmitData_successfulSubmission_shouldFailAfterSecondSurvey() throws Exception {
    final String deviceToken = buildBase64String(config.getIos().getMinDeviceTokenLength() + 1);
    final String deviceTokenForSurvey = buildBase64String(config.getIos().getMinDeviceTokenLength() + 2);
    final String apiToken = buildUuid();
    final String otp = buildUuid();
    final OffsetDateTime now = now();
    final PerDeviceDataResponse perDeviceDataResponse = buildIosDeviceData(now.minusMonths(1), true);
    final PPADataRequestIOS dataLoad = buildPPADataRequestIosPayload(apiToken, deviceToken, false);
    final EDUSOneTimePasswordRequestIOS edusLoad = buildEdusOneTimePasswordPayload(apiToken, deviceTokenForSurvey, otp);

    when(iosDeviceApiClient.queryDeviceData(any(), any())).thenReturn(ok(jsonify(perDeviceDataResponse)));

    final ResponseEntity<OtpCreationResponse> responseEntity = postSubmission(dataLoad, rest, IOS + DATA, false);
    final ResponseEntity<OtpCreationResponse> surveyResponseEntity = postSurvey(edusLoad, rest, IOS + OTP, false);
    verify(scenarioRepo, times(1)).saveForPpa(any());
    verify(scenarioRepo, times(1)).updateForEdus(any());

    final DeviceToken newDeviceToken = buildDeviceToken(dataLoad.getAuthentication().getDeviceToken());
    final DeviceToken newSurveyDeviceToken = buildDeviceToken(edusLoad.getAuthentication().getDeviceToken());

    final Optional<DeviceToken> byDeviceTokenHash = deviceTokenRepo
        .findByDeviceTokenHash(newDeviceToken.getDeviceTokenHash());
    final Optional<DeviceToken> surveyDeviceToken = deviceTokenRepo
        .findByDeviceTokenHash(newSurveyDeviceToken.getDeviceTokenHash());
    final Optional<ApiTokenData> apiTokenOptional = apiTokenRepo.findById(apiToken);

    assertThat(responseEntity.getStatusCode()).isEqualTo(OK);
    assertThat(surveyResponseEntity.getStatusCode()).isEqualTo(OK);
    assertThat(byDeviceTokenHash).isPresent();
    assertThat(surveyDeviceToken).isPresent();
    assertThat(apiTokenOptional).isPresent();
    assertThat(apiTokenOptional.get().getApiToken()).isEqualTo(apiToken);
    assertThat(apiTokenOptional.get().getLastUsedEdus()).isPresent();
    assertThat(apiTokenOptional.get().getLastUsedPpac()).isPresent();

    final String secondToken = buildBase64String(config.getIos().getMinDeviceTokenLength() + 3);
    final EDUSOneTimePasswordRequestIOS payload = buildEdusOneTimePasswordPayload(apiToken, secondToken, otp);

    final ResponseEntity<DataSubmissionResponse> errorResponse = postErrSurvey(payload, rest, IOS + OTP, false);
    assertThat(errorResponse.getStatusCode()).isEqualTo(TOO_MANY_REQUESTS);
    assertThat(errorResponse.getBody()).isNotNull();
    assertThat(errorResponse.getBody().getErrorCode()).isEqualTo(API_TOKEN_QUOTA_EXCEEDED);
  }

  @Test
  void testSubmitData_updatePerDeviceData() throws Exception {
    // Have no API Token YET
    // and a submission that correspond to per-device data that was last updated last month
    // Per-Device Data should be updated and a new API Token should be created with expiration set to end of the current
    // month.
    final OffsetDateTime now = now();
    final String deviceToken = buildBase64String(config.getIos().getMinDeviceTokenLength() + 1);
    final String apiToken = buildUuid();
    final PerDeviceDataResponse data = buildIosDeviceData(now.minusMonths(1), true);
    final PPADataRequestIOS payload = buildPPADataRequestIosPayload(apiToken, deviceToken, false);
    final ArgumentCaptor<PerDeviceDataUpdateRequest> deviceTokenArgumentCaptor = forClass(
        PerDeviceDataUpdateRequest.class);
    final ArgumentCaptor<PerDeviceDataQueryRequest> queryRequestArgumentCaptor = forClass(
        PerDeviceDataQueryRequest.class);

    // when
    when(iosDeviceApiClient.queryDeviceData(anyString(), queryRequestArgumentCaptor.capture()))
        .thenReturn(ok(jsonify(data)));
    when(iosDeviceApiClient.updatePerDeviceData(anyString(), deviceTokenArgumentCaptor.capture()))
        .thenReturn(ok().build());
    final ResponseEntity<OtpCreationResponse> response = postSubmission(payload, rest, IOS + DATA, false);

    // then
    final Optional<ApiTokenData> optionalApiToken = apiTokenRepo.findById(apiToken);
    final Optional<DeviceToken> deviceTokenOptional = deviceTokenRepo
        .findByDeviceTokenHash(buildDeviceToken(payload.getAuthentication().getDeviceToken()).getDeviceTokenHash());
    assertThat(deviceTokenOptional).isPresent();
    assertThat(optionalApiToken).isPresent();

    final Long expirationDate = optionalApiToken.get().getExpirationDate();
    final LocalDate localDateFor = getLocalDateFor(expirationDate);
    assertThat(localDateFor).isEqualTo(now().with(TemporalAdjusters.lastDayOfMonth()).toLocalDate());

    assertThat(deviceTokenArgumentCaptor.getValue().isBit0()).isFalse();
    assertThat(deviceTokenArgumentCaptor.getValue().isBit1()).isFalse();
    assertThat(response.getStatusCode()).isEqualTo(OK);
    assertThat(response.getBody()).isInstanceOf(OtpCreationResponse.class);
  }
}
