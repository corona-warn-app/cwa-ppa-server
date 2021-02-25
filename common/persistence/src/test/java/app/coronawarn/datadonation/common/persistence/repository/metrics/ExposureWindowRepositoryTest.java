package app.coronawarn.datadonation.common.persistence.repository.metrics;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

import app.coronawarn.datadonation.common.persistence.domain.metrics.ExposureWindow;
import app.coronawarn.datadonation.common.persistence.domain.metrics.ScanInstance;
import app.coronawarn.datadonation.common.persistence.domain.metrics.TechnicalMetadata;
import app.coronawarn.datadonation.common.persistence.domain.metrics.embeddable.ClientMetadataDetails;
import java.time.LocalDate;
import java.time.ZoneId;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.Set;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.data.jdbc.DataJdbcTest;

@DataJdbcTest
class ExposureWindowRepositoryTest {

  @Autowired
  private ExposureWindowRepository exposureWindowRepository;

  @AfterEach
  void tearDown() {
    exposureWindowRepository.deleteAll();
  }

  @Test
  void exposureWindowShouldBePersistedCorrectly() {
    LocalDate justADate = LocalDate.now(ZoneId.of("UTC"));
    ClientMetadataDetails clientMetadata = new ClientMetadataDetails(1, 1, 1, "abc", 2, 2, 3, 1, 2);
    TechnicalMetadata technicalMetadata =
        new TechnicalMetadata(justADate, true, false, true, false);
    Set<ScanInstance> scanInstances =
        Set.of(new ScanInstance(null, null, 5, 4, 2), new ScanInstance(null, null, 7, 7, 7));
    ExposureWindow exposureMetrics =
        new ExposureWindow(null, justADate, 1, 1, 1, 2, 2.23, clientMetadata, technicalMetadata, scanInstances);

    exposureWindowRepository.save(exposureMetrics);
    ExposureWindow loadedEntity = exposureWindowRepository.findAll().iterator().next();
    assertEquals(loadedEntity.getCalibrationConfidence(),
        exposureMetrics.getCalibrationConfidence());
    assertEquals(loadedEntity.getClientMetadata(), exposureMetrics.getClientMetadata());
    assertEquals(loadedEntity.getDate(), exposureMetrics.getDate());
    assertEquals(loadedEntity.getInfectiousness(), exposureMetrics.getInfectiousness());
    assertEquals(loadedEntity.getNormalizedTime(), exposureMetrics.getNormalizedTime());
    assertEquals(loadedEntity.getReportType(), exposureMetrics.getReportType());
    assertEquals(loadedEntity.getTechnicalMetadata(), exposureMetrics.getTechnicalMetadata());
    assertEquals(loadedEntity.getTransmissionRiskLevel(),
        exposureMetrics.getTransmissionRiskLevel());
    assertNotNull(loadedEntity.getId());
    assertScanInstancesDataAreEqual(scanInstances, loadedEntity.getScanInstances());
  }

  private void assertScanInstancesDataAreEqual(Set<ScanInstance> beforePersistence,
      Set<ScanInstance> afterPersistence) {
    ArrayList<ScanInstance> beforePersistenceScans = new ArrayList<>(beforePersistence);
    ArrayList<ScanInstance> afterPersistenceScans = new ArrayList<>(afterPersistence);
    beforePersistenceScans.sort(Comparator.comparingInt(ScanInstance::getMinimumAttenuation)
        .thenComparingInt(ScanInstance::getTypicalAttenuation)
        .thenComparingInt(ScanInstance::getSecondsSinceLastScan));
    afterPersistenceScans.sort(Comparator.comparingInt(ScanInstance::getMinimumAttenuation)
        .thenComparingInt(ScanInstance::getTypicalAttenuation)
        .thenComparingInt(ScanInstance::getSecondsSinceLastScan));

    for (int i = 0; i < beforePersistenceScans.size(); i++) {
      ScanInstance beforePersistenceScan = beforePersistenceScans.get(i);
      ScanInstance afterPersistenceScan = afterPersistenceScans.get(i);
      if (beforePersistenceScan.getMinimumAttenuation() != afterPersistenceScan
          .getMinimumAttenuation()
          || beforePersistenceScan.getTypicalAttenuation() != afterPersistenceScan
              .getTypicalAttenuation()
          || beforePersistenceScan.getSecondsSinceLastScan() != afterPersistenceScan
              .getSecondsSinceLastScan()) {
        throw new AssertionError("scan instances were not persisted correctly");
      }
    }
  }
}
