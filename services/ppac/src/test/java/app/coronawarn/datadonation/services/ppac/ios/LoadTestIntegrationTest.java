package app.coronawarn.datadonation.services.ppac.ios;

import static app.coronawarn.datadonation.common.config.UrlConstants.DATA;
import static app.coronawarn.datadonation.common.config.UrlConstants.IOS;
import static app.coronawarn.datadonation.common.utils.TimeUtils.getEpochSecondFor;
import static app.coronawarn.datadonation.common.utils.TimeUtils.getLastDayOfMonthFor;
import static app.coronawarn.datadonation.services.ppac.ios.testdata.TestData.buildBase64String;
import static app.coronawarn.datadonation.services.ppac.ios.testdata.TestData.buildDeviceToken;
import static app.coronawarn.datadonation.services.ppac.ios.testdata.TestData.buildIosDeviceData;
import static app.coronawarn.datadonation.services.ppac.ios.testdata.TestData.buildPPADataRequestIosPayload;
import static app.coronawarn.datadonation.services.ppac.ios.testdata.TestData.buildUuid;
import static app.coronawarn.datadonation.services.ppac.ios.testdata.TestData.jsonify;
import static app.coronawarn.datadonation.services.ppac.ios.testdata.TestData.postSubmission;
import static java.time.OffsetDateTime.now;
import static java.time.OffsetDateTime.parse;
import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.when;
import static org.springframework.http.HttpStatus.OK;
import static org.springframework.http.ResponseEntity.ok;

import app.coronawarn.datadonation.common.persistence.domain.ApiTokenData;
import app.coronawarn.datadonation.common.persistence.domain.DeviceToken;
import app.coronawarn.datadonation.common.persistence.repository.ApiTokenRepository;
import app.coronawarn.datadonation.common.persistence.repository.DeviceTokenRepository;
import app.coronawarn.datadonation.common.persistence.service.OtpCreationResponse;
import app.coronawarn.datadonation.common.protocols.internal.ppdd.PPADataRequestIOS;
import app.coronawarn.datadonation.services.ppac.config.PpacConfiguration;
import app.coronawarn.datadonation.services.ppac.ios.client.IosDeviceApiClient;
import app.coronawarn.datadonation.services.ppac.ios.client.domain.PerDeviceDataResponse;
import app.coronawarn.datadonation.services.ppac.ios.verification.JwtProvider;
import java.time.OffsetDateTime;
import java.util.Optional;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.http.ResponseEntity;
import org.springframework.test.context.ActiveProfiles;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@ActiveProfiles("loadtest")
class LoadTestIntegrationTest {

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

  @Autowired
  ApiTokenRepository apiTokenRepo;

  @BeforeEach
  void clearDatabase() {
    deviceTokenRepo.deleteAll();
    apiTokenRepo.deleteAll();
    when(jwtProvider.generateJwt()).thenReturn("jwt");
  }

  @Test
  void testSubmitDataShouldNotThrowApiTokenAlreadyExpired() throws Exception {
    // given
    // A valid DeviceToken and an existing ApiToken that is expired last month. While authenticating the
    // existing ApiToken it should not throw an exception if the loadtest profile is active.
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
    final ResponseEntity<OtpCreationResponse> response = postSubmission(payload, rest, IOS + DATA, false);

    // then
    final Optional<ApiTokenData> apiTokenOptional = apiTokenRepo.findById(apiToken);
    assertThat(response.getStatusCode()).isEqualTo(OK);
    assertThat(apiTokenOptional).isPresent();
    assertThat(apiTokenOptional.get().getExpirationDate()).isEqualTo(expirationDate);
    assertThat(response.getBody()).isInstanceOf(OtpCreationResponse.class);
  }

  @Test
  void testSubmitDataShouldNotThrowApiTokenAlreadyUsed() throws Exception {
    // given
    // a valid deviceToken and a new ApiToken. Return per-device Data that was updated right NOW. This means
    // that the per-Device Data was already updated so the ApiToken cannot be used again.
    final String deviceToken = buildBase64String(config.getIos().getMinDeviceTokenLength() + 1);
    final String apiToken = buildUuid();
    final PerDeviceDataResponse data = buildIosDeviceData(now(), true);
    final PPADataRequestIOS payload = buildPPADataRequestIosPayload(apiToken, deviceToken, false);

    // when
    when(iosDeviceApiClient.queryDeviceData(anyString(), any())).thenReturn(ok(jsonify(data)));
    final ResponseEntity<OtpCreationResponse> response = postSubmission(payload, rest, IOS + DATA, false);

    // then
    final Optional<ApiTokenData> optionalApiToken = apiTokenRepo.findById(apiToken);
    assertThat(response.getStatusCode()).isEqualTo(OK);
    assertThat(optionalApiToken).isPresent();
    assertThat(response.getBody()).isInstanceOf(OtpCreationResponse.class);
  }

  @Test
  void testSubmitDataShouldNotThrowApiTokenQuotaExceeded() throws Exception {
    // given
    // A valid device Token and an existing ApiToken. So while authenticating the existing api token we need to check if
    // this api token
    // was already used today for PPA. If so then NORMALLY there would be a API_TOKEN_QUOTA_EXCEEDED but
    // with the loadtest profile this check is skipped
    final String deviceToken = buildBase64String(config.getIos().getMinDeviceTokenLength() + 1);
    final String apiToken = buildUuid();
    final PerDeviceDataResponse data = buildIosDeviceData(now(), true);
    final PPADataRequestIOS payload = buildPPADataRequestIosPayload(apiToken, deviceToken, false);

    final OffsetDateTime now = now();
    final Long expirationDate = getLastDayOfMonthFor(now);
    final long timestamp = getEpochSecondFor(now);

    apiTokenRepo.insert(apiToken, expirationDate, expirationDate, timestamp, null, null);
    // when
    when(iosDeviceApiClient.queryDeviceData(anyString(), any())).thenReturn(ok(jsonify(data)));
    final ResponseEntity<OtpCreationResponse> response = postSubmission(payload, rest, IOS + DATA, false);

    // then
    assertThat(response.getStatusCode()).isEqualTo(OK);
    assertThat(response.getBody()).isInstanceOf(OtpCreationResponse.class);
  }

  @Test
  void testSubmitDataShouldNotThrowDeviceTokenRedeemed() throws Exception {
    // given
    // an existing DeviceTokenHash that would produce a DEVICE_TOKEN_REDEEMED but not when loadprofile is active.
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
    final ResponseEntity<OtpCreationResponse> response = postSubmission(payload, rest, IOS + DATA, false);

    // then
    // The request fails because the device token already exists in the device token hash table
    assertThat(response.getStatusCode()).isEqualTo(OK);
    assertThat(response.getBody()).isInstanceOf(OtpCreationResponse.class);
  }
}
