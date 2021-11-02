package app.coronawarn.datadonation.common.persistence.repository.metrics;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;

import app.coronawarn.datadonation.common.persistence.domain.metrics.KeySubmissionMetadataWithClientMetadata;
import app.coronawarn.datadonation.common.persistence.domain.metrics.KeySubmissionMetadataWithUserMetadata;
import app.coronawarn.datadonation.common.persistence.domain.metrics.TechnicalMetadata;
import app.coronawarn.datadonation.common.persistence.domain.metrics.embeddable.ClientMetadataDetails;
import app.coronawarn.datadonation.common.persistence.domain.metrics.embeddable.CwaVersionMetadata;
import app.coronawarn.datadonation.common.persistence.domain.metrics.embeddable.UserMetadataDetails;
import java.time.LocalDate;
import java.time.ZoneId;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

public class KeySubmissionMetadataTest {

  static private final LocalDate date = LocalDate.now(ZoneId.of("UTC"));
  static private final CwaVersionMetadata cwaVersionMetadata = new CwaVersionMetadata(1, 1, 1);
  static private final ClientMetadataDetails clientMetadataDetails = new ClientMetadataDetails(cwaVersionMetadata,
      "abc", 2, 2, 3,
      1l, 2l);
  static private final CwaVersionMetadata cwaVersionMetadata1 = new CwaVersionMetadata(2, 2, 2);
  static private final ClientMetadataDetails alteredClientMetadataDetails = new ClientMetadataDetails(
      cwaVersionMetadata1, "abc", 2,
      2, 3, 1l, 2l);

  static private final TechnicalMetadata technicalMetadata = new TechnicalMetadata(date, true, false, true, false);

  static private final TechnicalMetadata alteredTechnicalMetadata = new TechnicalMetadata(date, true, false, true,
      true);

  static final UserMetadataDetails userMetadataDetails = new UserMetadataDetails(1, 1, 1);
  static final UserMetadataDetails alteredUserMetadataDetails = new UserMetadataDetails(2, 2, 2);

  private static final KeySubmissionMetadataWithUserMetadata keySubmissionMetadataWithUserMetadata = new KeySubmissionMetadataWithUserMetadata(
      1L, true, true, true, false, 1, 1, 1, 1, 1, 1,
      userMetadataDetails, technicalMetadata, cwaVersionMetadata);

  private static final KeySubmissionMetadataWithClientMetadata keySubmissionMetadataWithClientMetadata = new KeySubmissionMetadataWithClientMetadata(
      1L, true, true, true, true, true, 1, false, clientMetadataDetails, technicalMetadata);

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
          1L, true, true, true, true, true, 1, false, clientMetadataDetails, technicalMetadata);

      assertThat(keySubmissionMetadataWithClientMetadata).isEqualTo(equivalentKeySubmissionMetadataWithClientMetadata);
    }

    @Test
    void testEqualsWithObjectOfDifferentClass() {
      assertThat(keySubmissionMetadataWithClientMetadata).isNotEqualTo("abc");
    }

    @Test
    void testEqualsOnAdvancedConsentGiven() {
      KeySubmissionMetadataWithClientMetadata noAdvancedConsentGiven = new KeySubmissionMetadataWithClientMetadata(1L,
          true, true, true, true, null, 1, false, clientMetadataDetails, technicalMetadata);

      KeySubmissionMetadataWithClientMetadata alteredAdvancedConsentGiven = new KeySubmissionMetadataWithClientMetadata(
          1L, true, true, true, true, false, 1, false, clientMetadataDetails, technicalMetadata);

      assertThat(keySubmissionMetadataWithClientMetadata).isNotEqualTo(alteredAdvancedConsentGiven);
      assertThat(keySubmissionMetadataWithClientMetadata).isNotEqualTo(noAdvancedConsentGiven);
      assertThat(noAdvancedConsentGiven).isNotEqualTo(alteredAdvancedConsentGiven);
    }

    @Test
    void testEqualsOnClientMetadata() {
      KeySubmissionMetadataWithClientMetadata noClientMetadata = new KeySubmissionMetadataWithClientMetadata(1L, true,
          true, true, true, true, 1, false, null, technicalMetadata);

      KeySubmissionMetadataWithClientMetadata alteredClientMetadata = new KeySubmissionMetadataWithClientMetadata(1L,
          true, true, true, true, true, 1, false, alteredClientMetadataDetails, technicalMetadata);

      assertThat(keySubmissionMetadataWithClientMetadata).isNotEqualTo(alteredClientMetadata);
      assertThat(keySubmissionMetadataWithClientMetadata).isNotEqualTo(noClientMetadata);
      assertThat(noClientMetadata).isNotEqualTo(alteredClientMetadata);
    }

    @Test
    void testEqualsOnId() {
      KeySubmissionMetadataWithClientMetadata noId = new KeySubmissionMetadataWithClientMetadata(null, true, true, true,
          true, true, 1, false, clientMetadataDetails, technicalMetadata);

      KeySubmissionMetadataWithClientMetadata alteredId = new KeySubmissionMetadataWithClientMetadata(2L, true, true,
          true, true, true, 1, false, clientMetadataDetails, technicalMetadata);

      assertThat(keySubmissionMetadataWithClientMetadata).isNotEqualTo(alteredId);
      assertThat(keySubmissionMetadataWithClientMetadata).isNotEqualTo(noId);
      assertThat(noId).isNotEqualTo(alteredId);
    }

    @Test
    void testEqualsOnLastSubmissionFlowScreen() {
      KeySubmissionMetadataWithClientMetadata noLastSubmissionFlowScreen = new KeySubmissionMetadataWithClientMetadata(
          1L, true, true, true, true, true, null, false, clientMetadataDetails, technicalMetadata);

      KeySubmissionMetadataWithClientMetadata alteredLastSubmissionFlowScreen = new KeySubmissionMetadataWithClientMetadata(
          1L, true, true, true, true, true, 2, false, clientMetadataDetails, technicalMetadata);

      assertThat(keySubmissionMetadataWithClientMetadata).isNotEqualTo(alteredLastSubmissionFlowScreen);
      assertThat(keySubmissionMetadataWithClientMetadata).isNotEqualTo(noLastSubmissionFlowScreen);
      assertThat(noLastSubmissionFlowScreen).isNotEqualTo(alteredLastSubmissionFlowScreen);
    }

    @Test
    void testEqualsOnSubmitted() {
      KeySubmissionMetadataWithClientMetadata noSubmitted = new KeySubmissionMetadataWithClientMetadata(1L, null, true,
          true, true, true, 1, false, clientMetadataDetails, technicalMetadata);

      KeySubmissionMetadataWithClientMetadata alteredSubmitted = new KeySubmissionMetadataWithClientMetadata(1L, false,
          true, true, true, true, 1, false, clientMetadataDetails, technicalMetadata);

      assertThat(keySubmissionMetadataWithClientMetadata).isNotEqualTo(alteredSubmitted);
      assertThat(keySubmissionMetadataWithClientMetadata).isNotEqualTo(noSubmitted);
      assertThat(noSubmitted).isNotEqualTo(alteredSubmitted);
    }

    @Test
    void testEqualsOnSubmittedAfterCancel() {
      KeySubmissionMetadataWithClientMetadata noSubmittedAfterCancel = new KeySubmissionMetadataWithClientMetadata(1L,
          true, true, null, true, true, 1, false, clientMetadataDetails, technicalMetadata);

      KeySubmissionMetadataWithClientMetadata alteredSubmittedAfterCancel = new KeySubmissionMetadataWithClientMetadata(
          1L, true, true, false, true, true, 1, false, clientMetadataDetails, technicalMetadata);

      assertThat(keySubmissionMetadataWithClientMetadata).isNotEqualTo(alteredSubmittedAfterCancel);
      assertThat(keySubmissionMetadataWithClientMetadata).isNotEqualTo(noSubmittedAfterCancel);
      assertThat(noSubmittedAfterCancel).isNotEqualTo(alteredSubmittedAfterCancel);
    }

    @Test
    void testEqualsOnSubmittedAfterSymptomFlow() {
      KeySubmissionMetadataWithClientMetadata noSubmittedAfterSymptomFlow = new KeySubmissionMetadataWithClientMetadata(
          1L, true, true, true, null, true, 1, false, clientMetadataDetails, technicalMetadata);

      KeySubmissionMetadataWithClientMetadata alteredSubmittedAfterSymptomFlow = new KeySubmissionMetadataWithClientMetadata(
          1L, true, true, true, false, true, 1, false, clientMetadataDetails, technicalMetadata);

      assertThat(keySubmissionMetadataWithClientMetadata).isNotEqualTo(alteredSubmittedAfterSymptomFlow);
      assertThat(keySubmissionMetadataWithClientMetadata).isNotEqualTo(noSubmittedAfterSymptomFlow);
      assertThat(noSubmittedAfterSymptomFlow).isNotEqualTo(alteredSubmittedAfterSymptomFlow);
    }

    @Test
    void testEqualsOnSubmittedInBackground() {
      KeySubmissionMetadataWithClientMetadata noSubmittedInBackground = new KeySubmissionMetadataWithClientMetadata(1L,
          true, null, true, true, true, 1, false, clientMetadataDetails, technicalMetadata);

      KeySubmissionMetadataWithClientMetadata alteredSubmittedInBackground = new KeySubmissionMetadataWithClientMetadata(
          1L, true, false, true, true, true, 1, false, clientMetadataDetails, technicalMetadata);

      assertThat(keySubmissionMetadataWithClientMetadata).isNotEqualTo(alteredSubmittedInBackground);
      assertThat(keySubmissionMetadataWithClientMetadata).isNotEqualTo(noSubmittedInBackground);
      assertThat(noSubmittedInBackground).isNotEqualTo(alteredSubmittedInBackground);
    }

    @Test
    void testEqualsOnSubmittedWithCheckIns() {
      KeySubmissionMetadataWithClientMetadata noSubmittedWithCheckins = new KeySubmissionMetadataWithClientMetadata(1L,
          true, true, false, true, true, 1, false, clientMetadataDetails, technicalMetadata);

      KeySubmissionMetadataWithClientMetadata alteredSubmittedWithCheckins = new KeySubmissionMetadataWithClientMetadata(
          1L, true, true, false, true, true, 1, null, clientMetadataDetails, technicalMetadata);

      assertThat(keySubmissionMetadataWithClientMetadata).isNotEqualTo(alteredSubmittedWithCheckins);
      assertThat(keySubmissionMetadataWithClientMetadata).isNotEqualTo(noSubmittedWithCheckins);
      assertThat(noSubmittedWithCheckins).isNotEqualTo(alteredSubmittedWithCheckins);
    }

    @Test
    void testEqualsOnTechnicalMetadata() {
      KeySubmissionMetadataWithClientMetadata noTechnicalMetadata = new KeySubmissionMetadataWithClientMetadata(1L,
          true, true, true, true, true, 1, false, clientMetadataDetails, null);

      KeySubmissionMetadataWithClientMetadata alteredTechnicalMetadata = new KeySubmissionMetadataWithClientMetadata(1L,
          true, true, true, true, true, 1, false, clientMetadataDetails,
          KeySubmissionMetadataTest.alteredTechnicalMetadata);

      assertThat(keySubmissionMetadataWithClientMetadata).isNotEqualTo(alteredTechnicalMetadata);
      assertThat(keySubmissionMetadataWithClientMetadata).isNotEqualTo(noTechnicalMetadata);
      assertThat(noTechnicalMetadata).isNotEqualTo(alteredTechnicalMetadata);
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
          1L, true, true, true, false, 1, 1, 1, 1, 1, 1,
          userMetadataDetails, technicalMetadata, cwaVersionMetadata);

      assertThat(keySubmissionMetadataWithUserMetadata).isEqualTo(equivalentKeySubmissionMetadataWithUserMetadata);
    }

    @Test
    void testEqualsWithObjectOfDifferentClass() {
      assertThat(keySubmissionMetadataWithUserMetadata).isNotEqualTo("abc");
    }

    @Test
    void testEqualsOnDaysSinceMostRecentDateAtRiskLevelAtTestRegistration() {
      KeySubmissionMetadataWithUserMetadata noDaysSinceMostRecentDateAtRiskLevelAtTestRegistration = new KeySubmissionMetadataWithUserMetadata(
          1L, true, true, true, false, 1, 1, null, 1, 1, 1,
          userMetadataDetails, technicalMetadata, cwaVersionMetadata);

      KeySubmissionMetadataWithUserMetadata alteredDaysSinceMostRecentDateAtRiskLevelAtTestRegistration = new KeySubmissionMetadataWithUserMetadata(
          1L, true, true, true, false, 1, 1, 2, 1, 1, 1,
          userMetadataDetails, technicalMetadata, cwaVersionMetadata);

      assertThat(keySubmissionMetadataWithUserMetadata)
          .isNotEqualTo(alteredDaysSinceMostRecentDateAtRiskLevelAtTestRegistration);
      assertThat(keySubmissionMetadataWithUserMetadata)
          .isNotEqualTo(noDaysSinceMostRecentDateAtRiskLevelAtTestRegistration);
      assertThat(noDaysSinceMostRecentDateAtRiskLevelAtTestRegistration)
          .isNotEqualTo(alteredDaysSinceMostRecentDateAtRiskLevelAtTestRegistration);
    }

    @Test
    void testEqualsOnPtDaysSinceMostRecentDateAtRiskLevelAtTestRegistration() {
      KeySubmissionMetadataWithUserMetadata noPtDaysSinceMostRecentDateAtRiskLevelAtTestRegistration = new KeySubmissionMetadataWithUserMetadata(
          1L, true, true, true, false, 1, 1, 2, 1, null, 1,
          userMetadataDetails, technicalMetadata, cwaVersionMetadata);

      KeySubmissionMetadataWithUserMetadata alteredPtDaysSinceMostRecentDateAtRiskLevelAtTestRegistration = new KeySubmissionMetadataWithUserMetadata(
          1L, true, true, true, false, 1, 1, 2, 1, 1, 1,
          userMetadataDetails, technicalMetadata, cwaVersionMetadata);

      assertThat(keySubmissionMetadataWithUserMetadata)
          .isNotEqualTo(alteredPtDaysSinceMostRecentDateAtRiskLevelAtTestRegistration);
      assertThat(keySubmissionMetadataWithUserMetadata)
          .isNotEqualTo(noPtDaysSinceMostRecentDateAtRiskLevelAtTestRegistration);
      assertThat(noPtDaysSinceMostRecentDateAtRiskLevelAtTestRegistration)
          .isNotEqualTo(alteredPtDaysSinceMostRecentDateAtRiskLevelAtTestRegistration);
    }

    @Test
    void testEqualsOnHoursSinceHighRiskWarningAtTestRegistration() {
      KeySubmissionMetadataWithUserMetadata noHoursSinceHighRiskWarningAtTestRegistration = new KeySubmissionMetadataWithUserMetadata(
          1L, true, true, true, false, 1, 1, 1, null, 1, 1,
          userMetadataDetails, technicalMetadata, cwaVersionMetadata);

      KeySubmissionMetadataWithUserMetadata alteredHoursSinceHighRiskWarningAtTestRegistration = new KeySubmissionMetadataWithUserMetadata(
          1L, true, true, true, false, 1, 1, 1, 2, 1, 1,
          userMetadataDetails, technicalMetadata, cwaVersionMetadata);

      assertThat(keySubmissionMetadataWithUserMetadata)
          .isNotEqualTo(alteredHoursSinceHighRiskWarningAtTestRegistration);
      assertThat(keySubmissionMetadataWithUserMetadata).isNotEqualTo(noHoursSinceHighRiskWarningAtTestRegistration);
      assertThat(noHoursSinceHighRiskWarningAtTestRegistration)
          .isNotEqualTo(alteredHoursSinceHighRiskWarningAtTestRegistration);
    }

    @Test
    void testEqualsOnPtHoursSinceHighRiskWarningAtTestRegistration() {
      KeySubmissionMetadataWithUserMetadata noPtHoursSinceHighRiskWarningAtTestRegistration = new KeySubmissionMetadataWithUserMetadata(
          1L, true, true, true, false, 1, 1, 1, 2, 1, null,
          userMetadataDetails, technicalMetadata, cwaVersionMetadata);

      KeySubmissionMetadataWithUserMetadata alteredPtHoursSinceHighRiskWarningAtTestRegistration = new KeySubmissionMetadataWithUserMetadata(
          1L, true, true, true, false, 1, 1, 1, 2, 1, 1,
          userMetadataDetails, technicalMetadata, cwaVersionMetadata);

      assertThat(keySubmissionMetadataWithUserMetadata)
          .isNotEqualTo(alteredPtHoursSinceHighRiskWarningAtTestRegistration);
      assertThat(keySubmissionMetadataWithUserMetadata).isNotEqualTo(noPtHoursSinceHighRiskWarningAtTestRegistration);
      assertThat(noPtHoursSinceHighRiskWarningAtTestRegistration)
          .isNotEqualTo(alteredPtHoursSinceHighRiskWarningAtTestRegistration);
    }

    @Test
    void testEqualsOnHoursSinceReceptionOfTestResult() {
      KeySubmissionMetadataWithUserMetadata noHoursSinceReceptionOfTestResult = new KeySubmissionMetadataWithUserMetadata(
          1L, true, true, true, false, null, 1, 1, 1, 1,
          1, userMetadataDetails, technicalMetadata, cwaVersionMetadata);

      KeySubmissionMetadataWithUserMetadata alteredHoursSinceReceptionOfTestResult = new KeySubmissionMetadataWithUserMetadata(
          1L, true, true, true, false, 2, 1, 1, 2, 1, 1,
          userMetadataDetails, technicalMetadata, cwaVersionMetadata);

      assertThat(keySubmissionMetadataWithUserMetadata).isNotEqualTo(alteredHoursSinceReceptionOfTestResult);
      assertThat(keySubmissionMetadataWithUserMetadata).isNotEqualTo(noHoursSinceReceptionOfTestResult);
      assertThat(noHoursSinceReceptionOfTestResult).isNotEqualTo(alteredHoursSinceReceptionOfTestResult);
    }

    @Test
    void testEqualsOnHoursSinceTestRegistration() {
      KeySubmissionMetadataWithUserMetadata noHoursSinceTestRegistration = new KeySubmissionMetadataWithUserMetadata(1L,
          true, true, true, false, 1, null, 1, 1, 1, 1,
          userMetadataDetails, technicalMetadata, cwaVersionMetadata);

      KeySubmissionMetadataWithUserMetadata alteredHoursSinceTestRegistration = new KeySubmissionMetadataWithUserMetadata(
          1L, true, true, true, false, 1, 2, 1, 2, 1, 1,
          userMetadataDetails, technicalMetadata, cwaVersionMetadata);

      assertThat(keySubmissionMetadataWithUserMetadata).isNotEqualTo(alteredHoursSinceTestRegistration);
      assertThat(keySubmissionMetadataWithUserMetadata).isNotEqualTo(noHoursSinceTestRegistration);
      assertThat(noHoursSinceTestRegistration).isNotEqualTo(alteredHoursSinceTestRegistration);
    }

    @Test
    void testEqualsOnId() {
      KeySubmissionMetadataWithUserMetadata noId = new KeySubmissionMetadataWithUserMetadata(null, true, true, true,
          false, 1,
          1, 1, 1, 1, 1,
          userMetadataDetails, technicalMetadata, cwaVersionMetadata);

      KeySubmissionMetadataWithUserMetadata alteredId = new KeySubmissionMetadataWithUserMetadata(2L, true, true, true,
          false,
          1, 1, 1, 2, 1, 1,
          userMetadataDetails, technicalMetadata, cwaVersionMetadata);

      assertThat(keySubmissionMetadataWithUserMetadata).isNotEqualTo(alteredId);
      assertThat(keySubmissionMetadataWithUserMetadata).isNotEqualTo(noId);
      assertThat(noId).isNotEqualTo(alteredId);
    }

    @Test
    void testEqualsOnSubmitted() {
      KeySubmissionMetadataWithUserMetadata noSubmitted = new KeySubmissionMetadataWithUserMetadata(1L, null, true,
          true, false, 1, 1, 1, 1, 1, 1,
          userMetadataDetails, technicalMetadata, cwaVersionMetadata);

      KeySubmissionMetadataWithUserMetadata alteredSubmitted = new KeySubmissionMetadataWithUserMetadata(1L, false,
          true, false, true, 1, 1, 1, 2, 1, 1,
          userMetadataDetails, technicalMetadata, cwaVersionMetadata);

      assertThat(keySubmissionMetadataWithUserMetadata).isNotEqualTo(alteredSubmitted);
      assertThat(keySubmissionMetadataWithUserMetadata).isNotEqualTo(noSubmitted);
      assertThat(noSubmitted).isNotEqualTo(alteredSubmitted);
    }

    @Test
    void testEqualsOnSubmittedAfterSymptomFlow() {
      KeySubmissionMetadataWithUserMetadata noSubmittedAfterSymptomFlow = new KeySubmissionMetadataWithUserMetadata(1L,
          true, null, true, false, 1, 1, 1, 1, 1, 1,
          userMetadataDetails, technicalMetadata, cwaVersionMetadata);

      KeySubmissionMetadataWithUserMetadata alteredSubmittedAfterSymptomFlow = new KeySubmissionMetadataWithUserMetadata(
          1L, true, false, true, false, 1, 1, 1, 2, 1, 1,
          userMetadataDetails, technicalMetadata, cwaVersionMetadata);

      assertThat(keySubmissionMetadataWithUserMetadata).isNotEqualTo(alteredSubmittedAfterSymptomFlow);
      assertThat(keySubmissionMetadataWithUserMetadata).isNotEqualTo(noSubmittedAfterSymptomFlow);
      assertThat(noSubmittedAfterSymptomFlow).isNotEqualTo(alteredSubmittedAfterSymptomFlow);
    }

    @Test
    void testEqualsOnSubmittedWithTeletan() {
      KeySubmissionMetadataWithUserMetadata noSubmittedWithTeletan = new KeySubmissionMetadataWithUserMetadata(1L, true,
          true, null, false, 1, 1, 1, 1, 1, 1,
          userMetadataDetails, technicalMetadata, cwaVersionMetadata);

      KeySubmissionMetadataWithUserMetadata alteredSubmittedWithTeletan = new KeySubmissionMetadataWithUserMetadata(1L,
          true, true, false, false, 1, 1, 1, 2, 1, 1,
          userMetadataDetails, technicalMetadata, cwaVersionMetadata);

      assertThat(keySubmissionMetadataWithUserMetadata).isNotEqualTo(alteredSubmittedWithTeletan);
      assertThat(keySubmissionMetadataWithUserMetadata).isNotEqualTo(noSubmittedWithTeletan);
      assertThat(noSubmittedWithTeletan).isNotEqualTo(alteredSubmittedWithTeletan);
    }

    @Test
    void testEqualsOnSubmittedAfterRapidAntigenTest() {
      KeySubmissionMetadataWithUserMetadata noSubmittedAfterRapidAntigenTest = new KeySubmissionMetadataWithUserMetadata(
          1L, true,
          true, false, false, 1, 1, 1, 1, 1, 1,
          userMetadataDetails, technicalMetadata, cwaVersionMetadata);

      KeySubmissionMetadataWithUserMetadata alteredSubmittedAfterRapidAntigenTest = new KeySubmissionMetadataWithUserMetadata(
          1L,
          true, true, false, true, 1, 1, 1, 2, 1, 1,
          userMetadataDetails, technicalMetadata, cwaVersionMetadata);

      assertThat(keySubmissionMetadataWithUserMetadata).isNotEqualTo(alteredSubmittedAfterRapidAntigenTest);
      assertThat(keySubmissionMetadataWithUserMetadata).isNotEqualTo(noSubmittedAfterRapidAntigenTest);
      assertThat(noSubmittedAfterRapidAntigenTest).isNotEqualTo(alteredSubmittedAfterRapidAntigenTest);
    }

    @Test
    void testEqualsOnTechnicalMetadata() {
      KeySubmissionMetadataWithUserMetadata noTechnicalMetadata = new KeySubmissionMetadataWithUserMetadata(1L, true,
          true, true, false, 1, 1, 1, 1, 1, 1,
          userMetadataDetails, null, cwaVersionMetadata);

      KeySubmissionMetadataWithUserMetadata alteredTechnicalMetadata = new KeySubmissionMetadataWithUserMetadata(1L,
          true, true, true, false, 1, 1, 1, 2, 1, 1,
          userMetadataDetails, KeySubmissionMetadataTest.alteredTechnicalMetadata, cwaVersionMetadata);

      assertThat(keySubmissionMetadataWithUserMetadata).isNotEqualTo(alteredTechnicalMetadata);
      assertThat(keySubmissionMetadataWithUserMetadata).isNotEqualTo(noTechnicalMetadata);
      assertThat(noTechnicalMetadata).isNotEqualTo(alteredTechnicalMetadata);
    }

    @Test
    void testEqualsOnUserMetadata() {
      KeySubmissionMetadataWithUserMetadata noUserMetadata = new KeySubmissionMetadataWithUserMetadata(1L, true, true,
          true, false, 1, 1, 1, 1, 1, 1,
          null, technicalMetadata, cwaVersionMetadata);

      KeySubmissionMetadataWithUserMetadata alteredUserMetadata = new KeySubmissionMetadataWithUserMetadata(1L, true,
          true, true, false, 1, 1, 1, 2, 1, 1,
          alteredUserMetadataDetails, technicalMetadata, cwaVersionMetadata);

      assertThat(keySubmissionMetadataWithUserMetadata).isNotEqualTo(alteredUserMetadata);
      assertThat(keySubmissionMetadataWithUserMetadata).isNotEqualTo(noUserMetadata);
      assertThat(noUserMetadata).isNotEqualTo(alteredUserMetadata);
    }

  }
}
