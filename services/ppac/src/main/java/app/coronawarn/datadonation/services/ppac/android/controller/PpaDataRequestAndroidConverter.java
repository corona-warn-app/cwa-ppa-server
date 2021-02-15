package app.coronawarn.datadonation.services.ppac.android.controller;

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
import app.coronawarn.datadonation.common.protocols.internal.ppdd.PPAClientMetadataAndroid;
import app.coronawarn.datadonation.common.protocols.internal.ppdd.PPADataAndroid;
import app.coronawarn.datadonation.common.protocols.internal.ppdd.PPAExposureWindow;
import app.coronawarn.datadonation.common.protocols.internal.ppdd.PPAKeySubmissionMetadata;
import app.coronawarn.datadonation.common.protocols.internal.ppdd.PPANewExposureWindow;
import app.coronawarn.datadonation.common.protocols.internal.ppdd.PPASemanticVersion;
import app.coronawarn.datadonation.common.protocols.internal.ppdd.PPATestResultMetadata;
import app.coronawarn.datadonation.common.protocols.internal.ppdd.PPAUserMetadata;
import app.coronawarn.datadonation.common.protocols.internal.ppdd.PpaDataRequestAndroid.PPADataRequestAndroid;
import app.coronawarn.datadonation.services.ppac.android.attestation.AttestationStatement;
import app.coronawarn.datadonation.services.ppac.android.attestation.AttestationStatement.EvaluationType;
import app.coronawarn.datadonation.services.ppac.commons.PpaDataRequestConverter;
import app.coronawarn.datadonation.services.ppac.config.PpacConfiguration;
import java.time.LocalDate;
import java.time.ZoneId;
import java.util.List;
import java.util.stream.Collectors;
import org.springframework.stereotype.Component;

@Component
public class PpaDataRequestAndroidConverter extends PpaDataRequestConverter<PPADataRequestAndroid> {

  /**
   * Extract data from the given request object and convert it to the PPA entity data model in the form of a {@link
   * PpaDataStorageRequest}.
   */
  public PpaDataStorageRequest convertToStorageRequest(PPADataRequestAndroid ppaDataRequest,
      PpacConfiguration ppacConfiguration, AttestationStatement attestationStatement) {

    PPADataAndroid payload = ppaDataRequest.getPayload();
    List<ExposureRiskMetadata> exposureRiskMetadata = payload.getExposureRiskMetadataSetList();
    List<PPANewExposureWindow> newExposureWindows =
        sliceExposureWindows(payload.getNewExposureWindowsList(), ppacConfiguration);

    List<PPATestResultMetadata> testResults = payload.getTestResultMetadataSetList();
    List<PPAKeySubmissionMetadata> keySubmissionsMetadata =
        payload.getKeySubmissionMetadataSetList();
    PPAClientMetadataAndroid clientMetadata = payload.getClientMetadata();
    PPAUserMetadata userMetadata = payload.getUserMetadata();
    
    TechnicalMetadata technicalMetadata = createTechnicalMetadata(attestationStatement);
    
    app.coronawarn.datadonation.common.persistence.domain.metrics.ExposureRiskMetadata exposureRiskMetric =
        convertToExposureMetrics(exposureRiskMetadata, userMetadata, technicalMetadata);
    List<ExposureWindow> exposureWinowsMetric =
        convertToExposureWindowMetrics(newExposureWindows, clientMetadata, technicalMetadata);
    TestResultMetadata testResultMetric = convertToTestResultMetrics(testResults, userMetadata, technicalMetadata);
    KeySubmissionMetadataWithClientMetadata keySubmissionWithClientMetadata =
        convertToKeySubmissionWithClientMetadataMetrics(keySubmissionsMetadata, clientMetadata);
    KeySubmissionMetadataWithUserMetadata keySubmissionWithUserMetadata =
        convertToKeySubmissionWithUserMetadataMetrics(keySubmissionsMetadata, userMetadata, technicalMetadata);
    UserMetadata userMetadataEntity = convertToUserMetadataEntity(userMetadata, technicalMetadata);
    ClientMetadata clientMetadataEntity = convertToClientMetadataEntity(clientMetadata, technicalMetadata);
    
    return new PpaDataStorageRequest(exposureRiskMetric, exposureWinowsMetric, testResultMetric,
        keySubmissionWithClientMetadata, keySubmissionWithUserMetadata, userMetadataEntity, clientMetadataEntity);
  }

  private TechnicalMetadata createTechnicalMetadata(AttestationStatement attestationStatement) {
    return new TechnicalMetadata(LocalDate.now(ZoneId.of("UTC")), attestationStatement.isBasicIntegrity(), 
        attestationStatement.isCtsProfileMatch(), attestationStatement.isEvaluationTypeEqualTo(EvaluationType.BASIC), 
        attestationStatement.isEvaluationTypeEqualTo(EvaluationType.HARDWARE_BACKED));
  }

  /**
   * Convert the given proto structure to a domain {@link ClientMetadata} entity.
   */
  private ClientMetadata convertToClientMetadataEntity(PPAClientMetadataAndroid clientMetadata,
      TechnicalMetadata technicalMetadata) {
    return new ClientMetadata(null, convertToClientMetadataDetails(clientMetadata),
        technicalMetadata);
  }

  private KeySubmissionMetadataWithClientMetadata convertToKeySubmissionWithClientMetadataMetrics(
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
          convertToClientMetadataDetails(clientMetadata), TechnicalMetadata.newEmptyInstance());
    }
    return null;
  }

  private List<ExposureWindow> convertToExposureWindowMetrics(
      List<PPANewExposureWindow> newExposureWindows, PPAClientMetadataAndroid clientMetadata,
      TechnicalMetadata technicalMetadata) {
    if (!newExposureWindows.isEmpty()) {
      return newExposureWindows.stream()
          .map(newWindow -> convertToExposureWindowEntity(newWindow, clientMetadata))
          .collect(Collectors.toList());
    }
    return null;
  }

  private ExposureWindow convertToExposureWindowEntity(PPANewExposureWindow newExposureWindow,
      PPAClientMetadataAndroid clientMetadata) {
    PPAExposureWindow exposureWindow = newExposureWindow.getExposureWindow();
    return new ExposureWindow(null, getLocalDateFor(exposureWindow.getDate()),
        exposureWindow.getReportTypeValue(), exposureWindow.getInfectiousness().getNumber(),
        exposureWindow.getCalibrationConfidence(), newExposureWindow.getTransmissionRiskLevel(),
        newExposureWindow.getNormalizedTime(), convertToClientMetadataDetails(clientMetadata),
        TechnicalMetadata.newEmptyInstance());
  }
  
  private ClientMetadataDetails convertToClientMetadataDetails(
      PPAClientMetadataAndroid clientMetadata) {
    PPASemanticVersion cwaVersion = clientMetadata.getCwaVersion();
    return new ClientMetadataDetails(cwaVersion.getMajor(), cwaVersion.getMinor(), cwaVersion.getPatch(),
        clientMetadata.getAppConfigETag(), null, null, null,
        Long.valueOf(clientMetadata.getAndroidApiLevel()).intValue(),
        Long.valueOf(clientMetadata.getEnfVersion()).intValue());
  }
}
