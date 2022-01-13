package app.coronawarn.datadonation.common.persistence.domain.metrics;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotEquals;

import app.coronawarn.datadonation.common.persistence.domain.metrics.embeddable.CwaVersionMetadata;
import app.coronawarn.datadonation.common.persistence.domain.metrics.embeddable.UserMetadataDetails;
import org.junit.jupiter.api.Test;

class TestResultMetadataTest {

  @Test
  void testHashCode() {
    TestResultMetadata fixture = new TestResultMetadata(null, null, null, null, null, null, null, null, null, null,
        null, null);
    assertEquals(-293403007, fixture.hashCode());
  }

  @Test
  void testEqualsObject() {
    UserMetadataDetails userMetadata = new UserMetadataDetails(null, null, null);
    TechnicalMetadata technicalMetadata = new TechnicalMetadata(null, null, null, null, null);
    CwaVersionMetadata cwaVersionMetadata = new CwaVersionMetadata(null, null, null);
    TestResultMetadata fixture = new TestResultMetadata(42L, 42, 42, 42, 42, 42, 42, 42, 42, userMetadata,
        technicalMetadata, cwaVersionMetadata);
    assertEquals(fixture, fixture);
    assertEquals(new TestResultMetadata(42L, 42, 42, 42, 42, 42, 42, 42, 42, userMetadata,
        technicalMetadata, cwaVersionMetadata), fixture);

    assertNotEquals(null, fixture);
    assertNotEquals(new Object(), fixture);
    assertNotEquals(new TestResultMetadata(23L, 42, 42, 42, 42, 42, 42, 42, 42, userMetadata,
        technicalMetadata, cwaVersionMetadata), fixture);
    assertNotEquals(new TestResultMetadata(42L, 23, 42, 42, 42, 42, 42, 42, 42, userMetadata,
        technicalMetadata, cwaVersionMetadata), fixture);
    assertNotEquals(new TestResultMetadata(42L, 42, 23, 42, 42, 42, 42, 42, 42, userMetadata,
        technicalMetadata, cwaVersionMetadata), fixture);
    assertNotEquals(new TestResultMetadata(42L, 42, 42, 23, 42, 42, 42, 42, 42, userMetadata,
        technicalMetadata, cwaVersionMetadata), fixture);
    assertNotEquals(new TestResultMetadata(42L, 42, 42, 42, 23, 42, 42, 42, 42, userMetadata,
        technicalMetadata, cwaVersionMetadata), fixture);
    assertNotEquals(new TestResultMetadata(42L, 42, 42, 42, 42, 23, 42, 42, 42, userMetadata,
        technicalMetadata, cwaVersionMetadata), fixture);
    assertNotEquals(new TestResultMetadata(42L, 42, 42, 42, 42, 42, 23, 42, 42, userMetadata,
        technicalMetadata, cwaVersionMetadata), fixture);
    assertNotEquals(new TestResultMetadata(42L, 42, 42, 42, 42, 42, 42, 23, 42, userMetadata,
        technicalMetadata, cwaVersionMetadata), fixture);
    assertNotEquals(new TestResultMetadata(42L, 42, 42, 42, 42, 42, 42, 42, 23, userMetadata,
        technicalMetadata, cwaVersionMetadata), fixture);
    assertNotEquals(new TestResultMetadata(42L, 42, 42, 42, 42, 42, 42, 42, 42, new UserMetadataDetails(42, 42, 42),
            technicalMetadata, cwaVersionMetadata), fixture);
    assertNotEquals(new TestResultMetadata(42L, 42, 42, 42, 42, 42, 42, 42, 42, userMetadata,
        new TechnicalMetadata(null, true, true, true, true), cwaVersionMetadata), fixture);
    assertNotEquals(new TestResultMetadata(42L, 42, 42, 42, 42, 42, 42, 42, 42, userMetadata,
        technicalMetadata, new CwaVersionMetadata(42, 42, 42)), fixture);
  }
}
