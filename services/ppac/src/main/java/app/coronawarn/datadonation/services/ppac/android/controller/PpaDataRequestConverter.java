package app.coronawarn.datadonation.services.ppac.android.controller;

import app.coronawarn.datadonation.common.persistence.domain.metrics.ClientMetadata;
import app.coronawarn.datadonation.common.persistence.domain.metrics.ExposureWindow;
import app.coronawarn.datadonation.common.persistence.domain.metrics.KeySubmissionMetadataWithClientMetadata;
import app.coronawarn.datadonation.common.persistence.domain.metrics.KeySubmissionMetadataWithUserMetadata;
import app.coronawarn.datadonation.common.persistence.domain.metrics.TechnicalMetadata;
import app.coronawarn.datadonation.common.persistence.domain.metrics.TestResultMetadata;
import app.coronawarn.datadonation.common.persistence.domain.metrics.UserMetadata;
import app.coronawarn.datadonation.common.persistence.service.PpaDataStorageRequest;
import app.coronawarn.datadonation.common.protocols.internal.ppdd.ExposureRiskMetadata;
import app.coronawarn.datadonation.common.protocols.internal.ppdd.PPAClientMetadataAndroid;
import app.coronawarn.datadonation.common.protocols.internal.ppdd.PPADataAndroid;
import app.coronawarn.datadonation.common.protocols.internal.ppdd.PPAExposureWindow;
import app.coronawarn.datadonation.common.protocols.internal.ppdd.PPAKeySubmissionMetadata;
import app.coronawarn.datadonation.common.protocols.internal.ppdd.PPANewExposureWindow;
import app.coronawarn.datadonation.common.protocols.internal.ppdd.PPASemanticVersion;
import app.coronawarn.datadonation.common.protocols.internal.ppdd.PPATestResultMetadata;
import app.coronawarn.datadonation.common.protocols.internal.ppdd.PPAUserMetadata;
import app.coronawarn.datadonation.common.protocols.internal.ppdd.PpaDataRequestAndroid.PPADataRequestAndroid;
import app.coronawarn.datadonation.services.ppac.utils.TimeUtils;
import java.util.List;

public class PpaDataRequestConverter {

  /**
   * Extract data from the given request object and convert it to the PPA entity data model in the
   * form of a {@link PpaDataStorageRequest}.
   */
  public static PpaDataStorageRequest convertToStorageRequest(
      PPADataRequestAndroid ppaDataRequest) {
    PPADataAndroid payload = ppaDataRequest.getPayload();
    List<ExposureRiskMetadata> exposureRiskMetadata = payload.getExposureRiskMetadataSetList();
    List<PPANewExposureWindow> newExposureWindows = payload.getNewExposureWindowsList();
    List<PPATestResultMetadata> testResults = payload.getTestResultMetadataSetList();
    List<PPAKeySubmissionMetadata> keySubmissionsMetadata =
        payload.getKeySubmissionMetadataSetList();
    PPAClientMetadataAndroid clientMetadata = payload.getClientMetadata();
    PPAUserMetadata userMetadata = payload.getUserMetadata();

    app.coronawarn.datadonation.common.persistence.domain.metrics.ExposureRiskMetadata exposureRiskMetric =
        convertToExposureMetrics(exposureRiskMetadata, userMetadata);
    ExposureWindow exposureWinowsMetric =
        convertToExposureWindowMetrics(newExposureWindows, clientMetadata);
    TestResultMetadata testResultMetric = convertToTestResultMetrics(testResults, userMetadata);
    KeySubmissionMetadataWithClientMetadata keySubmissionWithClientMetadata =
        convertToKeySubmissionWithClientMetadataMetrics(keySubmissionsMetadata, clientMetadata);
    KeySubmissionMetadataWithUserMetadata keySubmissionWithUserMetadata =
        convertToKeySubmissionWithUserMetadataMetrics(keySubmissionsMetadata, userMetadata);
    return new PpaDataStorageRequest(exposureRiskMetric, exposureWinowsMetric, testResultMetric,
        keySubmissionWithClientMetadata, keySubmissionWithUserMetadata);
  }

  private static KeySubmissionMetadataWithUserMetadata convertToKeySubmissionWithUserMetadataMetrics(
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

  private static KeySubmissionMetadataWithClientMetadata convertToKeySubmissionWithClientMetadataMetrics(
      List<PPAKeySubmissionMetadata> keySubmissionsMetadata,
      PPAClientMetadataAndroid clientMetadata) {
    if (!keySubmissionsMetadata.isEmpty()) {
      PPAKeySubmissionMetadata keySubmissionElement = keySubmissionsMetadata.iterator().next();
      return new KeySubmissionMetadataWithClientMetadata(null, keySubmissionElement.getSubmitted(),
          keySubmissionElement.getSubmittedInBackground(),
          keySubmissionElement.getSubmittedAfterCancel(),
          keySubmissionElement.getSubmittedAfterSymptomFlow(),
          keySubmissionElement.getAdvancedConsentGiven(),
          keySubmissionElement.getLastSubmissionFlowScreenValue(),
          convertToClientMetadataEntity(clientMetadata), TechnicalMetadata.newEmptyInstance());
    }
    return null;
  }

  private static TestResultMetadata convertToTestResultMetrics(
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

  private static ExposureWindow convertToExposureWindowMetrics(
      List<PPANewExposureWindow> newExposureWindows, PPAClientMetadataAndroid clientMetadata) {
    if (!newExposureWindows.isEmpty()) {
      PPANewExposureWindow newWindowElement = newExposureWindows.iterator().next();
      PPAExposureWindow exposureWindow = newWindowElement.getExposureWindow();
      return new ExposureWindow(null, TimeUtils.getLocalDateFor(exposureWindow.getDate()),
          exposureWindow.getReportTypeValue(), exposureWindow.getInfectiousness().getNumber(),
          exposureWindow.getCalibrationConfidence(), newWindowElement.getTransmissionRiskLevel(),
          newWindowElement.getNormalizedTime(), convertToClientMetadataEntity(clientMetadata),
          TechnicalMetadata.newEmptyInstance());
    }
    return null;
  }

  private static ClientMetadata convertToClientMetadataEntity(
      PPAClientMetadataAndroid clientMetadata) {
    PPASemanticVersion cwaVersion = clientMetadata.getCwaVersion();
    return new ClientMetadata(cwaVersion.getMajor(), cwaVersion.getMinor(), cwaVersion.getPatch(),
        clientMetadata.getAppConfigETag(), null, null, null,
        Long.valueOf(clientMetadata.getAndroidApiLevel()).intValue(),
        Long.valueOf(clientMetadata.getEnfVersion()).intValue());
  }

  private static app.coronawarn.datadonation.common.persistence.domain.metrics.ExposureRiskMetadata 
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

  private static UserMetadata convertToUserMetadataEntity(PPAUserMetadata userMetadata) {
    return new UserMetadata(userMetadata.getFederalStateValue(),
        userMetadata.getAdministrativeUnit(), userMetadata.getAgeGroup().getNumber());
  }
}
