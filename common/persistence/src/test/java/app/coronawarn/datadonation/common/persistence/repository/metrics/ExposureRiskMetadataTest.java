package app.coronawarn.datadonation.common.persistence.repository.metrics;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;

import app.coronawarn.datadonation.common.persistence.domain.metrics.ExposureRiskMetadata;
import app.coronawarn.datadonation.common.persistence.domain.metrics.TechnicalMetadata;
import app.coronawarn.datadonation.common.persistence.domain.metrics.embeddable.UserMetadataDetails;
import java.time.LocalDate;
import java.time.ZoneId;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

;

public class ExposureRiskMetadataTest {

  static private final LocalDate date = LocalDate.now(ZoneId.of("UTC"));

  static final UserMetadataDetails userMetadataDetails = new UserMetadataDetails(1, 1, 1);
  static final UserMetadataDetails alteredUserMetadataDetails = new UserMetadataDetails(2, 2, 2);

  static private final TechnicalMetadata technicalMetadata =
      new TechnicalMetadata(date, true, false, true, false);

  static private final TechnicalMetadata alteredTechnicalMetadata =
      new TechnicalMetadata(date, true, false, true, true);

  ExposureRiskMetadata exposureRiskMetadata = new ExposureRiskMetadata(1L, 1, true,
      date, true, 1, true, date, true, userMetadataDetails,
      technicalMetadata);

  @Nested
  @DisplayName("testEquals")
  class TestEquals {

    @Test
    void testEqualsSelf() {
      assertThat(exposureRiskMetadata).isEqualTo(exposureRiskMetadata);
    }

    @Test
    void testEqualsEquivalent() {
      ExposureRiskMetadata equivalentExposureRiskMetadata = new ExposureRiskMetadata(1L, 1, true,
          date, true, 1, true, date, true, userMetadataDetails,
          technicalMetadata);
      assertThat(exposureRiskMetadata).isEqualTo(equivalentExposureRiskMetadata);
    }

    @Test
    void testEqualsObjectOfDifferentClass() {
      assertThat(exposureRiskMetadata).isNotEqualTo("abc");
    }

    @Test
    void testEqualsOnId() {
      ExposureRiskMetadata noId = new ExposureRiskMetadata(null, 1, true, date,
          true, 1, true, date, true, userMetadataDetails,
          technicalMetadata);
      ExposureRiskMetadata alteredId = new ExposureRiskMetadata(2L, 1, true, date, true, 1, true, date, true,
          userMetadataDetails, technicalMetadata);

      assertThat(exposureRiskMetadata).isNotEqualTo(alteredId);
      assertThat(exposureRiskMetadata).isNotEqualTo(noId);
      assertThat(noId).isNotEqualTo(exposureRiskMetadata);
    }

    @Test
    void testEqualsOnMostRecentDateAtRiskLevel() {
      ExposureRiskMetadata noMostRecentDateAtRiskLevel = new ExposureRiskMetadata(1L, 1, true, null, true, 1, true,
          null, true, userMetadataDetails, technicalMetadata);
      ExposureRiskMetadata alteredMostRecentDateAtRiskLevel = new ExposureRiskMetadata(1L, 1, true,
          date.plusDays(1), true, 1, true, date.plusDays(1), true, userMetadataDetails, technicalMetadata);

      assertThat(exposureRiskMetadata).isNotEqualTo(alteredMostRecentDateAtRiskLevel);
      assertThat(exposureRiskMetadata).isNotEqualTo(noMostRecentDateAtRiskLevel);
      assertThat(noMostRecentDateAtRiskLevel).isNotEqualTo(alteredMostRecentDateAtRiskLevel);
    }

    @Test
    void testEqualsOnPtMostRecentDateAtRiskLevel() {
      ExposureRiskMetadata noPtMostRecentDateAtRiskLevel = new ExposureRiskMetadata(1L, 1, true, null, true, 1, true,
          null, true, userMetadataDetails, technicalMetadata);
      ExposureRiskMetadata alteredPtMostRecentDateAtRiskLevel = new ExposureRiskMetadata(1L, 1, true, null, true, 1, true,
          date.plusDays(1), true, userMetadataDetails, technicalMetadata);

      assertThat(exposureRiskMetadata).isNotEqualTo(alteredPtMostRecentDateAtRiskLevel);
      assertThat(exposureRiskMetadata).isNotEqualTo(noPtMostRecentDateAtRiskLevel);
      assertThat(noPtMostRecentDateAtRiskLevel).isNotEqualTo(alteredPtMostRecentDateAtRiskLevel);
    }

    @Test
    void testEqualsOnMostRecentDateChanged() {
      ExposureRiskMetadata noMostRecentDateChanged = new ExposureRiskMetadata(1L, 1, true,
          null, true, 1, true, null, true, userMetadataDetails, technicalMetadata);
      ExposureRiskMetadata alteredMostRecentDateChanged = new ExposureRiskMetadata(1L, 1, true,
          date.plusDays(1), true, 1, true, null, true, userMetadataDetails, technicalMetadata);

      assertThat(exposureRiskMetadata).isNotEqualTo(alteredMostRecentDateChanged);
      assertThat(exposureRiskMetadata).isNotEqualTo(noMostRecentDateChanged);
      assertThat(noMostRecentDateChanged).isNotEqualTo(alteredMostRecentDateChanged);
    }

    @Test
    void testEqualsOnPtMostRecentDateChanged() {
      ExposureRiskMetadata noPtMostRecentDateChanged = new ExposureRiskMetadata(1L, 1, true,
          null, true, 1, true, null, true,
          userMetadataDetails, technicalMetadata);
      ExposureRiskMetadata alteredPtMostRecentDateChanged = new ExposureRiskMetadata(1L, 1, true,
          null, true, 1, true, date.plusDays(1), true, userMetadataDetails, technicalMetadata);

      assertThat(exposureRiskMetadata).isNotEqualTo(alteredPtMostRecentDateChanged);
      assertThat(exposureRiskMetadata).isNotEqualTo(noPtMostRecentDateChanged);
      assertThat(noPtMostRecentDateChanged).isNotEqualTo(alteredPtMostRecentDateChanged);
    }

    @Test
    void testEqualsOnRiskLevel() {
      ExposureRiskMetadata noRiskLevel = new ExposureRiskMetadata(1L, null, true, date,
          true, null, true, date, true, userMetadataDetails,
          technicalMetadata);
      ExposureRiskMetadata alteredRiskLevel = new ExposureRiskMetadata(1L, 2, true,
          date, true, null, true, date, true, userMetadataDetails,
          technicalMetadata);

      assertThat(exposureRiskMetadata).isNotEqualTo(alteredRiskLevel);
      assertThat(exposureRiskMetadata).isNotEqualTo(noRiskLevel);
      assertThat(noRiskLevel).isNotEqualTo(alteredRiskLevel);
    }

    @Test
    void testEqualsOnPtRiskLevel() {
      ExposureRiskMetadata noPtRiskLevel = new ExposureRiskMetadata(1L, null, true, date,
          true, null, true, date, true, userMetadataDetails,
          technicalMetadata);
      ExposureRiskMetadata alteredPtRiskLevel = new ExposureRiskMetadata(1L, null, true,
          date, true, 2, true, date, true, userMetadataDetails,
          technicalMetadata);

      assertThat(exposureRiskMetadata).isNotEqualTo(alteredPtRiskLevel);
      assertThat(exposureRiskMetadata).isNotEqualTo(noPtRiskLevel);
      assertThat(noPtRiskLevel).isNotEqualTo(alteredPtRiskLevel);
    }
    @Test
    void testEqualsOnRiskLevelChanged() {
      ExposureRiskMetadata noRiskLevelChange = new ExposureRiskMetadata(1L, 1, false,
          date, true, 1, null, date, true, userMetadataDetails, technicalMetadata);
      ExposureRiskMetadata alteredRiskLevelChange = new ExposureRiskMetadata(1L, 1, false,
          date,true, 1, false, date, true, userMetadataDetails, technicalMetadata);

      assertThat(exposureRiskMetadata).isNotEqualTo(alteredRiskLevelChange);
      assertThat(exposureRiskMetadata).isNotEqualTo(noRiskLevelChange);
      assertThat(noRiskLevelChange).isNotEqualTo(alteredRiskLevelChange);
    }

    @Test
    void testEqualsOnPtRiskLevelChanged() {
      ExposureRiskMetadata noPtRiskLevelChange = new ExposureRiskMetadata(1L, 1, null,
          date, true, 1, null, date, true, userMetadataDetails, technicalMetadata);
      ExposureRiskMetadata alteredPTRiskLevelChange = new ExposureRiskMetadata(1L, 1, false,
          date,true, 1, false, date, true, userMetadataDetails, technicalMetadata);

      assertThat(exposureRiskMetadata).isNotEqualTo(alteredPTRiskLevelChange);
      assertThat(exposureRiskMetadata).isNotEqualTo(noPtRiskLevelChange);
      assertThat(noPtRiskLevelChange).isNotEqualTo(alteredPTRiskLevelChange);
    }

    @Test
    void testEqualsOnTechnicalMetadata() {
      ExposureRiskMetadata noTechnicalMetadata = new ExposureRiskMetadata(1L, 1, true,
          date, true, 1, true, date, true, userMetadataDetails,
          null);
      ExposureRiskMetadata alteredTechnicalMetadata = new ExposureRiskMetadata(1L, 1, true,
          date, true, 1, true, date, true,
          userMetadataDetails,
          ExposureRiskMetadataTest.alteredTechnicalMetadata);

      assertThat(exposureRiskMetadata).isNotEqualTo(alteredTechnicalMetadata);
      assertThat(exposureRiskMetadata).isNotEqualTo(noTechnicalMetadata);
      assertThat(noTechnicalMetadata).isNotEqualTo(exposureRiskMetadata);
    }

    @Test
    void testEqualsOnUserMetadata() {
      ExposureRiskMetadata noUserMetadata = new ExposureRiskMetadata(1L, 1, true, date,
           true, 1, true, date, true, null, technicalMetadata);
      ExposureRiskMetadata alteredUserMetadata = new ExposureRiskMetadata(1L, 1, true,
          date, true, 1, true, date, true, alteredUserMetadataDetails, technicalMetadata);

      assertThat(exposureRiskMetadata).isNotEqualTo(alteredUserMetadata);
      assertThat(exposureRiskMetadata).isNotEqualTo(noUserMetadata);
      assertThat(noUserMetadata).isNotEqualTo(exposureRiskMetadata);
    }
  }
}
