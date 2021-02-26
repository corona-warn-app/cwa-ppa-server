package app.coronawarn.datadonation.common.persistence.repository.metrics;

import app.coronawarn.datadonation.common.persistence.domain.metrics.embeddable.ClientMetadataDetails;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;

public class ClientMetadataDetailsTest {

  @Nested
  @DisplayName("testEquals")
  class TestEquals {

    ClientMetadataDetails clientMetadataDetails = new ClientMetadataDetails(1, 1, 1, "etag", 1, 1, 1, 1, 1);

    @Test
    void testEqualsSelf() {
      assertThat(clientMetadataDetails).isEqualTo(clientMetadataDetails);
    }

    @Test
    void testEqualsEquivalent() {
      ClientMetadataDetails equivalentClientMetadataDetails = new ClientMetadataDetails(1, 1, 1, "etag", 1, 1, 1, 1, 1);
      assertThat(clientMetadataDetails).isEqualTo(equivalentClientMetadataDetails);
    }

    @Test
    void testEqualsObjectOfDifferentClass() {
      assertThat(clientMetadataDetails).isNotEqualTo("String");
    }

    @Test
    void testEqualsOnAndroidApiLevel() {
      ClientMetadataDetails clientMetadataDetailsNoAndroidApiLevel = new ClientMetadataDetails(1, 1, 1, "etag", 1, 1, 1,
          null, 1);
      ClientMetadataDetails clientMetadataDetailsDifferentAndroidApiLevel = new ClientMetadataDetails(1, 1, 1, "etag",
          1, 1, 1, 2, 1);

      assertThat(clientMetadataDetails).isNotEqualTo(clientMetadataDetailsNoAndroidApiLevel);
      assertThat(clientMetadataDetailsNoAndroidApiLevel).isNotEqualTo(clientMetadataDetails);
      assertThat(clientMetadataDetails).isNotEqualTo(clientMetadataDetailsDifferentAndroidApiLevel);
    }

    @Test
    void testEqualsOnAndroidEnfVersion() {
      ClientMetadataDetails clientMetadataDetailsNoAndroidEnfVersion = new ClientMetadataDetails(1, 1, 1, "etag", 1, 1,
          1,
          1, null);
      ClientMetadataDetails clientMetadataDetailsDifferentAndroidEnfVersion = new ClientMetadataDetails(1, 1, 1, "etag",
          1, 1, 1, 1, 2);

      assertThat(clientMetadataDetails).isNotEqualTo(clientMetadataDetailsNoAndroidEnfVersion);
      assertThat(clientMetadataDetailsNoAndroidEnfVersion).isNotEqualTo(clientMetadataDetails);
      assertThat(clientMetadataDetails).isNotEqualTo(clientMetadataDetailsDifferentAndroidEnfVersion);
    }

    @Test
    void testEqualsOnEtag() {
      ClientMetadataDetails clientMetadataDetailsNoEtag = new ClientMetadataDetails(1, 1, 1, null, 1, 1,
          1,
          1, 1);
      ClientMetadataDetails clientMetadataDetailsDifferentEtag = new ClientMetadataDetails(1, 1, 1, "etagTwo",
          1, 1, 1, 1, 1);

      assertThat(clientMetadataDetails).isNotEqualTo(clientMetadataDetailsNoEtag);
      assertThat(clientMetadataDetailsNoEtag).isNotEqualTo(clientMetadataDetails);
      assertThat(clientMetadataDetails).isNotEqualTo(clientMetadataDetailsDifferentEtag);
    }

    @Test
    void testEqualsOnCwaVersionMajor() {
      ClientMetadataDetails clientMetadataDetailsNoVersionMajor = new ClientMetadataDetails(null, 1, 1, "etag", 1, 1,
          1,
          1, 1);
      ClientMetadataDetails clientMetadataDetailsDifferentVersionMajor = new ClientMetadataDetails(2, 1, 1, "etag",
          1, 1, 1, 1, 1);

      assertThat(clientMetadataDetails).isNotEqualTo(clientMetadataDetailsNoVersionMajor);
      assertThat(clientMetadataDetailsNoVersionMajor).isNotEqualTo(clientMetadataDetails);
      assertThat(clientMetadataDetails).isNotEqualTo(clientMetadataDetailsDifferentVersionMajor);
    }

    @Test
    void testEqualsOnCwaVersionMinor() {
      ClientMetadataDetails clientMetadataDetailsNoVersionMinor = new ClientMetadataDetails(1, null, 1, "etag", 1, 1,
          1,
          1, 1);
      ClientMetadataDetails clientMetadataDetailsDifferentVersionMinor = new ClientMetadataDetails(1, 2, 1, "etag",
          1, 1, 1, 1, 1);

      assertThat(clientMetadataDetails).isNotEqualTo(clientMetadataDetailsNoVersionMinor);
      assertThat(clientMetadataDetailsNoVersionMinor).isNotEqualTo(clientMetadataDetails);
      assertThat(clientMetadataDetails).isNotEqualTo(clientMetadataDetailsDifferentVersionMinor);
    }

    @Test
    void testEqualsOnCwaVersionPatch() {
      ClientMetadataDetails clientMetadataDetailsNoVersionPatch = new ClientMetadataDetails(
          1, 1, null, "etag", 1, 1,
          1,
          1, 1);
      ClientMetadataDetails clientMetadataDetailsDifferentPatchVersion = new ClientMetadataDetails(1, 1, 2, "etag",
          1, 1, 1, 1, 1);

      assertThat(clientMetadataDetails).isNotEqualTo(clientMetadataDetailsNoVersionPatch);
      assertThat(clientMetadataDetailsNoVersionPatch).isNotEqualTo(clientMetadataDetails);
      assertThat(clientMetadataDetails).isNotEqualTo(clientMetadataDetailsDifferentPatchVersion);
    }

    @Test
    void testEqualsOnIosVersionMajor() {
      ClientMetadataDetails clientMetadataDetailsNoIosVersionMajor = new ClientMetadataDetails(1, 1, 1, "etag", null, 1,
          1,
          1, 1);
      ClientMetadataDetails clientMetadataDetailsDifferentIosVersionMajor = new ClientMetadataDetails(1, 1, 1, "etag",
          2, 1, 1, 1, 1);

      assertThat(clientMetadataDetails).isNotEqualTo(clientMetadataDetailsNoIosVersionMajor);
      assertThat(clientMetadataDetailsNoIosVersionMajor).isNotEqualTo(clientMetadataDetails);
      assertThat(clientMetadataDetails).isNotEqualTo(clientMetadataDetailsDifferentIosVersionMajor);
    }

    @Test
    void testEqualsOnIosVersionMinor() {
      ClientMetadataDetails clientMetadataDetailsNoIosVersionMinor = new ClientMetadataDetails(1, 1, 1, "etag", 1, null,
          1,
          1, 1);
      ClientMetadataDetails clientMetadataDetailsDifferentIosVersionMinor = new ClientMetadataDetails(1, 1, 1, "etag",
          1, 2, 1, 1, 1);

      assertThat(clientMetadataDetails).isNotEqualTo(clientMetadataDetailsNoIosVersionMinor);
      assertThat(clientMetadataDetailsNoIosVersionMinor).isNotEqualTo(clientMetadataDetails);
      assertThat(clientMetadataDetails).isNotEqualTo(clientMetadataDetailsDifferentIosVersionMinor);
    }

    @Test
    void testEqualsOnIosVersionPatch() {
      ClientMetadataDetails clientMetadataDetailsNoIosVersionPatch = new ClientMetadataDetails(1, 1, 1, "etag", 1, 1,
          null,
          1, 1);
      ClientMetadataDetails clientMetadataDetailsDifferentIosVersionPatch = new ClientMetadataDetails(1, 1, 1, "etag",
          1, 1, 2, 1, 1);

      assertThat(clientMetadataDetails).isNotEqualTo(clientMetadataDetailsNoIosVersionPatch);
      assertThat(clientMetadataDetailsNoIosVersionPatch).isNotEqualTo(clientMetadataDetails);
      assertThat(clientMetadataDetails).isNotEqualTo(clientMetadataDetailsDifferentIosVersionPatch);
    }

    @Test
    void equalsToDifferentObjectWithSameFields() {
      ClientMetadataDetails equivalentCMD = new ClientMetadataDetails(1, 1, 1, "etag", 1, 1, 1, 1, 1);
      assertThat(clientMetadataDetails).isEqualTo(equivalentCMD);
    }

  }

}
