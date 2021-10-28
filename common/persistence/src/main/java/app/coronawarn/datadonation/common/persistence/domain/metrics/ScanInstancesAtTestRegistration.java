package app.coronawarn.datadonation.common.persistence.domain.metrics;

import java.time.LocalDate;
import java.time.ZoneOffset;
import java.util.Objects;
import javax.validation.constraints.NotNull;

public class ScanInstancesAtTestRegistration extends DataDonationMetric {

  /**
   * The date (no time information) of when the record was submitted to the server.
   */
  @NotNull
  private final LocalDate submittedAt;
  /**
   * Foreign key to reference the ID of the corresponding Exposure Window.
   */
  @NotNull
  private final Integer exposureWindowId;
  /**
   * The typical attenuation of the scan instance.
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

  /**
   * Constructs an immutable instance.
   */
  public ScanInstancesAtTestRegistration(Long id, Integer exposureWindowId, Integer typicalAttenuation,
      Integer minimumAttenuation, Integer secondsSinceLastScan, LocalDate submittedAt) {
    super(id);
    this.submittedAt = submittedAt == null ? LocalDate.now(ZoneOffset.UTC) : submittedAt;
    this.exposureWindowId = exposureWindowId;
    this.typicalAttenuation = typicalAttenuation;
    this.minimumAttenuation = minimumAttenuation;
    this.secondsSinceLastScan = secondsSinceLastScan;
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

  @Override
  public int hashCode() {
    return Objects.hash(id, exposureWindowId, minimumAttenuation, secondsSinceLastScan,
        typicalAttenuation);
  }

  @Override
  public boolean equals(Object obj) {
    if (this == obj) {
      return true;
    }
    if (obj == null || getClass() != obj.getClass()) {
      return false;
    }
    ScanInstancesAtTestRegistration other = (ScanInstancesAtTestRegistration) obj;
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
    return true;
  }
}
