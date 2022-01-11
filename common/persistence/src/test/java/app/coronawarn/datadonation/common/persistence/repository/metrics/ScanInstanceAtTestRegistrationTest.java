package app.coronawarn.datadonation.common.persistence.repository.metrics;

import static org.junit.jupiter.api.Assertions.assertEquals;

import app.coronawarn.datadonation.common.persistence.domain.metrics.ScanInstancesAtTestRegistration;
import app.coronawarn.datadonation.common.persistence.domain.metrics.TechnicalMetadata;
import java.time.LocalDate;
import java.time.ZoneId;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

public class ScanInstanceAtTestRegistrationTest {

  @Test
  void testScanInstanceEquals() {
    LocalDate date = LocalDate.now(ZoneId.of("UTC"));
    TechnicalMetadata technicalMetadata = new TechnicalMetadata(date, true, false, true, false);
    ScanInstancesAtTestRegistration fixture = new ScanInstancesAtTestRegistration(2L, 2, 42, 42, 42, technicalMetadata);
    assertEquals(fixture, fixture);

    Assertions.assertNotEquals(fixture,
        new ScanInstancesAtTestRegistration(2L, 2, 42, 42, 42, null));
    Assertions.assertNotEquals(fixture,
        new ScanInstancesAtTestRegistration(2L, 2, 42, 42, 2, technicalMetadata));
    Assertions.assertNotEquals(fixture,
        new ScanInstancesAtTestRegistration(2L, 2, 42, 2, 42, technicalMetadata));
    Assertions.assertNotEquals(fixture,
        new ScanInstancesAtTestRegistration(2L, 2, 2, 42, 42, technicalMetadata));
    Assertions.assertNotEquals(fixture,
        new ScanInstancesAtTestRegistration(2L, 1, 42, 42, 42, technicalMetadata));
    Assertions.assertNotEquals(fixture,
        new ScanInstancesAtTestRegistration(1L, 2, 42, 42, 42, technicalMetadata));
  }

  @Test
  void testHashCode() {
    ScanInstancesAtTestRegistration fixture = new ScanInstancesAtTestRegistration(null, null, null, null, null, null);
    assertEquals(887503681, fixture.hashCode());
  }
}
