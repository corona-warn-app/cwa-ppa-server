package app.coronawarn.datadonation.common.persistence.repository.metrics;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;

import app.coronawarn.datadonation.common.persistence.domain.metrics.embeddable.ClientMetadataDetails;
import app.coronawarn.datadonation.common.persistence.domain.metrics.embeddable.CwaVersionMetadata;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

public class ClientMetadataDetailsTest {

  @Nested
  @DisplayName("testEquals")
  class TestEquals {

    CwaVersionMetadata cwaVersionMetadata = new CwaVersionMetadata(1, 1, 1);
    ClientMetadataDetails clientMetadataDetails = new ClientMetadataDetails(cwaVersionMetadata, "etag",
        1, 1, 1, 1l, 1l);

    @Test
    void testEqualsSelf() {
      assertThat(clientMetadataDetails).isEqualTo(clientMetadataDetails);
    }

    @Test
    void testEqualsEquivalent() {
      ClientMetadataDetails equivalentClientMetadataDetails = new ClientMetadataDetails(cwaVersionMetadata,
          "etag", 1, 1, 1, 1l, 1l);
      assertThat(clientMetadataDetails).isEqualTo(equivalentClientMetadataDetails);
    }

    @Test
    void testEqualsObjectOfDifferentClass() {
      assertThat(clientMetadataDetails).isNotEqualTo("String");
    }

    @Test
    void testEqualsOnAndroidApiLevel() {
      CwaVersionMetadata cwaVersionMetadata = new CwaVersionMetadata(1, 1, 1);
      ClientMetadataDetails clientMetadataDetailsNoAndroidApiLevel = new ClientMetadataDetails(cwaVersionMetadata,
          "etag", 1, 1, 1, null, 1l);
      ClientMetadataDetails clientMetadataDetailsDifferentAndroidApiLevel =
          new ClientMetadataDetails(cwaVersionMetadata, "etag", 1, 1, 1, 2l, 1l);

      assertThat(clientMetadataDetails).isNotEqualTo(clientMetadataDetailsNoAndroidApiLevel);
      assertThat(clientMetadataDetailsNoAndroidApiLevel).isNotEqualTo(clientMetadataDetails);
      assertThat(clientMetadataDetails).isNotEqualTo(clientMetadataDetailsDifferentAndroidApiLevel);
    }

    @Test
    void testEqualsOnAndroidEnfVersion() {
      CwaVersionMetadata cwaVersionMetadata = new CwaVersionMetadata(1, 1, 1);
      ClientMetadataDetails clientMetadataDetailsNoAndroidEnfVersion = new ClientMetadataDetails(cwaVersionMetadata, "etag", 1, 1,
          1, 1l, null);
      ClientMetadataDetails clientMetadataDetailsDifferentAndroidEnfVersion = new ClientMetadataDetails(cwaVersionMetadata, "etag",
          1, 1, 1, 1l, 2l);

      assertThat(clientMetadataDetails).isNotEqualTo(clientMetadataDetailsNoAndroidEnfVersion);
      assertThat(clientMetadataDetailsNoAndroidEnfVersion).isNotEqualTo(clientMetadataDetails);
      assertThat(clientMetadataDetails).isNotEqualTo(clientMetadataDetailsDifferentAndroidEnfVersion);
    }

    @Test
    void testEqualsOnEtag() {
      CwaVersionMetadata cwaVersionMetadata = new CwaVersionMetadata(1, 1, 1);
      ClientMetadataDetails clientMetadataDetailsNoEtag = new ClientMetadataDetails(cwaVersionMetadata,
          null, 1, 1, 1, 1l, 1l);
      ClientMetadataDetails clientMetadataDetailsDifferentEtag = new ClientMetadataDetails(cwaVersionMetadata,
          "etagTwo", 1, 1, 1, 1l, 1l);

      assertThat(clientMetadataDetails).isNotEqualTo(clientMetadataDetailsNoEtag);
      assertThat(clientMetadataDetailsNoEtag).isNotEqualTo(clientMetadataDetails);
      assertThat(clientMetadataDetails).isNotEqualTo(clientMetadataDetailsDifferentEtag);
    }

    @Test
    void testEqualsOnCwaVersionMajor() {
      CwaVersionMetadata cwaVersionMetadataWithNull = new CwaVersionMetadata(null, 1, 1);
      ClientMetadataDetails clientMetadataDetailsNoVersionMajor = new ClientMetadataDetails(cwaVersionMetadataWithNull,
          "etag", 1, 1, 1, 1l, 1l);
      CwaVersionMetadata cwaVersionMetadata = new CwaVersionMetadata(2, 1, 1);
      ClientMetadataDetails clientMetadataDetailsDifferentVersionMajor = new ClientMetadataDetails(cwaVersionMetadata,
          "etag", 1, 1, 1, 1l, 1l);

      assertThat(clientMetadataDetails).isNotEqualTo(clientMetadataDetailsNoVersionMajor);
      assertThat(clientMetadataDetailsNoVersionMajor).isNotEqualTo(clientMetadataDetails);
      assertThat(clientMetadataDetails).isNotEqualTo(clientMetadataDetailsDifferentVersionMajor);
    }

    @Test
    void testEqualsOnCwaVersionMinor() {
      CwaVersionMetadata cwaVersionMetadataWithNull = new CwaVersionMetadata(1, null, 1);
      ClientMetadataDetails clientMetadataDetailsNoVersionMinor = new ClientMetadataDetails(cwaVersionMetadataWithNull,
          "etag", 1, 1, 1, 1l, 1l);
      CwaVersionMetadata cwaVersionMetadata = new CwaVersionMetadata(1, 2, 1);

      ClientMetadataDetails clientMetadataDetailsDifferentVersionMinor = new ClientMetadataDetails(cwaVersionMetadata,
          "etag", 1, 1, 1, 1l, 1l);

      assertThat(clientMetadataDetails).isNotEqualTo(clientMetadataDetailsNoVersionMinor);
      assertThat(clientMetadataDetailsNoVersionMinor).isNotEqualTo(clientMetadataDetails);
      assertThat(clientMetadataDetails).isNotEqualTo(clientMetadataDetailsDifferentVersionMinor);
    }

    @Test
    void testEqualsOnCwaVersionPatch() {
      CwaVersionMetadata cwaVersionMetadataWithNull = new CwaVersionMetadata(1, 1, null);
      ClientMetadataDetails clientMetadataDetailsNoVersionPatch = new ClientMetadataDetails(cwaVersionMetadataWithNull,
          "etag", 1, 1, 1, 1l, 1l);
      CwaVersionMetadata cwaVersionMetadata = new CwaVersionMetadata(1, 1, 2);
      ClientMetadataDetails clientMetadataDetailsDifferentPatchVersion = new ClientMetadataDetails(cwaVersionMetadata, "etag", 1,
          1, 1, 1l, 1l);

      assertThat(clientMetadataDetails).isNotEqualTo(clientMetadataDetailsNoVersionPatch);
      assertThat(clientMetadataDetailsNoVersionPatch).isNotEqualTo(clientMetadataDetails);
      assertThat(clientMetadataDetails).isNotEqualTo(clientMetadataDetailsDifferentPatchVersion);
    }

    @Test
    void testEqualsOnIosVersionMajor() {
      CwaVersionMetadata cwaVersionMetadata = new CwaVersionMetadata(1, 1, 1);
      ClientMetadataDetails clientMetadataDetailsNoIosVersionMajor = new ClientMetadataDetails(cwaVersionMetadata,
          "etag", null, 1, 1, 1l, 1l);
      ClientMetadataDetails clientMetadataDetailsDifferentIosVersionMajor =
          new ClientMetadataDetails(cwaVersionMetadata, "etag", 2, 1, 1, 1l, 1l);

      assertThat(clientMetadataDetails).isNotEqualTo(clientMetadataDetailsNoIosVersionMajor);
      assertThat(clientMetadataDetailsNoIosVersionMajor).isNotEqualTo(clientMetadataDetails);
      assertThat(clientMetadataDetails).isNotEqualTo(clientMetadataDetailsDifferentIosVersionMajor);
    }

    @Test
    void testEqualsOnIosVersionMinor() {
      CwaVersionMetadata cwaVersionMetadata = new CwaVersionMetadata(1, 1, 1);
      ClientMetadataDetails clientMetadataDetailsNoIosVersionMinor = new ClientMetadataDetails(cwaVersionMetadata
          , "etag", 1, null, 1, 1l, 1l);
      ClientMetadataDetails clientMetadataDetailsDifferentIosVersionMinor = new ClientMetadataDetails(cwaVersionMetadata, "etag",
          1, 2, 1, 1l, 1l);

      assertThat(clientMetadataDetails).isNotEqualTo(clientMetadataDetailsNoIosVersionMinor);
      assertThat(clientMetadataDetailsNoIosVersionMinor).isNotEqualTo(clientMetadataDetails);
      assertThat(clientMetadataDetails).isNotEqualTo(clientMetadataDetailsDifferentIosVersionMinor);
    }

    @Test
    void testEqualsOnIosVersionPatch() {
      CwaVersionMetadata cwaVersionMetadata = new CwaVersionMetadata(1, 1, 1);
      ClientMetadataDetails clientMetadataDetailsNoIosVersionPatch = new ClientMetadataDetails(cwaVersionMetadata,
          "etag", 1, 1, null, 1l, 1l);
      ClientMetadataDetails clientMetadataDetailsDifferentIosVersionPatch =
          new ClientMetadataDetails(cwaVersionMetadata, "etag", 1, 1, 2, 1l, 1l);

      assertThat(clientMetadataDetails).isNotEqualTo(clientMetadataDetailsNoIosVersionPatch);
      assertThat(clientMetadataDetailsNoIosVersionPatch).isNotEqualTo(clientMetadataDetails);
      assertThat(clientMetadataDetails).isNotEqualTo(clientMetadataDetailsDifferentIosVersionPatch);
    }
  }
}
