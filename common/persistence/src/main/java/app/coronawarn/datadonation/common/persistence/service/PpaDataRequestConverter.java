package app.coronawarn.datadonation.common.persistence.service;

import app.coronawarn.datadonation.common.persistence.domain.metrics.KeySubmissionMetadataWithUserMetadata;
import app.coronawarn.datadonation.common.persistence.domain.metrics.TechnicalMetadata;
import app.coronawarn.datadonation.common.persistence.domain.metrics.TestResultMetadata;
import app.coronawarn.datadonation.common.persistence.domain.metrics.UserMetadata;
import app.coronawarn.datadonation.common.protocols.internal.ppdd.ExposureRiskMetadata;
import app.coronawarn.datadonation.common.protocols.internal.ppdd.PPAKeySubmissionMetadata;
import app.coronawarn.datadonation.common.protocols.internal.ppdd.PPATestResultMetadata;
import app.coronawarn.datadonation.common.protocols.internal.ppdd.PPAUserMetadata;
import app.coronawarn.datadonation.common.utils.TimeUtils;
import java.util.List;

public abstract class PpaDataRequestConverter<T> {

  public abstract PpaDataStorageRequest convertToStorageRequest(T value);

  public app.coronawarn.datadonation.common.persistence.domain.metrics.ExposureRiskMetadata
  convertToExposureMetrics(List<ExposureRiskMetadata> exposureRiskMetadata, PPAUserMetadata userMetadata) {
    if (!exposureRiskMetadata.isEmpty()) {
      ExposureRiskMetadata riskElement = exposureRiskMetadata.iterator().next();
      return new app.coronawarn.datadonation.common.persistence.domain.metrics.ExposureRiskMetadata(
          null, riskElement.getRiskLevelValue(),
          riskElement.getRiskLevelChangedComparedToPreviousSubmission(),
          TimeUtils.getLocalDateFor(riskElement.getMostRecentDateAtRiskLevel()),
          riskElement.getDateChangedComparedToPreviousSubmission(),
          convertToUserMetadataEntity(userMetadata), TechnicalMetadata.newEmptyInstance());
    }
    return null;
  }

  public UserMetadata convertToUserMetadataEntity(PPAUserMetadata userMetadata) {
    return new UserMetadata(userMetadata.getFederalStateValue(),
        userMetadata.getAdministrativeUnit(), userMetadata.getAgeGroup().getNumber());
  }

  public TestResultMetadata convertToTestResultMetrics(
      List<PPATestResultMetadata> testResults, PPAUserMetadata userMetadata) {
    if (!testResults.isEmpty()) {
      PPATestResultMetadata resultElement = testResults.iterator().next();
      return new TestResultMetadata(null, resultElement.getTestResult().getNumber(),
          resultElement.getHoursSinceTestRegistration(),
          resultElement.getRiskLevelAtTestRegistrationValue(),
          resultElement.getDaysSinceMostRecentDateAtRiskLevelAtTestRegistration(),
          resultElement.getHoursSinceHighRiskWarningAtTestRegistration(),
          convertToUserMetadataEntity(userMetadata), TechnicalMetadata.newEmptyInstance());
    }
    return null;
  }

  public KeySubmissionMetadataWithUserMetadata convertToKeySubmissionWithUserMetadataMetrics(
      List<PPAKeySubmissionMetadata> keySubmissionsMetadata, PPAUserMetadata userMetadata) {
    if (!keySubmissionsMetadata.isEmpty()) {
      PPAKeySubmissionMetadata keySubmissionElement = keySubmissionsMetadata.iterator().next();
      return new KeySubmissionMetadataWithUserMetadata(null, keySubmissionElement.getSubmitted(),
          keySubmissionElement.getSubmittedAfterSymptomFlow(),
          keySubmissionElement.getSubmittedWithTeleTAN(),
          keySubmissionElement.getHoursSinceTestResult(),
          keySubmissionElement.getHoursSinceTestRegistration(),
          keySubmissionElement.getDaysSinceMostRecentDateAtRiskLevelAtTestRegistration(),
          keySubmissionElement.getHoursSinceHighRiskWarningAtTestRegistration(),
          convertToUserMetadataEntity(userMetadata), TechnicalMetadata.newEmptyInstance());
    }
    return null;
  }

}
