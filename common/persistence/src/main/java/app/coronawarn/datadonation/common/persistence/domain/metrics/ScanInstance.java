package app.coronawarn.datadonation.common.persistence.domain.metrics;

import java.util.Objects;
import org.springframework.data.annotation.Id;

public class ScanInstance {

  @Id
  private final Long id;
  /**
   * Foreign key to reference the ID of the corresponding Exposure Window.
   */
  private final Integer exposureWindowId;
  /**
   * The typical attenuation of the scan instance.
   */
  private final Integer typicalAttenuation;
  /**
   * The minimum attenuation of the scan instance.
   */
  private final Integer minimumAttenuation;
  /**
   * The second since last scan of the scan instance.
   */
  private final Integer secondsSinceLastScan;

  /**
   * Constructs an immutable instance.
   */
  public ScanInstance(Long id, Integer exposureWindowId, Integer typicalAttenuation,
      Integer minimumAttenuation, Integer secondsSinceLastScan) {
    this.id = id;
    this.exposureWindowId = exposureWindowId;
    this.typicalAttenuation = typicalAttenuation;
    this.minimumAttenuation = minimumAttenuation;
    this.secondsSinceLastScan = secondsSinceLastScan;
  }

  public Long getId() {
    return id;
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
    return true;
  }
}