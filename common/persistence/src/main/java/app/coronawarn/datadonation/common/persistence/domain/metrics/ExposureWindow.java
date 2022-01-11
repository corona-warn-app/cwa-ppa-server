package app.coronawarn.datadonation.common.persistence.domain.metrics;

import app.coronawarn.datadonation.common.persistence.domain.metrics.embeddable.ClientMetadataDetails;
import java.time.LocalDate;
import java.util.Objects;
import java.util.Set;
import javax.validation.constraints.NotNull;
import org.springframework.data.relational.core.mapping.Column;
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
  @Column("callibration_confidence")
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
    return Objects.hash(id, date, reportType, infectiousness, calibrationConfidence,
        transmissionRiskLevel, normalizedTime, clientMetadata, technicalMetadata,
        scanInstances);
  }

  @Override
  public boolean equals(Object o) {
    if (this == o) {
      return true;
    }
    if (o == null || getClass() != o.getClass()) {
      return false;
    }
    ExposureWindow that = (ExposureWindow) o;
    return Objects.equals(id, that.id)
        && Objects.equals(date, that.date)
        && Objects.equals(reportType, that.reportType)
        && Objects.equals(infectiousness, that.infectiousness)
        && Objects.equals(calibrationConfidence, that.calibrationConfidence)
        && Objects.equals(transmissionRiskLevel, that.transmissionRiskLevel)
        && Objects.equals(normalizedTime, that.normalizedTime)
        && Objects.equals(clientMetadata, that.clientMetadata)
        && Objects.equals(technicalMetadata, that.technicalMetadata)
        && Objects.equals(scanInstances, that.scanInstances);
  }
}
