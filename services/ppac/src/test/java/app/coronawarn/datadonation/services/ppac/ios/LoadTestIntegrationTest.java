package app.coronawarn.datadonation.services.ppac.ios;

import app.coronawarn.datadonation.common.config.UrlConstants;
import app.coronawarn.datadonation.common.persistence.domain.DeviceToken;
import app.coronawarn.datadonation.common.persistence.repository.ApiTokenRepository;
import app.coronawarn.datadonation.common.persistence.repository.DeviceTokenRepository;
import app.coronawarn.datadonation.common.protocols.internal.ppdd.PpaDataRequestIos.PPADataRequestIOS;
import app.coronawarn.datadonation.services.ppac.commons.web.DataSubmissionResponse;
import app.coronawarn.datadonation.services.ppac.config.PpacConfiguration;
import app.coronawarn.datadonation.services.ppac.ios.client.IosDeviceApiClient;
import app.coronawarn.datadonation.services.ppac.ios.client.domain.PerDeviceDataResponse;
import app.coronawarn.datadonation.services.ppac.ios.verification.JwtProvider;
import app.coronawarn.datadonation.services.ppac.ios.verification.devicetoken.DeviceTokenRedemptionStrategy;
import app.coronawarn.datadonation.services.ppac.logging.PpacErrorCode;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.test.context.ActiveProfiles;
import java.time.OffsetDateTime;
import java.util.Optional;

import static app.coronawarn.datadonation.common.utils.TimeUtils.getEpochSecondForNow;
import static app.coronawarn.datadonation.common.utils.TimeUtils.getLastDayOfMonthForNow;
import static app.coronawarn.datadonation.services.ppac.ios.testdata.TestData.*;
import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.when;

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
    // loadtest profile is active so we should NOT throw DEVICE_TOKEN_REDEEMED
    // given
    // Per Device Data that was updated last month
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
    // loadtest profile is active so we should NOT throw DEVICE_TOKEN_REDEEMED

    final String deviceToken = buildBase64String(this.configuration.getIos().getMinDeviceTokenLength() + 1);
    final String apiToken = buildUuid();
    final OffsetDateTime now = OffsetDateTime.now();
    final PerDeviceDataResponse perDeviceDataResponse = buildIosDeviceData(now.minusMonths(1), true);
    final PPADataRequestIOS submissionPayloadIos = buildPPADataRequestIosPayload(apiToken, deviceToken, false);
    apiTokenRepository.insert(apiToken, getLastDayOfMonthForNow(), getEpochSecondForNow(), null, null);

    when(iosDeviceApiClient.queryDeviceData(any(), any()))
        .thenReturn(ResponseEntity.ok(jsonify(perDeviceDataResponse)));

    final ResponseEntity<DataSubmissionResponse> responseEntity = postSubmission(submissionPayloadIos, testRestTemplate,
        IOS_SERVICE_URL, false);
    final DeviceToken newDeviceToken = buildDeviceToken(submissionPayloadIos.getAuthentication().getDeviceToken());
    final Optional<DeviceToken> byDeviceTokenHash = deviceTokenRepository
        .findByDeviceTokenHash(newDeviceToken.getDeviceTokenHash());

    assertThat(responseEntity.getStatusCode()).isEqualTo(HttpStatus.NO_CONTENT);
    assertThat(byDeviceTokenHash).isPresent();
  }

  @Test
  public void testSubmitDataShouldNotThrowApiTokenAlreadyUsed() {
    // loadtest profile is active so we should NOT throw DEVICE_TOKEN_REDEEMED

    final String deviceToken = buildBase64String(this.configuration.getIos().getMinDeviceTokenLength() + 1);
    final String apiToken = buildUuid();
    final OffsetDateTime now = OffsetDateTime.now();
    final PerDeviceDataResponse perDeviceDataResponse = buildIosDeviceData(now.minusMonths(1), true);
    final PPADataRequestIOS submissionPayloadIos = buildPPADataRequestIosPayload(apiToken, deviceToken, false);
    apiTokenRepository.insert(apiToken, getLastDayOfMonthForNow(), getEpochSecondForNow(), null, null);

    when(iosDeviceApiClient.queryDeviceData(any(), any()))
        .thenReturn(ResponseEntity.ok(jsonify(perDeviceDataResponse)));

    final ResponseEntity<DataSubmissionResponse> responseEntity = postSubmission(submissionPayloadIos, testRestTemplate,
        IOS_SERVICE_URL, false);
    final DeviceToken newDeviceToken = buildDeviceToken(submissionPayloadIos.getAuthentication().getDeviceToken());
    final Optional<DeviceToken> byDeviceTokenHash = deviceTokenRepository
        .findByDeviceTokenHash(newDeviceToken.getDeviceTokenHash());

    assertThat(responseEntity.getStatusCode()).isEqualTo(HttpStatus.NO_CONTENT);
    assertThat(byDeviceTokenHash).isPresent();
  }

  @Test
  public void testSubmitDataShouldNotThrowApiTokenQuotaExceeded() {
    // loadtest profile is active so we should NOT throw DEVICE_TOKEN_REDEEMED

    final String deviceToken = buildBase64String(this.configuration.getIos().getMinDeviceTokenLength() + 1);
    final String apiToken = buildUuid();
    final OffsetDateTime now = OffsetDateTime.now();
    final PerDeviceDataResponse perDeviceDataResponse = buildIosDeviceData(now.minusMonths(1), true);
    final PPADataRequestIOS submissionPayloadIos = buildPPADataRequestIosPayload(apiToken, deviceToken, false);
    apiTokenRepository.insert(apiToken, getLastDayOfMonthForNow(), getEpochSecondForNow(), null, null);

    when(iosDeviceApiClient.queryDeviceData(any(), any()))
        .thenReturn(ResponseEntity.ok(jsonify(perDeviceDataResponse)));

    final ResponseEntity<DataSubmissionResponse> responseEntity = postSubmission(submissionPayloadIos, testRestTemplate,
        IOS_SERVICE_URL, false);
    final DeviceToken newDeviceToken = buildDeviceToken(submissionPayloadIos.getAuthentication().getDeviceToken());
    final Optional<DeviceToken> byDeviceTokenHash = deviceTokenRepository
        .findByDeviceTokenHash(newDeviceToken.getDeviceTokenHash());

    assertThat(responseEntity.getStatusCode()).isEqualTo(HttpStatus.NO_CONTENT);
    assertThat(byDeviceTokenHash).isPresent();
  }

}
