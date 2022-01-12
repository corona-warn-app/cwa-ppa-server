package app.coronawarn.datadonation.common.persistence.domain.metrics;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;

import app.coronawarn.datadonation.common.persistence.domain.metrics.embeddable.UserMetadataDetails;
import java.time.LocalDate;
import java.time.ZoneId;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

public class UserMetadataTest {

  static private final LocalDate date = LocalDate.now(ZoneId.of("UTC"));
  static final UserMetadataDetails userMetadataDetails = new UserMetadataDetails(1, 1, 1);
  static private final TechnicalMetadata technicalMetadata =
      new TechnicalMetadata(date, true, false, true, false);
  UserMetadata userMetadata = new UserMetadata(1L, userMetadataDetails, technicalMetadata);

  @Nested
  @DisplayName("testEquals")
  class TestEquals {

    @Test
    void testEqualsSelf() {
      assertThat(userMetadata).isEqualTo(userMetadata);
    }

    @Test
    void testEqualsEquivalent() {
      UserMetadata equivalentUserMetadata = new UserMetadata(1L, userMetadataDetails, technicalMetadata);
      assertThat(userMetadata).isEqualTo(equivalentUserMetadata);
    }

    @Test
    void testEqualsObjectOfDifferentClass() {
      assertThat(userMetadata).isNotEqualTo("abc");
    }

    @Test
    void testEqualsOnTechnicalMetadata() {
      UserMetadata noTechnicalMetadata = new UserMetadata(1L, userMetadataDetails, null);
      UserMetadata alteredTechnicalMetadata = new UserMetadata(1L, userMetadataDetails,
          new TechnicalMetadata(date, true, false, true, true));

      assertThat(userMetadata).isNotEqualTo(alteredTechnicalMetadata);
      assertThat(userMetadata).isNotEqualTo(noTechnicalMetadata);
      assertThat(noTechnicalMetadata).isNotEqualTo(userMetadata);
    }

    @Test
    void testEqualsOnUserMetadataDetails() {
      UserMetadata noUserMetadataDetails = new UserMetadata(1L, null, technicalMetadata);
      UserMetadata alteredUserMetadataDetails = new UserMetadata(1L, new UserMetadataDetails(0, 0, 0),
          technicalMetadata);

      assertThat(userMetadata).isNotEqualTo(alteredUserMetadataDetails);
      assertThat(userMetadata).isNotEqualTo(noUserMetadataDetails);
      assertThat(noUserMetadataDetails).isNotEqualTo(userMetadata);
    }
  }

}
