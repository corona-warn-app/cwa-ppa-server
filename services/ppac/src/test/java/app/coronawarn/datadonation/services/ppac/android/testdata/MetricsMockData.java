package app.coronawarn.datadonation.services.ppac.android.testdata;

import app.coronawarn.datadonation.common.persistence.domain.metrics.ClientMetadata;
import app.coronawarn.datadonation.common.persistence.domain.metrics.ExposureRiskMetadata;
import app.coronawarn.datadonation.common.persistence.domain.metrics.ExposureWindow;
import app.coronawarn.datadonation.common.persistence.domain.metrics.KeySubmissionMetadataWithClientMetadata;
import app.coronawarn.datadonation.common.persistence.domain.metrics.KeySubmissionMetadataWithUserMetadata;
import app.coronawarn.datadonation.common.persistence.domain.metrics.ScanInstance;
import app.coronawarn.datadonation.common.persistence.domain.metrics.TechnicalMetadata;
import app.coronawarn.datadonation.common.persistence.domain.metrics.TestResultMetadata;
import app.coronawarn.datadonation.common.persistence.domain.metrics.UserMetadata;
import app.coronawarn.datadonation.common.persistence.domain.metrics.embeddable.ClientMetadataDetails;
import app.coronawarn.datadonation.common.persistence.domain.metrics.embeddable.UserMetadataDetails;
import java.time.LocalDate;
import java.util.List;
import java.util.Set;

public final class MetricsMockData {

  private static final UserMetadataDetails mockUserMetadata = new UserMetadataDetails(2, 2, 3);
  private static final TechnicalMetadata mockTechnicalMetadata = new TechnicalMetadata(LocalDate.now(), true, true,
      true, true);
  private static final ClientMetadataDetails mockClientMetadata = new ClientMetadataDetails(1, 2, 2, "eTag", 2, 2, 1,
      2l, 3l);

  public static ExposureRiskMetadata getExposureRiskMetadataWithInvalidRiskLevel() {
    return new ExposureRiskMetadata(null, 4, true, LocalDate.now(),
        false, 4, true, LocalDate.now(), false, mockUserMetadata, mockTechnicalMetadata);
  }

  public static ExposureRiskMetadata getExposureRiskMetadata() {
    return new ExposureRiskMetadata(null, 1, true, LocalDate.now(),
        false, 1, true, LocalDate.now(), false, mockUserMetadata, mockTechnicalMetadata);
  }

  public static List<ExposureWindow> getExposureWindow() {
    return List.of(new ExposureWindow(null, LocalDate.now(), 1, 3, 2, 3, 4.54, mockClientMetadata,
        mockTechnicalMetadata, getScanInstances()));
  }

  public static TestResultMetadata getTestResultMetric() {
    return new TestResultMetadata(null, 1, 2, 3, 4, 1, 1,
        1, 1,
        mockUserMetadata, mockTechnicalMetadata);
  }

  public static List<KeySubmissionMetadataWithClientMetadata> getKeySubmissionWithClientMetadata() {
    return List.of(new KeySubmissionMetadataWithClientMetadata(null, true, true, false, false, true, 1, mockClientMetadata,
        mockTechnicalMetadata));
  }

  public static List<KeySubmissionMetadataWithUserMetadata> getKeySubmissionWithUserMetadata() {
    return List.of(new KeySubmissionMetadataWithUserMetadata(null, true, true, false, false, 1, 2, 3, 4, mockUserMetadata,
        mockTechnicalMetadata));
  }

  public static UserMetadata getUserMetadata() {
    return new UserMetadata(null, mockUserMetadata, mockTechnicalMetadata);
  }

  public static ClientMetadata getClientMetadata() {
    return new ClientMetadata(null, mockClientMetadata, mockTechnicalMetadata);
  }

  private static Set<ScanInstance> getScanInstances() {
    return Set.of(new ScanInstance(null, null, 3, 4, 5), new ScanInstance(null, null, 6, 7, 7));
  }
}
