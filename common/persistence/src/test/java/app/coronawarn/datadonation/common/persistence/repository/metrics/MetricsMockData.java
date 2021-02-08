package app.coronawarn.datadonation.common.persistence.repository.metrics;

import java.time.LocalDate;
import app.coronawarn.datadonation.common.persistence.domain.metrics.ClientMetadata;
import app.coronawarn.datadonation.common.persistence.domain.metrics.ExposureRiskMetadata;
import app.coronawarn.datadonation.common.persistence.domain.metrics.ExposureWindow;
import app.coronawarn.datadonation.common.persistence.domain.metrics.KeySubmissionMetadataWithClientMetadata;
import app.coronawarn.datadonation.common.persistence.domain.metrics.KeySubmissionMetadataWithUserMetadata;
import app.coronawarn.datadonation.common.persistence.domain.metrics.TechnicalMetadata;
import app.coronawarn.datadonation.common.persistence.domain.metrics.TestResultMetadata;
import app.coronawarn.datadonation.common.persistence.domain.metrics.UserMetadata;

public final class MetricsMockData {

  private static final UserMetadata mockUserMetadata = new UserMetadata(2, 2, 3);
  private static final TechnicalMetadata mockTechnicalMetadata =
      new TechnicalMetadata(LocalDate.now(), true, true, false, false, true);
  private static final ClientMetadata mockClientMetadata =
      new ClientMetadata(1, 2, 2, "eTag", 2, 2, 1, 2, 3);

  
  public static ExposureRiskMetadata getExposureRiskMetadataWithInvalidRiskLevel() {
    return new ExposureRiskMetadata(null, 4, true, LocalDate.now(), false, mockUserMetadata,
        mockTechnicalMetadata);
  }

  public static ExposureRiskMetadata getExposureRiskMetadata() {
    return new ExposureRiskMetadata(null, 1, true, LocalDate.now(), false, mockUserMetadata,
        mockTechnicalMetadata);
  }

  public static ExposureWindow getExposureWindow() {
    return new ExposureWindow(null, LocalDate.now(), 1, 3, 2, 3, 4.54, mockClientMetadata,
        mockTechnicalMetadata);
  }

  public static TestResultMetadata getTestResultMetric() {
    return new TestResultMetadata(null, 1, 2, 3, 4, 1, mockUserMetadata, mockTechnicalMetadata);
  }

  public static KeySubmissionMetadataWithClientMetadata getKeySubmissionWithClientMetadata() {
    return new KeySubmissionMetadataWithClientMetadata(null, true, true, false, false, true, 1,
        mockClientMetadata, mockTechnicalMetadata);
  }

  public static KeySubmissionMetadataWithUserMetadata getKeySubmissionWithUserMetadata() {
    return new KeySubmissionMetadataWithUserMetadata(null, true, true, false, 1, 2, 3, 4,
        mockUserMetadata, mockTechnicalMetadata);
  }
}
