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
    TestResultMetadata fixture = new TestResultMetadata(42l, 42, 42, 42, 42, 42, 42, 42, 42, userMetadata,
        technicalMetadata, cwaVersionMetadata);
    assertEquals(fixture, fixture);
    assertEquals(fixture, new TestResultMetadata(42l, 42, 42, 42, 42, 42, 42, 42, 42, userMetadata,
        technicalMetadata, cwaVersionMetadata));

    assertNotEquals(fixture, null);
    assertNotEquals(fixture, new Object());
    assertNotEquals(fixture, new TestResultMetadata(23l, 42, 42, 42, 42, 42, 42, 42, 42, userMetadata,
        technicalMetadata, cwaVersionMetadata));
    assertNotEquals(fixture, new TestResultMetadata(42l, 23, 42, 42, 42, 42, 42, 42, 42, userMetadata,
        technicalMetadata, cwaVersionMetadata));
    assertNotEquals(fixture, new TestResultMetadata(42l, 42, 23, 42, 42, 42, 42, 42, 42, userMetadata,
        technicalMetadata, cwaVersionMetadata));
    assertNotEquals(fixture, new TestResultMetadata(42l, 42, 42, 23, 42, 42, 42, 42, 42, userMetadata,
        technicalMetadata, cwaVersionMetadata));
    assertNotEquals(fixture, new TestResultMetadata(42l, 42, 42, 42, 23, 42, 42, 42, 42, userMetadata,
        technicalMetadata, cwaVersionMetadata));
    assertNotEquals(fixture, new TestResultMetadata(42l, 42, 42, 42, 42, 23, 42, 42, 42, userMetadata,
        technicalMetadata, cwaVersionMetadata));
    assertNotEquals(fixture, new TestResultMetadata(42l, 42, 42, 42, 42, 42, 23, 42, 42, userMetadata,
        technicalMetadata, cwaVersionMetadata));
    assertNotEquals(fixture, new TestResultMetadata(42l, 42, 42, 42, 42, 42, 42, 23, 42, userMetadata,
        technicalMetadata, cwaVersionMetadata));
    assertNotEquals(fixture, new TestResultMetadata(42l, 42, 42, 42, 42, 42, 42, 42, 23, userMetadata,
        technicalMetadata, cwaVersionMetadata));
    assertNotEquals(fixture,
        new TestResultMetadata(42l, 42, 42, 42, 42, 42, 42, 42, 42, new UserMetadataDetails(42, 42, 42),
            technicalMetadata, cwaVersionMetadata));
    assertNotEquals(fixture, new TestResultMetadata(42l, 42, 42, 42, 42, 42, 42, 42, 42, userMetadata,
        new TechnicalMetadata(null, true, true, true, true), cwaVersionMetadata));
    assertNotEquals(fixture, new TestResultMetadata(42l, 42, 42, 42, 42, 42, 42, 42, 42, userMetadata,
        technicalMetadata, new CwaVersionMetadata(42, 42, 42)));
  }
}
