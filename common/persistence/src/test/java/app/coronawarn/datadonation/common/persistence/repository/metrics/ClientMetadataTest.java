package app.coronawarn.datadonation.common.persistence.repository.metrics;

import app.coronawarn.datadonation.common.persistence.domain.metrics.ClientMetadata;
import app.coronawarn.datadonation.common.persistence.domain.metrics.TechnicalMetadata;
import app.coronawarn.datadonation.common.persistence.domain.metrics.embeddable.ClientMetadataDetails;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import java.time.LocalDate;
import java.time.ZoneId;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;

public class ClientMetadataTest {

  static private final LocalDate date = LocalDate.now(ZoneId.of("UTC"));
  static private final ClientMetadataDetails clientMetadataDetails = new ClientMetadataDetails(1, 1, 1, "abc", 2, 2, 3,
      1, 2);
  static private final TechnicalMetadata technicalMetadata =
      new TechnicalMetadata(date, true, false, true, false);
  private static final ClientMetadata clientMetadata = new ClientMetadata(1L, clientMetadataDetails, technicalMetadata);

  @Nested
  @DisplayName("testEquals")
  class TestEquals {

    @Test
    void testEqualsSelf() {
      assertThat(clientMetadata).isEqualTo(clientMetadata);
    }

    @Test
    void testEqualsEquivalent() {
      ClientMetadata equivalentClientMetadata = new ClientMetadata(1L, clientMetadataDetails, technicalMetadata);
      assertThat(clientMetadata).isEqualTo(equivalentClientMetadata);
    }

    @Test
    void testEqualsObjectOfDifferentClass() {
      assertThat(clientMetadata).isNotEqualTo("abc");
    }

    @Test
    void testEqualsOnClientMetadataDetails() {
      ClientMetadata noClientMetadataDetails = new ClientMetadata(1L, null, technicalMetadata);
      ClientMetadata alteredClientMetadataDetails = new ClientMetadata(1L,
          new ClientMetadataDetails(2, 2, 1, "abc", 2, 2, 3, 1, 2),
          technicalMetadata);

      assertThat(clientMetadata).isNotEqualTo(alteredClientMetadataDetails);
      assertThat(clientMetadata).isNotEqualTo(noClientMetadataDetails);
      assertThat(noClientMetadataDetails).isNotEqualTo(clientMetadata);
    }

    @Test
    void testEqualsOnTechnicalMetadata() {
      ClientMetadata noTechnicalMetadata = new ClientMetadata(1L, clientMetadataDetails, null);
      ClientMetadata alteredTechnicalMetadata = new ClientMetadata(1L, clientMetadataDetails,
          new TechnicalMetadata(date, true, false, true, true));

      assertThat(clientMetadata).isNotEqualTo(alteredTechnicalMetadata);
      assertThat(clientMetadata).isNotEqualTo(noTechnicalMetadata);
      assertThat(noTechnicalMetadata).isNotEqualTo(clientMetadata);
    }
  }

}
