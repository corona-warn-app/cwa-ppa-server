package app.coronawarn.datadonation.common.persistence.domain.metrics;

import java.time.LocalDate;
import javax.validation.constraints.NotNull;
import org.springframework.data.relational.core.mapping.Embedded;
import org.springframework.data.relational.core.mapping.Embedded.OnEmpty;
import app.coronawarn.datadonation.common.persistence.domain.metrics.embeddable.ClientMetadataDetails;

public class ExposureWindow extends DataDonationMetric {
  
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
  
  @Embedded(onEmpty = OnEmpty.USE_EMPTY)
  private final ClientMetadataDetails clientMetadata;
  @Embedded(onEmpty = OnEmpty.USE_EMPTY)
  private final TechnicalMetadata technicalMetadata;

  /**
   * Constructs an immutable instance.
   */
  public ExposureWindow(Long id, LocalDate date, Integer reportType, Integer infectiousness,
      Integer callibrationConfidence, Integer transmissionRiskLevel, Double normalizedTime,
      ClientMetadataDetails clientMetadata, TechnicalMetadata technicalMetadata) {
    super(id);
    this.date = date;
    this.reportType = reportType;
    this.infectiousness = infectiousness;
    this.callibrationConfidence = callibrationConfidence;
    this.transmissionRiskLevel = transmissionRiskLevel;
    this.normalizedTime = normalizedTime;
    this.clientMetadata = clientMetadata;
    this.technicalMetadata = technicalMetadata;
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

  public Integer getCallibrationConfidence() {
    return callibrationConfidence;
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
}
