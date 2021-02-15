package app.coronawarn.datadonation.services.ppac.commons;

import app.coronawarn.datadonation.common.persistence.domain.metrics.KeySubmissionMetadataWithUserMetadata;
import app.coronawarn.datadonation.common.persistence.domain.metrics.TechnicalMetadata;
import app.coronawarn.datadonation.common.persistence.domain.metrics.TestResultMetadata;
import app.coronawarn.datadonation.common.persistence.domain.metrics.UserMetadata;
import app.coronawarn.datadonation.common.persistence.domain.metrics.embeddable.UserMetadataDetails;
import app.coronawarn.datadonation.common.persistence.service.PpaDataStorageRequest;
import app.coronawarn.datadonation.common.protocols.internal.ppdd.ExposureRiskMetadata;
import app.coronawarn.datadonation.common.protocols.internal.ppdd.PPAKeySubmissionMetadata;
import app.coronawarn.datadonation.common.protocols.internal.ppdd.PPANewExposureWindow;
import app.coronawarn.datadonation.common.protocols.internal.ppdd.PPATestResultMetadata;
import app.coronawarn.datadonation.common.protocols.internal.ppdd.PPAUserMetadata;
import app.coronawarn.datadonation.common.utils.TimeUtils;
import app.coronawarn.datadonation.services.ppac.config.PpacConfiguration;
import java.util.List;
import java.util.stream.Collectors;

public abstract class PpaDataRequestConverter<T> {

  public abstract PpaDataStorageRequest convertToStorageRequest(T value, PpacConfiguration ppacConfiguration);

  /**
   * Convert exposure risk meta data to the internal format.
   *
   * @param exposureRiskMetadata the collection of exposure risk metadata contained in an request that needs to be
   *                             mapped to the internal data model.
   * @param userMetadata         the corresponding user meta data that is need to build the exposure metrics.
   * @return a new instance of  exposure risk meta data.
   */
  protected app.coronawarn.datadonation.common.persistence.domain.metrics.ExposureRiskMetadata
      convertToExposureMetrics(List<ExposureRiskMetadata> exposureRiskMetadata, PPAUserMetadata userMetadata) {
    if (!exposureRiskMetadata.isEmpty()) {
      ExposureRiskMetadata riskElement = exposureRiskMetadata.iterator().next();
      return new app.coronawarn.datadonation.common.persistence.domain.metrics.ExposureRiskMetadata(
          null, riskElement.getRiskLevelValue(),
          riskElement.getRiskLevelChangedComparedToPreviousSubmission(),
          TimeUtils.getLocalDateFor(riskElement.getMostRecentDateAtRiskLevel()),
          riskElement.getDateChangedComparedToPreviousSubmission(),
          convertToUserMetadataDetails(userMetadata), TechnicalMetadata.newEmptyInstance());
    }
    return null;
  }

  /**
   * Limit the number of exposure windows to convert for storage based on application configuration.
   */
  protected List<PPANewExposureWindow> sliceExposureWindows(
      List<PPANewExposureWindow> newExposureWindowsList, PpacConfiguration ppacConfiguration) {
    return newExposureWindowsList.stream().limit(ppacConfiguration.getMaxExposureWindowsToStore())
        .collect(Collectors.toList());
  }
  
  /**
   * Convert user meta data to its internal data format.
   *
   * @param userMetadata user meta data from an incoming requests that will be mapped to the internal data format.
   * @return a newly created instance  of {@link UserMetadataDetails }
   */
  protected UserMetadataDetails convertToUserMetadataDetails(PPAUserMetadata userMetadata) {
    return new UserMetadataDetails(userMetadata.getFederalStateValue(),
        userMetadata.getAdministrativeUnit(), userMetadata.getAgeGroup().getNumber());
  }


  /**
   * Convert the given proto structure to a domain {@link UserMetadata} entity.
   */
  protected UserMetadata convertToUserMetadataEntity(PPAUserMetadata userMetadata) {
    // TODO Auto-generated method stub
    return null;
  }

  /**
   * Convert test result meta data to its internal data format.
   *
   * @param testResults  a collection of key submission meta data that is mapped to the internal data format.
   * @param userMetadata the corresponding user meta data.
   * @return a newly created instance  of {@link TestResultMetadata }
   */
  protected TestResultMetadata convertToTestResultMetrics(
      List<PPATestResultMetadata> testResults, PPAUserMetadata userMetadata) {
    if (!testResults.isEmpty()) {
      PPATestResultMetadata resultElement = testResults.iterator().next();
      return new TestResultMetadata(null, resultElement.getTestResult().getNumber(),
          resultElement.getHoursSinceTestRegistration(),
          resultElement.getRiskLevelAtTestRegistrationValue(),
          resultElement.getDaysSinceMostRecentDateAtRiskLevelAtTestRegistration(),
          resultElement.getHoursSinceHighRiskWarningAtTestRegistration(),
          convertToUserMetadataDetails(userMetadata), TechnicalMetadata.newEmptyInstance());
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
  protected KeySubmissionMetadataWithUserMetadata convertToKeySubmissionWithUserMetadataMetrics(
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
          convertToUserMetadataDetails(userMetadata), TechnicalMetadata.newEmptyInstance());
    }
    return null;
  }

}
