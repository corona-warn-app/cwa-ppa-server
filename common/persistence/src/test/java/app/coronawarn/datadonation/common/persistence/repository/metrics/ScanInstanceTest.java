package app.coronawarn.datadonation.common.persistence.repository.metrics;

import static org.junit.jupiter.api.Assertions.assertEquals;

import app.coronawarn.datadonation.common.persistence.domain.metrics.ScanInstance;
import app.coronawarn.datadonation.common.persistence.domain.metrics.TechnicalMetadata;
import java.time.LocalDate;
import java.time.ZoneId;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

public class ScanInstanceTest {

  @Test
  void testScanInstanceEquals() {
    LocalDate date = LocalDate.now(ZoneId.of("UTC"));
    TechnicalMetadata technicalMetadata = new TechnicalMetadata(date, true, false, true, false);
    ScanInstance fixture = new ScanInstance(2L, 2, 42, 42, 42, technicalMetadata);
    assertEquals(fixture, fixture);

    Assertions.assertNotEquals(fixture,
        new ScanInstance(2L, 2, 42, 42, 42, null));
    Assertions.assertNotEquals(fixture,
        new ScanInstance(2L, 2, 42, 42, 2, technicalMetadata));
    Assertions.assertNotEquals(fixture,
        new ScanInstance(2L, 2, 42, 2, 42, technicalMetadata));
    Assertions.assertNotEquals(fixture,
        new ScanInstance(2L, 2, 2, 42, 42, technicalMetadata));
    Assertions.assertNotEquals(fixture,
        new ScanInstance(2L, 1, 42, 42, 42, technicalMetadata));
    Assertions.assertNotEquals(fixture,
        new ScanInstance(1L, 2, 42, 42, 42, technicalMetadata));
  }

  @Test
  void testHashCode() {
    ScanInstance fixture = new ScanInstance(null, null, null, null, null, null);
    assertEquals(887503681, fixture.hashCode());
  }
}
