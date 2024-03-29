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
  public ExposureWindowsAtTestRegistration(Long id, // NOSONAR number of parameters
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
    if (!super.equals(obj)) {
      return false;
    }
    ExposureWindowsAtTestRegistration that = (ExposureWindowsAtTestRegistration) obj;
    return Objects.equals(afterTestRegistration, that.afterTestRegistration)
        && Objects.equals(reportType, that.reportType)
        && Objects.equals(infectiousness, that.infectiousness)
        && Objects.equals(calibrationConfidence, that.calibrationConfidence)
        && Objects.equals(transmissionRiskLevel, that.transmissionRiskLevel)
        && Objects.equals(exposureWindowTestResultId, that.exposureWindowTestResultId)
        && Objects.equals(normalizedTime, that.normalizedTime)
        && Objects.equals(date, that.date)
        && Objects.equals(technicalMetadata, that.technicalMetadata)
        && Objects.equals(scanInstancesAtTestRegistration, that.scanInstancesAtTestRegistration);
  }

  @Override
  public int hashCode() {
    return hash(id, date, reportType, infectiousness, calibrationConfidence, transmissionRiskLevel, normalizedTime,
        exposureWindowTestResultId, afterTestRegistration, scanInstancesAtTestRegistration, technicalMetadata);
  }
}
