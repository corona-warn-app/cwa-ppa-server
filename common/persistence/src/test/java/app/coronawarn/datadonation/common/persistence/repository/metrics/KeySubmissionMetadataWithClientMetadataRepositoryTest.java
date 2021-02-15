package app.coronawarn.datadonation.common.persistence.repository.metrics;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

import app.coronawarn.datadonation.common.persistence.domain.metrics.KeySubmissionMetadataWithClientMetadata;
import app.coronawarn.datadonation.common.persistence.domain.metrics.TechnicalMetadata;
import app.coronawarn.datadonation.common.persistence.domain.metrics.embeddable.ClientMetadataDetails;
import java.time.LocalDate;
import java.time.ZoneId;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.data.jdbc.DataJdbcTest;

@DataJdbcTest
class KeySubmissionMetadataWithClientMetadataRepositoryTest {

  @Autowired
  private KeySubmissionMetadataWithClientMetadataRepository keySubmissionMetadataClientMetadataRepository;

  @AfterEach
  void tearDown() {
    keySubmissionMetadataClientMetadataRepository.deleteAll();
  }

  @Test
  void keySubmissionWithClientMetadataShouldBePersistedCorrectly() {
    LocalDate justADate = LocalDate.now(ZoneId.of("UTC"));
    ClientMetadataDetails clientMetadata = new ClientMetadataDetails(1, 1, 1, "abc", 2, 2, 3, 1, 2);
    TechnicalMetadata technicalMetadata =
        new TechnicalMetadata(justADate, true, false, true, false);
    KeySubmissionMetadataWithClientMetadata keySubmissionMetadata =
        new KeySubmissionMetadataWithClientMetadata(null, true, false, true, false, true, 1,
            clientMetadata, technicalMetadata);

    keySubmissionMetadataClientMetadataRepository.save(keySubmissionMetadata);

    KeySubmissionMetadataWithClientMetadata loadedEntity =
        keySubmissionMetadataClientMetadataRepository.findAll().iterator().next();
    assertEquals(loadedEntity.getAdvancedConsentGiven(),
        keySubmissionMetadata.getAdvancedConsentGiven());
    assertEquals(loadedEntity.getLastSubmissionFlowScreen(),
        keySubmissionMetadata.getLastSubmissionFlowScreen());
    assertEquals(loadedEntity.getSubmitted(),
        keySubmissionMetadata.getSubmitted());

    assertEquals(loadedEntity.getSubmittedAfterCancel(),
        keySubmissionMetadata.getSubmittedAfterCancel());
    assertEquals(loadedEntity.getSubmittedAfterSymptomFlow(),
        keySubmissionMetadata.getSubmittedAfterSymptomFlow());
    assertEquals(loadedEntity.getSubmittedInBackground(),
        keySubmissionMetadata.getSubmittedInBackground());

    assertEquals(loadedEntity.getTechnicalMetadata(), keySubmissionMetadata.getTechnicalMetadata());
    assertEquals(loadedEntity.getClientMetadata(), keySubmissionMetadata.getClientMetadata());
    assertNotNull(loadedEntity.getId());
  }
}
