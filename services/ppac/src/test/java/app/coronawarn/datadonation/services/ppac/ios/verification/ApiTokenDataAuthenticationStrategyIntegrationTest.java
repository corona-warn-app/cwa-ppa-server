package app.coronawarn.datadonation.services.ppac.ios.verification;

import static app.coronawarn.datadonation.common.config.UrlConstants.DATA;
import static app.coronawarn.datadonation.common.config.UrlConstants.IOS;
import static app.coronawarn.datadonation.services.ppac.ios.testdata.TestData.buildBase64String;
import static app.coronawarn.datadonation.services.ppac.ios.testdata.TestData.buildIosDeviceData;
import static app.coronawarn.datadonation.services.ppac.ios.testdata.TestData.buildPPADataRequestIosPayload;
import static app.coronawarn.datadonation.services.ppac.ios.testdata.TestData.buildUuid;
import static app.coronawarn.datadonation.services.ppac.ios.testdata.TestData.jsonify;
import static app.coronawarn.datadonation.services.ppac.ios.testdata.TestData.postErrSubmission;
import static app.coronawarn.datadonation.services.ppac.ios.testdata.TestData.postSubmission;
import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.only;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.http.HttpStatus.FORBIDDEN;
import static org.springframework.http.HttpStatus.OK;
import static org.springframework.http.ResponseEntity.ok;

import app.coronawarn.datadonation.common.persistence.domain.ApiTokenData;
import app.coronawarn.datadonation.common.persistence.repository.ApiTokenRepository;
import app.coronawarn.datadonation.common.persistence.repository.DeviceTokenRepository;
import app.coronawarn.datadonation.common.persistence.service.OtpCreationResponse;
import app.coronawarn.datadonation.common.protocols.internal.ppdd.PPADataRequestIOS;
import app.coronawarn.datadonation.services.ppac.commons.web.DataSubmissionResponse;
import app.coronawarn.datadonation.services.ppac.config.PpacConfiguration;
import app.coronawarn.datadonation.services.ppac.ios.client.IosDeviceApiClient;
import app.coronawarn.datadonation.services.ppac.ios.client.domain.PerDeviceDataResponse;
import app.coronawarn.datadonation.services.ppac.ios.verification.apitoken.authentication.TestApiTokenAuthenticationStrategy;
import app.coronawarn.datadonation.services.ppac.logging.PpacErrorCode;
import java.time.OffsetDateTime;
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
import org.springframework.test.context.ActiveProfiles;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@ActiveProfiles("test")
class ApiTokenDataAuthenticationStrategyIntegrationTest {

  @Autowired
  private TestRestTemplate rest;

  @Autowired
  private ApiTokenRepository apiTokenRepo;

  @Autowired
  private DeviceTokenRepository deviceTokenRepo;

  @Autowired
  private PpacConfiguration config;

  @MockBean
  private JwtProvider jwtProvider;

  @MockBean
  private IosDeviceApiClient iosDeviceApiClient;

  @SpyBean
  private TestApiTokenAuthenticationStrategy testApiTokenAuthenticator;

  @BeforeEach
  void clearDatabase() {
    apiTokenRepo.deleteAll();
    deviceTokenRepo.deleteAll();
    when(jwtProvider.generateJwt()).thenReturn("jwt");
  }

  @Test
  void testSubmitData_apiTokenAlreadyUsed() throws Exception {
    // Toy ios device data that has last update NOW - this will be compared against current server time
    // so this means that someone altered the per device data already this month with an api token.

    // given
    final String deviceToken = buildBase64String(config.getIos().getMinDeviceTokenLength() + 1);
    final String apiToken = buildUuid();
    final OffsetDateTime now = OffsetDateTime.now();
    final PerDeviceDataResponse perDeviceDataResponse = buildIosDeviceData(now, true);
    final PPADataRequestIOS payload = buildPPADataRequestIosPayload(apiToken, deviceToken, false);
    final ArgumentCaptor<Boolean> skipValidationCaptor = ArgumentCaptor.forClass(Boolean.class);

    // when
    when(iosDeviceApiClient.queryDeviceData(anyString(), any())).thenReturn(ok(jsonify(perDeviceDataResponse)));

    final ResponseEntity<DataSubmissionResponse> response = postErrSubmission(payload, rest, IOS + DATA, false);

    // then
    final Optional<ApiTokenData> optionalApiToken = apiTokenRepo.findById(apiToken);
    verify(testApiTokenAuthenticator, times(1)).checkApiTokenAlreadyIssued(any(), skipValidationCaptor.capture());
    assertThat(skipValidationCaptor.getValue()).isFalse();
    assertThat(response.getStatusCode()).isEqualTo(FORBIDDEN);
    assertThat(optionalApiToken).isEmpty();
    PpacErrorCode errorCode = null;
    if (response.getBody() != null) {
      errorCode = response.getBody().getErrorCode();
    }
    assertThat(errorCode).isEqualTo(PpacErrorCode.API_TOKEN_ALREADY_ISSUED);
  }

  @Test
  void testSubmitData_apiTokenAlreadyUsed_skipValidation() throws Exception {
    // Toy ios device data that has last update NOW - this will be compared against current server time
    // so this means that someone altered the per device data already this month with an api token. BUT skipValidation
    // is enabled with "test" Profile.

    // given
    final String deviceToken = buildBase64String(config.getIos().getMinDeviceTokenLength() + 1);
    final String apiToken = buildUuid();
    final OffsetDateTime now = OffsetDateTime.now();
    final PerDeviceDataResponse perDeviceDataResponse = buildIosDeviceData(now.minusMonths(1), true);

    final PPADataRequestIOS payload = buildPPADataRequestIosPayload(apiToken, deviceToken, false);
    final ArgumentCaptor<Boolean> skipValidationCaptor = ArgumentCaptor.forClass(Boolean.class);

    // when
    when(iosDeviceApiClient.queryDeviceData(anyString(), any())).thenReturn(ok(jsonify(perDeviceDataResponse)));

    final ResponseEntity<OtpCreationResponse> response = postSubmission(payload, rest, IOS + DATA, true);

    // then
    final Optional<ApiTokenData> optionalApiToken = apiTokenRepo.findById(apiToken);
    verify(testApiTokenAuthenticator, only()).checkApiTokenAlreadyIssued(any(), skipValidationCaptor.capture());
    assertThat(skipValidationCaptor.getValue()).isTrue();
    assertThat(response.getStatusCode()).isEqualTo(OK);
    assertThat(optionalApiToken).isPresent();
    assertThat(response.getBody()).isInstanceOf(OtpCreationResponse.class);
  }
}
