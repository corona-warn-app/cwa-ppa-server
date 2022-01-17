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
import app.coronawarn.datadonation.common.persistence.repository.metrics.ClientMetadataRepository;
import app.coronawarn.datadonation.common.persistence.repository.metrics.ExposureRiskMetadataRepository;
import app.coronawarn.datadonation.common.persistence.repository.metrics.ExposureWindowRepository;
import app.coronawarn.datadonation.common.persistence.repository.metrics.ExposureWindowTestResultsRepository;
import app.coronawarn.datadonation.common.persistence.repository.metrics.KeySubmissionMetadataWithClientMetadataRepository;
import app.coronawarn.datadonation.common.persistence.repository.metrics.KeySubmissionMetadataWithUserMetadataRepository;
import app.coronawarn.datadonation.common.persistence.repository.metrics.SummarizedExposureWindowsWithUserMetadataRepository;
import app.coronawarn.datadonation.common.persistence.repository.metrics.TestResultMetadataRepository;
import app.coronawarn.datadonation.common.persistence.repository.metrics.UserMetadataRepository;
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
class PpaIosIntegrationTest {

  @Autowired
  private TestRestTemplate testRestTemplate;
  @Autowired
  private PpacConfiguration ppacConfiguration;
  @Autowired
  private ApiTokenRepository apiTokenRepository;
  @Autowired
  private DeviceTokenRepository deviceTokenRepository;
  @Autowired
  private ExposureRiskMetadataRepository exposureRiskMetadataRepo;
  @Autowired
  private ExposureWindowRepository exposureWindowRepo;
  @Autowired
  private TestResultMetadataRepository testResultRepo;
  @Autowired
  private KeySubmissionMetadataWithUserMetadataRepository keySubmissionWithUserMetadataRepo;
  @Autowired
  private KeySubmissionMetadataWithClientMetadataRepository keySubmissionWithClientMetadataRepo;
  @Autowired
  private UserMetadataRepository userMetadataRepo;
  @Autowired
  private ClientMetadataRepository clientMetadataRepo;
  @Autowired
  private SummarizedExposureWindowsWithUserMetadataRepository summarizedExposureWindowsWithUserMetadataRepo;
  @Autowired
  private ExposureWindowTestResultsRepository exposureWindowTestResultsRepo;

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
  void testSavePpaDataRequestIos() {
    PPADataRequestIOS ppaDataRequestIOS = buildPPADataRequestIosPayload(buildUuid(),
        buildBase64String(ppacConfiguration.getIos().getMinDeviceTokenLength() + 1), true);
    PerDeviceDataResponse data = buildIosDeviceData(OffsetDateTime.now().minusMonths(1), true);
    when(iosDeviceApiClient.queryDeviceData(anyString(), any())).thenReturn(ResponseEntity.ok(jsonify(data)));
    when(iosDeviceApiClient.updatePerDeviceData(anyString(), any())).thenReturn(ResponseEntity.ok().build());
    final ResponseEntity<DataSubmissionResponse> responseEntity = postSubmission(
        ppaDataRequestIOS, testRestTemplate, UrlConstants.IOS + UrlConstants.DATA, false);

    assertThat(responseEntity.getStatusCode()).isEqualTo(HttpStatus.NO_CONTENT);
    assertDataWasSaved();
  }

  @Test
  void shouldFailWhenUpdatingDeviceTokenFails() {
    PPADataRequestIOS ppaDataRequestIOS = buildPPADataRequestIosPayload(buildUuid(),
        buildBase64String(ppacConfiguration.getIos().getMinDeviceTokenLength() + 1), true);
    PerDeviceDataResponse data = buildIosDeviceData(OffsetDateTime.now().minusMonths(1), true);
    when(iosDeviceApiClient.queryDeviceData(anyString(), any())).thenReturn(ResponseEntity.ok(jsonify(data)));
    when(iosDeviceApiClient.updatePerDeviceData(anyString(), any())).thenThrow(FeignException.class);
    final ResponseEntity<DataSubmissionResponse> response = postSubmission(
        ppaDataRequestIOS, testRestTemplate, UrlConstants.IOS + UrlConstants.DATA, false);

    assertThat(response.getStatusCode()).isEqualTo(HttpStatus.INTERNAL_SERVER_ERROR);
  }

  private void assertDataWasSaved() {
    assertThat(exposureRiskMetadataRepo.findAll()).isNotEmpty();
    assertThat(exposureWindowRepo.findAll()).isNotEmpty();
    assertThat(testResultRepo.findAll()).isNotEmpty();
    assertThat(keySubmissionWithUserMetadataRepo.findAll()).isNotEmpty();
    assertThat(keySubmissionWithClientMetadataRepo.findAll()).isNotEmpty();
    assertThat(userMetadataRepo.findAll()).isNotEmpty();
    assertThat(clientMetadataRepo.findAll()).isNotEmpty();
    assertThat(summarizedExposureWindowsWithUserMetadataRepo.findAll()).isNotEmpty();
    assertThat(exposureWindowTestResultsRepo.findAll()).isNotEmpty();
  }
}
