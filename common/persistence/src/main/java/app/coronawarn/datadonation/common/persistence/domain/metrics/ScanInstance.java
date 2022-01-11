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
  public boolean equals(Object o) {
    if (this == o) {
      return true;
    }
    if (o == null || getClass() != o.getClass()) {
      return false;
    }
    ScanInstance that = (ScanInstance) o;
    return Objects.equals(id, that.id)
        && Objects.equals(exposureWindowId, that.exposureWindowId)
        && Objects.equals(minimumAttenuation, that.minimumAttenuation)
        && Objects.equals(secondsSinceLastScan, that.secondsSinceLastScan)
        && Objects.equals(typicalAttenuation, that.typicalAttenuation)
        && Objects.equals(technicalMetadata, that.technicalMetadata);
  }
}
