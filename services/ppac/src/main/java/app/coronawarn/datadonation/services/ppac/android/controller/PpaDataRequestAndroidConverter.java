package app.coronawarn.datadonation.services.ppac.android.controller;

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
import app.coronawarn.datadonation.common.protocols.internal.ppdd.PPAClientMetadataAndroid;
import app.coronawarn.datadonation.common.protocols.internal.ppdd.PPADataAndroid;
import app.coronawarn.datadonation.common.protocols.internal.ppdd.PPADataRequestAndroid;
import app.coronawarn.datadonation.common.protocols.internal.ppdd.PPAExposureWindow;
import app.coronawarn.datadonation.common.protocols.internal.ppdd.PPAKeySubmissionMetadata;
import app.coronawarn.datadonation.common.protocols.internal.ppdd.PPANewExposureWindow;
import app.coronawarn.datadonation.common.protocols.internal.ppdd.PPASemanticVersion;
import app.coronawarn.datadonation.common.protocols.internal.ppdd.PPATestResultMetadata;
import app.coronawarn.datadonation.common.protocols.internal.ppdd.PPAUserMetadata;
import app.coronawarn.datadonation.services.ppac.android.attestation.AttestationStatement;
import app.coronawarn.datadonation.services.ppac.android.attestation.AttestationStatement.EvaluationType;
import app.coronawarn.datadonation.services.ppac.commons.PpaDataRequestConverter;
import app.coronawarn.datadonation.services.ppac.config.PpacConfiguration;
import java.time.LocalDate;
import java.time.ZoneId;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;
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

    List<KeySubmissionMetadataWithClientMetadata> keySubmissionWithClientMetadata =
        convertToKeySubmissionWithClientMetadataMetrics(keySubmissionsMetadata, clientMetadata, technicalMetadata);
    List<KeySubmissionMetadataWithUserMetadata> keySubmissionWithUserMetadata =
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

  private List<KeySubmissionMetadataWithClientMetadata> convertToKeySubmissionWithClientMetadataMetrics(
      List<PPAKeySubmissionMetadata> keySubmissionsMetadata,
      PPAClientMetadataAndroid clientMetadata, TechnicalMetadata technicalMetadata) {
    final List<KeySubmissionMetadataWithClientMetadata> keySubmissionMetadataWithClientMetadataList =
        new ArrayList<>(ARRAY_SIZE_KEY_SUBMISSION_METADATA);
    if (!keySubmissionsMetadata.isEmpty()) {
      keySubmissionsMetadata.forEach(keySubmissionMetadata ->
          keySubmissionMetadataWithClientMetadataList.add(
              new KeySubmissionMetadataWithClientMetadata(null, keySubmissionMetadata.getSubmitted(),
                  keySubmissionMetadata.getSubmittedInBackground(),
                  keySubmissionMetadata.getSubmittedAfterCancel(),
                  keySubmissionMetadata.getSubmittedAfterSymptomFlow(),
                  keySubmissionMetadata.getAdvancedConsentGiven(),
                  keySubmissionMetadata.getLastSubmissionFlowScreenValue(),
                  keySubmissionMetadata.getSubmittedWithCheckIns(),
                  convertToClientMetadataDetails(clientMetadata), technicalMetadata)
          )
      );
    }
    return keySubmissionMetadataWithClientMetadataList.isEmpty() ? null : keySubmissionMetadataWithClientMetadataList;
  }

  private List<ExposureWindow> convertToExposureWindowMetrics(
      List<PPANewExposureWindow> newExposureWindows, PPAClientMetadataAndroid clientMetadata,
      TechnicalMetadata technicalMetadata) {
    if (!newExposureWindows.isEmpty()) {
      return newExposureWindows.stream()
          .map(newWindow -> convertToExposureWindowEntity(newWindow, clientMetadata, technicalMetadata))
          .collect(Collectors.toList());
    }
    return null;
  }

  private ExposureWindow convertToExposureWindowEntity(PPANewExposureWindow newExposureWindow,
      PPAClientMetadataAndroid clientMetadata, TechnicalMetadata technicalMetadata) {
    PPAExposureWindow exposureWindow = newExposureWindow.getExposureWindow();
    Set<ScanInstance> scanInstances = convertToScanInstancesEntities(newExposureWindow);

    return new ExposureWindow(null, getLocalDateFor(exposureWindow.getDate()),
        exposureWindow.getReportTypeValue(), exposureWindow.getInfectiousness().getNumber(),
        exposureWindow.getCalibrationConfidence(), newExposureWindow.getTransmissionRiskLevel(),
        newExposureWindow.getNormalizedTime(), convertToClientMetadataDetails(clientMetadata),
        technicalMetadata, scanInstances);
  }

  private ClientMetadataDetails convertToClientMetadataDetails(
      PPAClientMetadataAndroid clientMetadata) {
    PPASemanticVersion cwaVersion = clientMetadata.getCwaVersion();
    return new ClientMetadataDetails(cwaVersion.getMajor(), cwaVersion.getMinor(), cwaVersion.getPatch(),
        clientMetadata.getAppConfigETag(), null, null, null,
        clientMetadata.getAndroidApiLevel(),
        clientMetadata.getEnfVersion());
  }
}
