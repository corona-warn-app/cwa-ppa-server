package app.coronawarn.datadonation.common.persistence.domain.metrics;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotEquals;

import java.time.LocalDate;
import java.time.ZoneId;
import org.junit.jupiter.api.Test;

class TechnicalMetadataTest {

  @Test
  void testHashCode() {
    TechnicalMetadata fixture = new TechnicalMetadata(null, null, null, null, null);
    assertEquals(28629151, fixture.hashCode());
  }

  @Test
  void testEqualsObject() {
    LocalDate date = LocalDate.now(ZoneId.of("UTC"));
    TechnicalMetadata fixture = new TechnicalMetadata(date, true, true, true, true);
    assertEquals(fixture, fixture);
    assertEquals(fixture, new TechnicalMetadata(date, true, true, true, true));

    assertNotEquals(null, fixture);
    assertNotEquals(new Object(), fixture);
    assertNotEquals(new TechnicalMetadata(date, true, true, true, false), fixture);
    assertNotEquals(new TechnicalMetadata(date, true, true, false, true), fixture);
    assertNotEquals(new TechnicalMetadata(date, true, false, true, true), fixture);
    assertNotEquals(new TechnicalMetadata(date, false, true, true, true), fixture);
    assertNotEquals(new TechnicalMetadata(LocalDate.ofEpochDay(42L), true, true, true, true), fixture);
  }
}
