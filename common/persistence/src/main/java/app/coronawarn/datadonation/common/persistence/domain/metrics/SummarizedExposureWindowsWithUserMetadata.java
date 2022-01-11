package app.coronawarn.datadonation.common.persistence.domain.metrics;

import app.coronawarn.datadonation.common.persistence.domain.metrics.embeddable.UserMetadataDetails;
import java.time.LocalDate;
import java.util.Objects;
import javax.validation.Valid;
import javax.validation.constraints.NotNull;
import org.springframework.data.relational.core.mapping.Embedded;
import org.springframework.data.relational.core.mapping.Embedded.OnEmpty;

public class SummarizedExposureWindowsWithUserMetadata extends DataDonationMetric {

  @NotNull
  private final String batchId;
  @NotNull
  private final LocalDate date;
  @NotNull
  private final Integer transmissionRiskLevel;
  @NotNull
  private final Double normalizedTime;

  @Valid
  @NotNull
  @Embedded(onEmpty = OnEmpty.USE_EMPTY)
  private final UserMetadataDetails userMetadataDetails;

  @Valid
  @NotNull
  @Embedded(onEmpty = OnEmpty.USE_EMPTY)
  private final TechnicalMetadata technicalMetadata;

  /**
   * constructs an immutable instance.
   */
  public SummarizedExposureWindowsWithUserMetadata(Long id, LocalDate date, String batchId,
      Integer transmissionRiskLevel, Double normalizedTime, UserMetadataDetails userMetadataDetails,
      TechnicalMetadata technicalMetadata) {
    super(id);
    this.date = date;
    this.batchId = batchId;
    this.transmissionRiskLevel = transmissionRiskLevel;
    this.normalizedTime = normalizedTime;
    this.userMetadataDetails = userMetadataDetails;
    this.technicalMetadata = technicalMetadata;
  }

  public String getBatchId() {
    return batchId;
  }

  public LocalDate getDate() {
    return date;
  }

  public Integer getTransmissionRiskLevel() {
    return transmissionRiskLevel;
  }

  public Double getNormalizedTime() {
    return normalizedTime;
  }

  public UserMetadataDetails getUserMetadataDetails() {
    return userMetadataDetails;
  }

  public TechnicalMetadata getTechnicalMetadata() {
    return technicalMetadata;
  }

  @Override
  public boolean equals(Object o) {
    if (this == o) {
      return true;
    }
    if (o == null || getClass() != o.getClass()) {
      return false;
    }
    SummarizedExposureWindowsWithUserMetadata that = (SummarizedExposureWindowsWithUserMetadata) o;
    return Objects.equals(id, that.id)
        && Objects.equals(date, that.date)
        && Objects.equals(batchId, that.batchId)
        && Objects.equals(transmissionRiskLevel, that.transmissionRiskLevel)
        && Objects.equals(normalizedTime, that.normalizedTime)
        && Objects.equals(userMetadataDetails, that.userMetadataDetails)
        && Objects.equals(technicalMetadata, that.technicalMetadata);
  }

  @Override
  public int hashCode() {
    return Objects.hash(
        id, date, batchId, transmissionRiskLevel, normalizedTime, userMetadataDetails, technicalMetadata);
  }
}
