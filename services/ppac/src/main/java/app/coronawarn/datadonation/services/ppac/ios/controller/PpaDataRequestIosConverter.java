package app.coronawarn.datadonation.services.ppac.ios.controller;

import static app.coronawarn.datadonation.common.utils.TimeUtils.getLocalDateFor;

import app.coronawarn.datadonation.common.persistence.domain.metrics.ClientMetadata;
import app.coronawarn.datadonation.common.persistence.domain.metrics.ExposureWindow;
import app.coronawarn.datadonation.common.persistence.domain.metrics.KeySubmissionMetadataWithClientMetadata;
import app.coronawarn.datadonation.common.persistence.domain.metrics.KeySubmissionMetadataWithUserMetadata;
import app.coronawarn.datadonation.common.persistence.domain.metrics.TechnicalMetadata;
import app.coronawarn.datadonation.common.persistence.domain.metrics.TestResultMetadata;
import app.coronawarn.datadonation.common.persistence.domain.metrics.UserMetadata;
import app.coronawarn.datadonation.common.persistence.domain.metrics.embeddable.ClientMetadataDetails;
import app.coronawarn.datadonation.common.persistence.service.PpaDataStorageRequest;
import app.coronawarn.datadonation.common.protocols.internal.ppdd.ExposureRiskMetadata;
import app.coronawarn.datadonation.common.protocols.internal.ppdd.PPAClientMetadataIOS;
import app.coronawarn.datadonation.common.protocols.internal.ppdd.PPADataIOS;
import app.coronawarn.datadonation.common.protocols.internal.ppdd.PPAExposureWindow;
import app.coronawarn.datadonation.common.protocols.internal.ppdd.PPAKeySubmissionMetadata;
import app.coronawarn.datadonation.common.protocols.internal.ppdd.PPANewExposureWindow;
import app.coronawarn.datadonation.common.protocols.internal.ppdd.PPASemanticVersion;
import app.coronawarn.datadonation.common.protocols.internal.ppdd.PPATestResultMetadata;
import app.coronawarn.datadonation.common.protocols.internal.ppdd.PPAUserMetadata;
import app.coronawarn.datadonation.common.protocols.internal.ppdd.PpaDataRequestIos.PPADataRequestIOS;
import app.coronawarn.datadonation.services.ppac.commons.PpaDataRequestConverter;
import app.coronawarn.datadonation.services.ppac.config.PpacConfiguration;
import java.util.List;
import java.util.stream.Collectors;
import org.springframework.stereotype.Component;

@Component
public class PpaDataRequestIosConverter extends PpaDataRequestConverter<PPADataRequestIOS> {

  @Override
  public PpaDataStorageRequest convertToStorageRequest(PPADataRequestIOS ppaDataRequest,
      PpacConfiguration ppacConfiguration) {
    final PPADataIOS payload = ppaDataRequest.getPayload();
    List<ExposureRiskMetadata> exposureRiskMetadata = payload.getExposureRiskMetadataSetList();
    List<PPANewExposureWindow> newExposureWindows = payload.getNewExposureWindowsList();
    List<PPATestResultMetadata> testResults = payload.getTestResultMetadataSetList();
    List<PPAKeySubmissionMetadata> keySubmissionsMetadata =
        payload.getKeySubmissionMetadataSetList();
    PPAClientMetadataIOS clientMetadata = payload.getClientMetadata();
    PPAUserMetadata userMetadata = payload.getUserMetadata();

    app.coronawarn.datadonation.common.persistence.domain.metrics.ExposureRiskMetadata exposureRiskMetric =
        convertToExposureMetrics(exposureRiskMetadata, userMetadata);
    List<ExposureWindow> exposureWindowsMetric =
        convertToExposureWindowMetrics(newExposureWindows, clientMetadata);
    TestResultMetadata testResultMetric = convertToTestResultMetrics(testResults, userMetadata);
    KeySubmissionMetadataWithClientMetadata keySubmissionWithClientMetadata =
        convertToKeySubmissionWithClientMetadataMetrics(keySubmissionsMetadata, clientMetadata);
    KeySubmissionMetadataWithUserMetadata keySubmissionWithUserMetadata =
        convertToKeySubmissionWithUserMetadataMetrics(keySubmissionsMetadata, userMetadata);
    UserMetadata userMetadataEntity = convertToUserMetadataEntity(userMetadata);
    ClientMetadata clientMetadataEntity = convertToClientMetadataEntity(clientMetadata);
    
    return new PpaDataStorageRequest(exposureRiskMetric, exposureWindowsMetric, testResultMetric,
        keySubmissionWithClientMetadata, keySubmissionWithUserMetadata, userMetadataEntity, clientMetadataEntity);
  }

  /**
   * Convert the given proto structure to a domain {@link ClientMetadata} entity.
   */
  private ClientMetadata convertToClientMetadataEntity(PPAClientMetadataIOS clientMetadata) {
    return new ClientMetadata(null, convertToClientMetadataDetails(clientMetadata),
        TechnicalMetadata.newEmptyInstance());
  }
  
  private List<ExposureWindow> convertToExposureWindowMetrics(
      List<PPANewExposureWindow> newExposureWindows, PPAClientMetadataIOS clientMetadata) {
    if (!newExposureWindows.isEmpty()) {
      return newExposureWindows.stream()
          .map(newWindow -> convertToExposureWindowEntity(newWindow, clientMetadata))
          .collect(Collectors.toList());
    }
    return null;
  }

  private ExposureWindow convertToExposureWindowEntity(PPANewExposureWindow newExposureWindow,
      PPAClientMetadataIOS clientMetadata) {
    PPAExposureWindow exposureWindow = newExposureWindow.getExposureWindow();
    return new ExposureWindow(null, getLocalDateFor(exposureWindow.getDate()),
        exposureWindow.getReportTypeValue(), exposureWindow.getInfectiousness().getNumber(),
        exposureWindow.getCalibrationConfidence(), newExposureWindow.getTransmissionRiskLevel(),
        newExposureWindow.getNormalizedTime(), convertToClientMetadataDetails(clientMetadata),
        TechnicalMetadata.newEmptyInstance());
  }
  
  private KeySubmissionMetadataWithClientMetadata convertToKeySubmissionWithClientMetadataMetrics(
      List<PPAKeySubmissionMetadata> keySubmissionsMetadata,
      PPAClientMetadataIOS clientMetadata) {
    if (!keySubmissionsMetadata.isEmpty()) {
      PPAKeySubmissionMetadata keySubmissionElement = keySubmissionsMetadata.iterator().next();
      return new KeySubmissionMetadataWithClientMetadata(null, keySubmissionElement.getSubmitted(),
          keySubmissionElement.getSubmittedInBackground(),
          keySubmissionElement.getSubmittedAfterCancel(),
          keySubmissionElement.getSubmittedAfterSymptomFlow(),
          keySubmissionElement.getAdvancedConsentGiven(),
          keySubmissionElement.getLastSubmissionFlowScreenValue(),
          convertToClientMetadataDetails(clientMetadata), TechnicalMetadata.newEmptyInstance());
    }
    return null;
  }

  private ClientMetadataDetails convertToClientMetadataDetails(
      PPAClientMetadataIOS clientMetadata) {
    PPASemanticVersion cwaVersion = clientMetadata.getCwaVersion();
    final PPASemanticVersion iosVersion = clientMetadata.getIosVersion();
    return new ClientMetadataDetails(cwaVersion.getMajor(), cwaVersion.getMinor(), cwaVersion.getPatch(),
        clientMetadata.getAppConfigETag(), iosVersion.getMajor(), iosVersion.getMinor(), iosVersion.getPatch(),
        null,
        null);
  }

}
