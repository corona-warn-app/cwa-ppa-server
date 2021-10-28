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
  public boolean equals(Object obj) {
    if (this == obj) {
      return true;
    }
    if (obj == null || getClass() != obj.getClass()) {
      return false;
    }
    ExposureWindowTestResult other = (ExposureWindowTestResult) obj;
    if (technicalMetadata == null) {
      if (other.technicalMetadata != null) {
        return false;
      }
    } else if (!technicalMetadata.equals(other.technicalMetadata)) {
      return false;
    }
    if (testResult == null) {
      if (other.testResult != null) {
        return false;
      }
    } else if (!testResult.equals(other.testResult)) {
      return false;
    }
    if (clientMetadata == null) {
      if (other.clientMetadata != null) {
        return false;
      }
    } else if (!clientMetadata.equals(other.clientMetadata)) {
      return false;
    }
    if (exposureWindowsAtTestRegistrations == null) {
      if (other.exposureWindowsAtTestRegistrations != null) {
        return false;
      }
    } else if (!exposureWindowsAtTestRegistrations.equals(other.exposureWindowsAtTestRegistrations)) {
      return false;
    }
    return true;
  }

  @Override
  public int hashCode() {
    return Objects.hash(id, testResult, clientMetadata, technicalMetadata, exposureWindowsAtTestRegistrations);
  }
}
