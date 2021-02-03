package app.coronawarn.datadonation.common.persistence.domain.metrics;

import java.time.LocalDate;
import org.springframework.data.annotation.Id;
import org.springframework.data.relational.core.mapping.Embedded;
import org.springframework.data.relational.core.mapping.Embedded.OnEmpty;

public class ExposureWindow {
  
  @Id
  private final Long id;
  private final LocalDate date;
  private final Integer reportType;
  private final Integer infectiousness;
  private final Integer callibrationConfidence;
  private final Integer transmissionRiskLevel;
  private final Integer normalizedTime;
  
  @Embedded(onEmpty = OnEmpty.USE_EMPTY)
  private final ClientMetadata clientMetadata;
  @Embedded(onEmpty = OnEmpty.USE_EMPTY)
  private final TechnicalMetadata technicalMetadata;

  /**
   * Constructs an immutable instance.
   */
  public ExposureWindow(Long id, LocalDate date, Integer reportType, Integer infectiousness,
      Integer callibrationConfidence, Integer transmissionRiskLevel, Integer normalizedTime,
      ClientMetadata clientMetadata, TechnicalMetadata technicalMetadata) {
    this.id = id;
    this.date = date;
    this.reportType = reportType;
    this.infectiousness = infectiousness;
    this.callibrationConfidence = callibrationConfidence;
    this.transmissionRiskLevel = transmissionRiskLevel;
    this.normalizedTime = normalizedTime;
    this.clientMetadata = clientMetadata;
    this.technicalMetadata = technicalMetadata;
  }

  public Long getId() {
    return id;
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

  public Integer getNormalizedTime() {
    return normalizedTime;
  }

  public ClientMetadata getClientMetadata() {
    return clientMetadata;
  }

  public TechnicalMetadata getTechnicalMetadata() {
    return technicalMetadata;
  }
}
