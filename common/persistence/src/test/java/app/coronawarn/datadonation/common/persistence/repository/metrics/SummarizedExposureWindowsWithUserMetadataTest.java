package app.coronawarn.datadonation.common.persistence.repository.metrics;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotEquals;

import app.coronawarn.datadonation.common.persistence.domain.metrics.SummarizedExposureWindowsWithUserMetadata;
import app.coronawarn.datadonation.common.persistence.domain.metrics.TechnicalMetadata;
import app.coronawarn.datadonation.common.persistence.domain.metrics.embeddable.UserMetadataDetails;
import java.time.LocalDate;
import java.time.ZoneId;
import org.junit.jupiter.api.Test;

public class SummarizedExposureWindowsWithUserMetadataTest {

  @Test
  void testSummarizedExposureWindowsWithUserMetadataEquals() {
    LocalDate date = LocalDate.now(ZoneId.of("UTC"));
    UserMetadataDetails userMetadataDetails = new UserMetadataDetails(1, 1, 1);
    TechnicalMetadata technicalMetadata = new TechnicalMetadata(date, true, false, true, false);

    SummarizedExposureWindowsWithUserMetadata fixture = new SummarizedExposureWindowsWithUserMetadata(
        2L, date, "batchId", 2, 2.0, userMetadataDetails, technicalMetadata);

    assertEquals(fixture, fixture);

    assertNotEquals(fixture,
        new SummarizedExposureWindowsWithUserMetadata(2L, date, "batchId", 2, 2.0, userMetadataDetails,
            new TechnicalMetadata(date, false, true, true, false)));
    assertNotEquals(fixture,
        new SummarizedExposureWindowsWithUserMetadata(2L, date, "batchId", 2, 2.0, new UserMetadataDetails(1, 3, 1),
            technicalMetadata));
    assertNotEquals(fixture,
        new SummarizedExposureWindowsWithUserMetadata(2L, date, "batchId", 2, 3.0, userMetadataDetails,
            technicalMetadata));
    assertNotEquals(fixture,
        new SummarizedExposureWindowsWithUserMetadata(2L, date, "batchId", 3, 2.0, userMetadataDetails,
            technicalMetadata));
    assertNotEquals(fixture,
        new SummarizedExposureWindowsWithUserMetadata(2L, date, "batchId2", 2, 2.0, userMetadataDetails,
            technicalMetadata));
    assertNotEquals(fixture,
        new SummarizedExposureWindowsWithUserMetadata(2L, date.minusDays(1), "batchId", 2, 2.0, userMetadataDetails,
            technicalMetadata));
  }

  @Test
  void testHashCode() {
    SummarizedExposureWindowsWithUserMetadata fixture = new SummarizedExposureWindowsWithUserMetadata(null, null, null,
        null, null, null, null);
    assertEquals(1742810335, fixture.hashCode());
  }
}
