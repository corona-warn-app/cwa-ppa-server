package app.coronawarn.datadonation.services.ppac.ios.controller;

import static app.coronawarn.datadonation.common.utils.TimeUtils.getLocalDateFor;

import app.coronawarn.datadonation.common.persistence.domain.metrics.ClientMetadata;
import app.coronawarn.datadonation.common.persistence.domain.metrics.ExposureWindow;
import app.coronawarn.datadonation.common.persistence.domain.metrics.KeySubmissionMetadataWithClientMetadata;
import app.coronawarn.datadonation.common.persistence.domain.metrics.KeySubmissionMetadataWithUserMetadata;
import app.coronawarn.datadonation.common.persistence.domain.metrics.ScanInstance;
import app.coronawarn.datadonation.common.persistence.domain.metrics.TechnicalMetadata;
import app.coronawarn.datadonation.common.persistence.domain.metrics.TestResultMetadata;
import app.coronawarn.datadonation.common.persistence.domain.metrics.UserMetadata;
import app.coronawarn.datadonation.common.persistence.domain.metrics.embeddable.ClientMetadataDetails;
import app.coronawarn.datadonation.common.persistence.service.PpaDataStorageRequest;
import app.coronawarn.datadonation.common.protocols.internal.ppdd.ExposureRiskMetadata;
import app.coronawarn.datadonation.common.protocols.internal.ppdd.PPAClientMetadataIOS;
import app.coronawarn.datadonation.common.protocols.internal.ppdd.PPADataIOS;
import app.coronawarn.datadonation.common.protocols.internal.ppdd.PPADataRequestIOS;
import app.coronawarn.datadonation.common.protocols.internal.ppdd.PPAExposureWindow;
import app.coronawarn.datadonation.common.protocols.internal.ppdd.PPAKeySubmissionMetadata;
import app.coronawarn.datadonation.common.protocols.internal.ppdd.PPANewExposureWindow;
import app.coronawarn.datadonation.common.protocols.internal.ppdd.PPASemanticVersion;
import app.coronawarn.datadonation.common.protocols.internal.ppdd.PPATestResultMetadata;
import app.coronawarn.datadonation.common.protocols.internal.ppdd.PPAUserMetadata;
import app.coronawarn.datadonation.services.ppac.commons.PpaDataRequestConverter;
import app.coronawarn.datadonation.services.ppac.config.PpacConfiguration;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;
import org.springframework.stereotype.Component;

@Component
public class PpaDataRequestIosConverter extends PpaDataRequestConverter<PPADataRequestIOS> {

  /**
   * Convert the given IOS proto structure to a storage request.
   */
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

    TechnicalMetadata technicalMetadata = TechnicalMetadata.newEmptyInstance();
    
    app.coronawarn.datadonation.common.persistence.domain.metrics.ExposureRiskMetadata exposureRiskMetric =
        convertToExposureMetrics(exposureRiskMetadata, userMetadata, technicalMetadata);
    List<ExposureWindow> exposureWindowsMetric =
        convertToExposureWindowMetrics(newExposureWindows, clientMetadata, technicalMetadata);
    TestResultMetadata testResultMetric = convertToTestResultMetrics(testResults, userMetadata, technicalMetadata);
    KeySubmissionMetadataWithClientMetadata keySubmissionWithClientMetadata =
        convertToKeySubmissionWithClientMetadataMetrics(keySubmissionsMetadata, clientMetadata, technicalMetadata);
    KeySubmissionMetadataWithUserMetadata keySubmissionWithUserMetadata =
        convertToKeySubmissionWithUserMetadataMetrics(keySubmissionsMetadata, userMetadata, technicalMetadata);
    UserMetadata userMetadataEntity = convertToUserMetadataEntity(userMetadata, technicalMetadata);
    ClientMetadata clientMetadataEntity = convertToClientMetadataEntity(clientMetadata, technicalMetadata);
    
    return new PpaDataStorageRequest(exposureRiskMetric, exposureWindowsMetric, testResultMetric,
        keySubmissionWithClientMetadata, keySubmissionWithUserMetadata, userMetadataEntity, clientMetadataEntity);
  }

  /**
   * Convert the given proto structure to a domain {@link ClientMetadata} entity.
   */
  private ClientMetadata convertToClientMetadataEntity(PPAClientMetadataIOS clientMetadata,
      TechnicalMetadata technicalMetadata) {
    return new ClientMetadata(null, convertToClientMetadataDetails(clientMetadata),
        technicalMetadata);
  }
  
  private List<ExposureWindow> convertToExposureWindowMetrics(
      List<PPANewExposureWindow> newExposureWindows, PPAClientMetadataIOS clientMetadata,
      TechnicalMetadata technicalMetadata) {
    if (!newExposureWindows.isEmpty()) {
      return newExposureWindows.stream().map(
          newWindow -> convertToExposureWindowEntity(newWindow, clientMetadata, technicalMetadata))
          .collect(Collectors.toList());
    }
    return null;
  }

  private ExposureWindow convertToExposureWindowEntity(PPANewExposureWindow newExposureWindow,
      PPAClientMetadataIOS clientMetadata,TechnicalMetadata technicalMetadata) {
    PPAExposureWindow exposureWindow = newExposureWindow.getExposureWindow();
    Set<ScanInstance> scanInstances = convertToScanInstancesEntities(newExposureWindow);
    
    return new ExposureWindow(null, getLocalDateFor(exposureWindow.getDate()),
        exposureWindow.getReportTypeValue(), exposureWindow.getInfectiousness().getNumber(),
        exposureWindow.getCalibrationConfidence(), newExposureWindow.getTransmissionRiskLevel(),
        newExposureWindow.getNormalizedTime(), convertToClientMetadataDetails(clientMetadata),
        technicalMetadata, scanInstances);
  }
  
  private KeySubmissionMetadataWithClientMetadata convertToKeySubmissionWithClientMetadataMetrics(
      List<PPAKeySubmissionMetadata> keySubmissionsMetadata,
      PPAClientMetadataIOS clientMetadata, TechnicalMetadata technicalMetadata) {
    if (!keySubmissionsMetadata.isEmpty()) {
      PPAKeySubmissionMetadata keySubmissionElement = keySubmissionsMetadata.iterator().next();
      return new KeySubmissionMetadataWithClientMetadata(null, keySubmissionElement.getSubmitted(),
          keySubmissionElement.getSubmittedInBackground(),
          keySubmissionElement.getSubmittedAfterCancel(),
          keySubmissionElement.getSubmittedAfterSymptomFlow(),
          keySubmissionElement.getAdvancedConsentGiven(),
          keySubmissionElement.getLastSubmissionFlowScreenValue(),
          convertToClientMetadataDetails(clientMetadata), technicalMetadata);
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
