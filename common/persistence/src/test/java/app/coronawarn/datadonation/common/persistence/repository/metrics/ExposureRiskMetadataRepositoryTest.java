package app.coronawarn.datadonation.common.persistence.repository.metrics;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

import app.coronawarn.datadonation.common.persistence.domain.metrics.ExposureRiskMetadata;
import app.coronawarn.datadonation.common.persistence.domain.metrics.TechnicalMetadata;
import app.coronawarn.datadonation.common.persistence.domain.metrics.UserMetadata;
import java.time.LocalDate;
import java.time.ZoneId;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.data.jdbc.DataJdbcTest;

@DataJdbcTest
class ExposureRiskMetadataRepositoryTest {

  @Autowired
  private ExposureRiskMetadataRepository exposureRiskMetadataRepository;

  @AfterEach
  void tearDown() {
    exposureRiskMetadataRepository.deleteAll();
  }

  @Test
  void exposureRiskMetadataShouldBePersistedCorrectly() {
    LocalDate justADate = LocalDate.now(ZoneId.of("UTC"));
    UserMetadata userMetadata = new UserMetadata(1, 2, 3);
    TechnicalMetadata technicalMetadata =
        new TechnicalMetadata(justADate, true, false, true, false, false);
    ExposureRiskMetadata exposureMetrics =
        new ExposureRiskMetadata(null, 1, true, justADate, false, userMetadata, technicalMetadata);

    exposureRiskMetadataRepository.save(exposureMetrics);

    ExposureRiskMetadata loadedEntity = exposureRiskMetadataRepository.findAll().iterator().next();
    assertEquals(loadedEntity.getMostRecentDateAtRiskLevel(),
        exposureMetrics.getMostRecentDateAtRiskLevel());
    assertEquals(loadedEntity.getMostRecentDateChanged(),
        exposureMetrics.getMostRecentDateChanged());
    assertEquals(loadedEntity.getRiskLevel(), exposureMetrics.getRiskLevel());
    assertEquals(loadedEntity.getRiskLevelChanged(), exposureMetrics.getRiskLevelChanged());
    assertEquals(loadedEntity.getTechnicalMetadata(), exposureMetrics.getTechnicalMetadata());
    assertEquals(loadedEntity.getUserMetadata(), exposureMetrics.getUserMetadata());
    assertNotNull(loadedEntity.getId());
  }
}
