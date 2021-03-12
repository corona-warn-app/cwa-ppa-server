package app.coronawarn.datadonation.services.retention;

import static org.junit.jupiter.api.Assertions.assertEquals;

import app.coronawarn.datadonation.common.persistence.repository.ApiTokenRepository;
import app.coronawarn.datadonation.common.persistence.repository.DeviceTokenRepository;
import app.coronawarn.datadonation.common.persistence.repository.ElsOneTimePasswordRepository;
import app.coronawarn.datadonation.common.persistence.repository.OneTimePasswordRepository;
import app.coronawarn.datadonation.common.persistence.repository.metrics.ClientMetadataRepository;
import app.coronawarn.datadonation.common.persistence.repository.metrics.ExposureRiskMetadataRepository;
import app.coronawarn.datadonation.common.persistence.repository.metrics.ExposureWindowRepository;
import app.coronawarn.datadonation.common.persistence.repository.metrics.KeySubmissionMetadataWithClientMetadataRepository;
import app.coronawarn.datadonation.common.persistence.repository.metrics.KeySubmissionMetadataWithUserMetadataRepository;
import app.coronawarn.datadonation.common.persistence.repository.metrics.TestResultMetadataRepository;
import app.coronawarn.datadonation.common.persistence.repository.ppac.android.SaltRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit.jupiter.SpringExtension;

@SpringBootTest
@ExtendWith(SpringExtension.class)
@ActiveProfiles("test")
public class RetentionPolicyIntegrationTest {

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
  KeySubmissionMetadataWithClientMetadataRepository keySubmissionWithclientMetadataRepository;

  @Autowired
  KeySubmissionMetadataWithUserMetadataRepository userMetadataRepository;

  @Autowired
  TestResultMetadataRepository testResultMetadataRepository;

  @Autowired
  ClientMetadataRepository clientMetadataRepository;

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
    assertEquals(5, keySubmissionWithclientMetadataRepository.count());
  }

  @Test
  void testShouldDeleteSubmissionMetadataWithUserSuccessfully() {
    assertEquals(6, userMetadataRepository.count());
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
}
