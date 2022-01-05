package app.coronawarn.datadonation.common.persistence.domain.metrics;

import java.util.Objects;
import javax.validation.Valid;
import javax.validation.constraints.NotNull;
import org.springframework.data.relational.core.mapping.Embedded;
import org.springframework.data.relational.core.mapping.Embedded.OnEmpty;

public class ScanInstance extends DataDonationMetric {

  /**
   * Foreign key to reference the ID of the corresponding Exposure Window.
   */
  @NotNull
  private final Integer exposureWindowId;
  /**
   * The typical attenuation of the scan instance.¬
   */
  @NotNull
  private final Integer typicalAttenuation;
  /**
   * The minimum attenuation of the scan instance.
   */
  @NotNull
  private final Integer minimumAttenuation;
  /**
   * The second since last scan of the scan instance.
   */
  @NotNull
  private final Integer secondsSinceLastScan;

  @Valid
  @NotNull
  @Embedded(onEmpty = OnEmpty.USE_EMPTY)
  private final TechnicalMetadata technicalMetadata;

  /**
   * Constructs an immutable instance.
   */
  public ScanInstance(Long id, Integer exposureWindowId, Integer typicalAttenuation,
      Integer minimumAttenuation, Integer secondsSinceLastScan, TechnicalMetadata technicalMetadata) {
    super(id);
    this.exposureWindowId = exposureWindowId;
    this.typicalAttenuation = typicalAttenuation;
    this.minimumAttenuation = minimumAttenuation;
    this.secondsSinceLastScan = secondsSinceLastScan;
    this.technicalMetadata = technicalMetadata;

  }

  public Integer getExposureWindowId() {
    return exposureWindowId;
  }

  public Integer getTypicalAttenuation() {
    return typicalAttenuation;
  }

  public Integer getMinimumAttenuation() {
    return minimumAttenuation;
  }

  public Integer getSecondsSinceLastScan() {
    return secondsSinceLastScan;
  }

  public TechnicalMetadata getTechnicalMetadata() {
    return technicalMetadata;
  }

  @Override
  public int hashCode() {
    return Objects.hash(id, exposureWindowId, minimumAttenuation, secondsSinceLastScan,
        typicalAttenuation, technicalMetadata);
  }

  @Override
  public boolean equals(Object obj) { //NOSONAR complexity
    if (this == obj) {
      return true;
    }
    if (obj == null || getClass() != obj.getClass()) {
      return false;
    }
    ScanInstance other = (ScanInstance) obj;
    if (exposureWindowId == null) {
      if (other.exposureWindowId != null) {
        return false;
      }
    } else if (!exposureWindowId.equals(other.exposureWindowId)) {
      return false;
    }
    if (id == null) {
      if (other.id != null) {
        return false;
      }
    } else if (!id.equals(other.id)) {
      return false;
    }
    if (minimumAttenuation == null) {
      if (other.minimumAttenuation != null) {
        return false;
      }
    } else if (!minimumAttenuation.equals(other.minimumAttenuation)) {
      return false;
    }
    if (secondsSinceLastScan == null) {
      if (other.secondsSinceLastScan != null) {
        return false;
      }
    } else if (!secondsSinceLastScan.equals(other.secondsSinceLastScan)) {
      return false;
    }
    if (typicalAttenuation == null) {
      if (other.typicalAttenuation != null) {
        return false;
      }
    } else if (!typicalAttenuation.equals(other.typicalAttenuation)) {
      return false;
    }
    if (technicalMetadata == null) {
      return other.technicalMetadata == null;
    } else {
      return technicalMetadata.equals(other.technicalMetadata);
    }
  }
}
