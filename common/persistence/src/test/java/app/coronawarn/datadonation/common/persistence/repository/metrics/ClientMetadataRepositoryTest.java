package app.coronawarn.datadonation.common.persistence.repository.metrics;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import java.time.LocalDate;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.data.jdbc.DataJdbcTest;
import app.coronawarn.datadonation.common.persistence.domain.metrics.ClientMetadata;
import app.coronawarn.datadonation.common.persistence.domain.metrics.TechnicalMetadata;
import app.coronawarn.datadonation.common.persistence.domain.metrics.embeddable.ClientMetadataDetails;

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
    ClientMetadataDetails clientMetadataDetails =
        new ClientMetadataDetails(1, 1, 1, "abc", 2, 2, 3, 1, 2);
    ClientMetadata clientMetadata = new ClientMetadata(null, clientMetadataDetails,
        new TechnicalMetadata(LocalDate.now(), true, true, false, false));

    clientMetadataRepository.save(clientMetadata);
    ClientMetadata loadedEntity = clientMetadataRepository.findAll().iterator().next();
    assertEquals(loadedEntity.getClientMetadataDetails(),
        clientMetadata.getClientMetadataDetails());
    assertEquals(loadedEntity.getTechnicalMetadata(), clientMetadata.getTechnicalMetadata());
    assertNotNull(loadedEntity.getId());
  }
}
