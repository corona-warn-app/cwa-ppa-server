package app.coronawarn.datadonation.common.persistence.domain.metrics;

import app.coronawarn.datadonation.common.persistence.domain.metrics.embeddable.ClientMetadataDetails;
import jakarta.validation.constraints.NotNull;
import java.util.Objects;
import org.springframework.data.relational.core.mapping.Embedded;
import org.springframework.data.relational.core.mapping.Embedded.OnEmpty;

public class KeySubmissionMetadataWithClientMetadata extends DataDonationMetric {

  /**
   * Boolean to indicate if the client submitted keys.
   */
  @NotNull
  private final Boolean submitted;
  /**
   * Boolean to indicate if keys were submitted in background.
   */
  @NotNull
  private final Boolean submittedInBackground;
  /**
   * Boolean to indicate if keys were submitted after the user canceled the submission flow.
   */
  @NotNull
  private final Boolean submittedAfterCancel;
  /**
   * Boolean to indicate if keys were submitted after following the symptom flow.
   */
  @NotNull
  private final Boolean submittedAfterSymptomFlow;
  /**
   * Boolean to indicate if the user agreed to share keys when registering the test.
   */
  @NotNull
  private final Boolean advancedConsentGiven;
  /**
   * Screen ID of the last screen of the submission flow that was displayed to the user.
   */
  @NotNull
  private final Integer lastSubmissionFlowScreen;
  /**
   * Boolean to indicate if the test were submitted with checkins.
   */
  private final Boolean submittedWithCheckIns;

  @Embedded(onEmpty = OnEmpty.USE_EMPTY)
  private final ClientMetadataDetails clientMetadata;
  @Embedded(onEmpty = OnEmpty.USE_EMPTY)
  private final TechnicalMetadata technicalMetadata;

  /**
   * Constructs an immutable instance.
   */
  public KeySubmissionMetadataWithClientMetadata(Long id, Boolean submitted, //NOSONAR parameter number
      Boolean submittedInBackground, Boolean submittedAfterCancel,
      Boolean submittedAfterSymptomFlow, Boolean advancedConsentGiven,
      Integer lastSubmissionFlowScreen, Boolean submittedWithCheckIns, ClientMetadataDetails clientMetadata,
      TechnicalMetadata technicalMetadata) {
    super(id);
    this.submitted = submitted;
    this.submittedInBackground = submittedInBackground;
    this.submittedAfterCancel = submittedAfterCancel;
    this.submittedAfterSymptomFlow = submittedAfterSymptomFlow;
    this.advancedConsentGiven = advancedConsentGiven;
    this.lastSubmissionFlowScreen = lastSubmissionFlowScreen;
    this.submittedWithCheckIns = submittedWithCheckIns;
    this.clientMetadata = clientMetadata;
    this.technicalMetadata = technicalMetadata;
  }

  public Boolean getSubmitted() {
    return submitted;
  }

  public Boolean getSubmittedInBackground() {
    return submittedInBackground;
  }

  public Boolean getSubmittedAfterCancel() {
    return submittedAfterCancel;
  }

  public Boolean getSubmittedAfterSymptomFlow() {
    return submittedAfterSymptomFlow;
  }

  public Boolean getAdvancedConsentGiven() {
    return advancedConsentGiven;
  }

  public Integer getLastSubmissionFlowScreen() {
    return lastSubmissionFlowScreen;
  }

  public Boolean getSubmittedWithCheckIns() {
    return submittedWithCheckIns;
  }

  public ClientMetadataDetails getClientMetadata() {
    return clientMetadata;
  }

  public TechnicalMetadata getTechnicalMetadata() {
    return technicalMetadata;
  }

  @Override
  public int hashCode() {
    return Objects.hash(id, clientMetadata, technicalMetadata, advancedConsentGiven,
        lastSubmissionFlowScreen, submitted, submittedAfterCancel, submittedAfterSymptomFlow,
        submittedInBackground, submittedWithCheckIns);
  }

  @Override
  public boolean equals(Object obj) {
    if (this == obj) {
      return true;
    }
    if (!super.equals(obj)) {
      return false;
    }
    KeySubmissionMetadataWithClientMetadata that = (KeySubmissionMetadataWithClientMetadata) obj;
    return Objects.equals(submitted, that.submitted)
        && Objects.equals(submittedInBackground, that.submittedInBackground)
        && Objects.equals(submittedAfterCancel, that.submittedAfterCancel)
        && Objects.equals(submittedAfterSymptomFlow, that.submittedAfterSymptomFlow)
        && Objects.equals(advancedConsentGiven, that.advancedConsentGiven)
        && Objects.equals(submittedWithCheckIns, that.submittedWithCheckIns)
        && Objects.equals(lastSubmissionFlowScreen, that.lastSubmissionFlowScreen)
        && Objects.equals(clientMetadata, that.clientMetadata)
        && Objects.equals(technicalMetadata, that.technicalMetadata);
  }
}
