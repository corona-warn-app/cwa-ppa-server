package app.coronawarn.datadonation.common.persistence.repository.metrics;

import static org.junit.jupiter.api.Assertions.assertEquals;

import app.coronawarn.datadonation.common.persistence.domain.metrics.ExposureWindowTestResult;
import app.coronawarn.datadonation.common.persistence.domain.metrics.ExposureWindowsAtTestRegistration;
import app.coronawarn.datadonation.common.persistence.domain.metrics.ScanInstancesAtTestRegistration;
import app.coronawarn.datadonation.common.persistence.domain.metrics.TechnicalMetadata;
import app.coronawarn.datadonation.common.persistence.domain.metrics.embeddable.ClientMetadataDetails;
import app.coronawarn.datadonation.common.persistence.domain.metrics.embeddable.CwaVersionMetadata;
import java.time.LocalDate;
import java.time.ZoneId;
import java.util.Set;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

public class ExposureWindowTestResultTest {

  @Test
  void testExposureWindowTestResultEquals() {
    LocalDate date = LocalDate.now(ZoneId.of("UTC"));
    CwaVersionMetadata cwaVersion = new CwaVersionMetadata(1, 1, 1);
    ClientMetadataDetails clientMetadata = new ClientMetadataDetails(cwaVersion, "abc",
        2, 2, 3, 1l, 2l);
    TechnicalMetadata technicalMetadata = new TechnicalMetadata(date, true, false, true, false);
    Set<ScanInstancesAtTestRegistration> scanInstancesAtTestRegistration = Set.of(
        new ScanInstancesAtTestRegistration(null, null, 5, 4, 2, null),
        new ScanInstancesAtTestRegistration(null, null, 7, 7, 7, null));
    Set<ExposureWindowsAtTestRegistration> exposureWindowsAtTestRegistrations = Set.of(
        new ExposureWindowsAtTestRegistration(null, null, date, 1, 1, 1, 2, 2.0, scanInstancesAtTestRegistration,
            false, technicalMetadata));

    ExposureWindowTestResult fixture = new ExposureWindowTestResult(2L, 2, clientMetadata, technicalMetadata,
        exposureWindowsAtTestRegistrations);

    assertEquals(fixture, fixture);

    Assertions.assertNotEquals(fixture,
        new ExposureWindowTestResult(2L, 2, clientMetadata, technicalMetadata, Set.of(
            new ExposureWindowsAtTestRegistration(null, null, date, 1, 1, 1, 2, 2.0, scanInstancesAtTestRegistration,
                true, technicalMetadata))));
    Assertions.assertNotEquals(fixture,
        new ExposureWindowTestResult(2L, 2, clientMetadata, new TechnicalMetadata(date, true, true, true, false),
            exposureWindowsAtTestRegistrations));
    Assertions.assertNotEquals(fixture,
        new ExposureWindowTestResult(2L, 2, new ClientMetadataDetails(cwaVersion, "abc",
            2, 2, 5, 1l, 2l), technicalMetadata, exposureWindowsAtTestRegistrations));
    Assertions.assertNotEquals(fixture,
        new ExposureWindowTestResult(2L, 1, clientMetadata, technicalMetadata, exposureWindowsAtTestRegistrations));
    Assertions.assertNotEquals(fixture,
        new ExposureWindowTestResult(1L, 2, clientMetadata, technicalMetadata, exposureWindowsAtTestRegistrations));
  }

  @Test
  void testHashCode() {
    ExposureWindowTestResult fixture = new ExposureWindowTestResult(null, null, null, null, null);
    assertEquals(28629151, fixture.hashCode());
  }
}
