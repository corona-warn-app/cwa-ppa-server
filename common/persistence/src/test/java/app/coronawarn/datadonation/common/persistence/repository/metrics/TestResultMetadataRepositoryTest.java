package app.coronawarn.datadonation.common.persistence.repository.metrics;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

import app.coronawarn.datadonation.common.persistence.domain.metrics.TechnicalMetadata;
import app.coronawarn.datadonation.common.persistence.domain.metrics.TestResultMetadata;
import app.coronawarn.datadonation.common.persistence.domain.metrics.embeddable.CwaVersionMetadata;
import app.coronawarn.datadonation.common.persistence.domain.metrics.embeddable.UserMetadataDetails;
import java.time.LocalDate;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.data.jdbc.DataJdbcTest;

@DataJdbcTest
class TestResultMetadataRepositoryTest {

  @Autowired
  private TestResultMetadataRepository testResultMetadataRepository;

  @AfterEach
  void tearDown() {
    testResultMetadataRepository.deleteAll();
  }

  @Test
  void testResultMetadataShouldBePersistedCorrectly() {
    CwaVersionMetadata cwaVersionMetadata = new CwaVersionMetadata(1, 1, 1);
    TestResultMetadata testResultMetadata =
        new TestResultMetadata(null, 1, 2, 3, 4, 5, 1, 1, 1, new UserMetadataDetails(1, 2, 2),
            new TechnicalMetadata(LocalDate.now(), true, true, false, false), cwaVersionMetadata);

    testResultMetadataRepository.save(testResultMetadata);
    TestResultMetadata loadedEntity = testResultMetadataRepository.findAll().iterator().next();
    assertEquals(loadedEntity.getUserMetadata(), testResultMetadata.getUserMetadata());
    assertEquals(loadedEntity.getDaysSinceMostRecentDateAtRiskLevelAtTestRegistration(),
        testResultMetadata.getDaysSinceMostRecentDateAtRiskLevelAtTestRegistration());
    assertEquals(loadedEntity.getHoursSinceHighRiskWarningAtTestRegistration(),
        testResultMetadata.getHoursSinceHighRiskWarningAtTestRegistration());
    assertEquals(loadedEntity.getHoursSinceTestRegistration(),
        testResultMetadata.getHoursSinceTestRegistration());
    assertEquals(loadedEntity.getRiskLevelAtTestRegistration(),
        testResultMetadata.getRiskLevelAtTestRegistration());
    assertEquals(loadedEntity.getPtHoursSinceHighRiskWarning(),
        testResultMetadata.getPtHoursSinceHighRiskWarning());
    assertEquals(loadedEntity.getPtRiskLevel(), testResultMetadata.getPtRiskLevel());
    assertEquals(loadedEntity.getPtDaysSinceMostRecentDateAtRiskLevel(),
        testResultMetadata.getPtDaysSinceMostRecentDateAtRiskLevel());
    assertEquals(loadedEntity.getTechnicalMetadata(), testResultMetadata.getTechnicalMetadata());
    assertEquals(loadedEntity.getTestResult(), testResultMetadata.getTestResult());
    assertNotNull(loadedEntity.getId());
    assertEquals(loadedEntity.getCwaVersionMetadata().getCwaVersionPatch(), cwaVersionMetadata.getCwaVersionPatch());
    assertEquals(loadedEntity.getCwaVersionMetadata().getCwaVersionMajor(), cwaVersionMetadata.getCwaVersionMajor());
    assertEquals(loadedEntity.getCwaVersionMetadata().getCwaVersionMinor(), cwaVersionMetadata.getCwaVersionMinor());
  }
}
