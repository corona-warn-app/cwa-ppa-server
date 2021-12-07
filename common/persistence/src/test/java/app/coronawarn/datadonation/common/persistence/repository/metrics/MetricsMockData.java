package app.coronawarn.datadonation.common.persistence.repository.metrics;

import app.coronawarn.datadonation.common.persistence.domain.metrics.ClientMetadata;
import app.coronawarn.datadonation.common.persistence.domain.metrics.ExposureRiskMetadata;
import app.coronawarn.datadonation.common.persistence.domain.metrics.ExposureWindow;
import app.coronawarn.datadonation.common.persistence.domain.metrics.ExposureWindowTestResult;
import app.coronawarn.datadonation.common.persistence.domain.metrics.ExposureWindowsAtTestRegistration;
import app.coronawarn.datadonation.common.persistence.domain.metrics.KeySubmissionMetadataWithClientMetadata;
import app.coronawarn.datadonation.common.persistence.domain.metrics.KeySubmissionMetadataWithUserMetadata;
import app.coronawarn.datadonation.common.persistence.domain.metrics.ScanInstance;
import app.coronawarn.datadonation.common.persistence.domain.metrics.ScanInstancesAtTestRegistration;
import app.coronawarn.datadonation.common.persistence.domain.metrics.SummarizedExposureWindowsWithUserMetadata;
import app.coronawarn.datadonation.common.persistence.domain.metrics.TechnicalMetadata;
import app.coronawarn.datadonation.common.persistence.domain.metrics.TestResultMetadata;
import app.coronawarn.datadonation.common.persistence.domain.metrics.UserMetadata;
import app.coronawarn.datadonation.common.persistence.domain.metrics.embeddable.ClientMetadataDetails;
import app.coronawarn.datadonation.common.persistence.domain.metrics.embeddable.CwaVersionMetadata;
import app.coronawarn.datadonation.common.persistence.domain.metrics.embeddable.UserMetadataDetails;
import java.time.LocalDate;
import java.util.List;
import java.util.Set;
import java.util.UUID;

public final class MetricsMockData {

  private static final UserMetadataDetails mockUserMetadata = new UserMetadataDetails(2, 2, 3);
  private static final TechnicalMetadata mockTechnicalMetadata = new TechnicalMetadata(LocalDate.now(), true, true,
      false, false);
  private static final CwaVersionMetadata cwaVersionMetadata =
      new CwaVersionMetadata(1, 2, 2);
  private static final ClientMetadataDetails mockClientMetadata = new ClientMetadataDetails(cwaVersionMetadata,
      "eTag", 2, 2, 1, 2l, 3l);

  public static ExposureRiskMetadata getExposureRiskMetadataWithInvalidRiskLevel() {
    return new ExposureRiskMetadata(null, 4, true, LocalDate.now(),
        false, 4, true, LocalDate.now(),
        false, mockUserMetadata, mockTechnicalMetadata, cwaVersionMetadata);
  }

  public static ExposureRiskMetadata getExposureRiskMetadata() {
    return new ExposureRiskMetadata(null, 1, true, LocalDate.now(),
        false, 1, true, LocalDate.now(), false,
        mockUserMetadata, mockTechnicalMetadata, cwaVersionMetadata);
  }

  public static List<ExposureWindow> getExposureWindows() {
    return List.of(new ExposureWindow(null, LocalDate.now(), 1, 3, 2, 3, 4.54, mockClientMetadata,
        mockTechnicalMetadata, getScanInstances()));
  }

  private static Set<ScanInstance> getScanInstances() {
    return Set.of(new ScanInstance(null, null, 3, 4, 5, null), new ScanInstance(null, null, 6, 7, 7, null));
  }

  public static List<TestResultMetadata> getTestResultMetric() {
    return List
        .of(new TestResultMetadata(null, 1, 2, 3, 4, 1, 1,
        1, 1,
        mockUserMetadata, mockTechnicalMetadata, cwaVersionMetadata));
  }

  public static List<KeySubmissionMetadataWithClientMetadata> getKeySubmissionWithClientMetadata() {
    return List
        .of(new KeySubmissionMetadataWithClientMetadata(null, true, true, false, false, true, 1, false,
            mockClientMetadata,
            mockTechnicalMetadata));
  }

  public static List<KeySubmissionMetadataWithUserMetadata> getKeySubmissionWithUserMetadata() {
    return List
        .of(new KeySubmissionMetadataWithUserMetadata(null, true, true, false, false, 1, 2, 3, 4,
            null, null, mockUserMetadata,
            mockTechnicalMetadata, cwaVersionMetadata));
  }

  public static TechnicalMetadata getTechnicalMetadata() {
    return mockTechnicalMetadata;
  }

  public static UserMetadata getUserMetadata() {
    return new UserMetadata(null, mockUserMetadata, mockTechnicalMetadata);
  }

  public static ClientMetadata getClientMetadata() {
    return new ClientMetadata(null, mockClientMetadata, mockTechnicalMetadata);
  }

  private static Set<ScanInstancesAtTestRegistration> getScanInstancesAtTestRegistration() {
    return Set.of(new ScanInstancesAtTestRegistration(null, null, 4, 5, 6, null),
        new ScanInstancesAtTestRegistration(null, null, 6, 7, 7, null));
  }

  public static Set<ExposureWindowsAtTestRegistration> getExposureWindowsAtTestRegistration() {
    return Set.of(new ExposureWindowsAtTestRegistration(null, null, LocalDate.now(), 3,
        2, 3, 3, 4.56, getScanInstancesAtTestRegistration(), false, getTechnicalMetadata()));
  }

  public static List<ExposureWindowTestResult> getExposureWindowTestResults() {
    return List.of(new ExposureWindowTestResult(
        null, 2, mockClientMetadata, mockTechnicalMetadata, getExposureWindowsAtTestRegistration()));
  }

  public static List<SummarizedExposureWindowsWithUserMetadata> getSummarizedExposureWindowsWithUserMetadata() {
    return List
        .of(new SummarizedExposureWindowsWithUserMetadata(null, LocalDate.now(), UUID.randomUUID().toString(), 3, 4.54,
            getUserMetadata().getUserMetadataDetails(), getTechnicalMetadata()));
  }
}
