package app.coronawarn.datadonation.common.persistence.repository.metrics;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;

import app.coronawarn.datadonation.common.persistence.domain.metrics.ClientMetadata;
import app.coronawarn.datadonation.common.persistence.domain.metrics.TechnicalMetadata;
import app.coronawarn.datadonation.common.persistence.domain.metrics.embeddable.ClientMetadataDetails;
import java.time.LocalDate;
import java.time.ZoneId;
import app.coronawarn.datadonation.common.persistence.domain.metrics.embeddable.CwaVersionMetadata;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

public class ClientMetadataTest {

  static private final LocalDate date = LocalDate.now(ZoneId.of("UTC"));
  static private final CwaVersionMetadata cwaVersion = new CwaVersionMetadata(1, 1, 1);
  static private final ClientMetadataDetails clientMetadataDetails = new ClientMetadataDetails(cwaVersion,
      "abc", 2, 2, 3, 1l, 2l);
  static private final TechnicalMetadata technicalMetadata = new TechnicalMetadata(date, true, false, true, false);
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
      CwaVersionMetadata cwaVersion = new CwaVersionMetadata(2, 2, 1);
      ClientMetadata alteredClientMetadataDetails = new ClientMetadata(1L,
          new ClientMetadataDetails(cwaVersion, "abc", 2, 2, 3, 1l, 2l), technicalMetadata);

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
