package app.coronawarn.datadonation.common.persistence.domain.metrics;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import static org.junit.jupiter.api.Assertions.assertEquals;

import java.time.LocalDate;
import java.util.Collections;
import java.util.Set;
import org.junit.jupiter.api.Test;

class ExposureWindowsAtTestRegistrationTest {

  @Test
  void testEquals() {
    LocalDate date = LocalDate.now();
    Set<ScanInstancesAtTestRegistration> set = Collections.emptySet();
    ExposureWindowsAtTestRegistration fixture = new ExposureWindowsAtTestRegistration(42l, 42, date, 42, 42, 42, 42,
        42.42, set, true, null);
    assertTrue(fixture.equals(fixture));

    assertTrue(
        fixture.equals(new ExposureWindowsAtTestRegistration(42l, 42, date, 42, 42, 42, 42, 42.42, set, true, null)));

    assertFalse(fixture.equals(null));

    assertFalse(fixture.equals(new ExposureWindowsAtTestRegistration(42l, 42, date, 42, 42, 42, 42, 42.42, set, true,
        new TechnicalMetadata(date, null, null, null, null))));
    assertFalse(
        fixture.equals(new ExposureWindowsAtTestRegistration(42l, 42, date, 42, 42, 42, 42, 42.42, set, false, null)));
    assertFalse(fixture.equals(new ExposureWindowsAtTestRegistration(42l, 42, date, 42, 42, 42, 42, 42.42,
        Collections.singleton(new ScanInstancesAtTestRegistration(null, null, null, null, null, null)), true, null)));
    assertFalse(
        fixture.equals(new ExposureWindowsAtTestRegistration(42l, 42, date, 42, 42, 42, 42, 23.23, set, true, null)));
    assertFalse(
        fixture.equals(new ExposureWindowsAtTestRegistration(42l, 42, date, 42, 42, 42, 0, 0.0, set, true, null)));
    assertFalse(
        fixture.equals(new ExposureWindowsAtTestRegistration(42l, 42, date, 42, 42, 0, 42, 42.42, set, true, null)));
    assertFalse(
        fixture.equals(new ExposureWindowsAtTestRegistration(42l, 42, date, 42, 0, 42, 42, 42.42, set, true, null)));
    assertFalse(
        fixture.equals(new ExposureWindowsAtTestRegistration(42l, 42, date, 0, 42, 42, 42, 42.42, set, true, null)));
    assertFalse(fixture.equals(new ExposureWindowsAtTestRegistration(42l, 42, LocalDate.ofEpochDay(42), 42, 42, 42, 42,
        42.42, set, true, null)));
    assertFalse(
        fixture.equals(new ExposureWindowsAtTestRegistration(42l, 0, date, 42, 42, 42, 42, 42.42, set, true, null)));
  }

  @Test
  void testHashCode() {
    ExposureWindowsAtTestRegistration fixture = new ExposureWindowsAtTestRegistration(null, null, null, null, null,
        null, null, null, null, null, null);
    assertEquals(-1796951359, fixture.hashCode());
  }
}
