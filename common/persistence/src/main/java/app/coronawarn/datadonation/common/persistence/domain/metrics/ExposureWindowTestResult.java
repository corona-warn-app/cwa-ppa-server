package app.coronawarn.datadonation.common.persistence.domain.metrics;

import app.coronawarn.datadonation.common.persistence.domain.metrics.embeddable.ClientMetadataDetails;
import java.util.Objects;
import java.util.Set;
import javax.validation.constraints.NotNull;
import org.springframework.data.relational.core.mapping.Embedded;
import org.springframework.data.relational.core.mapping.Embedded.OnEmpty;
import org.springframework.data.relational.core.mapping.MappedCollection;

public class ExposureWindowTestResult extends DataDonationMetric {

  @NotNull
  private final Integer testResult;

  @Embedded(onEmpty = OnEmpty.USE_EMPTY)
  private final ClientMetadataDetails clientMetadata;

  @Embedded(onEmpty = OnEmpty.USE_EMPTY)
  private final TechnicalMetadata technicalMetadata;

  @MappedCollection(idColumn = "exposure_window_test_result_id")
  private final Set<ExposureWindowsAtTestRegistration> exposureWindowsAtTestRegistrations;

  /**
   * Constructs an immutable instance.
   */
  public ExposureWindowTestResult(Long id, Integer testResult, ClientMetadataDetails clientMetadata,
      TechnicalMetadata technicalMetadata, Set<ExposureWindowsAtTestRegistration> exposureWindowsAtTestRegistrations) {
    super(id);
    this.testResult = testResult;
    this.clientMetadata = clientMetadata;
    this.technicalMetadata = technicalMetadata;
    this.exposureWindowsAtTestRegistrations = exposureWindowsAtTestRegistrations;
  }

  public Integer getTestResult() {
    return testResult;
  }

  public Set<ExposureWindowsAtTestRegistration> getExposureWindowsAtTestRegistrations() {
    return exposureWindowsAtTestRegistrations;
  }

  @Override
  public int hashCode() {
    return Objects.hash(id, testResult, clientMetadata, technicalMetadata, exposureWindowsAtTestRegistrations);
  }

  @Override
  public boolean equals(Object obj) {
    if (this == obj) {
      return true;
    }
    if (!super.equals(obj)) {
      return false;
    }
    ExposureWindowTestResult that = (ExposureWindowTestResult) obj;
    return Objects.equals(testResult, that.testResult)
        && Objects.equals(clientMetadata, that.clientMetadata)
        && Objects.equals(technicalMetadata, that.technicalMetadata)
        && Objects.equals(exposureWindowsAtTestRegistrations, that.exposureWindowsAtTestRegistrations);
  }
}
