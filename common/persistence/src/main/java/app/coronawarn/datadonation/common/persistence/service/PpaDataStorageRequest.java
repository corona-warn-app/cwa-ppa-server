package app.coronawarn.datadonation.common.persistence.service;

import app.coronawarn.datadonation.common.persistence.domain.metrics.ExposureRiskMetadata;
import app.coronawarn.datadonation.common.persistence.domain.metrics.ExposureWindow;
import app.coronawarn.datadonation.common.persistence.domain.metrics.KeySubmissionMetadataWithClientMetadata;
import app.coronawarn.datadonation.common.persistence.domain.metrics.KeySubmissionMetadataWithUserMetadata;
import app.coronawarn.datadonation.common.persistence.domain.metrics.TestResultMetadata;
import java.util.Optional;

/**
 * Brings together all PPA data types that need to be stored transactionally.
 */
public final class PpaDataStorageRequest {

  private final ExposureRiskMetadata exposureRiskMetric;
  private final ExposureWindow exposureWindowsMetric;
  private final KeySubmissionMetadataWithClientMetadata keySubmissionWithClientMetadata;
  private final TestResultMetadata testResultMetric;
  private final KeySubmissionMetadataWithUserMetadata keySubmissionWithUserMetadata;

  /**
   * Constructs an immutable instance.
   */
  public PpaDataStorageRequest(ExposureRiskMetadata exposureRiskMetric,
      ExposureWindow exposureWindowsMetric, TestResultMetadata testResultMetric,
      KeySubmissionMetadataWithClientMetadata keySubmissionWithClientMetadata,
      KeySubmissionMetadataWithUserMetadata keySubmissionWithUserMetadata) {

    this.exposureRiskMetric = exposureRiskMetric;
    this.exposureWindowsMetric = exposureWindowsMetric;
    this.testResultMetric = testResultMetric;
    this.keySubmissionWithClientMetadata = keySubmissionWithClientMetadata;
    this.keySubmissionWithUserMetadata = keySubmissionWithUserMetadata;
  }

  public Optional<ExposureRiskMetadata> getExposureRiskMetric() {
    return Optional.ofNullable(exposureRiskMetric);
  }

  public Optional<ExposureWindow> getExposureWindowsMetric() {
    return Optional.ofNullable(exposureWindowsMetric);
  }

  public Optional<KeySubmissionMetadataWithClientMetadata> getKeySubmissionWithClientMetadata() {
    return Optional.ofNullable(keySubmissionWithClientMetadata);
  }

  public Optional<TestResultMetadata> getTestResultMetric() {
    return Optional.ofNullable(testResultMetric);
  }

  public Optional<KeySubmissionMetadataWithUserMetadata> getKeySubmissionWithUserMetadata() {
    return Optional.ofNullable(keySubmissionWithUserMetadata);
  }
}
