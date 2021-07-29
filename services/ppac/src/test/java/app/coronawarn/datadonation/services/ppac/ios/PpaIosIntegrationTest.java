package app.coronawarn.datadonation.services.ppac.ios;

import static app.coronawarn.datadonation.services.ppac.ios.testdata.TestData.buildBase64String;
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
import app.coronawarn.datadonation.common.persistence.repository.ApiTokenRepository;
import app.coronawarn.datadonation.common.persistence.repository.DeviceTokenRepository;
import app.coronawarn.datadonation.common.protocols.internal.ppdd.PPADataRequestIOS;
import app.coronawarn.datadonation.services.ppac.commons.web.DataSubmissionResponse;
import app.coronawarn.datadonation.services.ppac.config.PpacConfiguration;
import app.coronawarn.datadonation.services.ppac.ios.client.IosDeviceApiClient;
import app.coronawarn.datadonation.services.ppac.ios.client.domain.PerDeviceDataResponse;
import app.coronawarn.datadonation.services.ppac.ios.verification.JwtProvider;
import feign.FeignException;
import java.time.OffsetDateTime;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.context.SpringBootTest.WebEnvironment;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.context.annotation.Profile;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

@SpringBootTest(webEnvironment = WebEnvironment.RANDOM_PORT)
@Profile("test")
public class PpaIosIntegrationTest {

  @Autowired
  private TestRestTemplate testRestTemplate;
  @Autowired
  private PpacConfiguration ppacConfiguration;

  @Autowired
  private ApiTokenRepository apiTokenRepository;

  @Autowired
  private DeviceTokenRepository deviceTokenRepository;

  @MockBean
  private IosDeviceApiClient iosDeviceApiClient;

  @MockBean
  private JwtProvider jwtProvider;

  @BeforeEach
  void clearDatabase() {
    when(jwtProvider.generateJwt()).thenReturn("jwt");
  }

  @Test
  void testSavePpaDataRequestIos() {
    PPADataRequestIOS ppaDataRequestIOS = buildPPADataRequestIosPayload(buildUuid(),
        buildBase64String(ppacConfiguration.getIos().getMinDeviceTokenLength() + 1), true);
    PerDeviceDataResponse data = buildIosDeviceData(OffsetDateTime.now().minusMonths(1), true);
    when(iosDeviceApiClient.queryDeviceData(anyString(), any())).thenReturn(ResponseEntity.ok(jsonify(data)));
    final ResponseEntity<DataSubmissionResponse> responseEntity = postSubmission(
        ppaDataRequestIOS, testRestTemplate, UrlConstants.IOS + UrlConstants.DATA, false);
    assertThat(responseEntity.getStatusCode()).isEqualTo(HttpStatus.NO_CONTENT);
  }

  @Test
  void shouldFailWhenUpdatingDeviceTokenFails() {
    PPADataRequestIOS ppaDataRequestIOS = buildPPADataRequestIosPayload(buildUuid(),
        buildBase64String(ppacConfiguration.getIos().getMinDeviceTokenLength() + 1), true);
    PerDeviceDataResponse data = buildIosDeviceData(OffsetDateTime.now(), true);
    when(iosDeviceApiClient.queryDeviceData(anyString(), any())).thenThrow(FeignException.class);
    final ResponseEntity<DataSubmissionResponse> response = postSubmission(
        ppaDataRequestIOS, testRestTemplate, UrlConstants.IOS + UrlConstants.DATA, false);

    assertThat(response.getStatusCode()).isEqualTo(HttpStatus.INTERNAL_SERVER_ERROR);
  }
}
