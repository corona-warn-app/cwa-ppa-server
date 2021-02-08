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

  /**
   * Convert exposure risk meta data to the internal format.
   *
   * @param exposureRiskMetadata the collection of exposure risk metadata contained in an request that needs to be
   *                             mapped to the internal data model.
   * @param userMetadata         the corresponding user meta data that is need to build the exposure metrics.
   * @return a new instance of  exposure risk meta data.
   */
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

  /**
   * Convert user meta data to its internal data format.
   *
   * @param userMetadata user meta data from an incoming requests that will be mapped to the internal data format.
   * @return a newly created instance  of {@link UserMetadata }
   */
  public UserMetadata convertToUserMetadataEntity(PPAUserMetadata userMetadata) {
    return new UserMetadata(userMetadata.getFederalStateValue(),
        userMetadata.getAdministrativeUnit(), userMetadata.getAgeGroup().getNumber());
  }

  /**
   * Convert test result meta data to its internal data format.
   *
   * @param testResults  a collection of key submission meta data that is mapped to the internal data format.
   * @param userMetadata the corresponding user meta data.
   * @return a newly created instance  of {@link TestResultMetadata }
   */
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

  /**
   * Convert key submission meta data to its internal data format.
   *
   * @param keySubmissionsMetadata a collection of key submission meta data that is mapped to the internal data format.
   * @param userMetadata           the corresponding user meta data.
   * @return a newly created instance  of {@link KeySubmissionMetadataWithUserMetadata }
   */
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
