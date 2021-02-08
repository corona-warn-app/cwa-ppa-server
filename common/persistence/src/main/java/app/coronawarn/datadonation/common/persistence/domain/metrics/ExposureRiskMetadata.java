package app.coronawarn.datadonation.common.persistence.domain.metrics;

import java.time.LocalDate;
import java.util.Objects;
import org.springframework.data.annotation.Id;
import org.springframework.data.relational.core.mapping.Embedded;
import org.springframework.data.relational.core.mapping.Embedded.OnEmpty;

public class ExposureRiskMetadata {

  @Id
  private final Long id;
  /**
   * The risk level reported by the client (0 to 3).
   */
  private final Integer riskLevel;
  /**
   * Boolean to indicate if the Risk Level changed compared to the previous submission of the
   * client.
   */
  private final Boolean riskLevelChanged;
  /**
   * The date of the most recent encounter at the given risk level (i.e. what is displayed on the
   * risk card)
   */
  private final LocalDate mostRecentDateAtRiskLevel;
  /**
   * Boolean to indicate if the date changed compared to the previous submission of the client.
   */
  private final Boolean mostRecentDateChanged;
  
  @Embedded(onEmpty = OnEmpty.USE_EMPTY)
  private final UserMetadata userMetadata;
  
  @Embedded(onEmpty = OnEmpty.USE_EMPTY)
  private final TechnicalMetadata technicalMetadata;

  /**
   * constructs an immutable instance.
   */
  public ExposureRiskMetadata(Long id, Integer riskLevel, Boolean riskLevelChanged,
      LocalDate mostRecentDateAtRiskLevel, Boolean mostRecentDateChanged, UserMetadata userMetadata,
      TechnicalMetadata technicalMetadata) {
    this.id = id;
    this.riskLevel = riskLevel;
    this.riskLevelChanged = riskLevelChanged;
    this.mostRecentDateAtRiskLevel = mostRecentDateAtRiskLevel;
    this.mostRecentDateChanged = mostRecentDateChanged;
    this.userMetadata = userMetadata;
    this.technicalMetadata = technicalMetadata;
  }

  public Long getId() {
    return id;
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

  public UserMetadata getUserMetadata() {
    return userMetadata;
  }

  public TechnicalMetadata getTechnicalMetadata() {
    return technicalMetadata;
  }

  @Override
  public int hashCode() {
    return Objects.hash(id, mostRecentDateAtRiskLevel, mostRecentDateChanged, riskLevel,
        riskLevelChanged, technicalMetadata, userMetadata);
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
