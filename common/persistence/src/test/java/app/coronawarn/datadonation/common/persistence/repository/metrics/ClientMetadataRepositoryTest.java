package app.coronawarn.datadonation.common.persistence.repository.metrics;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

import app.coronawarn.datadonation.common.persistence.domain.metrics.ClientMetadata;
import app.coronawarn.datadonation.common.persistence.domain.metrics.TechnicalMetadata;
import app.coronawarn.datadonation.common.persistence.domain.metrics.embeddable.ClientMetadataDetails;
import app.coronawarn.datadonation.common.persistence.domain.metrics.embeddable.CwaVersionMetadata;
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
    CwaVersionMetadata cwaVersionMetadata = new CwaVersionMetadata(1, 1, 1);
    ClientMetadataDetails clientMetadataDetails = new ClientMetadataDetails(cwaVersionMetadata, "abc", 2, 2, 3, 1l, 2l);
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
    assertEquals(clientMetadataDetails.getCwaVersion().getCwaVersionMajor(),
        loadedCMD.getCwaVersion().getCwaVersionMajor());
    assertEquals(clientMetadataDetails.getCwaVersion().getCwaVersionMinor(),
        loadedCMD.getCwaVersion().getCwaVersionMinor());
    assertEquals(clientMetadataDetails.getCwaVersion().getCwaVersionPatch(),
        loadedCMD.getCwaVersion().getCwaVersionPatch());
    assertEquals(clientMetadataDetails.getIosVersionMajor(), loadedCMD.getIosVersionMajor());
    assertEquals(clientMetadataDetails.getIosVersionMinor(), loadedCMD.getIosVersionMinor());
    assertEquals(clientMetadataDetails.getIosVersionPatch(), loadedCMD.getIosVersionPatch());
  }

  @Test
  void clientMetadataShouldBePersistedCorrectlyMax() {
    CwaVersionMetadata cwaVersionMetadata = new CwaVersionMetadata(1, 1, 1);
    ClientMetadataDetails clientMetadataDetails = new ClientMetadataDetails(cwaVersionMetadata, "abc", 2, 2, 3,
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
    assertEquals(clientMetadataDetails.getCwaVersion().getCwaVersionMajor(),
        loadedCMD.getCwaVersion().getCwaVersionMajor());
    assertEquals(clientMetadataDetails.getCwaVersion().getCwaVersionMinor(),
        loadedCMD.getCwaVersion().getCwaVersionMinor());
    assertEquals(clientMetadataDetails.getCwaVersion().getCwaVersionPatch(),
        loadedCMD.getCwaVersion().getCwaVersionPatch());
    assertEquals(clientMetadataDetails.getIosVersionMajor(), loadedCMD.getIosVersionMajor());
    assertEquals(clientMetadataDetails.getIosVersionMinor(), loadedCMD.getIosVersionMinor());
    assertEquals(clientMetadataDetails.getIosVersionPatch(), loadedCMD.getIosVersionPatch());
  }
}
