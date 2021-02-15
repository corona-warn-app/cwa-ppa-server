package app.coronawarn.datadonation.common.persistence.domain.metrics;

import app.coronawarn.datadonation.common.persistence.domain.metrics.embeddable.ClientMetadataDetails;
import java.util.Objects;
import javax.validation.constraints.NotNull;
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

  @Embedded(onEmpty = OnEmpty.USE_EMPTY)
  private final ClientMetadataDetails clientMetadata;
  @Embedded(onEmpty = OnEmpty.USE_EMPTY)
  private final TechnicalMetadata technicalMetadata;

  /**
   * Constructs an immutable instance.
   */
  public KeySubmissionMetadataWithClientMetadata(Long id, Boolean submitted,
      Boolean submittedInBackground, Boolean submittedAfterCancel,
      Boolean submittedAfterSymptomFlow, Boolean advancedConsentGiven,
      Integer lastSubmissionFlowScreen, ClientMetadataDetails clientMetadata,
      TechnicalMetadata technicalMetadata) {
    super(id);
    this.submitted = submitted;
    this.submittedInBackground = submittedInBackground;
    this.submittedAfterCancel = submittedAfterCancel;
    this.submittedAfterSymptomFlow = submittedAfterSymptomFlow;
    this.advancedConsentGiven = advancedConsentGiven;
    this.lastSubmissionFlowScreen = lastSubmissionFlowScreen;
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
        submittedInBackground);
  }

  @Override
  public boolean equals(Object obj) {
    if (this == obj) {
      return true;
    }
    if (obj == null || getClass() != obj.getClass()) {
      return false;
    }
    KeySubmissionMetadataWithClientMetadata other = (KeySubmissionMetadataWithClientMetadata) obj;
    if (advancedConsentGiven == null) {
      if (other.advancedConsentGiven != null) {
        return false;
      }
    } else if (!advancedConsentGiven.equals(other.advancedConsentGiven)) {
      return false;
    }
    if (clientMetadata == null) {
      if (other.clientMetadata != null) {
        return false;
      }
    } else if (!clientMetadata.equals(other.clientMetadata)) {
      return false;
    }
    if (id == null) {
      if (other.id != null) {
        return false;
      }
    } else if (!id.equals(other.id)) {
      return false;
    }
    if (lastSubmissionFlowScreen == null) {
      if (other.lastSubmissionFlowScreen != null) {
        return false;
      }
    } else if (!lastSubmissionFlowScreen.equals(other.lastSubmissionFlowScreen)) {
      return false;
    }
    if (submitted == null) {
      if (other.submitted != null) {
        return false;
      }
    } else if (!submitted.equals(other.submitted)) {
      return false;
    }
    if (submittedAfterCancel == null) {
      if (other.submittedAfterCancel != null) {
        return false;
      }
    } else if (!submittedAfterCancel.equals(other.submittedAfterCancel)) {
      return false;
    }
    if (submittedAfterSymptomFlow == null) {
      if (other.submittedAfterSymptomFlow != null) {
        return false;
      }
    } else if (!submittedAfterSymptomFlow.equals(other.submittedAfterSymptomFlow)) {
      return false;
    }
    if (submittedInBackground == null) {
      if (other.submittedInBackground != null) {
        return false;
      }
    } else if (!submittedInBackground.equals(other.submittedInBackground)) {
      return false;
    }
    if (technicalMetadata == null) {
      if (other.technicalMetadata != null) {
        return false;
      }
    } else if (!technicalMetadata.equals(other.technicalMetadata)) {
      return false;
    }
    return true;
  }
}
