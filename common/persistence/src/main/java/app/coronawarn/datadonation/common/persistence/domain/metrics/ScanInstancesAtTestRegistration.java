package app.coronawarn.datadonation.common.persistence.domain.metrics;

import java.util.Objects;
import javax.validation.constraints.NotNull;
import org.springframework.data.relational.core.mapping.Embedded;
import org.springframework.data.relational.core.mapping.Embedded.OnEmpty;

public class ScanInstancesAtTestRegistration extends DataDonationMetric {

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

  @Embedded(onEmpty = OnEmpty.USE_EMPTY)
  private final TechnicalMetadata technicalMetadata;

  /**
   * Constructs an immutable instance.
   */
  public ScanInstancesAtTestRegistration(Long id, Integer exposureWindowId, Integer typicalAttenuation,
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

  @Override
  public int hashCode() {
    return Objects.hash(id, exposureWindowId, minimumAttenuation, secondsSinceLastScan,
        typicalAttenuation, technicalMetadata);
  }

  @Override
  public boolean equals(Object obj) {
    if (this == obj) {
      return true;
    }
    if (!super.equals(obj)) {
      return false;
    }
    ScanInstancesAtTestRegistration that = (ScanInstancesAtTestRegistration) obj;
    return Objects.equals(exposureWindowId, that.exposureWindowId)
        && Objects.equals(minimumAttenuation, that.minimumAttenuation)
        && Objects.equals(secondsSinceLastScan, that.secondsSinceLastScan)
        && Objects.equals(typicalAttenuation, that.typicalAttenuation)
        && Objects.equals(technicalMetadata, that.technicalMetadata);
  }
}
