package app.coronawarn.datadonation.common.persistence.repository.metrics;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

import app.coronawarn.datadonation.common.persistence.domain.metrics.ClientMetadata;
import app.coronawarn.datadonation.common.persistence.domain.metrics.TechnicalMetadata;
import app.coronawarn.datadonation.common.persistence.domain.metrics.embeddable.ClientMetadataDetails;
import java.time.LocalDate;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.data.jdbc.DataJdbcTest;

@DataJdbcTest
class ClientMetadataRepositoryTest {

  @Autowired
  private ClientMetadataRepository clientMetadataRepository;

  @AfterEach
  void tearDown() {
    clientMetadataRepository.deleteAll();
  }

  @Test
  void clientMetadataShouldBePersistedCorrectly() {
    ClientMetadataDetails clientMetadataDetails = new ClientMetadataDetails(1, 1, 1, "abc", 2, 2, 3, 1l, 2l);
    ClientMetadata clientMetadata = new ClientMetadata(null, clientMetadataDetails,
        new TechnicalMetadata(LocalDate.now(), true, true, false, false));

    clientMetadataRepository.save(clientMetadata);
    ClientMetadata loadedEntity = clientMetadataRepository.findAll().iterator().next();
    ClientMetadataDetails loadedCMD = loadedEntity.getClientMetadataDetails();
    assertEquals(loadedCMD, clientMetadata.getClientMetadataDetails());
    assertEquals(loadedEntity.getTechnicalMetadata(), clientMetadata.getTechnicalMetadata());
    assertNotNull(loadedEntity.getId());

    assertEquals(clientMetadataDetails.getAndroidApiLevel(), loadedCMD.getAndroidApiLevel());
    assertEquals(clientMetadataDetails.getAndroidEnfVersion(), loadedCMD.getAndroidEnfVersion());
    assertEquals(clientMetadataDetails.getAppConfigEtag(), loadedCMD.getAppConfigEtag());
    assertEquals(clientMetadataDetails.getCwaVersionMajor(), loadedCMD.getCwaVersionMajor());
    assertEquals(clientMetadataDetails.getCwaVersionMinor(), loadedCMD.getCwaVersionMinor());
    assertEquals(clientMetadataDetails.getCwaVersionPatch(), loadedCMD.getCwaVersionPatch());
    assertEquals(clientMetadataDetails.getIosVersionMajor(), loadedCMD.getIosVersionMajor());
    assertEquals(clientMetadataDetails.getIosVersionMinor(), loadedCMD.getIosVersionMinor());
    assertEquals(clientMetadataDetails.getIosVersionPatch(), loadedCMD.getIosVersionPatch());
  }

  @Test
  void clientMetadataShouldBePersistedCorrectlyMax() {
    ClientMetadataDetails clientMetadataDetails = new ClientMetadataDetails(1, 1, 1, "abc", 2, 2, 3,
        2l * Integer.MAX_VALUE, 2l * Integer.MAX_VALUE);
    ClientMetadata clientMetadata = new ClientMetadata(null, clientMetadataDetails,
        new TechnicalMetadata(LocalDate.now(), true, true, false, false));

    clientMetadataRepository.save(clientMetadata);
    ClientMetadata loadedEntity = clientMetadataRepository.findAll().iterator().next();
    ClientMetadataDetails loadedCMD = loadedEntity.getClientMetadataDetails();
    assertEquals(loadedCMD, clientMetadata.getClientMetadataDetails());
    assertEquals(loadedEntity.getTechnicalMetadata(), clientMetadata.getTechnicalMetadata());
    assertNotNull(loadedEntity.getId());

    assertEquals(clientMetadataDetails.getAndroidApiLevel(), loadedCMD.getAndroidApiLevel());
    assertEquals(clientMetadataDetails.getAndroidEnfVersion(), loadedCMD.getAndroidEnfVersion());
    assertEquals(clientMetadataDetails.getAppConfigEtag(), loadedCMD.getAppConfigEtag());
    assertEquals(clientMetadataDetails.getCwaVersionMajor(), loadedCMD.getCwaVersionMajor());
    assertEquals(clientMetadataDetails.getCwaVersionMinor(), loadedCMD.getCwaVersionMinor());
    assertEquals(clientMetadataDetails.getCwaVersionPatch(), loadedCMD.getCwaVersionPatch());
    assertEquals(clientMetadataDetails.getIosVersionMajor(), loadedCMD.getIosVersionMajor());
    assertEquals(clientMetadataDetails.getIosVersionMinor(), loadedCMD.getIosVersionMinor());
    assertEquals(clientMetadataDetails.getIosVersionPatch(), loadedCMD.getIosVersionPatch());
  }
}
