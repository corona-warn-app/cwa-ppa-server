package app.coronawarn.datadonation.services.ppac.android.controller;

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
import app.coronawarn.datadonation.common.protocols.internal.ppdd.PPADataRequestAndroid;
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
import java.util.List;
import org.springframework.stereotype.Component;

@Component
public class PpaDataRequestAndroidConverter
    extends PpaDataRequestConverter<PPADataRequestAndroid, PPAClientMetadataAndroid> {

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

  @Override
  protected ClientMetadataDetails convertToClientMetadataDetails(PPAClientMetadataAndroid clientMetadata) {
    PPASemanticVersion cwaVersion = clientMetadata.getCwaVersion();
    return new ClientMetadataDetails(cwaVersion.getMajor(), cwaVersion.getMinor(), cwaVersion.getPatch(),
        clientMetadata.getAppConfigETag(), null, null, null,
        clientMetadata.getAndroidApiLevel(),
        clientMetadata.getEnfVersion());
  }
}
