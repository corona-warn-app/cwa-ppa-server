package app.coronawarn.datadonation.common.persistence.domain.metrics;

import java.util.Objects;
import javax.validation.constraints.NotNull;
import org.springframework.data.relational.core.mapping.Embedded;
import org.springframework.data.relational.core.mapping.Embedded.OnEmpty;

public class KeySubmissionMetadataWithUserMetadata extends DataDonationMetric {

  /**
   * Boolean to indicate if the client submitted keys.
   */
  @NotNull
  private final Boolean submitted;
  /**
   * Boolean to indicate if keys were submitted after following the symptom flow.
   */
  @NotNull
  private final Boolean submittedAfterSymptomFlow;
  /**
   * Boolean to indicate if keys were submitted due to a TeleTAN.
   */
  @NotNull
  private final Boolean submittedWithTeletan;
  /**
   * The hours since the test was registered on the device.
   */
  @NotNull
  private final Integer hoursSinceReceptionOfTestResult;
  /**
   * The hours since the test was registered on the device.
   */
  @NotNull
  private final Integer hoursSinceTestRegistration;
  /**
   * The number of days since the most recent encounter at the given risk level at test
   * registration.
   */
  @NotNull
  private final Integer daysSinceMostRecentDateAtRiskLevelAtTestRegistration;
  /**
   * The hours since a high risk warning was issued and the test was registered.
   */
  @NotNull
  private final Integer hoursSinceHighRiskWarningAtTestRegistration;

  @Embedded(onEmpty = OnEmpty.USE_EMPTY)
  private final UserMetadata userMetadata;

  @Embedded(onEmpty = OnEmpty.USE_EMPTY)
  private final TechnicalMetadata technicalMetadata;


  /**
   * Constructs an immutable instance.
   */
  public KeySubmissionMetadataWithUserMetadata(Long id, Boolean submitted,
      Boolean submittedAfterSymptomFlow, Boolean submittedWithTeletan,
      Integer hoursSinceReceptionOfTestResult, Integer hoursSinceTestRegistration,
      Integer daysSinceMostRecentDateAtRiskLevelAtTestRegistration,
      Integer hoursSinceHighRiskWarningAtTestRegistration, UserMetadata userMetadata,
      TechnicalMetadata technicalMetadata) {
    super(id);
    this.submitted = submitted;
    this.submittedAfterSymptomFlow = submittedAfterSymptomFlow;
    this.submittedWithTeletan = submittedWithTeletan;
    this.hoursSinceReceptionOfTestResult = hoursSinceReceptionOfTestResult;
    this.hoursSinceTestRegistration = hoursSinceTestRegistration;
    this.daysSinceMostRecentDateAtRiskLevelAtTestRegistration =
        daysSinceMostRecentDateAtRiskLevelAtTestRegistration;
    this.hoursSinceHighRiskWarningAtTestRegistration = hoursSinceHighRiskWarningAtTestRegistration;
    this.userMetadata = userMetadata;
    this.technicalMetadata = technicalMetadata;
  }

  public Boolean getSubmitted() {
    return submitted;
  }

  public Boolean getSubmittedAfterSymptomFlow() {
    return submittedAfterSymptomFlow;
  }

  public Boolean getSubmittedWithTeletan() {
    return submittedWithTeletan;
  }

  public Integer getHoursSinceReceptionOfTestResult() {
    return hoursSinceReceptionOfTestResult;
  }

  public Integer getHoursSinceTestRegistration() {
    return hoursSinceTestRegistration;
  }

  public Integer getDaysSinceMostRecentDateAtRiskLevelAtTestRegistration() {
    return daysSinceMostRecentDateAtRiskLevelAtTestRegistration;
  }

  public Integer getHoursSinceHighRiskWarningAtTestRegistration() {
    return hoursSinceHighRiskWarningAtTestRegistration;
  }

  public UserMetadata getUserMetadata() {
    return userMetadata;
  }

  public TechnicalMetadata getTechnicalMetadata() {
    return technicalMetadata;
  }

  @Override
  public int hashCode() {
    return Objects.hash(id, daysSinceMostRecentDateAtRiskLevelAtTestRegistration,
        hoursSinceHighRiskWarningAtTestRegistration, hoursSinceReceptionOfTestResult,
        hoursSinceTestRegistration, submittedAfterSymptomFlow, submittedWithTeletan,
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
    KeySubmissionMetadataWithUserMetadata other = (KeySubmissionMetadataWithUserMetadata) obj;
    if (daysSinceMostRecentDateAtRiskLevelAtTestRegistration == null) {
      if (other.daysSinceMostRecentDateAtRiskLevelAtTestRegistration != null) {
        return false;
      }
    } else if (!daysSinceMostRecentDateAtRiskLevelAtTestRegistration
        .equals(other.daysSinceMostRecentDateAtRiskLevelAtTestRegistration)) {
      return false;
    }
    if (hoursSinceHighRiskWarningAtTestRegistration == null) {
      if (other.hoursSinceHighRiskWarningAtTestRegistration != null) {
        return false;
      }
    } else if (!hoursSinceHighRiskWarningAtTestRegistration
        .equals(other.hoursSinceHighRiskWarningAtTestRegistration)) {
      return false;
    }
    if (hoursSinceReceptionOfTestResult == null) {
      if (other.hoursSinceReceptionOfTestResult != null) {
        return false;
      }
    } else if (!hoursSinceReceptionOfTestResult.equals(other.hoursSinceReceptionOfTestResult)) {
      return false;
    }
    if (hoursSinceTestRegistration == null) {
      if (other.hoursSinceTestRegistration != null) {
        return false;
      }
    } else if (!hoursSinceTestRegistration.equals(other.hoursSinceTestRegistration)) {
      return false;
    }
    if (id == null) {
      if (other.id != null) {
        return false;
      }
    } else if (!id.equals(other.id)) {
      return false;
    }
    if (submitted == null) {
      if (other.submitted != null) {
        return false;
      }
    } else if (!submitted.equals(other.submitted)) {
      return false;
    }
    if (submittedAfterSymptomFlow == null) {
      if (other.submittedAfterSymptomFlow != null) {
        return false;
      }
    } else if (!submittedAfterSymptomFlow.equals(other.submittedAfterSymptomFlow)) {
      return false;
    }
    if (submittedWithTeletan == null) {
      if (other.submittedWithTeletan != null) {
        return false;
      }
    } else if (!submittedWithTeletan.equals(other.submittedWithTeletan)) {
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