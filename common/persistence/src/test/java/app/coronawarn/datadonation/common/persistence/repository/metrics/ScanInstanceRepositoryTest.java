package app.coronawarn.datadonation.common.persistence.repository.metrics;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

import app.coronawarn.datadonation.common.persistence.domain.metrics.ScanInstance;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.data.jdbc.DataJdbcTest;

@DataJdbcTest
class ScanInstanceRepositoryTest {

  @Autowired
  private ScanInstanceRepository scanInstanceRepository;

  @AfterEach
  void tearDown() {
    scanInstanceRepository.deleteAll();
  }

  @Test
  void scanInstanceShouldBePersistedCorrectly() {
    ScanInstance scanInstance = new ScanInstance(null, 1, 2, 3, 4);

    scanInstanceRepository.save(scanInstance);
    ScanInstance loadedEntity = scanInstanceRepository.findAll().iterator().next();
    assertEquals(loadedEntity.getExposureWindowId(), scanInstance.getExposureWindowId());
    assertEquals(loadedEntity.getMinimumAttenuation(), scanInstance.getMinimumAttenuation());
    assertEquals(loadedEntity.getSecondsSinceLastScan(), scanInstance.getSecondsSinceLastScan());
    assertEquals(loadedEntity.getTypicalAttenuation(), scanInstance.getTypicalAttenuation());
    assertNotNull(loadedEntity.getId());
  }
}
