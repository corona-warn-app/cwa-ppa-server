package app.coronawarn.datadonation.common.persistence.domain.metrics;

import java.time.LocalDate;
import java.util.Objects;
import java.util.Set;
import javax.validation.Valid;
import javax.validation.constraints.NotNull;
import org.springframework.data.relational.core.mapping.Embedded;
import org.springframework.data.relational.core.mapping.Embedded.OnEmpty;
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
  @NotNull
  private final Boolean afterTestRegistration;

  @MappedCollection(idColumn = "exposure_window_id")
  private final Set<ScanInstancesAtTestRegistration> scanInstancesAtTestRegistration;

  @Valid
  @NotNull
  @Embedded(onEmpty = OnEmpty.USE_EMPTY)
  private final TechnicalMetadata technicalMetadata;

  /**
   * Constructs an immutable instance.
   */
  public ExposureWindowsAtTestRegistration(Long id, Integer exposureWindowTestResultId, //NOSONAR parameters
      LocalDate date, Integer reportType, Integer infectiousness,
      Integer calibrationConfidence, Integer transmissionRiskLevel, Double normalizedTime,
      Set<ScanInstancesAtTestRegistration> scanInstancesAtTestRegistration, Boolean afterTestRegistration,
      TechnicalMetadata technicalMetadata) {
    super(id);
    this.exposureWindowTestResultId = exposureWindowTestResultId;
    this.date = date;
    this.reportType = reportType;
    this.infectiousness = infectiousness;
    this.calibrationConfidence = calibrationConfidence;
    this.transmissionRiskLevel = transmissionRiskLevel;
    this.normalizedTime = normalizedTime;
    this.scanInstancesAtTestRegistration = scanInstancesAtTestRegistration;
    this.afterTestRegistration = afterTestRegistration;
    this.technicalMetadata = technicalMetadata;
  }

  @Override
  public boolean equals(Object obj) { //NOSONAR complexity
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
    if (afterTestRegistration == null) {
      if (other.afterTestRegistration != null) {
        return false;
      }
    } else if (!afterTestRegistration.equals(other.afterTestRegistration)) {
      return false;
    }
    if (technicalMetadata == null) {
      return other.technicalMetadata == null;
    } else {
      return technicalMetadata.equals(other.technicalMetadata);
    }
  }

  @Override
  public int hashCode() {
    return Objects.hash(id, exposureWindowTestResultId, date, reportType, infectiousness, calibrationConfidence,
        transmissionRiskLevel, normalizedTime, scanInstancesAtTestRegistration, technicalMetadata);
  }
}
