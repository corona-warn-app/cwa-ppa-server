package app.coronawarn.datadonation.services.ppac.ios;

import static app.coronawarn.datadonation.common.utils.TimeUtils.getEpochSecondFor;
import static app.coronawarn.datadonation.common.utils.TimeUtils.getLastDayOfMonthFor;
import static app.coronawarn.datadonation.services.ppac.ios.testdata.TestData.buildBase64String;
import static app.coronawarn.datadonation.services.ppac.ios.testdata.TestData.buildDeviceToken;
import static app.coronawarn.datadonation.services.ppac.ios.testdata.TestData.buildIosDeviceData;
import static app.coronawarn.datadonation.services.ppac.ios.testdata.TestData.buildPPADataRequestIosPayload;
import static app.coronawarn.datadonation.services.ppac.ios.testdata.TestData.buildUuid;
import static app.coronawarn.datadonation.services.ppac.ios.testdata.TestData.jsonify;
import static app.coronawarn.datadonation.services.ppac.ios.testdata.TestData.postSubmission;
import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.when;

import app.coronawarn.datadonation.common.config.UrlConstants;
import app.coronawarn.datadonation.common.persistence.domain.ApiTokenData;
import app.coronawarn.datadonation.common.persistence.domain.DeviceToken;
import app.coronawarn.datadonation.common.persistence.repository.ApiTokenRepository;
import app.coronawarn.datadonation.common.persistence.repository.DeviceTokenRepository;
import app.coronawarn.datadonation.common.protocols.internal.ppdd.PPADataRequestIOS;
import app.coronawarn.datadonation.services.ppac.commons.web.DataSubmissionResponse;
import app.coronawarn.datadonation.services.ppac.config.PpacConfiguration;
import app.coronawarn.datadonation.services.ppac.ios.client.IosDeviceApiClient;
import app.coronawarn.datadonation.services.ppac.ios.client.domain.PerDeviceDataResponse;
import app.coronawarn.datadonation.services.ppac.ios.verification.JwtProvider;
import app.coronawarn.datadonation.services.ppac.ios.verification.devicetoken.DeviceTokenRedemptionStrategy;
import java.time.OffsetDateTime;
import java.util.Optional;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.test.context.ActiveProfiles;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@ActiveProfiles("loadtest")
public class LoadTestIntegrationTest {

  private static final String IOS_SERVICE_URL = UrlConstants.IOS + UrlConstants.DATA;
  private static final OffsetDateTime OFFSET_DATE_TIME = OffsetDateTime.parse("2021-10-01T10:00:00+01:00");

  @Autowired
  private TestRestTemplate testRestTemplate;

  @Autowired
  private DeviceTokenRepository deviceTokenRepository;

  @Autowired
  private PpacConfiguration configuration;

  @Autowired
  private DeviceTokenRedemptionStrategy redemptionStrategy;

  @MockBean
  private IosDeviceApiClient iosDeviceApiClient;

  @MockBean
  private JwtProvider jwtProvider;

  @Autowired
  ApiTokenRepository apiTokenRepository;

  @BeforeEach
  void clearDatabase() {
    deviceTokenRepository.deleteAll();
    apiTokenRepository.deleteAll();
    when(jwtProvider.generateJwt()).thenReturn("jwt");
  }

  @Test
  public void testSubmitDataShouldNotThrowDeviceTokenRedeemed() {
    // given
    // an existing DeviceTokenHash that would produce a DEVICE_TOKEN_REDEEMED but not when loadprofile is active.
    String deviceToken = buildBase64String(this.configuration.getIos().getMinDeviceTokenLength() + 1);
    String apiToken = buildUuid();
    OffsetDateTime now = OffsetDateTime.now();
    PerDeviceDataResponse data = buildIosDeviceData(now.minusMonths(1), true);
    // And a valid payload
    PPADataRequestIOS submissionPayloadIos = buildPPADataRequestIosPayload(apiToken, deviceToken, false);
    // And an already existing device token
    DeviceToken newDeviceToken = buildDeviceToken(submissionPayloadIos.getAuthentication().getDeviceToken());
    deviceTokenRepository.save(newDeviceToken);

    // when the device api returns per-device data
    when(iosDeviceApiClient.queryDeviceData(anyString(), any())).thenReturn(ResponseEntity.ok(jsonify(data)));
    when(iosDeviceApiClient.updatePerDeviceData(anyString(), any())).thenReturn(ResponseEntity.ok().build());
    // And a new payload is sent to the server
    ResponseEntity<DataSubmissionResponse> response = postSubmission(submissionPayloadIos, testRestTemplate,
        IOS_SERVICE_URL, false);

    // then
    // The request fails because the device token already exists in the device token hash table
    assertThat(response.getStatusCode()).isEqualTo(HttpStatus.NO_CONTENT);
    assertThat(response.getBody()).isNull();
  }

  @Test
  public void testSubmitDataShouldNotThrowApiTokenAlreadyExpired() {
    // given
    // A valid DeviceToken and an existing ApiToken that is expired last month. While authenticating the
    // existing ApiToken it should not throw an exception if the loadtest profile is active.
    String deviceToken = buildBase64String(this.configuration.getIos().getMinDeviceTokenLength() + 1);
    String apiToken = buildUuid();
    OffsetDateTime now = OffsetDateTime.now();
    Long expirationDate = getLastDayOfMonthFor(now.minusMonths(1));
    long timestamp = getEpochSecondFor(now);

    apiTokenRepository.insert(apiToken, expirationDate, expirationDate, timestamp, timestamp);
    PerDeviceDataResponse data = buildIosDeviceData(OFFSET_DATE_TIME, true);
    PPADataRequestIOS submissionPayloadIos = buildPPADataRequestIosPayload(apiToken, deviceToken, false);

    // when
    when(iosDeviceApiClient.queryDeviceData(anyString(), any())).thenReturn(ResponseEntity.ok(jsonify(data)));
    ResponseEntity<DataSubmissionResponse> response = postSubmission(submissionPayloadIos, testRestTemplate,
        IOS_SERVICE_URL, false);

    // then
    Optional<ApiTokenData> apiTokenOptional = apiTokenRepository.findById(apiToken);
    assertThat(response.getStatusCode()).isEqualTo(HttpStatus.NO_CONTENT);
    assertThat(apiTokenOptional.isPresent()).isEqualTo(true);
    assertThat(apiTokenOptional.get().getExpirationDate()).isEqualTo(expirationDate);
    assertThat(response.getBody()).isNull();
  }

  @Test
  public void testSubmitDataShouldNotThrowApiTokenAlreadyUsed() {
    // given
    // a valid deviceToken and a new ApiToken. Return per-device Data that was updated right NOW. This means
    // that the per-Device Data was already updated so the ApiToken cannot be used again.
    String deviceToken = buildBase64String(this.configuration.getIos().getMinDeviceTokenLength() + 1);
    String apiToken = buildUuid();
    PerDeviceDataResponse data = buildIosDeviceData(OffsetDateTime.now(), true);
    PPADataRequestIOS submissionPayloadIos = buildPPADataRequestIosPayload(apiToken, deviceToken, false);

    // when
    when(iosDeviceApiClient.queryDeviceData(anyString(), any())).thenReturn(ResponseEntity.ok(jsonify(data)));
    ResponseEntity<DataSubmissionResponse> response = postSubmission(submissionPayloadIos, testRestTemplate,
        IOS_SERVICE_URL, false);

    // then
    Optional<ApiTokenData> optionalApiToken = apiTokenRepository.findById(apiToken);
    assertThat(response.getStatusCode()).isEqualTo(HttpStatus.NO_CONTENT);
    assertThat(optionalApiToken.isPresent()).isEqualTo(true);
    assertThat(response.getBody()).isNull();
  }

  @Test
  public void testSubmitDataShouldNotThrowApiTokenQuotaExceeded() {
    // given
    // A valid device Token and an existing ApiToken. So while authenticating the existing api token we need to check if
    // this api token
    // was already used today for PPA. If so then NORMALLY there would be a API_TOKEN_QUOTA_EXCEEDED but
    // with the loadtest profile this check is skipped
    String deviceToken = buildBase64String(this.configuration.getIos().getMinDeviceTokenLength() + 1);
    String apiToken = buildUuid();
    PerDeviceDataResponse data = buildIosDeviceData(OffsetDateTime.now(), true);
    PPADataRequestIOS submissionPayloadIos = buildPPADataRequestIosPayload(apiToken, deviceToken, false);

    OffsetDateTime now = OffsetDateTime.now();
    Long expirationDate = getLastDayOfMonthFor(now);
    long timestamp = getEpochSecondFor(now);

    apiTokenRepository.insert(apiToken, expirationDate, expirationDate, timestamp, null);
    // when
    when(iosDeviceApiClient.queryDeviceData(anyString(), any())).thenReturn(ResponseEntity.ok(jsonify(data)));
    ResponseEntity<DataSubmissionResponse> response = postSubmission(submissionPayloadIos, testRestTemplate,
        IOS_SERVICE_URL, false);

    // then
    assertThat(response.getStatusCode()).isEqualTo(HttpStatus.NO_CONTENT);
    assertThat(response.getBody()).isNull();
  }

}
