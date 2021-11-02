package app.coronawarn.datadonation.common.persistence.domain.metrics;

import java.time.LocalDate;
import java.util.Objects;
import java.util.Set;
import javax.validation.constraints.NotNull;
import org.springframework.data.relational.core.mapping.MappedCollection;

public class ExposureWindowsAtTestRegistration extends DataDonationMetric {

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
  @NotNull
  private final Integer exposureWindowTestResultId;

  @MappedCollection(idColumn = "exposure_window_id")
  private final Set<ScanInstancesAtTestRegistration> scanInstancesAtTestRegistration;

  /**
   * Constructs an immutable instance.
   */
  public ExposureWindowsAtTestRegistration(Long id, Integer exposureWindowTestResultId,
      LocalDate date, Integer reportType, Integer infectiousness,
      Integer calibrationConfidence, Integer transmissionRiskLevel, Double normalizedTime,
      Set<ScanInstancesAtTestRegistration> scanInstancesAtTestRegistration) {
    super(id);
    this.exposureWindowTestResultId = exposureWindowTestResultId;
    this.date = date;
    this.reportType = reportType;
    this.infectiousness = infectiousness;
    this.calibrationConfidence = calibrationConfidence;
    this.transmissionRiskLevel = transmissionRiskLevel;
    this.normalizedTime = normalizedTime;
    this.scanInstancesAtTestRegistration = scanInstancesAtTestRegistration;
  }

  @Override
  public boolean equals(Object obj) {
    if (this == obj) {
      return true;
    }
    if (obj == null || getClass() != obj.getClass()) {
      return false;
    }
    ExposureWindowsAtTestRegistration other = (ExposureWindowsAtTestRegistration) obj;
    if (exposureWindowTestResultId == null) {
      if (other.exposureWindowTestResultId != null) {
        return false;
      }
    } else if (!exposureWindowTestResultId.equals(other.exposureWindowTestResultId)) {
      return false;
    }
    if (date == null) {
      if (other.date != null) {
        return false;
      }
    } else if (!date.equals(other.date)) {
      return false;
    }
    if (reportType == null) {
      if (other.reportType != null) {
        return false;
      }
    } else if (!reportType.equals(other.reportType)) {
      return false;
    }
    if (infectiousness == null) {
      if (other.infectiousness != null) {
        return false;
      }
    } else if (!infectiousness.equals(other.infectiousness)) {
      return false;
    }
    if (calibrationConfidence == null) {
      if (other.calibrationConfidence != null) {
        return false;
      }
    } else if (!calibrationConfidence.equals(other.calibrationConfidence)) {
      return false;
    }
    if (transmissionRiskLevel == null) {
      if (other.transmissionRiskLevel != null) {
        return false;
      }
    } else if (!transmissionRiskLevel.equals(other.transmissionRiskLevel)) {
      return false;
    }
    if (normalizedTime == null) {
      if (other.normalizedTime != null) {
        return false;
      }
    } else if (!normalizedTime.equals(other.normalizedTime)) {
      return false;
    }
    if (scanInstancesAtTestRegistration == null) {
      if (other.scanInstancesAtTestRegistration != null) {
        return false;
      }
    } else if (!scanInstancesAtTestRegistration.equals(other.scanInstancesAtTestRegistration)) {
      return false;
    }
    return true;
  }

  @Override
  public int hashCode() {
    return Objects.hash(id, exposureWindowTestResultId, date, reportType, infectiousness, calibrationConfidence,
        transmissionRiskLevel, normalizedTime, scanInstancesAtTestRegistration);
  }
}
