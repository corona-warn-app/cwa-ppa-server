package app.coronawarn.datadonation.common.persistence.domain.metrics;

import java.time.LocalDate;
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
  private final Integer callibrationConfidence;
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
  public ExposureWindowsAtTestRegistration(Long id, LocalDate date, Integer reportType, Integer infectiousness,
      Integer callibrationConfidence, Integer transmissionRiskLevel, Double normalizedTime,
      Integer exposureWindowTestResultId,
      Set<ScanInstancesAtTestRegistration> scanInstancesAtTestRegistration) {
    super(id);
    this.date = date;
    this.reportType = reportType;
    this.infectiousness = infectiousness;
    this.callibrationConfidence = callibrationConfidence;
    this.transmissionRiskLevel = transmissionRiskLevel;
    this.normalizedTime = normalizedTime;
    this.exposureWindowTestResultId = exposureWindowTestResultId;
    this.scanInstancesAtTestRegistration = scanInstancesAtTestRegistration;
  }
}
