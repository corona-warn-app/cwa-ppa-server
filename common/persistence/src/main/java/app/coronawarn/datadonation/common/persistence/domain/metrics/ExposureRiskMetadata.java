package app.coronawarn.datadonation.common.persistence.domain.metrics;

import app.coronawarn.datadonation.common.persistence.domain.metrics.embeddable.UserMetadataDetails;
import java.time.LocalDate;
import java.util.Objects;
import javax.validation.constraints.NotNull;
import org.hibernate.validator.constraints.Range;
import org.springframework.data.relational.core.mapping.Embedded;
import org.springframework.data.relational.core.mapping.Embedded.OnEmpty;

public class ExposureRiskMetadata extends DataDonationMetric {

  private static final long MIN_RISK_LEVEL = 0;
  private static final long MAX_RISK_LEVEL = 3;

  /**
   * The risk level reported by the client ({@value #MIN_RISK_LEVEL} to {@value #MAX_RISK_LEVEL}).
   */
  @Range(min = MIN_RISK_LEVEL, max = MAX_RISK_LEVEL,
      message = "Risk Level must be in between " + MIN_RISK_LEVEL + " and " + MAX_RISK_LEVEL + ".")
  private final Integer riskLevel;

  /**
   * The risk level reported by the client from check-in-based presence tracing.
   * ({@value #MIN_RISK_LEVEL} to {@value #MAX_RISK_LEVEL}).
   */
  @Range(min = MIN_RISK_LEVEL, max = MAX_RISK_LEVEL,
      message = "Risk Level must be in between " + MIN_RISK_LEVEL + " and " + MAX_RISK_LEVEL + ".")
  private final Integer ptRiskLevel;

  /**
   * Boolean to indicate if the Risk Level changed compared to the previous submission of the
   * client.
   */
  @NotNull
  private final Boolean riskLevelChanged;
  /**
   * Boolean to indicate if the Risk Level changed compared to the previous submission of the
   * client.
   */
  private final Boolean ptRiskLevelChanged;
  /**
   * The date of the most recent encounter at the given risk level (i.e. what is displayed on the
   * risk card)
   */
  @NotNull
  private final LocalDate mostRecentDateAtRiskLevel;
  /**
   * The date of the most recent encounter at the given risk level (i.e. what is displayed on the
   * risk card)
   */
  private final LocalDate ptMostRecentDateAtRiskLevel;
  /**
   * Boolean to indicate if the date changed compared to the previous submission of the client.
   */
  @NotNull
  private final Boolean mostRecentDateChanged;
  /**
   * Boolean to indicate if the date changed compared to the previous submission of the client.
   */
  private final Boolean ptMostRecentDateChanged;

  @Embedded(onEmpty = OnEmpty.USE_EMPTY)
  private final UserMetadataDetails userMetadata;

  @Embedded(onEmpty = OnEmpty.USE_EMPTY)
  private final TechnicalMetadata technicalMetadata;

  /**
   * constructs an immutable instance.
   */
  public ExposureRiskMetadata(Long id, Integer riskLevel,
      Boolean riskLevelChanged,
      LocalDate mostRecentDateAtRiskLevel,
      Boolean mostRecentDateChanged,
      Integer ptRiskLevel,
      Boolean ptRiskLevelChanged,
      LocalDate ptMostRecentDateAtRiskLevel,
      Boolean ptMostRecentDateChanged,
      UserMetadataDetails userMetadata,
      TechnicalMetadata technicalMetadata) {
    super(id);
    this.riskLevel = riskLevel;
    this.ptRiskLevel = ptRiskLevel;
    this.riskLevelChanged = riskLevelChanged;
    this.ptRiskLevelChanged = ptRiskLevelChanged;
    this.mostRecentDateAtRiskLevel = mostRecentDateAtRiskLevel;
    this.ptMostRecentDateAtRiskLevel = ptMostRecentDateAtRiskLevel;
    this.mostRecentDateChanged = mostRecentDateChanged;
    this.ptMostRecentDateChanged = ptMostRecentDateChanged;
    this.userMetadata = userMetadata;
    this.technicalMetadata = technicalMetadata;
  }

  public Integer getRiskLevel() {
    return riskLevel;
  }

  public Boolean getRiskLevelChanged() {
    return riskLevelChanged;
  }

  public LocalDate getMostRecentDateAtRiskLevel() {
    return mostRecentDateAtRiskLevel;
  }

  public Boolean getMostRecentDateChanged() {
    return mostRecentDateChanged;
  }

  public UserMetadataDetails getUserMetadata() {
    return userMetadata;
  }

  public TechnicalMetadata getTechnicalMetadata() {
    return technicalMetadata;
  }

  public Integer getPtRiskLevel() {
    return ptRiskLevel;
  }

  public Boolean getPtRiskLevelChanged() {
    return ptRiskLevelChanged;
  }

  public LocalDate getPtMostRecentDateAtRiskLevel() {
    return ptMostRecentDateAtRiskLevel;
  }

  public Boolean getPtMostRecentDateChanged() {
    return ptMostRecentDateChanged;
  }

  @Override
  public int hashCode() {
    return Objects.hash(id, mostRecentDateAtRiskLevel, mostRecentDateChanged, riskLevel,
        riskLevelChanged, ptRiskLevel, ptRiskLevelChanged, ptMostRecentDateAtRiskLevel, ptRiskLevelChanged,
        technicalMetadata, userMetadata);
  }

  @Override
  public boolean equals(Object obj) {

    if (this == obj) {
      return true;
    }
    if (obj == null || getClass() != obj.getClass()) {
      return false;
    }

    ExposureRiskMetadata other = (ExposureRiskMetadata) obj;
    if (id == null) {
      if (other.id != null) {
        return false;
      }
    } else if (!id.equals(other.id)) {
      return false;
    }
    if (mostRecentDateAtRiskLevel == null) {
      if (other.mostRecentDateAtRiskLevel != null) {
        return false;
      }
    } else if (!mostRecentDateAtRiskLevel.equals(other.mostRecentDateAtRiskLevel)) {
      return false;
    }
    if (mostRecentDateChanged == null) {
      if (other.mostRecentDateChanged != null) {
        return false;
      }
    } else if (!mostRecentDateChanged.equals(other.mostRecentDateChanged)) {
      return false;
    }
    if (riskLevel == null) {
      if (other.riskLevel != null) {
        return false;
      }
    } else if (!riskLevel.equals(other.riskLevel)) {
      return false;
    }
    if (riskLevelChanged == null) {
      if (other.riskLevelChanged != null) {
        return false;
      }
    } else if (!riskLevelChanged.equals(other.riskLevelChanged)) {
      return false;
    }
    if (ptMostRecentDateAtRiskLevel == null) {
      if (other.ptMostRecentDateAtRiskLevel != null) {
        return false;
      }
    } else if (!ptMostRecentDateAtRiskLevel.equals(other.ptMostRecentDateAtRiskLevel)) {
      return false;
    }
    if (ptMostRecentDateChanged == null) {
      if (other.ptMostRecentDateChanged != null) {
        return false;
      }
    } else if (!ptMostRecentDateChanged.equals(other.ptMostRecentDateChanged)) {
      return false;
    }
    if (ptRiskLevel == null) {
      if (other.ptRiskLevel != null) {
        return false;
      }
    } else if (!ptRiskLevel.equals(other.ptRiskLevel)) {
      return false;
    }
    if (ptRiskLevelChanged == null) {
      if (other.ptRiskLevelChanged != null) {
        return false;
      }
    } else if (!ptRiskLevelChanged.equals(other.ptRiskLevelChanged)) {
      return false;
    }
    if (technicalMetadata == null) {
      if (other.technicalMetadata != null) {
        return false;
      }
    } else if (!technicalMetadata.equals(other.technicalMetadata)) {
      return false;
    }
    if (userMetadata == null) {
      if (other.userMetadata != null) {
        return false;
      }
    } else if (!userMetadata.equals(other.userMetadata)) {
      return false;
    }
    return true;
  }
}
