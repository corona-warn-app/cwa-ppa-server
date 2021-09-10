package app.coronawarn.datadonation.services.ppac.ios.verification;

import static app.coronawarn.datadonation.services.ppac.ios.testdata.TestData.buildBase64String;
import static app.coronawarn.datadonation.services.ppac.ios.testdata.TestData.buildIosDeviceData;
import static app.coronawarn.datadonation.services.ppac.ios.testdata.TestData.buildPPADataRequestIosPayload;
import static app.coronawarn.datadonation.services.ppac.ios.testdata.TestData.buildUuid;
import static app.coronawarn.datadonation.services.ppac.ios.testdata.TestData.jsonify;
import static app.coronawarn.datadonation.services.ppac.ios.testdata.TestData.postSubmission;
import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.only;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import app.coronawarn.datadonation.common.config.UrlConstants;
import app.coronawarn.datadonation.common.persistence.domain.ApiToken;
import app.coronawarn.datadonation.common.persistence.repository.ApiTokenRepository;
import app.coronawarn.datadonation.common.persistence.repository.DeviceTokenRepository;
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
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.test.context.ActiveProfiles;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@ActiveProfiles("test")
public class ApiTokenAuthenticationStrategyIntegrationTest {

  private static final String IOS_SERVICE_URL = UrlConstants.IOS + UrlConstants.DATA;

  @Autowired
  private TestRestTemplate testRestTemplate;
  @Autowired
  private ApiTokenRepository apiTokenRepository;

  @Autowired
  private DeviceTokenRepository deviceTokenRepository;

  @Autowired
  private PpacConfiguration configuration;

  @MockBean
  private JwtProvider jwtProvider;

  @MockBean
  private IosDeviceApiClient iosDeviceApiClient;

  @SpyBean
  private TestApiTokenAuthenticationStrategy testApiTokenAuthenticator;

  @BeforeEach
  void clearDatabase() {
    apiTokenRepository.deleteAll();
    deviceTokenRepository.deleteAll();
    when(jwtProvider.generateJwt()).thenReturn("jwt");
  }

  @Test
  public void testSubmitData_apiTokenAlreadyUsed_skipValidation() {
    // Toy ios device data that has last update NOW - this will be compared against current server time
    // so this means that someone altered the per device data already this month with an api token. BUT skipValidation is enabled with "test" Profile.

    // given
    String deviceToken = buildBase64String(this.configuration.getIos().getMinDeviceTokenLength() + 1);
    String apiToken = buildUuid();
    final OffsetDateTime now = OffsetDateTime.now();
    final PerDeviceDataResponse perDeviceDataResponse = buildIosDeviceData(now.minusMonths(1), true);

    PPADataRequestIOS submissionPayloadIos = buildPPADataRequestIosPayload(apiToken, deviceToken, false);
    ArgumentCaptor<Boolean> skipValidationCaptor = ArgumentCaptor.forClass(Boolean.class);

    // when
    when(iosDeviceApiClient.queryDeviceData(anyString(), any()))
        .thenReturn(ResponseEntity.ok(jsonify(perDeviceDataResponse)));

    ResponseEntity<DataSubmissionResponse> response = postSubmission(submissionPayloadIos, testRestTemplate,
        IOS_SERVICE_URL, true);

    // then
    Optional<ApiToken> optionalApiToken = apiTokenRepository.findById(apiToken);
    verify(testApiTokenAuthenticator, only()).checkApiTokenAlreadyIssued(any(), skipValidationCaptor.capture());
    assertThat(skipValidationCaptor.getValue()).isTrue();
    assertThat(response.getStatusCode()).isEqualTo(HttpStatus.NO_CONTENT);
    assertThat(optionalApiToken.isPresent()).isTrue();
    assertThat(response.getBody()).isNull();
  }

  @Test
  public void testSubmitData_apiTokenAlreadyUsed() {
    // Toy ios device data that has last update NOW - this will be compared against current server time
    // so this means that someone altered the per device data already this month with an api token.

    // given
    String deviceToken = buildBase64String(this.configuration.getIos().getMinDeviceTokenLength() + 1);
    String apiToken = buildUuid();
    final OffsetDateTime now = OffsetDateTime.now();
    final PerDeviceDataResponse perDeviceDataResponse = buildIosDeviceData(now, true);
    PPADataRequestIOS submissionPayloadIos = buildPPADataRequestIosPayload(apiToken, deviceToken, false);
    ArgumentCaptor<Boolean> skipValidationCaptor = ArgumentCaptor.forClass(Boolean.class);

    // when
    when(iosDeviceApiClient.queryDeviceData(anyString(), any()))
        .thenReturn(ResponseEntity.ok(jsonify(perDeviceDataResponse)));

    ResponseEntity<DataSubmissionResponse> response = postSubmission(submissionPayloadIos, testRestTemplate,
        IOS_SERVICE_URL, false);

    // then
    Optional<ApiToken> optionalApiToken = apiTokenRepository.findById(apiToken);
    verify(testApiTokenAuthenticator, times(1)).checkApiTokenAlreadyIssued(any(), skipValidationCaptor.capture());
    assertThat(skipValidationCaptor.getValue()).isFalse();
    assertThat(response.getStatusCode()).isEqualTo(HttpStatus.FORBIDDEN);
    assertThat(optionalApiToken.isPresent()).isFalse();
    assertThat(response.getBody().getErrorCode()).isEqualTo(PpacErrorCode.API_TOKEN_ALREADY_ISSUED);
  }
}
