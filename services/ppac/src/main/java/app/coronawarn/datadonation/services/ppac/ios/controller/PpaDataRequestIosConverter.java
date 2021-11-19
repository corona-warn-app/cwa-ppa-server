package app.coronawarn.datadonation.services.ppac.ios.controller;

import app.coronawarn.datadonation.common.persistence.domain.metrics.ClientMetadata;
import app.coronawarn.datadonation.common.persistence.domain.metrics.ExposureWindow;
import app.coronawarn.datadonation.common.persistence.domain.metrics.ExposureWindowTestResult;
import app.coronawarn.datadonation.common.persistence.domain.metrics.KeySubmissionMetadataWithClientMetadata;
import app.coronawarn.datadonation.common.persistence.domain.metrics.KeySubmissionMetadataWithUserMetadata;
import app.coronawarn.datadonation.common.persistence.domain.metrics.SummarizedExposureWindowsWithUserMetadata;
import app.coronawarn.datadonation.common.persistence.domain.metrics.TechnicalMetadata;
import app.coronawarn.datadonation.common.persistence.domain.metrics.TestResultMetadata;
import app.coronawarn.datadonation.common.persistence.domain.metrics.UserMetadata;
import app.coronawarn.datadonation.common.persistence.domain.metrics.embeddable.ClientMetadataDetails;
import app.coronawarn.datadonation.common.persistence.domain.metrics.embeddable.CwaVersionMetadata;
import app.coronawarn.datadonation.common.persistence.service.PpaDataStorageRequest;
import app.coronawarn.datadonation.common.protocols.internal.ppdd.ExposureRiskMetadata;
import app.coronawarn.datadonation.common.protocols.internal.ppdd.PPAClientMetadataIOS;
import app.coronawarn.datadonation.common.protocols.internal.ppdd.PPADataIOS;
import app.coronawarn.datadonation.common.protocols.internal.ppdd.PPADataRequestIOS;
import app.coronawarn.datadonation.common.protocols.internal.ppdd.PPAKeySubmissionMetadata;
import app.coronawarn.datadonation.common.protocols.internal.ppdd.PPANewExposureWindow;
import app.coronawarn.datadonation.common.protocols.internal.ppdd.PPASemanticVersion;
import app.coronawarn.datadonation.common.protocols.internal.ppdd.PPATestResult;
import app.coronawarn.datadonation.common.protocols.internal.ppdd.PPATestResultMetadata;
import app.coronawarn.datadonation.common.protocols.internal.ppdd.PPAUserMetadata;
import app.coronawarn.datadonation.services.ppac.commons.PpaDataRequestConverter;
import app.coronawarn.datadonation.services.ppac.config.PpacConfiguration;
import java.util.ArrayList;
import java.util.List;
import org.springframework.stereotype.Component;

@Component
public class PpaDataRequestIosConverter extends PpaDataRequestConverter<PPADataRequestIOS, PPAClientMetadataIOS> {

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
        convertToExposureMetrics(exposureRiskMetadata, userMetadata, technicalMetadata, clientMetadata);
    List<ExposureWindow> exposureWindowsMetric =
        convertToExposureWindowMetrics(newExposureWindows, clientMetadata, technicalMetadata);
    TestResultMetadata testResultMetric = convertToTestResultMetrics(testResults, userMetadata, technicalMetadata,
        clientMetadata);
    List<KeySubmissionMetadataWithClientMetadata> keySubmissionWithClientMetadata =
        convertToKeySubmissionWithClientMetadataMetrics(keySubmissionsMetadata, clientMetadata, technicalMetadata);
    List<KeySubmissionMetadataWithUserMetadata> keySubmissionWithUserMetadata =
        convertToKeySubmissionWithUserMetadataMetrics(keySubmissionsMetadata,
            userMetadata, technicalMetadata, clientMetadata);
    UserMetadata userMetadataEntity = convertToUserMetadataEntity(userMetadata, technicalMetadata);
    ClientMetadata clientMetadataEntity = convertToClientMetadataEntity(clientMetadata, technicalMetadata);

    List<SummarizedExposureWindowsWithUserMetadata> summarizedExposureWindowsWithUserMetadata = new ArrayList<>();
    List<ExposureWindowTestResult> exposureWindowTestResults = new ArrayList<>();

    summarizedExposureWindowsWithUserMetadata.addAll(convertToSummarizedExposureWindowsWithUserMetadata(
        newExposureWindows, userMetadata, technicalMetadata));

    testResults.forEach(testResult -> {
      if (testResult.getTestResult().equals(PPATestResult.TEST_RESULT_NEGATIVE)
          || testResult.getTestResult().equals(PPATestResult.TEST_RESULT_POSITIVE)
          || testResult.getTestResult().equals(PPATestResult.TEST_RESULT_RAT_NEGATIVE)
          || testResult.getTestResult().equals(PPATestResult.TEST_RESULT_RAT_POSITIVE)) {
        exposureWindowTestResults
            .add(convertToExposureWindowTestResult(testResult, clientMetadata, technicalMetadata));
      }
    });

    return new PpaDataStorageRequest(exposureRiskMetric, exposureWindowsMetric, testResultMetric,
        keySubmissionWithClientMetadata, keySubmissionWithUserMetadata, userMetadataEntity, clientMetadataEntity,
        exposureWindowTestResults, summarizedExposureWindowsWithUserMetadata);
  }

  @Override
  protected ClientMetadataDetails convertToClientMetadataDetails(PPAClientMetadataIOS clientMetadata) {
    final PPASemanticVersion iosVersion = clientMetadata.getIosVersion();
    return new ClientMetadataDetails(convertToCwaVersionMetadata(clientMetadata),
        clientMetadata.getAppConfigETag(), iosVersion.getMajor(), iosVersion.getMinor(), iosVersion.getPatch(),
        null, null);
  }

  @Override
  protected CwaVersionMetadata convertToCwaVersionMetadata(PPAClientMetadataIOS clientMetadata) {
    PPASemanticVersion cwaVersion = clientMetadata.getCwaVersion();
    return new CwaVersionMetadata(cwaVersion.getMajor(),
        cwaVersion.getMinor(), cwaVersion.getPatch());
  }
}
