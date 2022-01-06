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
  public boolean equals(Object obj) { //NOSONAR complexity
    if (this == obj) {
      return true;
    }
    if (obj == null || getClass() != obj.getClass()) {
      return false;
    }

    SummarizedExposureWindowsWithUserMetadata other = (SummarizedExposureWindowsWithUserMetadata) obj;
    if (batchId == null) {
      if (other.batchId != null) {
        return false;
      }
    } else if (!batchId.equals(other.batchId)) {
      return false;
    }

    if (date == null) {
      if (other.date != null) {
        return false;
      }
    } else if (!date.equals(other.date)) {
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
    if (technicalMetadata == null) {
      if (other.technicalMetadata != null) {
        return false;
      }
    } else if (!technicalMetadata.equals(other.technicalMetadata)) {
      return false;
    }
    if (userMetadataDetails == null) {
      return other.userMetadataDetails == null;
    } else {
      return userMetadataDetails.equals(other.userMetadataDetails);
    }
  }

  @Override
  public int hashCode() {
    return Objects
        .hash(id, date, batchId, transmissionRiskLevel, normalizedTime, userMetadataDetails, technicalMetadata);
  }
}
