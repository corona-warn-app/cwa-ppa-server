package app.coronawarn.datadonation.common.persistence.service;

import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.mock;
import java.util.List;
import org.junit.jupiter.api.Test;
import app.coronawarn.datadonation.common.persistence.domain.metrics.ExposureRiskMetadata;
import app.coronawarn.datadonation.common.persistence.domain.metrics.ExposureWindow;
import app.coronawarn.datadonation.common.persistence.domain.metrics.KeySubmissionMetadataWithClientMetadata;
import app.coronawarn.datadonation.common.persistence.domain.metrics.KeySubmissionMetadataWithUserMetadata;
import app.coronawarn.datadonation.common.persistence.domain.metrics.TestResultMetadata;
import app.coronawarn.datadonation.common.persistence.errors.MetricsDataCouldNotBeStored;
import app.coronawarn.datadonation.common.persistence.repository.metrics.ClientMetadataRepository;
import app.coronawarn.datadonation.common.persistence.repository.metrics.ExposureRiskMetadataRepository;
import app.coronawarn.datadonation.common.persistence.repository.metrics.ExposureWindowRepository;
import app.coronawarn.datadonation.common.persistence.repository.metrics.KeySubmissionMetadataWithClientMetadataRepository;
import app.coronawarn.datadonation.common.persistence.repository.metrics.KeySubmissionMetadataWithUserMetadataRepository;
import app.coronawarn.datadonation.common.persistence.repository.metrics.MetricsMockData;
import app.coronawarn.datadonation.common.persistence.repository.metrics.ScanInstanceRepository;
import app.coronawarn.datadonation.common.persistence.repository.metrics.TestResultMetadataRepository;
import app.coronawarn.datadonation.common.persistence.repository.metrics.UserMetadataRepository;

class PpaDataServiceTest {

  @Test
  void metricsShouldNotBeStoredIfMandatoryFieldsAreNull() {
    PpaDataService ppaDataService = getMockServiceInstance();
    assertThatThrownBy(() -> {
      ppaDataService.store(invalidRiskMetadataRequest());
    }).isInstanceOf(MetricsDataCouldNotBeStored.class);

    assertThatThrownBy(() -> {
      ppaDataService.store(invalidExpposureWidowRequest());
    }).isInstanceOf(MetricsDataCouldNotBeStored.class);

    assertThatThrownBy(() -> {
      ppaDataService.store(invalidTestResultRequest());
    }).isInstanceOf(MetricsDataCouldNotBeStored.class);

    assertThatThrownBy(() -> {
      ppaDataService.store(invalidKeySubmissionWithClientMetadataRequest());
    }).isInstanceOf(MetricsDataCouldNotBeStored.class);

    assertThatThrownBy(() -> {
      ppaDataService.store(invalidKeySubmissionWithUserMetadataRequest());
    }).isInstanceOf(MetricsDataCouldNotBeStored.class);
  }

  @Test
  void metricsShouldNotBeStoredIfExposureRiskHasInvalidValues() {
    PpaDataService ppaDataService = getMockServiceInstance();
    assertThatThrownBy(() -> {
      ppaDataService.store(
          new PpaDataStorageRequest(
          MetricsMockData.getExposureRiskMetadataWithInvalidRiskLevel(),
          MetricsMockData.getExposureWindows(), MetricsMockData.getTestResultMetric(),
          MetricsMockData.getKeySubmissionWithClientMetadata(),
          MetricsMockData.getKeySubmissionWithUserMetadata(),
          MetricsMockData.getUserMetadata(), MetricsMockData.getClientMetadata()));
    }).isInstanceOf(MetricsDataCouldNotBeStored.class);
  }

  private PpaDataStorageRequest invalidKeySubmissionWithUserMetadataRequest() {
    return new PpaDataStorageRequest(MetricsMockData.getExposureRiskMetadata(),
        MetricsMockData.getExposureWindows(), MetricsMockData.getTestResultMetric(),
        MetricsMockData.getKeySubmissionWithClientMetadata(),
        new KeySubmissionMetadataWithUserMetadata(null, null, null, null, null, null, null, null,
            null, null),
        MetricsMockData.getUserMetadata(), MetricsMockData.getClientMetadata());
  }

  private PpaDataStorageRequest invalidKeySubmissionWithClientMetadataRequest() {
    return new PpaDataStorageRequest(
        MetricsMockData.getExposureRiskMetadata(), MetricsMockData.getExposureWindows(),
        MetricsMockData.getTestResultMetric(), new KeySubmissionMetadataWithClientMetadata(null,
            null, null, null, null, null, null, null, null),
        MetricsMockData.getKeySubmissionWithUserMetadata(),
        MetricsMockData.getUserMetadata(), MetricsMockData.getClientMetadata());
  }

  private PpaDataStorageRequest invalidTestResultRequest() {
    return new PpaDataStorageRequest(MetricsMockData.getExposureRiskMetadata(),
        MetricsMockData.getExposureWindows(),
        new TestResultMetadata(null, null, null, null, null, null, null, null),
        MetricsMockData.getKeySubmissionWithClientMetadata(),
        MetricsMockData.getKeySubmissionWithUserMetadata(),
        MetricsMockData.getUserMetadata(), MetricsMockData.getClientMetadata());
  }

  private PpaDataStorageRequest invalidExpposureWidowRequest() {
    return new PpaDataStorageRequest(MetricsMockData.getExposureRiskMetadata(),
        List.of(new ExposureWindow(null, null, null, null, null, null, null, null, null)),
        MetricsMockData.getTestResultMetric(), MetricsMockData.getKeySubmissionWithClientMetadata(),
        MetricsMockData.getKeySubmissionWithUserMetadata(),
        MetricsMockData.getUserMetadata(), MetricsMockData.getClientMetadata());
  }

  private PpaDataStorageRequest invalidRiskMetadataRequest() {
    return new PpaDataStorageRequest(
        new ExposureRiskMetadata(null, null, null, null, null, null, null),
        MetricsMockData.getExposureWindows(), MetricsMockData.getTestResultMetric(),
        MetricsMockData.getKeySubmissionWithClientMetadata(),
        MetricsMockData.getKeySubmissionWithUserMetadata(),
        MetricsMockData.getUserMetadata(), MetricsMockData.getClientMetadata());
  }

  private PpaDataService getMockServiceInstance() {
    ExposureRiskMetadataRepository exposureRiskMetadataRepo =
        mock(ExposureRiskMetadataRepository.class);
    ExposureWindowRepository exposureWindowRepo = mock(ExposureWindowRepository.class);
    ScanInstanceRepository scanInstanceRepo = mock(ScanInstanceRepository.class);
    TestResultMetadataRepository testResultRepo = mock(TestResultMetadataRepository.class);
    KeySubmissionMetadataWithUserMetadataRepository keySubmissionWithUserMetadataRepo =
        mock(KeySubmissionMetadataWithUserMetadataRepository.class);
    KeySubmissionMetadataWithClientMetadataRepository keySubmissionWithClientMetadataRepo =
        mock(KeySubmissionMetadataWithClientMetadataRepository.class);
    UserMetadataRepository userMetadataRepo = mock(UserMetadataRepository.class);
    ClientMetadataRepository clientMetadataRepo = mock(ClientMetadataRepository.class);
    
    PpaDataService ppaDataService = new PpaDataService(exposureRiskMetadataRepo, exposureWindowRepo,
        scanInstanceRepo, testResultRepo, keySubmissionWithUserMetadataRepo,
        keySubmissionWithClientMetadataRepo, userMetadataRepo, clientMetadataRepo);
    return ppaDataService;
  }
}
