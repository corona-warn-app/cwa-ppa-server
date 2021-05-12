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
import app.coronawarn.datadonation.common.persistence.domain.metrics.embeddable.UserMetadataDetails;
import app.coronawarn.datadonation.common.persistence.errors.MetricsDataCouldNotBeStored;
import app.coronawarn.datadonation.common.persistence.repository.metrics.ClientMetadataRepository;
import app.coronawarn.datadonation.common.persistence.repository.metrics.ExposureRiskMetadataRepository;
import app.coronawarn.datadonation.common.persistence.repository.metrics.ExposureWindowRepository;
import app.coronawarn.datadonation.common.persistence.repository.metrics.KeySubmissionMetadataWithClientMetadataRepository;
import app.coronawarn.datadonation.common.persistence.repository.metrics.KeySubmissionMetadataWithUserMetadataRepository;
import app.coronawarn.datadonation.common.persistence.repository.metrics.MetricsMockData;
import app.coronawarn.datadonation.common.persistence.repository.metrics.TestResultMetadataRepository;
import app.coronawarn.datadonation.common.persistence.repository.metrics.UserMetadataRepository;
import java.util.List;
import java.util.Set;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

class PpaDataServiceTest {

  @Test
  void metricsShouldNotBeStoredIfMandatoryFieldsAreNull() {
    PpaDataService ppaDataService = getMockServiceInstance();
    assertThatThrownBy(() -> {
      ppaDataService.store(invalidRiskMetadataRequest());
    }).isInstanceOf(MetricsDataCouldNotBeStored.class);

    assertThatThrownBy(() -> {
      ppaDataService.store(invalidExposureWindowRequest());
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

    assertThatThrownBy(() -> {
      ppaDataService.store(invalidUserMetadataRequest());
    }).isInstanceOf(MetricsDataCouldNotBeStored.class);

    assertThatThrownBy(() -> {
      ppaDataService.store(invalidClientMetadataRequest());
    }).isInstanceOf(MetricsDataCouldNotBeStored.class);

    assertThatThrownBy(() -> {
      ppaDataService.store(invalidNestedPropertiesInUserMetadataRequest());
    }).isInstanceOf(MetricsDataCouldNotBeStored.class);

    assertThatThrownBy(() -> {
      ppaDataService.store(invalidNestedPropertiesInTechnicalMetadataRequest());
    }).isInstanceOf(MetricsDataCouldNotBeStored.class);

    assertThatThrownBy(() -> {
      ppaDataService.store(invalidNestedPropertiesInClientMetadataRequest());
    }).isInstanceOf(MetricsDataCouldNotBeStored.class);
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

  private PpaDataStorageRequest invalidUserMetadataRequest() {
    return new PpaDataStorageRequest(MetricsMockData.getExposureRiskMetadata(),
        MetricsMockData.getExposureWindows(), MetricsMockData.getTestResultMetric(),
        MetricsMockData.getKeySubmissionWithClientMetadata(),
        MetricsMockData.getKeySubmissionWithUserMetadata(),
        new UserMetadata(null, null, null), MetricsMockData.getClientMetadata());
  }

  private PpaDataStorageRequest invalidNestedPropertiesInUserMetadataRequest() {
    return new PpaDataStorageRequest(MetricsMockData.getExposureRiskMetadata(),
        MetricsMockData.getExposureWindows(), MetricsMockData.getTestResultMetric(),
        MetricsMockData.getKeySubmissionWithClientMetadata(),
        MetricsMockData.getKeySubmissionWithUserMetadata(),
        new UserMetadata(null, new UserMetadataDetails(null, null, null), null),
        MetricsMockData.getClientMetadata());
  }

  private PpaDataStorageRequest invalidNestedPropertiesInTechnicalMetadataRequest() {
    return new PpaDataStorageRequest(MetricsMockData.getExposureRiskMetadata(),
        MetricsMockData.getExposureWindows(), MetricsMockData.getTestResultMetric(),
        MetricsMockData.getKeySubmissionWithClientMetadata(),
        MetricsMockData.getKeySubmissionWithUserMetadata(),
        new UserMetadata(null, new UserMetadataDetails(null, 2, 3),
            new TechnicalMetadata(null, null, null, null, null)),
        MetricsMockData.getClientMetadata());
  }

  private PpaDataStorageRequest invalidNestedPropertiesInClientMetadataRequest() {
    return new PpaDataStorageRequest(MetricsMockData.getExposureRiskMetadata(),
        MetricsMockData.getExposureWindows(), MetricsMockData.getTestResultMetric(),
        MetricsMockData.getKeySubmissionWithClientMetadata(),
        MetricsMockData.getKeySubmissionWithUserMetadata(), MetricsMockData.getUserMetadata(),
        new ClientMetadata(null,
            new ClientMetadataDetails(null, null, null, null, null, null, null, null, null),
            MetricsMockData.getTechnicalMetadata()));
  }

  private PpaDataStorageRequest invalidClientMetadataRequest() {
    return new PpaDataStorageRequest(MetricsMockData.getExposureRiskMetadata(),
        MetricsMockData.getExposureWindows(), MetricsMockData.getTestResultMetric(),
        MetricsMockData.getKeySubmissionWithClientMetadata(),
        MetricsMockData.getKeySubmissionWithUserMetadata(),
        MetricsMockData.getUserMetadata(), new ClientMetadata(null, null, null));
  }

  private PpaDataStorageRequest invalidKeySubmissionWithUserMetadataRequest() {
    return new PpaDataStorageRequest(MetricsMockData.getExposureRiskMetadata(),
        MetricsMockData.getExposureWindows(), MetricsMockData.getTestResultMetric(),
        MetricsMockData.getKeySubmissionWithClientMetadata(),
        List.of(new KeySubmissionMetadataWithUserMetadata(null, null, null, null, false,null, null, null, null,
            null, null, null, null)),
        MetricsMockData.getUserMetadata(), MetricsMockData.getClientMetadata());
  }

  private PpaDataStorageRequest validKeySubmissionRequest() {
    return new PpaDataStorageRequest(MetricsMockData.getExposureRiskMetadata(),
        MetricsMockData.getExposureWindows(), MetricsMockData.getTestResultMetric(),
        MetricsMockData.getKeySubmissionWithClientMetadata(),
        MetricsMockData.getKeySubmissionWithUserMetadata(),
        MetricsMockData.getUserMetadata(), MetricsMockData.getClientMetadata());
  }

  private PpaDataStorageRequest invalidKeySubmissionWithClientMetadataRequest() {
    return new PpaDataStorageRequest(
        MetricsMockData.getExposureRiskMetadata(), MetricsMockData.getExposureWindows(),
        MetricsMockData.getTestResultMetric(), List.of(new KeySubmissionMetadataWithClientMetadata(null,
        null, null, null, null, null, null, false, null, null)),
        MetricsMockData.getKeySubmissionWithUserMetadata(),
        MetricsMockData.getUserMetadata(), MetricsMockData.getClientMetadata());
  }

  private PpaDataStorageRequest invalidTestResultRequest() {
    return new PpaDataStorageRequest(MetricsMockData.getExposureRiskMetadata(),
        MetricsMockData.getExposureWindows(),
        new TestResultMetadata(null, null, null, null, null, null, 1,
            1, 1, null, null),
        MetricsMockData.getKeySubmissionWithClientMetadata(),
        MetricsMockData.getKeySubmissionWithUserMetadata(),
        MetricsMockData.getUserMetadata(), MetricsMockData.getClientMetadata());
  }

  private PpaDataStorageRequest invalidExposureWindowRequest() {
    return new PpaDataStorageRequest(MetricsMockData.getExposureRiskMetadata(),
        List.of(new ExposureWindow(null, null, null, null, null, null, null, null, null, Set.of())),
        MetricsMockData.getTestResultMetric(), MetricsMockData.getKeySubmissionWithClientMetadata(),
        MetricsMockData.getKeySubmissionWithUserMetadata(),
        MetricsMockData.getUserMetadata(), MetricsMockData.getClientMetadata());
  }

  private PpaDataStorageRequest invalidRiskMetadataRequest() {
    return new PpaDataStorageRequest(
        new ExposureRiskMetadata(null, null, null, null,
            null, null, null, null, null, null, null),
        MetricsMockData.getExposureWindows(), MetricsMockData.getTestResultMetric(),
        MetricsMockData.getKeySubmissionWithClientMetadata(),
        MetricsMockData.getKeySubmissionWithUserMetadata(),
        MetricsMockData.getUserMetadata(), MetricsMockData.getClientMetadata());
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

    PpaDataService ppaDataService = new PpaDataService(exposureRiskMetadataRepo, exposureWindowRepo,
        testResultRepo, keySubmissionWithUserMetadataRepo, keySubmissionWithClientMetadataRepo,
        userMetadataRepo, clientMetadataRepo);
    return ppaDataService;
  }
}
