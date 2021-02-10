package app.coronawarn.datadonation.common.persistence.repository.metrics;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

import app.coronawarn.datadonation.common.persistence.domain.metrics.KeySubmissionMetadataWithUserMetadata;
import app.coronawarn.datadonation.common.persistence.domain.metrics.TechnicalMetadata;
import app.coronawarn.datadonation.common.persistence.domain.metrics.UserMetadata;
import java.time.LocalDate;
import java.time.ZoneId;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.data.jdbc.DataJdbcTest;

@DataJdbcTest
class KeySubmissionMetadataWithUserMetadataRepositoryTest {

  @Autowired
  private KeySubmissionMetadataWithUserMetadataRepository keySubmissionMetadataUserMetadataRepository;

  @AfterEach
  void tearDown() {
    keySubmissionMetadataUserMetadataRepository.deleteAll();
  }

  @Test
  void keySubmissionWithUserMetadataShouldBePersistedCorrectly() {
    LocalDate justADate = LocalDate.now(ZoneId.of("UTC"));
    UserMetadata userMetadata = new UserMetadata(1, 2, 3);
    TechnicalMetadata technicalMetadata =
        new TechnicalMetadata(justADate, true, false, true, false, false);
    KeySubmissionMetadataWithUserMetadata keySubmissionMetadata =
        new KeySubmissionMetadataWithUserMetadata(null, true, false, true, 1, 2, 3, 4, userMetadata,
            technicalMetadata);

    keySubmissionMetadataUserMetadataRepository.save(keySubmissionMetadata);

    KeySubmissionMetadataWithUserMetadata loadedEntity =
        keySubmissionMetadataUserMetadataRepository.findAll().iterator().next();
    assertEquals(loadedEntity.getDaysSinceMostRecentDateAtRiskLevelAtTestRegistration(),
        keySubmissionMetadata.getDaysSinceMostRecentDateAtRiskLevelAtTestRegistration());
    assertEquals(loadedEntity.getHoursSinceHighRiskWarningAtTestRegistration(),
        keySubmissionMetadata.getHoursSinceHighRiskWarningAtTestRegistration());
    assertEquals(loadedEntity.getHoursSinceReceptionOfTestResult(),
        keySubmissionMetadata.getHoursSinceReceptionOfTestResult());

    assertEquals(loadedEntity.getSubmitted(), keySubmissionMetadata.getSubmitted());
    assertEquals(loadedEntity.getSubmittedAfterSymptomFlow(),
        keySubmissionMetadata.getSubmittedAfterSymptomFlow());
    assertEquals(loadedEntity.getSubmittedWithTeletan(),
        keySubmissionMetadata.getSubmittedWithTeletan());

    assertEquals(loadedEntity.getTechnicalMetadata(), keySubmissionMetadata.getTechnicalMetadata());
    assertEquals(loadedEntity.getUserMetadata(), keySubmissionMetadata.getUserMetadata());
    assertNotNull(loadedEntity.getId());
  }
}