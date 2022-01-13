package app.coronawarn.datadonation.services.retention;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertEquals;

import app.coronawarn.datadonation.common.persistence.repository.ApiTokenRepository;
import app.coronawarn.datadonation.common.persistence.repository.DeviceTokenRepository;
import app.coronawarn.datadonation.common.persistence.repository.ElsOneTimePasswordRepository;
import app.coronawarn.datadonation.common.persistence.repository.OneTimePasswordRepository;
import app.coronawarn.datadonation.common.persistence.repository.metrics.ClientMetadataRepository;
import app.coronawarn.datadonation.common.persistence.repository.metrics.ExposureRiskMetadataRepository;
import app.coronawarn.datadonation.common.persistence.repository.metrics.ExposureWindowRepository;
import app.coronawarn.datadonation.common.persistence.repository.metrics.ExposureWindowTestResultsRepository;
import app.coronawarn.datadonation.common.persistence.repository.metrics.ExposureWindowsAtTestRegistrationRepository;
import app.coronawarn.datadonation.common.persistence.repository.metrics.KeySubmissionMetadataWithClientMetadataRepository;
import app.coronawarn.datadonation.common.persistence.repository.metrics.KeySubmissionMetadataWithUserMetadataRepository;
import app.coronawarn.datadonation.common.persistence.repository.metrics.ScanInstancesAtTestRegistrationRepository;
import app.coronawarn.datadonation.common.persistence.repository.metrics.SummarizedExposureWindowsWithUserMetadataRepository;
import app.coronawarn.datadonation.common.persistence.repository.metrics.TestResultMetadataRepository;
import app.coronawarn.datadonation.common.persistence.repository.metrics.UserMetadataRepository;
import app.coronawarn.datadonation.common.persistence.repository.ppac.android.SaltRepository;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;

@SpringBootTest
@ActiveProfiles("test")
class RetentionPolicyIntegrationTest {

  @Autowired
  ApiTokenRepository apiTokenRepository;

  @Autowired
  DeviceTokenRepository deviceTokenRepository;

  @Autowired
  OneTimePasswordRepository otpRepository;

  @Autowired
  ElsOneTimePasswordRepository elsOtpRepository;

  @Autowired
  SaltRepository saltRepository;

  @Autowired
  ExposureRiskMetadataRepository exposureRiskMetadataRepository;

  @Autowired
  ExposureWindowRepository exposureWindowRepository;

  @Autowired
  KeySubmissionMetadataWithClientMetadataRepository keySubmissionMetadataWithClientMetadataRepository;

  @Autowired
  KeySubmissionMetadataWithUserMetadataRepository keySubmissionMetadataWithUserMetadataRepository;

  @Autowired
  TestResultMetadataRepository testResultMetadataRepository;

  @Autowired
  ClientMetadataRepository clientMetadataRepository;

  @Autowired
  UserMetadataRepository userMetadataRepository;

  @Autowired
  ExposureWindowsAtTestRegistrationRepository exposureWindowsAtTestRegistrationRepository;

  @Autowired
  ExposureWindowTestResultsRepository exposureWindowTestResultsRepository;

  @Autowired
  SummarizedExposureWindowsWithUserMetadataRepository summarizedExposureWindowsWithUserMetadataRepository;

  @Autowired
  ScanInstancesAtTestRegistrationRepository scanInstancesAtTestRegistrationRepository;

  @Test
  void testShouldDeleteExposureRiskMetadataSuccessfully() {
    assertEquals(3, exposureRiskMetadataRepository.count());
  }

  @Test
  void testShouldDeleteExposureWindowsSuccessfully() {
    assertEquals(4, exposureWindowRepository.count());
  }

  @Test
  void testShouldDeleteSubmissionMetadataWithClientSuccessfully() {
    assertEquals(5, keySubmissionMetadataWithClientMetadataRepository.count());
  }

  @Test
  void testShouldDeleteSubmissionMetadataWithUserSuccessfully() {
    assertEquals(6, keySubmissionMetadataWithUserMetadataRepository.count());
  }

  @Test
  void testShouldDeleteTestResultsMetadataSuccessfully() {
    assertEquals(7, testResultMetadataRepository.count());
  }

  @Test
  void testShouldDeleteApiTokensSuccessfully() {
    assertEquals(8, apiTokenRepository.count());
  }

  @Test
  void testShouldDeleteDeviceTokensSuccessfully() {
    assertEquals(9, deviceTokenRepository.count());
  }

  @Test
  void testShouldDeleteOneTimePasswordsSuccessfully() {
    assertEquals(4, otpRepository.count());
  }

  @Test
  void testShouldDeleteElsOneTimePasswordsSuccessfully() {
    assertEquals(3, elsOtpRepository.count());
  }

  @Test
  void testShouldDeleteSaltsSuccessfully() {
    assertEquals(10, saltRepository.count());
  }

  @Test
  void testShouldDeleteClientMetadataSuccessfully() {
    assertEquals(11, clientMetadataRepository.count());
  }

  @Test
  void testShouldDeleteExposureWindowsAtTestRegistrationSuccessfully() {
    assertEquals(30, exposureWindowsAtTestRegistrationRepository.count());
  }

  @Test
  void testShouldDeleteExposureWindowTestResultSuccessfully() {
    assertEquals(15, exposureWindowTestResultsRepository.count());
  }

  @Test
  void testShouldDeleteSummarizedExposureWindowsWithUserMetadataSuccessfully() {
    assertEquals(15, summarizedExposureWindowsWithUserMetadataRepository.count());
  }

  @Test
  void testShouldDeleteScanInstancesAtTestRegistrationSuccessfully() {
    assertEquals(45, scanInstancesAtTestRegistrationRepository.count());
  }

  @Test
  void testShouldDeleteUserMetadataSuccessfully() {
    // Explanation --> TestData generates 12 UserMetadata's with the following submission dates
    // "now() minus i Days", where i is in range (0,12)
    // The Retention for UserMetadata is set 1 Day, so this means that UserMetadata with
    // submissions "now() - 0" and "now() - 1" are the only ones that remain after applying retention.
    assertThat(userMetadataRepository.count()).isEqualTo(2);
  }
}
