package app.coronawarn.datadonation.common.persistence.service;

import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

import app.coronawarn.datadonation.common.persistence.domain.metrics.ClientMetadata;
import app.coronawarn.datadonation.common.persistence.domain.metrics.ExposureRiskMetadata;
import app.coronawarn.datadonation.common.persistence.domain.metrics.ExposureWindow;
import app.coronawarn.datadonation.common.persistence.domain.metrics.KeySubmissionMetadataWithClientMetadata;
import app.coronawarn.datadonation.common.persistence.domain.metrics.KeySubmissionMetadataWithUserMetadata;
import app.coronawarn.datadonation.common.persistence.domain.metrics.TechnicalMetadata;
import app.coronawarn.datadonation.common.persistence.domain.metrics.TestResultMetadata;
import app.coronawarn.datadonation.common.persistence.domain.metrics.UserMetadata;
import app.coronawarn.datadonation.common.persistence.domain.metrics.embeddable.ClientMetadataDetails;
import app.coronawarn.datadonation.common.persistence.domain.metrics.embeddable.CwaVersionMetadata;
import app.coronawarn.datadonation.common.persistence.domain.metrics.embeddable.UserMetadataDetails;
import app.coronawarn.datadonation.common.persistence.errors.MetricsDataCouldNotBeStored;
import app.coronawarn.datadonation.common.persistence.repository.metrics.ClientMetadataRepository;
import app.coronawarn.datadonation.common.persistence.repository.metrics.ExposureRiskMetadataRepository;
import app.coronawarn.datadonation.common.persistence.repository.metrics.ExposureWindowRepository;
import app.coronawarn.datadonation.common.persistence.repository.metrics.ExposureWindowTestResultsRepository;
import app.coronawarn.datadonation.common.persistence.repository.metrics.ExposureWindowsAtTestRegistrationRepository;
import app.coronawarn.datadonation.common.persistence.repository.metrics.KeySubmissionMetadataWithClientMetadataRepository;
import app.coronawarn.datadonation.common.persistence.repository.metrics.KeySubmissionMetadataWithUserMetadataRepository;
import app.coronawarn.datadonation.common.persistence.repository.metrics.MetricsMockData;
import app.coronawarn.datadonation.common.persistence.repository.metrics.ScanInstancesAtTestRegistrationRepository;
import app.coronawarn.datadonation.common.persistence.repository.metrics.SummarizedExposureWindowsWithUserMetadataRepository;
import app.coronawarn.datadonation.common.persistence.repository.metrics.TestResultMetadataRepository;
import app.coronawarn.datadonation.common.persistence.repository.metrics.UserMetadataRepository;
import app.coronawarn.datadonation.common.protocols.internal.ppdd.PPAKeySubmissionType;
import java.util.List;
import java.util.Set;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

class PpaDataServiceTest {

  @Test
  void metricsShouldNotBeStoredIfMandatoryFieldsAreNull() {
    PpaDataService ppaDataService = getMockServiceInstance();
    final PpaDataStorageRequest invalidRiskMetadataRequest = invalidRiskMetadataRequest();
    assertThatThrownBy(() -> ppaDataService.store(invalidRiskMetadataRequest))
        .isInstanceOf(MetricsDataCouldNotBeStored.class);

    final PpaDataStorageRequest invalidExposureWindowRequest = invalidExposureWindowRequest();
    assertThatThrownBy(() -> ppaDataService.store(invalidExposureWindowRequest))
        .isInstanceOf(MetricsDataCouldNotBeStored.class);

    final PpaDataStorageRequest invalidTestResultRequest = invalidTestResultRequest();
    assertThatThrownBy(() -> ppaDataService.store(invalidTestResultRequest))
        .isInstanceOf(MetricsDataCouldNotBeStored.class);

    final PpaDataStorageRequest invalidKeySubmissionWithClientMetadataRequest =
        invalidKeySubmissionWithClientMetadataRequest();
    assertThatThrownBy(() -> ppaDataService.store(invalidKeySubmissionWithClientMetadataRequest))
          .isInstanceOf(MetricsDataCouldNotBeStored.class);

    final PpaDataStorageRequest invalidKeySubmissionWithUserMetadataRequest =
        invalidKeySubmissionWithUserMetadataRequest();
    assertThatThrownBy(() -> ppaDataService.store(invalidKeySubmissionWithUserMetadataRequest))
          .isInstanceOf(MetricsDataCouldNotBeStored.class);

    final PpaDataStorageRequest invalidUserMetadataRequest = invalidUserMetadataRequest();
    assertThatThrownBy(() -> ppaDataService.store(invalidUserMetadataRequest))
        .isInstanceOf(MetricsDataCouldNotBeStored.class);

    final PpaDataStorageRequest invalidClientMetadataRequest = invalidClientMetadataRequest();
    assertThatThrownBy(() -> ppaDataService.store(invalidClientMetadataRequest))
        .isInstanceOf(MetricsDataCouldNotBeStored.class);

    final PpaDataStorageRequest invalidNestedPropertiesInUserMetadataRequest =
        invalidNestedPropertiesInUserMetadataRequest();
    assertThatThrownBy(() -> ppaDataService.store(invalidNestedPropertiesInUserMetadataRequest))
          .isInstanceOf(MetricsDataCouldNotBeStored.class);

    final PpaDataStorageRequest invalidNestedPropertiesInTechnicalMetadataRequest =
        invalidNestedPropertiesInTechnicalMetadataRequest();
    assertThatThrownBy(() -> ppaDataService.store(invalidNestedPropertiesInTechnicalMetadataRequest))
          .isInstanceOf(MetricsDataCouldNotBeStored.class);

    final PpaDataStorageRequest invalidNestedPropertiesInClientMetadataRequest =
        invalidNestedPropertiesInClientMetadataRequest();
    assertThatThrownBy(() -> ppaDataService.store(invalidNestedPropertiesInClientMetadataRequest))
          .isInstanceOf(MetricsDataCouldNotBeStored.class);
  }

  @Test
  void storeValidMetrics() {
    PpaDataService ppaDataService = Mockito.spy(getMockServiceInstance());
    ppaDataService.store(validKeySubmissionRequest());
    verify(ppaDataService, times(1)).store(any());
  }

  @Test
  void metricsShouldNotBeStoredIfExposureRiskHasInvalidValues() {
    PpaDataService ppaDataService = getMockServiceInstance();
    PpaDataStorageRequest ppaDataStorageRequest = new PpaDataStorageRequest(
        MetricsMockData.getExposureRiskMetadataWithInvalidRiskLevel(),
        MetricsMockData.getExposureWindows(), MetricsMockData.getTestResultMetric(),
        MetricsMockData.getKeySubmissionWithClientMetadata(),
        MetricsMockData.getKeySubmissionWithUserMetadata(),
        MetricsMockData.getUserMetadata(), MetricsMockData.getClientMetadata(),
        MetricsMockData.getExposureWindowTestResults(),
        MetricsMockData.getSummarizedExposureWindowsWithUserMetadata());
    assertThatThrownBy(() -> ppaDataService.store(ppaDataStorageRequest))
        .isInstanceOf(MetricsDataCouldNotBeStored.class);
  }

  private PpaDataStorageRequest invalidUserMetadataRequest() {
    return new PpaDataStorageRequest(MetricsMockData.getExposureRiskMetadata(),
        MetricsMockData.getExposureWindows(), MetricsMockData.getTestResultMetric(),
        MetricsMockData.getKeySubmissionWithClientMetadata(),
        MetricsMockData.getKeySubmissionWithUserMetadata(),
        new UserMetadata(null, null, null), MetricsMockData.getClientMetadata(),
        MetricsMockData.getExposureWindowTestResults(),
        MetricsMockData.getSummarizedExposureWindowsWithUserMetadata());
  }

  private PpaDataStorageRequest invalidNestedPropertiesInUserMetadataRequest() {
    return new PpaDataStorageRequest(MetricsMockData.getExposureRiskMetadata(),
        MetricsMockData.getExposureWindows(), MetricsMockData.getTestResultMetric(),
        MetricsMockData.getKeySubmissionWithClientMetadata(),
        MetricsMockData.getKeySubmissionWithUserMetadata(),
        new UserMetadata(null, new UserMetadataDetails(null, null, null), null),
        MetricsMockData.getClientMetadata(),
        MetricsMockData.getExposureWindowTestResults(),
        MetricsMockData.getSummarizedExposureWindowsWithUserMetadata());
  }

  private PpaDataStorageRequest invalidNestedPropertiesInTechnicalMetadataRequest() {
    return new PpaDataStorageRequest(MetricsMockData.getExposureRiskMetadata(),
        MetricsMockData.getExposureWindows(), MetricsMockData.getTestResultMetric(),
        MetricsMockData.getKeySubmissionWithClientMetadata(),
        MetricsMockData.getKeySubmissionWithUserMetadata(),
        new UserMetadata(null, new UserMetadataDetails(null, 2, 3),
            new TechnicalMetadata(null, null, null, null, null)),
        MetricsMockData.getClientMetadata(),
        MetricsMockData.getExposureWindowTestResults(),
        MetricsMockData.getSummarizedExposureWindowsWithUserMetadata());
  }

  private PpaDataStorageRequest invalidNestedPropertiesInClientMetadataRequest() {
    return new PpaDataStorageRequest(MetricsMockData.getExposureRiskMetadata(),
        MetricsMockData.getExposureWindows(), MetricsMockData.getTestResultMetric(),
        MetricsMockData.getKeySubmissionWithClientMetadata(),
        MetricsMockData.getKeySubmissionWithUserMetadata(), MetricsMockData.getUserMetadata(),
        new ClientMetadata(null,
            new ClientMetadataDetails(new CwaVersionMetadata(null, null, null), null, null, null, null, null, null),
            MetricsMockData.getTechnicalMetadata()),
        MetricsMockData.getExposureWindowTestResults(),
        MetricsMockData.getSummarizedExposureWindowsWithUserMetadata());
  }

  private PpaDataStorageRequest invalidClientMetadataRequest() {
    return new PpaDataStorageRequest(MetricsMockData.getExposureRiskMetadata(),
        MetricsMockData.getExposureWindows(), MetricsMockData.getTestResultMetric(),
        MetricsMockData.getKeySubmissionWithClientMetadata(),
        MetricsMockData.getKeySubmissionWithUserMetadata(),
        MetricsMockData.getUserMetadata(), new ClientMetadata(null, null, null),
        MetricsMockData.getExposureWindowTestResults(),
        MetricsMockData.getSummarizedExposureWindowsWithUserMetadata());
  }

  private PpaDataStorageRequest invalidKeySubmissionWithUserMetadataRequest() {
    return new PpaDataStorageRequest(MetricsMockData.getExposureRiskMetadata(),
        MetricsMockData.getExposureWindows(), MetricsMockData.getTestResultMetric(),
        MetricsMockData.getKeySubmissionWithClientMetadata(),
        List.of(new KeySubmissionMetadataWithUserMetadata(null, null, null, null, false, null, null, null, null,
            null, null, null, null,
            MetricsMockData.getClientMetadata().getClientMetadataDetails().getCwaVersion(), 1)),
        MetricsMockData.getUserMetadata(), MetricsMockData.getClientMetadata(),
        MetricsMockData.getExposureWindowTestResults(),
        MetricsMockData.getSummarizedExposureWindowsWithUserMetadata());
  }

  private PpaDataStorageRequest validKeySubmissionRequest() {
    return new PpaDataStorageRequest(MetricsMockData.getExposureRiskMetadata(),
        MetricsMockData.getExposureWindows(), MetricsMockData.getTestResultMetric(),
        MetricsMockData.getKeySubmissionWithClientMetadata(),
        MetricsMockData.getKeySubmissionWithUserMetadata(),
        MetricsMockData.getUserMetadata(), MetricsMockData.getClientMetadata(),
        MetricsMockData.getExposureWindowTestResults(),
        MetricsMockData.getSummarizedExposureWindowsWithUserMetadata());
  }

  private PpaDataStorageRequest invalidKeySubmissionWithClientMetadataRequest() {
    return new PpaDataStorageRequest(
        MetricsMockData.getExposureRiskMetadata(), MetricsMockData.getExposureWindows(),
        MetricsMockData.getTestResultMetric(),
        List.of(new KeySubmissionMetadataWithClientMetadata(null, null, null, null, null, null, null, false, null, null,
            PPAKeySubmissionType.SUBMISSION_TYPE_REGISTERED_TEST_VALUE)),
        MetricsMockData.getKeySubmissionWithUserMetadata(),
        MetricsMockData.getUserMetadata(), MetricsMockData.getClientMetadata(),
        MetricsMockData.getExposureWindowTestResults(),
        MetricsMockData.getSummarizedExposureWindowsWithUserMetadata());
  }

  private PpaDataStorageRequest invalidTestResultRequest() {
    return new PpaDataStorageRequest(MetricsMockData.getExposureRiskMetadata(),
        MetricsMockData.getExposureWindows(),
        List.of(new TestResultMetadata(null, null, null, null, null, null, 1,
            1, 1, null, null,
            MetricsMockData.getClientMetadata().getClientMetadataDetails().getCwaVersion())),
        MetricsMockData.getKeySubmissionWithClientMetadata(),
        MetricsMockData.getKeySubmissionWithUserMetadata(),
        MetricsMockData.getUserMetadata(), MetricsMockData.getClientMetadata(),
        MetricsMockData.getExposureWindowTestResults(),
        MetricsMockData.getSummarizedExposureWindowsWithUserMetadata());
  }

  private PpaDataStorageRequest invalidExposureWindowRequest() {
    return new PpaDataStorageRequest(MetricsMockData.getExposureRiskMetadata(),
        List.of(new ExposureWindow(null, null, null, null, null, null, null, null, null, Set.of())),
        MetricsMockData.getTestResultMetric(), MetricsMockData.getKeySubmissionWithClientMetadata(),
        MetricsMockData.getKeySubmissionWithUserMetadata(),
        MetricsMockData.getUserMetadata(), MetricsMockData.getClientMetadata(),
        MetricsMockData.getExposureWindowTestResults(),
        MetricsMockData.getSummarizedExposureWindowsWithUserMetadata());
  }

  private PpaDataStorageRequest invalidRiskMetadataRequest() {
    return new PpaDataStorageRequest(
        new ExposureRiskMetadata(null, null, null, null,
            null, null, null, null, null, null, null,
            MetricsMockData.getClientMetadata().getClientMetadataDetails().getCwaVersion()),
        MetricsMockData.getExposureWindows(), MetricsMockData.getTestResultMetric(),
        MetricsMockData.getKeySubmissionWithClientMetadata(),
        MetricsMockData.getKeySubmissionWithUserMetadata(),
        MetricsMockData.getUserMetadata(), MetricsMockData.getClientMetadata(),
        MetricsMockData.getExposureWindowTestResults(),
        MetricsMockData.getSummarizedExposureWindowsWithUserMetadata());
  }

  private PpaDataService getMockServiceInstance() {
    ExposureRiskMetadataRepository exposureRiskMetadataRepo =
        mock(ExposureRiskMetadataRepository.class);
    ExposureWindowRepository exposureWindowRepo = mock(ExposureWindowRepository.class);
    TestResultMetadataRepository testResultRepo = mock(TestResultMetadataRepository.class);
    KeySubmissionMetadataWithUserMetadataRepository keySubmissionWithUserMetadataRepo =
        mock(KeySubmissionMetadataWithUserMetadataRepository.class);
    KeySubmissionMetadataWithClientMetadataRepository keySubmissionWithClientMetadataRepo =
        mock(KeySubmissionMetadataWithClientMetadataRepository.class);
    UserMetadataRepository userMetadataRepo = mock(UserMetadataRepository.class);
    ClientMetadataRepository clientMetadataRepo = mock(ClientMetadataRepository.class);
    SummarizedExposureWindowsWithUserMetadataRepository summarizedExposureWindowsWithUserMetadataRepo =
        mock(SummarizedExposureWindowsWithUserMetadataRepository.class);
    ExposureWindowTestResultsRepository testResultMetadataRepo = mock(ExposureWindowTestResultsRepository.class);
    ExposureWindowsAtTestRegistrationRepository exposureWindowsAtTestRegistrationRepo =
        mock(ExposureWindowsAtTestRegistrationRepository.class);
    ScanInstancesAtTestRegistrationRepository scanInstancesAtTestRegistrationRepo =
        mock(ScanInstancesAtTestRegistrationRepository.class);

    return new PpaDataService(exposureRiskMetadataRepo, exposureWindowRepo,
        testResultRepo, keySubmissionWithUserMetadataRepo, keySubmissionWithClientMetadataRepo,
        userMetadataRepo, clientMetadataRepo, testResultMetadataRepo,
        summarizedExposureWindowsWithUserMetadataRepo);
  }
}
