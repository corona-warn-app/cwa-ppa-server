package app.coronawarn.datadonation.services.ppac.ios;

import static app.coronawarn.datadonation.services.ppac.utils.TimeUtils.getEpochSecondFor;
import static app.coronawarn.datadonation.services.ppac.utils.TimeUtils.getEpochSecondForNow;
import static app.coronawarn.datadonation.services.ppac.utils.TimeUtils.getLastDayOfMonthFor;
import static app.coronawarn.datadonation.services.ppac.utils.TimeUtils.getLastDayOfMonthForNow;
import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.when;

import app.coronawarn.datadonation.common.config.UrlConstants;
import app.coronawarn.datadonation.common.persistence.domain.ApiToken;
import app.coronawarn.datadonation.common.persistence.domain.DeviceToken;
import app.coronawarn.datadonation.common.persistence.repository.ApiTokenRepository;
import app.coronawarn.datadonation.common.persistence.repository.DeviceTokenRepository;
import app.coronawarn.datadonation.common.protocols.internal.ppdd.PPADataIOS;
import app.coronawarn.datadonation.common.protocols.internal.ppdd.PpaDataRequestIos.PPADataRequestIOS;
import app.coronawarn.datadonation.common.protocols.internal.ppdd.PpacIos.PPACIOS;
import app.coronawarn.datadonation.services.ppac.config.PpacConfiguration;
import app.coronawarn.datadonation.services.ppac.config.TestWebSecurityConfig;
import app.coronawarn.datadonation.services.ppac.ios.client.IosDeviceApiClient;
import app.coronawarn.datadonation.services.ppac.ios.client.domain.PerDeviceDataQueryRequest;
import app.coronawarn.datadonation.services.ppac.ios.client.domain.PerDeviceDataResponse;
import app.coronawarn.datadonation.services.ppac.ios.client.domain.PerDeviceDataUpdateRequest;
import app.coronawarn.datadonation.services.ppac.domain.DataSubmissionResponse;
import app.coronawarn.datadonation.services.ppac.ios.verification.JwtProvider;
import app.coronawarn.datadonation.services.ppac.logging.PpacErrorState;
import feign.FeignException;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.time.OffsetDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Base64;
import java.util.Optional;
import java.util.UUID;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.context.annotation.Import;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.testcontainers.shaded.com.fasterxml.jackson.core.JsonProcessingException;
import org.testcontainers.shaded.com.fasterxml.jackson.databind.ObjectMapper;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@Import(TestWebSecurityConfig.class)
public class IosAuthenticationIntegrationTest {

  private static final String IOS_SERVICE_URL = UrlConstants.IOS + UrlConstants.DATA;
  private static final OffsetDateTime OFFSET_DATE_TIME = OffsetDateTime.parse("2021-10-01T10:00:00+01:00");

  @Autowired
  private TestRestTemplate testRestTemplate;
  @Autowired
  private ApiTokenRepository apiTokenRepository;

  @Autowired
  private DeviceTokenRepository deviceTokenRepository;

  @Autowired
  private PpacConfiguration configuration;

  @MockBean
  private IosDeviceApiClient iosDeviceApiClient;

  @MockBean
  private JwtProvider jwtProvider;

  @BeforeEach
  void clearDatabase() {
    deviceTokenRepository.deleteAll();
    apiTokenRepository.deleteAll();
    when(jwtProvider.generateJwt()).thenReturn("jwt");
  }

  @Test
  public void testSubmitData_authenticateExistingApiToken_successfulPpac() {
    final String deviceToken = buildBase64String(this.configuration.getIos().getMinDeviceTokenLength() + 1);
    final String apiToken = buildUuid();
    final OffsetDateTime now = OffsetDateTime.now();
    final PerDeviceDataResponse perDeviceDataResponse = buildIosDeviceData(now.minusMonths(1), true);
    final PPADataRequestIOS submissionPayloadIos = buildPPADataRequestIosPayload(apiToken, deviceToken);
    apiTokenRepository.insert(apiToken, getLastDayOfMonthForNow(), getEpochSecondForNow(), null, null);

    when(iosDeviceApiClient.queryDeviceData(any(), any()))
        .thenReturn(ResponseEntity.ok(jsonify(perDeviceDataResponse)));

    final ResponseEntity<DataSubmissionResponse> responseEntity = postSubmission(submissionPayloadIos);
    final DeviceToken newDeviceToken = buildDeviceToken(submissionPayloadIos.getAuthentication().getDeviceToken());
    final Optional<DeviceToken> byDeviceTokenHash = deviceTokenRepository
        .findByDeviceTokenHash(newDeviceToken.getDeviceTokenHash());

    assertThat(responseEntity.getStatusCode()).isEqualTo(HttpStatus.NO_CONTENT);
    assertThat(byDeviceTokenHash).isPresent();
  }

  @Test
  public void testSubmitData_invalidPayload() {
    PPADataRequestIOS submissionPayloadIos = buildInvalidPPADataRequestIosPayload();
    ResponseEntity<DataSubmissionResponse> response = postSubmission(submissionPayloadIos);

    assertThat(response.getStatusCode()).isEqualTo(HttpStatus.BAD_REQUEST);
    assertThat(response.getBody()).isNotNull();
    assertThat(response.getBody()).isInstanceOf(DataSubmissionResponse.class);
    assertThat(response.getBody().getPpacIosErrorState()).isEqualTo(PpacErrorState.DEVICE_TOKEN_SYNTAX_ERROR);
  }

  @Test
  public void testSubmitData_storeDeviceTokenHash_uniqueKeyViolation() {
    // given
    // Per Device Data that was updated last month
    String deviceToken = buildBase64String(this.configuration.getIos().getMinDeviceTokenLength() + 1);
    String apiToken = buildUuid();
    OffsetDateTime now = OffsetDateTime.now();
    PerDeviceDataResponse data = buildIosDeviceData(now.minusMonths(1), true);
    // And a valid payload
    PPADataRequestIOS submissionPayloadIos = buildPPADataRequestIosPayload(apiToken, deviceToken);
    // And an already existing device token
    DeviceToken newDeviceToken = buildDeviceToken(submissionPayloadIos.getAuthentication().getDeviceToken());
    deviceTokenRepository.save(newDeviceToken);

    // when the device api returns per-device data
    when(iosDeviceApiClient.queryDeviceData(anyString(), any())).thenReturn(ResponseEntity.ok(jsonify(data)));
    when(iosDeviceApiClient.updatePerDeviceData(anyString(), any())).thenReturn(ResponseEntity.ok().build());
    // And a new payload is sent to the server
    ResponseEntity<DataSubmissionResponse> response = postSubmission(submissionPayloadIos);

    // then
    // The request fails because the device token already exists in the device token hash table
    assertThat(response.getStatusCode()).isEqualTo(HttpStatus.FORBIDDEN);
    assertThat(response.getBody()).isNotNull();
    assertThat(response.getBody()).isInstanceOf(DataSubmissionResponse.class);
    assertThat(response.getBody().getPpacIosErrorState()).isEqualTo(PpacErrorState.DEVICE_TOKEN_REDEEMED);
  }

  @Test
  public void testSubmitData_errorUpdatingPerDevicedata_rollback() {
    // Have no API Token YET
    // and a submission that corresponds to per-device data that was last updated last month.
    // Per-Device Data should be updated and a new API Token should be created with expiration set to end of the current month.
    // After exception is thrown the db insertion should be rollbacked. So no API Token will be found.
    OffsetDateTime now = OffsetDateTime.now();
    String deviceToken = buildBase64String(this.configuration.getIos().getMinDeviceTokenLength() + 1);
    String apiToken = buildUuid();
    PerDeviceDataResponse data = buildIosDeviceData(now.minusMonths(1), true);
    PPADataRequestIOS submissionPayloadIos = buildPPADataRequestIosPayload(apiToken, deviceToken);

    // when
    when(iosDeviceApiClient.queryDeviceData(anyString(), any())).thenReturn(ResponseEntity.ok(jsonify(data)));
    doThrow(FeignException.class).when(iosDeviceApiClient).updatePerDeviceData(anyString(), any());
    postSubmission(submissionPayloadIos);

    // then
    Optional<ApiToken> optionalApiToken = apiTokenRepository.findById(apiToken);
    assertThat(optionalApiToken.isPresent()).isEqualTo(false);
  }

  @Test
  public void testSubmitData_updatePerDeviceData() {
    // Have no API Token YET
    // and a submission that correspond to per-device data that was last updated last month
    // Per-Device Data should be updated and a new API Token should be created with expiration set to end of the current month.
    OffsetDateTime now = OffsetDateTime.now();
    String deviceToken = buildBase64String(this.configuration.getIos().getMinDeviceTokenLength() + 1);
    String apiToken = buildUuid();
    PerDeviceDataResponse data = buildIosDeviceData(now.minusMonths(1), true);
    PPADataRequestIOS submissionPayloadIos = buildPPADataRequestIosPayload(apiToken, deviceToken);
    ArgumentCaptor<PerDeviceDataUpdateRequest> deviceTokenArgumentCaptor = ArgumentCaptor
        .forClass(PerDeviceDataUpdateRequest.class);
    ArgumentCaptor<PerDeviceDataQueryRequest> queryRequestArgumentCaptor = ArgumentCaptor
        .forClass(PerDeviceDataQueryRequest.class);

    // when
    when(iosDeviceApiClient.queryDeviceData(anyString(), queryRequestArgumentCaptor.capture()))
        .thenReturn(ResponseEntity.ok(jsonify(data)));
    when(iosDeviceApiClient.updatePerDeviceData(anyString(), deviceTokenArgumentCaptor.capture()))
        .thenReturn(ResponseEntity.ok().build());
    final ResponseEntity<DataSubmissionResponse> response = postSubmission(submissionPayloadIos);

    // then
    Optional<ApiToken> optionalApiToken = apiTokenRepository.findById(apiToken);

    Optional<DeviceToken> deviceTokenOptional = deviceTokenRepository
        .findByDeviceTokenHash(
            buildDeviceToken(submissionPayloadIos.getAuthentication().getDeviceToken()).getDeviceTokenHash());

    assertThat(deviceTokenOptional).isPresent();
    assertThat(deviceTokenOptional.get().getCreatedAt())
        .isEqualTo(queryRequestArgumentCaptor.getValue().getTimestamp());
    assertThat(optionalApiToken).isPresent();
    assertThat(optionalApiToken.get().getExpirationDate())
        .isEqualTo(getLastDayOfMonthForNow());
    assertThat(deviceTokenArgumentCaptor.getValue().isBit0()).isEqualTo(false);
    assertThat(deviceTokenArgumentCaptor.getValue().isBit1()).isEqualTo(false);
    assertThat(response.getStatusCode()).isEqualTo(HttpStatus.NO_CONTENT);
    assertThat(response.getBody()).isNull();
  }

  @Test
  public void testSubmitData_apiTokenAlreadyUsed() {
    // Toy ios device data that has last update NOW - this will be compared against current server time
    // so this means that someone altered the per device data already this month with an api token.

    // given
    String deviceToken = buildBase64String(this.configuration.getIos().getMinDeviceTokenLength() + 1);
    String apiToken = buildUuid();
    PerDeviceDataResponse data = buildIosDeviceData(OffsetDateTime.now(), true);
    PPADataRequestIOS submissionPayloadIos = buildPPADataRequestIosPayload(apiToken, deviceToken);

    // when
    when(iosDeviceApiClient.queryDeviceData(anyString(), any())).thenReturn(ResponseEntity.ok(jsonify(data)));
    ResponseEntity<DataSubmissionResponse> response = postSubmission(submissionPayloadIos);

    // then
    Optional<ApiToken> optionalApiToken = apiTokenRepository.findById(apiToken);
    assertThat(response.getStatusCode()).isEqualTo(HttpStatus.FORBIDDEN);
    assertThat(optionalApiToken.isPresent()).isEqualTo(false);
    assertThat(response.getBody()).isNotNull();
    assertThat(response.getBody()).isInstanceOf(DataSubmissionResponse.class);
    assertThat(response.getBody().getPpacIosErrorState()).isEqualTo(PpacErrorState.API_TOKEN_ALREADY_ISSUED);
  }

  @Test
  public void testSubmitData_apiTokenExpired() {
    // Existing API Token that expired LAST month is compared against current timestamp
    // submission will fail because the API Token expired last month.

    // given
    String deviceToken = buildBase64String(this.configuration.getIos().getMinDeviceTokenLength() + 1);
    String apiToken = buildUuid();
    OffsetDateTime now = OffsetDateTime.now();
    Long expirationDate = getLastDayOfMonthFor(now.minusMonths(1));
    long timestamp = getEpochSecondFor(now);

    apiTokenRepository.insert(apiToken, expirationDate, expirationDate, timestamp, timestamp);
    PerDeviceDataResponse data = buildIosDeviceData(OFFSET_DATE_TIME, true);
    PPADataRequestIOS submissionPayloadIos = buildPPADataRequestIosPayload(apiToken, deviceToken);

    // when
    when(iosDeviceApiClient.queryDeviceData(anyString(), any())).thenReturn(ResponseEntity.ok(jsonify(data)));
    ResponseEntity<DataSubmissionResponse> response = postSubmission(submissionPayloadIos);

    // then

    Optional<ApiToken> apiTokenOptional = apiTokenRepository.findById(apiToken);
    assertThat(response.getStatusCode()).isEqualTo(HttpStatus.FORBIDDEN);
    assertThat(apiTokenOptional.isPresent()).isEqualTo(true);
    assertThat(apiTokenOptional.get().getExpirationDate()).isEqualTo(expirationDate);
    assertThat(response.getBody()).isNotNull();
    assertThat(response.getBody()).isInstanceOf(DataSubmissionResponse.class);
    assertThat(response.getBody().getPpacIosErrorState()).isEqualTo(PpacErrorState.API_TOKEN_EXPIRED);
  }

  @Test
  public void testSubmitData_failRetrievingPerDeviceData_badRequest() {
    // given
    String deviceToken = buildBase64String(this.configuration.getIos().getMinDeviceTokenLength() + 1);
    String apiToken = buildUuid();

    when(iosDeviceApiClient.queryDeviceData(anyString(), any())).thenThrow(FeignException.BadRequest.class);

    PPADataRequestIOS submissionPayloadIos = buildPPADataRequestIosPayload(apiToken, deviceToken);
    ResponseEntity<DataSubmissionResponse> response = postSubmission(submissionPayloadIos);
    assertThat(response.getStatusCode()).isEqualTo(HttpStatus.BAD_REQUEST);
    assertThat(response.getBody()).isNotNull();
    assertThat(response.getBody()).isInstanceOf(DataSubmissionResponse.class);
    assertThat(response.getBody().getPpacIosErrorState()).isEqualTo(PpacErrorState.DEVICE_TOKEN_SYNTAX_ERROR);

  }

  @Test
  public void testSubmitData_failRetrievingPerDeviceData_internalServerError() {
    // Querying the apple device api returns a statuscode that is not 400 nor 200

    // given
    String deviceToken = buildBase64String(this.configuration.getIos().getMinDeviceTokenLength() + 1);
    String apiToken = buildUuid();
    PPADataRequestIOS submissionPayloadIos = buildPPADataRequestIosPayload(apiToken, deviceToken);

    // when
    when(iosDeviceApiClient.queryDeviceData(anyString(), any())).thenThrow(FeignException.class);
    ResponseEntity<DataSubmissionResponse> response = postSubmission(submissionPayloadIos);

    // then
    assertThat(response.getStatusCode()).isEqualTo(HttpStatus.INTERNAL_SERVER_ERROR);
    assertThat(response.getBody()).isNull();
  }

  @Test
  public void testSubmitData_invalidPerDeviceData() {
    // Toy data contains invalid values for bot0 and bit1 (both have state 1)

    // given
    String deviceToken = buildBase64String(this.configuration.getIos().getMinDeviceTokenLength() + 1);
    String apiToken = buildUuid();
    PerDeviceDataResponse data = buildIosDeviceData(OFFSET_DATE_TIME, false);
    PPADataRequestIOS submissionPayloadIos = buildPPADataRequestIosPayload(apiToken, deviceToken);

    // when
    when(iosDeviceApiClient.queryDeviceData(anyString(), any())).thenReturn(ResponseEntity.ok(jsonify(data)));
    when(iosDeviceApiClient.updatePerDeviceData(anyString(), any())).thenReturn(ResponseEntity.ok().build());
    ResponseEntity<DataSubmissionResponse> response = postSubmission(submissionPayloadIos);

    // when
    assertThat(response.getStatusCode()).isEqualTo(HttpStatus.UNAUTHORIZED);
    assertThat(response.getBody()).isNotNull();
    assertThat(response.getBody()).isInstanceOf(DataSubmissionResponse.class);
    assertThat(response.getBody().getPpacIosErrorState()).isEqualTo(PpacErrorState.DEVICE_BLOCKED);
  }

  private PerDeviceDataResponse buildIosDeviceData(OffsetDateTime lastUpdated, boolean valid) {
    PerDeviceDataResponse data = new PerDeviceDataResponse();
    if (valid) {
      data.setBit0(true);
      data.setBit1(false);
    } else {
      data.setBit0(true);
      data.setBit1(true);
    }
    data.setLastUpdated(lastUpdated.format(DateTimeFormatter.ofPattern("yyyy-MM")));
    return data;
  }

  private ResponseEntity<DataSubmissionResponse> postSubmission(
      PPADataRequestIOS submissionPayloadIos) {

    HttpHeaders httpHeaders = new HttpHeaders();
    httpHeaders.setContentType(MediaType.valueOf("application/x-protobuf"));
    return testRestTemplate.exchange(IOS_SERVICE_URL, HttpMethod.POST,
        new HttpEntity<>(submissionPayloadIos), DataSubmissionResponse.class,
        httpHeaders);

  }

  private PPADataRequestIOS buildInvalidPPADataRequestIosPayload() {
    PPACIOS authIos = PPACIOS.newBuilder().setApiToken("apiToken").setDeviceToken("deviceToken").build();
    PPADataIOS metrics = PPADataIOS.newBuilder().build();
    return PPADataRequestIOS.newBuilder().setAuthentication(authIos).setPayload(metrics).build();

  }

  private PPADataRequestIOS buildPPADataRequestIosPayload(String apiToken, String deviceToken) {
    PPACIOS authIos = PPACIOS.newBuilder().setApiToken(apiToken).setDeviceToken(deviceToken).build();
    PPADataIOS metrics = PPADataIOS.newBuilder().build();
    return PPADataRequestIOS.newBuilder().setAuthentication(authIos).setPayload(metrics).build();
  }

  private String buildUuid() {
    return UUID.randomUUID().toString();
  }

  private String buildBase64String(int length) {
    String key = "thisIsAReallyLongDeviceToken";
    return Base64.getEncoder().encodeToString(key.getBytes(Charset.defaultCharset()))
        .substring(key.length() - length, key.length());
  }

  private String jsonify(PerDeviceDataResponse data) {
    ObjectMapper objectMapper = new ObjectMapper();
    String result = null;
    try {
      result = objectMapper.writeValueAsString(data);
    } catch (JsonProcessingException e) {
      e.printStackTrace();
    }
    return result;
  }

  private DeviceToken buildDeviceToken(String deviceToken) {
    MessageDigest digest = null;
    try {
      digest = MessageDigest.getInstance("SHA-256");
    } catch (NoSuchAlgorithmException e) {
      e.printStackTrace();
    }
    return new DeviceToken(digest.digest(deviceToken.getBytes(StandardCharsets.UTF_8)),
        getEpochSecondForNow());
  }
}
