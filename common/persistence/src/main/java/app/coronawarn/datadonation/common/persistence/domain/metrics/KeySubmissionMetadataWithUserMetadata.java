package app.coronawarn.datadonation.common.persistence.domain.metrics;

import app.coronawarn.datadonation.common.persistence.domain.metrics.embeddable.CwaVersionMetadata;
import app.coronawarn.datadonation.common.persistence.domain.metrics.embeddable.UserMetadataDetails;
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
   * Boolean to indicate if keys were submitted as rapid antigen test.
   */
  @NotNull
  private final Boolean submittedAfterRapidAntigenTest;
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
  /**
   * The number of days since the most recent encounter at the given risk level at test
   * registration.
   */
  private final Integer ptDaysSinceMostRecentDateAtRiskLevel;
  /**
   * The hours since the test was registered on the device.
   */
  private final Integer ptHoursSinceHighRiskWarning;

  @Embedded(onEmpty = OnEmpty.USE_EMPTY)
  private final UserMetadataDetails userMetadata;

  @Embedded(onEmpty = OnEmpty.USE_EMPTY)
  private final TechnicalMetadata technicalMetadata;

  @Embedded(onEmpty = OnEmpty.USE_EMPTY)
  private final CwaVersionMetadata cwaVersionMetadata;

  /**
   * Constructs an immutable instance.
   */
  public KeySubmissionMetadataWithUserMetadata(Long id, Boolean submitted, //NOSONAR parameters
      Boolean submittedAfterSymptomFlow, Boolean submittedWithTeletan, Boolean submittedAfterRapidAntigenTest,
      Integer hoursSinceReceptionOfTestResult, Integer hoursSinceTestRegistration,
      Integer daysSinceMostRecentDateAtRiskLevelAtTestRegistration,
      Integer hoursSinceHighRiskWarningAtTestRegistration,
      Integer ptDaysSinceMostRecentDateAtRiskLevel, Integer ptHoursSinceHighRiskWarning,
      UserMetadataDetails userMetadata,
      TechnicalMetadata technicalMetadata,
      CwaVersionMetadata cwaVersionMetadata) {
    super(id);
    this.submitted = submitted;
    this.submittedAfterSymptomFlow = submittedAfterSymptomFlow;
    this.submittedWithTeletan = submittedWithTeletan;
    this.submittedAfterRapidAntigenTest = submittedAfterRapidAntigenTest;
    this.hoursSinceReceptionOfTestResult = hoursSinceReceptionOfTestResult;
    this.hoursSinceTestRegistration = hoursSinceTestRegistration;
    this.daysSinceMostRecentDateAtRiskLevelAtTestRegistration =
        daysSinceMostRecentDateAtRiskLevelAtTestRegistration;
    this.hoursSinceHighRiskWarningAtTestRegistration = hoursSinceHighRiskWarningAtTestRegistration;
    this.ptHoursSinceHighRiskWarning = ptHoursSinceHighRiskWarning;
    this.ptDaysSinceMostRecentDateAtRiskLevel = ptDaysSinceMostRecentDateAtRiskLevel;
    this.userMetadata = userMetadata;
    this.technicalMetadata = technicalMetadata;
    this.cwaVersionMetadata = cwaVersionMetadata;
  }

  public CwaVersionMetadata getCwaVersionMetadata() {
    return cwaVersionMetadata;
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

  public Boolean getSubmittedAfterRapidAntigenTest() {
    return submittedAfterRapidAntigenTest;
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

  public UserMetadataDetails getUserMetadata() {
    return userMetadata;
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
    KeySubmissionMetadataWithUserMetadata that = (KeySubmissionMetadataWithUserMetadata) o;
    return Objects.equals(id, that.id)
        && Objects.equals(submitted, that.submitted)
        && Objects.equals(submittedAfterSymptomFlow, that.submittedAfterSymptomFlow)
        && Objects.equals(submittedWithTeletan, that.submittedWithTeletan)
        && Objects.equals(submittedAfterRapidAntigenTest, that.submittedAfterRapidAntigenTest)
        && Objects.equals(hoursSinceReceptionOfTestResult, that.hoursSinceReceptionOfTestResult)
        && Objects.equals(hoursSinceTestRegistration, that.hoursSinceTestRegistration)
        && Objects.equals(daysSinceMostRecentDateAtRiskLevelAtTestRegistration,
            that.daysSinceMostRecentDateAtRiskLevelAtTestRegistration)
        && Objects.equals(hoursSinceHighRiskWarningAtTestRegistration,
            that.hoursSinceHighRiskWarningAtTestRegistration)
        && Objects.equals(ptDaysSinceMostRecentDateAtRiskLevel,
            that.ptDaysSinceMostRecentDateAtRiskLevel)
        && Objects.equals(ptHoursSinceHighRiskWarning,
            that.ptHoursSinceHighRiskWarning)
        && Objects.equals(userMetadata, that.userMetadata)
        && Objects.equals(technicalMetadata, that.technicalMetadata)
        && Objects.equals(cwaVersionMetadata, that.cwaVersionMetadata);
  }

  @Override
  public int hashCode() {
    return Objects.hash(id, submitted, submittedAfterSymptomFlow, submittedWithTeletan, submittedAfterRapidAntigenTest,
        hoursSinceReceptionOfTestResult, hoursSinceTestRegistration,
        daysSinceMostRecentDateAtRiskLevelAtTestRegistration, hoursSinceHighRiskWarningAtTestRegistration,
        ptDaysSinceMostRecentDateAtRiskLevel, ptHoursSinceHighRiskWarning, userMetadata, technicalMetadata,
        cwaVersionMetadata);
  }
}
