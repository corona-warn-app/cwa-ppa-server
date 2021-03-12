package app.coronawarn.datadonation.services.ppac.ios.controller;

import static app.coronawarn.datadonation.services.ppac.ios.testdata.TestData.buildBase64String;
import static app.coronawarn.datadonation.services.ppac.ios.testdata.TestData.buildDeviceToken;
import static app.coronawarn.datadonation.services.ppac.ios.testdata.TestData.buildIosDeviceData;
import static app.coronawarn.datadonation.services.ppac.ios.testdata.TestData.buildPPADataRequestIosPayload;
import static app.coronawarn.datadonation.services.ppac.ios.testdata.TestData.buildUuid;
import static app.coronawarn.datadonation.services.ppac.ios.testdata.TestData.jsonify;
import static app.coronawarn.datadonation.services.ppac.ios.testdata.TestData.postSubmission;
import static app.coronawarn.datadonation.services.ppac.ios.testdata.TestData.postSurvey;
import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.any;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import app.coronawarn.datadonation.common.config.UrlConstants;
import app.coronawarn.datadonation.common.persistence.domain.ApiToken;
import app.coronawarn.datadonation.common.persistence.domain.DeviceToken;
import app.coronawarn.datadonation.common.persistence.repository.ApiTokenRepository;
import app.coronawarn.datadonation.common.persistence.repository.DeviceTokenRepository;
import app.coronawarn.datadonation.common.protocols.internal.ppdd.EDUSOneTimePasswordRequestIOS;
import app.coronawarn.datadonation.common.protocols.internal.ppdd.PPADataRequestIOS;
import app.coronawarn.datadonation.services.ppac.commons.web.DataSubmissionResponse;
import app.coronawarn.datadonation.services.ppac.config.PpacConfiguration;
import app.coronawarn.datadonation.services.ppac.ios.client.IosDeviceApiClient;
import app.coronawarn.datadonation.services.ppac.ios.client.domain.PerDeviceDataResponse;
import app.coronawarn.datadonation.services.ppac.ios.testdata.TestData;
import app.coronawarn.datadonation.services.ppac.ios.verification.JwtProvider;
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

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
public class IosApiErrorHandlerTest {

  // Create a setup that throws an ApiTokenQuotaExceeded during processing
  // Then throws a ClientAbortException during exception handling
  // then see what happens...


  private static final String IOS_SERVICE_URL = UrlConstants.IOS + UrlConstants.DATA;
  private static final String IOS_SURVEY_URL = UrlConstants.IOS + UrlConstants.OTP;
  private static final OffsetDateTime OFFSET_DATE_TIME = OffsetDateTime.parse("2021-10-01T10:00:00+01:00");

  @Autowired
  private TestRestTemplate testRestTemplate;

  @Autowired
  private DeviceTokenRepository deviceTokenRepository;

  @Autowired
  private PpacConfiguration configuration;

  @MockBean
  private IosDeviceApiClient iosDeviceApiClient;

  @MockBean
  private JwtProvider jwtProvider;

  @SpyBean
  IosApiErrorHandler iosApiErrorHandler;

  @Autowired
  ApiTokenRepository apiTokenRepository;

  @BeforeEach
  void clearDatabase() {
    deviceTokenRepository.deleteAll();
    apiTokenRepository.deleteAll();
    when(jwtProvider.generateJwt()).thenReturn("jwt");
  }

  @Test
  public void checkExceptionsInExceptionhandlerAreCaughtAndStatusCode500IsReturnedInResponse() {
    final String deviceToken = buildBase64String(this.configuration.getIos().getMinDeviceTokenLength() + 1);
    final String deviceTokenForSurvey = buildBase64String(this.configuration.getIos().getMinDeviceTokenLength() + 2);
    final String apiToken = buildUuid();
    final String otp = buildUuid();
    final OffsetDateTime now = OffsetDateTime.now();
    final PerDeviceDataResponse perDeviceDataResponse = buildIosDeviceData(now.minusMonths(1), true);
    final PPADataRequestIOS submissionPayloadIos = buildPPADataRequestIosPayload(apiToken, deviceToken, false);
    final EDUSOneTimePasswordRequestIOS edusOneTimePasswordRequestIOS = TestData
        .buildEdusOneTimePasswordPayload(apiToken, deviceTokenForSurvey, otp);

    when(iosDeviceApiClient.queryDeviceData(any(), any()))
        .thenReturn(ResponseEntity.ok(jsonify(perDeviceDataResponse)));

    final ResponseEntity<DataSubmissionResponse> responseEntity = postSubmission(submissionPayloadIos, testRestTemplate,
        IOS_SERVICE_URL, false);
    final ResponseEntity<DataSubmissionResponse> surveyResponseEntity = postSurvey(edusOneTimePasswordRequestIOS,
        testRestTemplate,
        IOS_SURVEY_URL, false);

    final DeviceToken newDeviceToken = buildDeviceToken(submissionPayloadIos.getAuthentication().getDeviceToken());
    final DeviceToken newSurveyDeviceToken = buildDeviceToken(
        edusOneTimePasswordRequestIOS.getAuthentication().getDeviceToken());

    final Optional<DeviceToken> byDeviceTokenHash = deviceTokenRepository
        .findByDeviceTokenHash(newDeviceToken.getDeviceTokenHash());
    final Optional<DeviceToken> surveyDeviceToken = deviceTokenRepository
        .findByDeviceTokenHash(newSurveyDeviceToken.getDeviceTokenHash());
    final Optional<ApiToken> apiTokenOptional = apiTokenRepository.findById(apiToken);

    final String secondDeviceTokenForSurvey = buildBase64String(
        this.configuration.getIos().getMinDeviceTokenLength() + 3);
    final EDUSOneTimePasswordRequestIOS secondEdusOneTimePasswordRequestIOS = TestData
        .buildEdusOneTimePasswordPayload(apiToken, secondDeviceTokenForSurvey, otp);

    final ResponseEntity<DataSubmissionResponse> errorResponse = postSurvey(secondEdusOneTimePasswordRequestIOS,
        testRestTemplate,
        IOS_SURVEY_URL, false);
    assertThat(errorResponse.getStatusCode()).isEqualTo(HttpStatus.TOO_MANY_REQUESTS);
    assertThat(errorResponse.getBody().getErrorCode()).isEqualTo(PpacErrorCode.API_TOKEN_QUOTA_EXCEEDED);
    verify(iosApiErrorHandler, times(1)).handleTooManyRequestsErrors(any(), any());
  }
}
