package app.coronawarn.datadonation.common.persistence.repository.metrics;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import java.time.LocalDate;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.data.jdbc.DataJdbcTest;
import app.coronawarn.datadonation.common.persistence.domain.metrics.TechnicalMetadata;
import app.coronawarn.datadonation.common.persistence.domain.metrics.UserMetadata;
import app.coronawarn.datadonation.common.persistence.domain.metrics.embeddable.UserMetadataDetails;

@DataJdbcTest
class UserMetadataRepositoryTest {

  @Autowired
  private UserMetadataRepository userMetadataRepository;

  @AfterEach
  void tearDown() {
    userMetadataRepository.deleteAll();
  }

  @Test
  void testResultMetadataShouldBePersistedCorrectly() {
    UserMetadata userMetadata =
        new UserMetadata(null, new UserMetadataDetails(1, 2, 2),
            new TechnicalMetadata(LocalDate.now(), true, true, false, false));

    userMetadataRepository.save(userMetadata);
    UserMetadata loadedEntity = userMetadataRepository.findAll().iterator().next();
    assertEquals(loadedEntity.getUserMetadataDetails(), userMetadata.getUserMetadataDetails());
    assertEquals(loadedEntity.getTechnicalMetadata(), userMetadata.getTechnicalMetadata());
    assertNotNull(loadedEntity.getId());
  }
}
