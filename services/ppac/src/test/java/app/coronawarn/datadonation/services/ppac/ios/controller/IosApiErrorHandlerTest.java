package app.coronawarn.datadonation.services.ppac.ios.controller;

import static app.coronawarn.datadonation.common.config.UrlConstants.DATA;
import static app.coronawarn.datadonation.common.config.UrlConstants.IOS;
import static app.coronawarn.datadonation.common.utils.TimeUtils.getEpochSecondFor;
import static app.coronawarn.datadonation.common.utils.TimeUtils.getLastDayOfMonthFor;
import static app.coronawarn.datadonation.services.ppac.ios.testdata.TestData.buildBase64String;
import static app.coronawarn.datadonation.services.ppac.ios.testdata.TestData.buildIosDeviceData;
import static app.coronawarn.datadonation.services.ppac.ios.testdata.TestData.buildPPADataRequestIosPayload;
import static app.coronawarn.datadonation.services.ppac.ios.testdata.TestData.buildUuid;
import static app.coronawarn.datadonation.services.ppac.ios.testdata.TestData.jsonify;
import static app.coronawarn.datadonation.services.ppac.ios.testdata.TestData.postErrSubmission;
import static app.coronawarn.datadonation.services.ppac.logging.PpacErrorCode.API_TOKEN_QUOTA_EXCEEDED;
import static java.time.OffsetDateTime.now;
import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.http.HttpStatus.TOO_MANY_REQUESTS;
import static org.springframework.http.ResponseEntity.ok;

import app.coronawarn.datadonation.common.persistence.repository.ApiTokenRepository;
import app.coronawarn.datadonation.common.protocols.internal.ppdd.PPADataRequestIOS;
import app.coronawarn.datadonation.services.ppac.commons.web.DataSubmissionResponse;
import app.coronawarn.datadonation.services.ppac.config.PpacConfiguration;
import app.coronawarn.datadonation.services.ppac.ios.client.IosDeviceApiClient;
import app.coronawarn.datadonation.services.ppac.ios.client.domain.PerDeviceDataResponse;
import app.coronawarn.datadonation.services.ppac.ios.verification.JwtProvider;
import java.time.OffsetDateTime;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.boot.test.mock.mockito.SpyBean;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.http.ResponseEntity;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
class IosApiErrorHandlerTest {

  @Autowired
  private TestRestTemplate rest;

  @Autowired
  private PpacConfiguration config;

  @MockBean
  private IosDeviceApiClient iosDeviceApiClient;

  @MockBean
  private JwtProvider jwtProvider;

  @SpyBean
  IosApiErrorHandler iosApiErrorHandler;

  @Autowired
  ApiTokenRepository apiTokenRepo;

  @BeforeEach
  void clearDatabase() {
    apiTokenRepo.deleteAll();
    when(jwtProvider.generateJwt()).thenReturn("jwt");
  }

  /**
   * Create a setup that throws an ApiTokenQuotaExceeded during processing Then throws a ClientAbortException during
   * exception handling then see what happens...
   */
  @Disabled
  @Test
  void testApiTokenQuotaExceeded_with_manual_ClientAbortException() throws Exception {
    // Given a valid device Token and an existing ApiToken we need to check if
    // this api token was already used today for PPA.
    // If so then NORMALLY there would be a API_TOKEN_QUOTA_EXCEEDED
    final String deviceToken = buildBase64String(config.getIos().getMinDeviceTokenLength() + 1);
    final String apiToken = buildUuid();
    final PerDeviceDataResponse data = buildIosDeviceData(now(), true);
    final PPADataRequestIOS payload = buildPPADataRequestIosPayload(apiToken, deviceToken, false);
    final OffsetDateTime now = now();
    final Long expirationDate = getLastDayOfMonthFor(now);
    final long timestamp = getEpochSecondFor(now);
    apiTokenRepo.insert(apiToken, expirationDate, expirationDate, timestamp, timestamp, timestamp);

    // Triggering the ClientAbortException is not easy, as this is pretty low-level in the call stack
    // For more info, see https://stackoverflow.com/a/38733497/58997
    // Instead, set a breakpoint in org.apache.catalina.connector.OutputBuffer.doFlush(OutputBuffer.java:305)
    // and manually trigger the exception in the debugger:
    // IntelliJ: Right Click on the StackFrame -> Throw Exception

    when(iosDeviceApiClient.queryDeviceData(anyString(), any())).thenReturn(ok(jsonify(data)));
    final ResponseEntity<DataSubmissionResponse> response = postErrSubmission(payload, rest, IOS + DATA, false);

    assertThat(response.getStatusCode()).isEqualTo(TOO_MANY_REQUESTS);
    assertThat(response.getBody()).isNotNull();
    assertThat(response.getBody().getErrorCode()).isEqualTo(API_TOKEN_QUOTA_EXCEEDED);
    verify(iosApiErrorHandler, times(1)).handleTooManyRequestsErrors(any(), any());
  }
}
