package app.coronawarn.datadonation.common.persistence.domain.metrics;

import app.coronawarn.datadonation.common.persistence.domain.metrics.embeddable.ClientMetadataDetails;
import java.time.LocalDate;
import java.util.Objects;
import java.util.Set;
import javax.validation.constraints.NotNull;
import org.springframework.data.relational.core.mapping.Embedded;
import org.springframework.data.relational.core.mapping.Embedded.OnEmpty;
import org.springframework.data.relational.core.mapping.MappedCollection;

public class ExposureWindow extends DataDonationMetric {

  @NotNull
  private final LocalDate date;
  @NotNull
  private final Integer reportType;
  @NotNull
  private final Integer infectiousness;
  @NotNull
  private final Integer calibrationConfidence;
  @NotNull
  private final Integer transmissionRiskLevel;
  @NotNull
  private final Double normalizedTime;

  @Embedded(onEmpty = OnEmpty.USE_EMPTY)
  private final ClientMetadataDetails clientMetadata;
  @Embedded(onEmpty = OnEmpty.USE_EMPTY)
  private final TechnicalMetadata technicalMetadata;

  @MappedCollection(idColumn = "exposure_window_id")
  private final Set<ScanInstance> scanInstances;

  /**
   * Constructs an immutable instance.
   */
  public ExposureWindow(Long id, LocalDate date, Integer reportType, //NOSONAR number of parameters
      Integer infectiousness, Integer calibrationConfidence, Integer transmissionRiskLevel, Double normalizedTime,
      ClientMetadataDetails clientMetadata, TechnicalMetadata technicalMetadata, Set<ScanInstance> scanInstances) {
    super(id);
    this.date = date;
    this.reportType = reportType;
    this.infectiousness = infectiousness;
    this.calibrationConfidence = calibrationConfidence;
    this.transmissionRiskLevel = transmissionRiskLevel;
    this.normalizedTime = normalizedTime;
    this.clientMetadata = clientMetadata;
    this.technicalMetadata = technicalMetadata;
    this.scanInstances = Set.copyOf(scanInstances);
  }

  public LocalDate getDate() {
    return date;
  }

  public Integer getReportType() {
    return reportType;
  }

  public Integer getInfectiousness() {
    return infectiousness;
  }

  public Integer getCalibrationConfidence() {
    return calibrationConfidence;
  }

  public Integer getTransmissionRiskLevel() {
    return transmissionRiskLevel;
  }

  public Double getNormalizedTime() {
    return normalizedTime;
  }

  public ClientMetadataDetails getClientMetadata() {
    return clientMetadata;
  }

  public TechnicalMetadata getTechnicalMetadata() {
    return technicalMetadata;
  }

  public Set<ScanInstance> getScanInstances() {
    return scanInstances;
  }

  @Override
  public int hashCode() {
    return Objects.hash(calibrationConfidence, clientMetadata, date, infectiousness,
        normalizedTime, scanInstances, transmissionRiskLevel);
  }

  @Override
  public boolean equals(Object obj) { //NOSONAR complexity of equals
    if (this == obj) {
      return true;
    }
    if (obj == null || getClass() != obj.getClass()) {
      return false;
    }

    ExposureWindow other = (ExposureWindow) obj;
    if (calibrationConfidence == null) {
      if (other.calibrationConfidence != null) {
        return false;
      }
    } else if (!calibrationConfidence.equals(other.calibrationConfidence)) {
      return false;
    }
    if (clientMetadata == null) {
      if (other.clientMetadata != null) {
        return false;
      }
    } else if (!clientMetadata.equals(other.clientMetadata)) {
      return false;
    }
    if (date == null) {
      if (other.date != null) {
        return false;
      }
    } else if (!date.equals(other.date)) {
      return false;
    }
    if (infectiousness == null) {
      if (other.infectiousness != null) {
        return false;
      }
    } else if (!infectiousness.equals(other.infectiousness)) {
      return false;
    }
    if (normalizedTime == null) {
      if (other.normalizedTime != null) {
        return false;
      }
    } else if (!normalizedTime.equals(other.normalizedTime)) {
      return false;
    }
    if (reportType == null) {
      if (other.reportType != null) {
        return false;
      }
    } else if (!reportType.equals(other.reportType)) {
      return false;
    }
    if (scanInstances == null) {
      if (other.scanInstances != null) {
        return false;
      }
    } else if (!scanInstances.equals(other.scanInstances)) {
      return false;
    }
    if (technicalMetadata == null) {
      if (other.technicalMetadata != null) {
        return false;
      }
    } else if (!technicalMetadata.equals(other.technicalMetadata)) {
      return false;
    }
    if (transmissionRiskLevel == null) {
      return other.transmissionRiskLevel == null;
    } else {
      return transmissionRiskLevel.equals(other.transmissionRiskLevel);
    }
  }
}
