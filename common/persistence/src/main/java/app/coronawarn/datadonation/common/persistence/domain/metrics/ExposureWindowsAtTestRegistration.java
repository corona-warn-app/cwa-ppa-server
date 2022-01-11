package app.coronawarn.datadonation.common.persistence.domain.metrics;

import static java.util.Objects.hash;

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
  public ExposureWindowsAtTestRegistration(Long id, //NOSONAR number of parameters
      Integer exposureWindowTestResultId, LocalDate date,
      Integer reportType, Integer infectiousness, Integer calibrationConfidence, Integer transmissionRiskLevel,
      Double normalizedTime, Set<ScanInstancesAtTestRegistration> scanInstancesAtTestRegistration,
      Boolean afterTestRegistration, TechnicalMetadata technicalMetadata) {
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
  public boolean equals(Object obj) {
    if (this == obj) {
      return true;
    }
    if (obj == null || getClass() != obj.getClass()) {
      return false;
    }

    final ExposureWindowsAtTestRegistration other = (ExposureWindowsAtTestRegistration) obj;
    if (!Objects.equals(exposureWindowTestResultId, other.exposureWindowTestResultId)) {
      return false;
    }

    if (!Objects.equals(date, other.date)) {
      return false;
    }

    if (!Objects.equals(reportType, other.reportType)) {
      return false;
    }

    if (!Objects.equals(infectiousness, other.infectiousness)) {
      return false;
    }

    if (!Objects.equals(calibrationConfidence, other.calibrationConfidence)) {
      return false;
    }

    if (!Objects.equals(transmissionRiskLevel, other.transmissionRiskLevel)) {
      return false;
    }

    if (!Objects.equals(normalizedTime, other.normalizedTime)) {
      return false;
    }

    if (!Objects.equals(scanInstancesAtTestRegistration, other.scanInstancesAtTestRegistration)) {
      return false;
    }

    if (!Objects.equals(afterTestRegistration, other.afterTestRegistration)) {
      return false;
    }

    if (!Objects.equals(afterTestRegistration, other.afterTestRegistration)) {
      return false;
    }

    return Objects.equals(technicalMetadata, other.technicalMetadata);
  }

  @Override
  public int hashCode() {
    return hash(id, exposureWindowTestResultId, date, reportType, infectiousness, calibrationConfidence,
        transmissionRiskLevel, normalizedTime, scanInstancesAtTestRegistration, technicalMetadata);
  }
}
