package app.coronawarn.datadonation.common.persistence.repository.metrics;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;

import app.coronawarn.datadonation.common.persistence.domain.metrics.KeySubmissionMetadataWithClientMetadata;
import app.coronawarn.datadonation.common.persistence.domain.metrics.KeySubmissionMetadataWithUserMetadata;
import app.coronawarn.datadonation.common.persistence.domain.metrics.TechnicalMetadata;
import app.coronawarn.datadonation.common.persistence.domain.metrics.embeddable.ClientMetadataDetails;
import app.coronawarn.datadonation.common.persistence.domain.metrics.embeddable.UserMetadataDetails;
import java.time.LocalDate;
import java.time.ZoneId;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

public class KeySubmissionMetadataTest {

  static private final LocalDate date = LocalDate.now(ZoneId.of("UTC"));

  static private final ClientMetadataDetails clientMetadataDetails = new ClientMetadataDetails(1, 1, 1, "abc", 2, 2, 3,
      1l, 2l);

  static private final ClientMetadataDetails alteredClientMetadataDetails = new ClientMetadataDetails(2, 2, 2, "abc", 2,
      2, 3, 1l, 2l);

  static private final TechnicalMetadata technicalMetadata = new TechnicalMetadata(date, true, false, true, false);

  static private final TechnicalMetadata alteredTechnicalMetadata = new TechnicalMetadata(date, true, false, true,
      true);

  static final UserMetadataDetails userMetadataDetails = new UserMetadataDetails(1, 1, 1);
  static final UserMetadataDetails alteredUserMetadataDetails = new UserMetadataDetails(2, 2, 2);

  private static final KeySubmissionMetadataWithUserMetadata keySubmissionMetadataWithUserMetadata = new KeySubmissionMetadataWithUserMetadata(
      1L, true, true, true, 1, 1, 1, 1, userMetadataDetails, technicalMetadata);

  private static final KeySubmissionMetadataWithClientMetadata keySubmissionMetadataWithClientMetadata = new KeySubmissionMetadataWithClientMetadata(
      1L, true, true, true, true, true, 1, clientMetadataDetails, technicalMetadata);

  @Nested
  @DisplayName("withClientMetadata")
  class TestWithClientMetadataEquals {

    @Test
    void testEqualsWithSelf() {
      assertThat(keySubmissionMetadataWithClientMetadata).isEqualTo(keySubmissionMetadataWithClientMetadata);
    }

    @Test
    void testEqualsWithEquivalent() {
      KeySubmissionMetadataWithClientMetadata equivalentKeySubmissionMetadataWithClientMetadata = new KeySubmissionMetadataWithClientMetadata(
          1L, true, true, true, true, true, 1, clientMetadataDetails, technicalMetadata);

      assertThat(keySubmissionMetadataWithClientMetadata).isEqualTo(equivalentKeySubmissionMetadataWithClientMetadata);
    }

    @Test
    void testEqualsWithObjectOfDifferentClass() {
      assertThat(keySubmissionMetadataWithClientMetadata).isNotEqualTo("abc");
    }

    @Test
    void testEqualsOnAdvancedConsentGiven() {
      KeySubmissionMetadataWithClientMetadata noAdvancedConsentGiven = new KeySubmissionMetadataWithClientMetadata(1L,
          true, true, true, true, null, 1, clientMetadataDetails, technicalMetadata);

      KeySubmissionMetadataWithClientMetadata alteredAdvancedConsentGiven = new KeySubmissionMetadataWithClientMetadata(
          1L, true, true, true, true, false, 1, clientMetadataDetails, technicalMetadata);

      assertThat(keySubmissionMetadataWithClientMetadata).isNotEqualTo(alteredAdvancedConsentGiven);
      assertThat(keySubmissionMetadataWithClientMetadata).isNotEqualTo(noAdvancedConsentGiven);
      assertThat(noAdvancedConsentGiven).isNotEqualTo(keySubmissionMetadataWithClientMetadata);
    }

    @Test
    void testEqualsOnClientMetadata() {
      KeySubmissionMetadataWithClientMetadata noClientMetadata = new KeySubmissionMetadataWithClientMetadata(1L, true,
          true, true, true, true, 1, null, technicalMetadata);

      KeySubmissionMetadataWithClientMetadata alteredClientMetadata = new KeySubmissionMetadataWithClientMetadata(1L,
          true, true, true, true, true, 1, alteredClientMetadataDetails, technicalMetadata);

      assertThat(keySubmissionMetadataWithClientMetadata).isNotEqualTo(alteredClientMetadata);
      assertThat(keySubmissionMetadataWithClientMetadata).isNotEqualTo(noClientMetadata);
      assertThat(noClientMetadata).isNotEqualTo(keySubmissionMetadataWithClientMetadata);
    }

    @Test
    void testEqualsOnId() {
      KeySubmissionMetadataWithClientMetadata noId = new KeySubmissionMetadataWithClientMetadata(null, true, true, true,
          true, true, 1, clientMetadataDetails, technicalMetadata);

      KeySubmissionMetadataWithClientMetadata alteredId = new KeySubmissionMetadataWithClientMetadata(2L, true, true,
          true, true, true, 1, clientMetadataDetails, technicalMetadata);

      assertThat(keySubmissionMetadataWithClientMetadata).isNotEqualTo(alteredId);
      assertThat(keySubmissionMetadataWithClientMetadata).isNotEqualTo(noId);
      assertThat(noId).isNotEqualTo(keySubmissionMetadataWithClientMetadata);
    }

    @Test
    void testEqualsOnLastSubmissionFlowScreen() {
      KeySubmissionMetadataWithClientMetadata noLastSubmissionFlowScreen = new KeySubmissionMetadataWithClientMetadata(
          1L, true, true, true, true, true, null, clientMetadataDetails, technicalMetadata);

      KeySubmissionMetadataWithClientMetadata alteredLastSubmissionFlowScreen = new KeySubmissionMetadataWithClientMetadata(
          1L, true, true, true, true, true, 2, clientMetadataDetails, technicalMetadata);

      assertThat(keySubmissionMetadataWithClientMetadata).isNotEqualTo(alteredLastSubmissionFlowScreen);
      assertThat(keySubmissionMetadataWithClientMetadata).isNotEqualTo(noLastSubmissionFlowScreen);
      assertThat(noLastSubmissionFlowScreen).isNotEqualTo(keySubmissionMetadataWithClientMetadata);
    }

    @Test
    void testEqualsOnSubmitted() {
      KeySubmissionMetadataWithClientMetadata noSubmitted = new KeySubmissionMetadataWithClientMetadata(1L, null, true,
          true, true, true, 1, clientMetadataDetails, technicalMetadata);

      KeySubmissionMetadataWithClientMetadata alteredSubmitted = new KeySubmissionMetadataWithClientMetadata(1L, false,
          true, true, true, true, 1, clientMetadataDetails, technicalMetadata);

      assertThat(keySubmissionMetadataWithClientMetadata).isNotEqualTo(alteredSubmitted);
      assertThat(keySubmissionMetadataWithClientMetadata).isNotEqualTo(noSubmitted);
      assertThat(noSubmitted).isNotEqualTo(keySubmissionMetadataWithClientMetadata);
    }

    @Test
    void testEqualsOnSubmittedAfterCancel() {
      KeySubmissionMetadataWithClientMetadata noSubmittedAfterCancel = new KeySubmissionMetadataWithClientMetadata(1L,
          true, true, null, true, true, 1, clientMetadataDetails, technicalMetadata);

      KeySubmissionMetadataWithClientMetadata alteredSubmittedAfterCancel = new KeySubmissionMetadataWithClientMetadata(
          1L, true, true, false, true, true, 1, clientMetadataDetails, technicalMetadata);

      assertThat(keySubmissionMetadataWithClientMetadata).isNotEqualTo(alteredSubmittedAfterCancel);
      assertThat(keySubmissionMetadataWithClientMetadata).isNotEqualTo(noSubmittedAfterCancel);
      assertThat(noSubmittedAfterCancel).isNotEqualTo(keySubmissionMetadataWithClientMetadata);
    }

    @Test
    void testEqualsOnSubmittedAfterSymptomFlow() {
      KeySubmissionMetadataWithClientMetadata noSubmittedAfterSymptomFlow = new KeySubmissionMetadataWithClientMetadata(
          1L, true, true, true, null, true, 1, clientMetadataDetails, technicalMetadata);

      KeySubmissionMetadataWithClientMetadata alteredSubmittedAfterSymptomFlow = new KeySubmissionMetadataWithClientMetadata(
          1L, true, true, true, false, true, 1, clientMetadataDetails, technicalMetadata);

      assertThat(keySubmissionMetadataWithClientMetadata).isNotEqualTo(alteredSubmittedAfterSymptomFlow);
      assertThat(keySubmissionMetadataWithClientMetadata).isNotEqualTo(noSubmittedAfterSymptomFlow);
      assertThat(noSubmittedAfterSymptomFlow).isNotEqualTo(keySubmissionMetadataWithClientMetadata);
    }

    @Test
    void testEqualsOnSubmittedInBackground() {
      KeySubmissionMetadataWithClientMetadata noSubmittedInBackground = new KeySubmissionMetadataWithClientMetadata(1L,
          true, null, true, true, true, 1, clientMetadataDetails, technicalMetadata);

      KeySubmissionMetadataWithClientMetadata alteredSubmittedInBackground = new KeySubmissionMetadataWithClientMetadata(
          1L, true, false, true, true, true, 1, clientMetadataDetails, technicalMetadata);

      assertThat(keySubmissionMetadataWithClientMetadata).isNotEqualTo(alteredSubmittedInBackground);
      assertThat(keySubmissionMetadataWithClientMetadata).isNotEqualTo(noSubmittedInBackground);
      assertThat(noSubmittedInBackground).isNotEqualTo(keySubmissionMetadataWithClientMetadata);
    }

    @Test
    void testEqualsOnTechnicalMetadata() {
      KeySubmissionMetadataWithClientMetadata noTechnicalMetadata = new KeySubmissionMetadataWithClientMetadata(1L,
          true, true, true, true, true, 1, clientMetadataDetails, null);

      KeySubmissionMetadataWithClientMetadata alteredTechnicalMetadata = new KeySubmissionMetadataWithClientMetadata(1L,
          true, true, true, true, true, 1, clientMetadataDetails, KeySubmissionMetadataTest.alteredTechnicalMetadata);

      assertThat(keySubmissionMetadataWithClientMetadata).isNotEqualTo(alteredTechnicalMetadata);
      assertThat(keySubmissionMetadataWithClientMetadata).isNotEqualTo(noTechnicalMetadata);
      assertThat(noTechnicalMetadata).isNotEqualTo(keySubmissionMetadataWithClientMetadata);
    }
  }

  @Nested
  @DisplayName("withUserMetadata")
  class TestWithUserMetadataEquals {

    @Test
    void testEqualsWithSelf() {
      assertThat(keySubmissionMetadataWithUserMetadata).isEqualTo(keySubmissionMetadataWithUserMetadata);
    }

    @Test
    void testEqualsWithEquivalent() {
      KeySubmissionMetadataWithUserMetadata equivalentKeySubmissionMetadataWithUserMetadata = new KeySubmissionMetadataWithUserMetadata(
          1L, true, true, true, 1, 1, 1, 1, userMetadataDetails, technicalMetadata);

      assertThat(keySubmissionMetadataWithUserMetadata).isEqualTo(equivalentKeySubmissionMetadataWithUserMetadata);
    }

    @Test
    void testEqualsWithObjectOfDifferentClass() {
      assertThat(keySubmissionMetadataWithUserMetadata).isNotEqualTo("abc");
    }

    @Test
    void testEqualsOnDaysSinceMostRecentDateAtRiskLevelAtTestRegistration() {
      KeySubmissionMetadataWithUserMetadata noDaysSinceMostRecentDateAtRiskLevelAtTestRegistration = new KeySubmissionMetadataWithUserMetadata(
          1L, true, true, true, 1, 1, null, 1, userMetadataDetails, technicalMetadata);

      KeySubmissionMetadataWithUserMetadata alteredDaysSinceMostRecentDateAtRiskLevelAtTestRegistration = new KeySubmissionMetadataWithUserMetadata(
          1L, true, true, true, 1, 1, 2, 1, userMetadataDetails, technicalMetadata);

      assertThat(keySubmissionMetadataWithUserMetadata)
          .isNotEqualTo(alteredDaysSinceMostRecentDateAtRiskLevelAtTestRegistration);
      assertThat(keySubmissionMetadataWithUserMetadata)
          .isNotEqualTo(noDaysSinceMostRecentDateAtRiskLevelAtTestRegistration);
      assertThat(noDaysSinceMostRecentDateAtRiskLevelAtTestRegistration)
          .isNotEqualTo(keySubmissionMetadataWithUserMetadata);
    }

    @Test
    void testEqualsOnHoursSinceHighRiskWarningAtTestRegistration() {
      KeySubmissionMetadataWithUserMetadata noHoursSinceHighRiskWarningAtTestRegistration = new KeySubmissionMetadataWithUserMetadata(
          1L, true, true, true, 1, 1, 1, null, userMetadataDetails, technicalMetadata);

      KeySubmissionMetadataWithUserMetadata alteredHoursSinceHighRiskWarningAtTestRegistration = new KeySubmissionMetadataWithUserMetadata(
          1L, true, true, true, 1, 1, 1, 2, userMetadataDetails, technicalMetadata);

      assertThat(keySubmissionMetadataWithUserMetadata)
          .isNotEqualTo(alteredHoursSinceHighRiskWarningAtTestRegistration);
      assertThat(keySubmissionMetadataWithUserMetadata).isNotEqualTo(noHoursSinceHighRiskWarningAtTestRegistration);
      assertThat(noHoursSinceHighRiskWarningAtTestRegistration).isNotEqualTo(keySubmissionMetadataWithUserMetadata);
    }

    @Test
    void testEqualsOnHoursSinceReceptionOfTestResult() {
      KeySubmissionMetadataWithUserMetadata noHoursSinceReceptionOfTestResult = new KeySubmissionMetadataWithUserMetadata(
          1L, true, true, true, null, 1, 1, 1, userMetadataDetails, technicalMetadata);

      KeySubmissionMetadataWithUserMetadata alteredHoursSinceReceptionOfTestResult = new KeySubmissionMetadataWithUserMetadata(
          1L, true, true, true, 2, 1, 1, 2, userMetadataDetails, technicalMetadata);

      assertThat(keySubmissionMetadataWithUserMetadata).isNotEqualTo(alteredHoursSinceReceptionOfTestResult);
      assertThat(keySubmissionMetadataWithUserMetadata).isNotEqualTo(noHoursSinceReceptionOfTestResult);
      assertThat(noHoursSinceReceptionOfTestResult).isNotEqualTo(keySubmissionMetadataWithUserMetadata);
    }

    @Test
    void testEqualsOnHoursSinceTestRegistration() {
      KeySubmissionMetadataWithUserMetadata noHoursSinceTestRegistration = new KeySubmissionMetadataWithUserMetadata(1L,
          true, true, true, 1, null, 1, 1, userMetadataDetails, technicalMetadata);

      KeySubmissionMetadataWithUserMetadata alteredHoursSinceTestRegistration = new KeySubmissionMetadataWithUserMetadata(
          1L, true, true, true, 1, 2, 1, 2, userMetadataDetails, technicalMetadata);

      assertThat(keySubmissionMetadataWithUserMetadata).isNotEqualTo(alteredHoursSinceTestRegistration);
      assertThat(keySubmissionMetadataWithUserMetadata).isNotEqualTo(noHoursSinceTestRegistration);
      assertThat(noHoursSinceTestRegistration).isNotEqualTo(keySubmissionMetadataWithUserMetadata);
    }

    @Test
    void testEqualsOnId() {
      KeySubmissionMetadataWithUserMetadata noId = new KeySubmissionMetadataWithUserMetadata(null, true, true, true, 1,
          1, 1, 1, userMetadataDetails, technicalMetadata);

      KeySubmissionMetadataWithUserMetadata alteredId = new KeySubmissionMetadataWithUserMetadata(2L, true, true, true,
          1, 1, 1, 2, userMetadataDetails, technicalMetadata);

      assertThat(keySubmissionMetadataWithUserMetadata).isNotEqualTo(alteredId);
      assertThat(keySubmissionMetadataWithUserMetadata).isNotEqualTo(noId);
      assertThat(noId).isNotEqualTo(keySubmissionMetadataWithUserMetadata);
    }

    @Test
    void testEqualsOnSubmitted() {
      KeySubmissionMetadataWithUserMetadata noSubmitted = new KeySubmissionMetadataWithUserMetadata(1L, null, true,
          true, 1, 1, 1, 1, userMetadataDetails, technicalMetadata);

      KeySubmissionMetadataWithUserMetadata alteredSubmitted = new KeySubmissionMetadataWithUserMetadata(1L, false,
          true, true, 1, 1, 1, 2, userMetadataDetails, technicalMetadata);

      assertThat(keySubmissionMetadataWithUserMetadata).isNotEqualTo(alteredSubmitted);
      assertThat(keySubmissionMetadataWithUserMetadata).isNotEqualTo(noSubmitted);
      assertThat(noSubmitted).isNotEqualTo(keySubmissionMetadataWithUserMetadata);
    }

    @Test
    void testEqualsOnSubmittedAfterSymptomFlow() {
      KeySubmissionMetadataWithUserMetadata noSubmittedAfterSymptomFlow = new KeySubmissionMetadataWithUserMetadata(1L,
          true, null, true, 1, 1, 1, 1, userMetadataDetails, technicalMetadata);

      KeySubmissionMetadataWithUserMetadata alteredSubmittedAfterSymptomFlow = new KeySubmissionMetadataWithUserMetadata(
          1L, true, false, true, 1, 1, 1, 2, userMetadataDetails, technicalMetadata);

      assertThat(keySubmissionMetadataWithUserMetadata).isNotEqualTo(alteredSubmittedAfterSymptomFlow);
      assertThat(keySubmissionMetadataWithUserMetadata).isNotEqualTo(noSubmittedAfterSymptomFlow);
      assertThat(noSubmittedAfterSymptomFlow).isNotEqualTo(keySubmissionMetadataWithUserMetadata);
    }

    @Test
    void testEqualsOnSubmittedWithTeletan() {
      KeySubmissionMetadataWithUserMetadata noSubmittedWithTeletan = new KeySubmissionMetadataWithUserMetadata(1L, true,
          true, null, 1, 1, 1, 1, userMetadataDetails, technicalMetadata);

      KeySubmissionMetadataWithUserMetadata alteredSubmittedWithTeletan = new KeySubmissionMetadataWithUserMetadata(1L,
          true, true, false, 1, 1, 1, 2, userMetadataDetails, technicalMetadata);

      assertThat(keySubmissionMetadataWithUserMetadata).isNotEqualTo(alteredSubmittedWithTeletan);
      assertThat(keySubmissionMetadataWithUserMetadata).isNotEqualTo(noSubmittedWithTeletan);
      assertThat(noSubmittedWithTeletan).isNotEqualTo(keySubmissionMetadataWithUserMetadata);
    }

    @Test
    void testEqualsOnTechnicalMetadata() {
      KeySubmissionMetadataWithUserMetadata noTechnicalMetadata = new KeySubmissionMetadataWithUserMetadata(1L, true,
          true, true, 1, 1, 1, 1, userMetadataDetails, null);

      KeySubmissionMetadataWithUserMetadata alteredTechnicalMetadata = new KeySubmissionMetadataWithUserMetadata(1L,
          true, true, true, 1, 1, 1, 2, userMetadataDetails, KeySubmissionMetadataTest.alteredTechnicalMetadata);

      assertThat(keySubmissionMetadataWithUserMetadata).isNotEqualTo(alteredTechnicalMetadata);
      assertThat(keySubmissionMetadataWithUserMetadata).isNotEqualTo(noTechnicalMetadata);
      assertThat(noTechnicalMetadata).isNotEqualTo(keySubmissionMetadataWithUserMetadata);
    }

    @Test
    void testEqualsOnUserMetadata() {
      KeySubmissionMetadataWithUserMetadata noUserMetadata = new KeySubmissionMetadataWithUserMetadata(1L, true, true,
          true, 1, 1, 1, 1, null, technicalMetadata);

      KeySubmissionMetadataWithUserMetadata alteredUserMetadata = new KeySubmissionMetadataWithUserMetadata(1L, true,
          true, true, 1, 1, 1, 2, alteredUserMetadataDetails, technicalMetadata);

      assertThat(keySubmissionMetadataWithUserMetadata).isNotEqualTo(alteredUserMetadata);
      assertThat(keySubmissionMetadataWithUserMetadata).isNotEqualTo(noUserMetadata);
      assertThat(noUserMetadata).isNotEqualTo(keySubmissionMetadataWithUserMetadata);
    }

  }
}
