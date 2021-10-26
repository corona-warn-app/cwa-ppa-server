package app.coronawarn.datadonation.common.persistence.service;

import app.coronawarn.datadonation.common.persistence.domain.metrics.ClientMetadata;
import app.coronawarn.datadonation.common.persistence.domain.metrics.ExposureRiskMetadata;
import app.coronawarn.datadonation.common.persistence.domain.metrics.ExposureWindow;
import app.coronawarn.datadonation.common.persistence.domain.metrics.ExposureWindowTestResult;
import app.coronawarn.datadonation.common.persistence.domain.metrics.ExposureWindowsAtTestRegistration;
import app.coronawarn.datadonation.common.persistence.domain.metrics.KeySubmissionMetadataWithClientMetadata;
import app.coronawarn.datadonation.common.persistence.domain.metrics.KeySubmissionMetadataWithUserMetadata;
import app.coronawarn.datadonation.common.persistence.domain.metrics.ScanInstancesAtTestRegistration;
import app.coronawarn.datadonation.common.persistence.domain.metrics.SummarizedExposureWindowsWithUserMetadata;
import app.coronawarn.datadonation.common.persistence.domain.metrics.TestResultMetadata;
import app.coronawarn.datadonation.common.persistence.domain.metrics.UserMetadata;
import java.util.List;
import java.util.Optional;

/**
 * Brings together all PPA data types that need to be stored transactionally.
 */
public final class PpaDataStorageRequest {

  private final ExposureRiskMetadata exposureRiskMetric;
  private final List<ExposureWindow> exposureWindowsMetric;
  private final List<KeySubmissionMetadataWithClientMetadata> keySubmissionWithClientMetadata;
  private final TestResultMetadata testResultMetric;
  private final List<KeySubmissionMetadataWithUserMetadata> keySubmissionWithUserMetadata;
  private final UserMetadata userMetadata;
  private final ClientMetadata clientMetadata;
  private final List<ExposureWindowsAtTestRegistration> exposureWindowsAtTestRegistration;
  private final ExposureWindowTestResult exposureWindowTestResult;
  private final ScanInstancesAtTestRegistration scanInstancesAtTestRegistration;
  private final SummarizedExposureWindowsWithUserMetadata summarizedExposureWindowsWithUserMetadata;

  /**
   * Constructs an immutable instance.
   */
  public PpaDataStorageRequest(ExposureRiskMetadata exposureRiskMetric,
      List<ExposureWindow> exposureWindowsMetric, TestResultMetadata testResultMetric,
      List<KeySubmissionMetadataWithClientMetadata> keySubmissionWithClientMetadata,
      List<KeySubmissionMetadataWithUserMetadata> keySubmissionWithUserMetadata,
      UserMetadata userMetadata, ClientMetadata clientMetadata,
      List<ExposureWindowsAtTestRegistration> exposureWindowsAtTestRegistration,
      ExposureWindowTestResult exposureWindowTestResult,
      ScanInstancesAtTestRegistration scanInstancesAtTestRegistration,
      SummarizedExposureWindowsWithUserMetadata summarizedExposureWindowsWithUserMetadata) {

    this.exposureRiskMetric = exposureRiskMetric;
    this.exposureWindowsMetric = exposureWindowsMetric;
    this.testResultMetric = testResultMetric;
    this.keySubmissionWithClientMetadata = keySubmissionWithClientMetadata;
    this.keySubmissionWithUserMetadata = keySubmissionWithUserMetadata;
    this.userMetadata = userMetadata;
    this.clientMetadata = clientMetadata;
    this.exposureWindowsAtTestRegistration = exposureWindowsAtTestRegistration;
    this.exposureWindowTestResult = exposureWindowTestResult;
    this.scanInstancesAtTestRegistration = scanInstancesAtTestRegistration;
    this.summarizedExposureWindowsWithUserMetadata = summarizedExposureWindowsWithUserMetadata;
  }

  public Optional<ExposureRiskMetadata> getExposureRiskMetric() {
    return Optional.ofNullable(exposureRiskMetric);
  }

  public Optional<List<ExposureWindow>> getExposureWindowsMetric() {
    return Optional.ofNullable(exposureWindowsMetric);
  }

  public Optional<List<KeySubmissionMetadataWithClientMetadata>> getKeySubmissionWithClientMetadata() {
    return Optional.ofNullable(keySubmissionWithClientMetadata);
  }

  public Optional<TestResultMetadata> getTestResultMetric() {
    return Optional.ofNullable(testResultMetric);
  }

  public Optional<List<KeySubmissionMetadataWithUserMetadata>> getKeySubmissionWithUserMetadata() {
    return Optional.ofNullable(keySubmissionWithUserMetadata);
  }

  public Optional<UserMetadata> getUserMetadata() {
    return Optional.ofNullable(userMetadata);
  }

  public Optional<ClientMetadata> getClientMetadata() {
    return Optional.ofNullable(clientMetadata);
  }

  public Optional<List<ExposureWindowsAtTestRegistration>> getExposureWindowsAtTestRegistration() {
    return Optional.ofNullable(exposureWindowsAtTestRegistration);
  }

  public Optional<ExposureWindowTestResult> getExposureWindowTestResult() {
    return Optional.ofNullable(exposureWindowTestResult);
  }

  public Optional<ScanInstancesAtTestRegistration> getScanInstancesAtTestRegistration() {
    return Optional.ofNullable(scanInstancesAtTestRegistration);
  }

  public Optional<SummarizedExposureWindowsWithUserMetadata> getSummarizedExposureWindowsWithUserMetadata() {
    return Optional.ofNullable(summarizedExposureWindowsWithUserMetadata);
  }
}
