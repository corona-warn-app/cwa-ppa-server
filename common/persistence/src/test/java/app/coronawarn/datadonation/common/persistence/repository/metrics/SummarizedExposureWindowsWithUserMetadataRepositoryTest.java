package app.coronawarn.datadonation.common.persistence.repository.metrics;

import static org.junit.jupiter.api.Assertions.assertEquals;

import app.coronawarn.datadonation.common.persistence.domain.metrics.SummarizedExposureWindowsWithUserMetadata;
import app.coronawarn.datadonation.common.persistence.domain.metrics.TechnicalMetadata;
import app.coronawarn.datadonation.common.persistence.domain.metrics.embeddable.UserMetadataDetails;
import java.time.LocalDate;
import java.util.UUID;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.data.jdbc.DataJdbcTest;

@DataJdbcTest
public class SummarizedExposureWindowsWithUserMetadataRepositoryTest {

  @Autowired
  SummarizedExposureWindowsWithUserMetadataRepository summarizedExposureWindowsWithUserMetadataRepo;

  @AfterEach
  void tearDown() {
    summarizedExposureWindowsWithUserMetadataRepo.deleteAll();
  }

  @Test
  void testResultMetadataShouldBePersistedCorrectly() {
    UserMetadataDetails userMetadataDetails = new UserMetadataDetails(5, 4, 3);
    TechnicalMetadata technicalMetadata = new TechnicalMetadata(LocalDate.now(), true,
        false, true, true);
    SummarizedExposureWindowsWithUserMetadata summarizedExposureWindowsWithUserMetadata =
        new SummarizedExposureWindowsWithUserMetadata(null, LocalDate.now(), UUID.randomUUID().toString(), 1, 4.56,
            userMetadataDetails, technicalMetadata);
    summarizedExposureWindowsWithUserMetadataRepo.save(summarizedExposureWindowsWithUserMetadata);

    SummarizedExposureWindowsWithUserMetadata loadedEntity = summarizedExposureWindowsWithUserMetadataRepo
        .findAll().iterator().next();

    assertEquals(loadedEntity.getNormalizedTime(), summarizedExposureWindowsWithUserMetadata.getNormalizedTime());
    assertEquals(loadedEntity.getDate(), summarizedExposureWindowsWithUserMetadata.getDate());
    assertEquals(loadedEntity.getBatchId(), summarizedExposureWindowsWithUserMetadata.getBatchId());
    assertEquals(loadedEntity.getTransmissionRiskLevel(),
        summarizedExposureWindowsWithUserMetadata.getTransmissionRiskLevel());
    assertEquals(loadedEntity.getTechnicalMetadata(),
        summarizedExposureWindowsWithUserMetadata.getTechnicalMetadata());
    assertEquals(loadedEntity.getUserMetadataDetails(),
        summarizedExposureWindowsWithUserMetadata.getUserMetadataDetails());

  }
}
