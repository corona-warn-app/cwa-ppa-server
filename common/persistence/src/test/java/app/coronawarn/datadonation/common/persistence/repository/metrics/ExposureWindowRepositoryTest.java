package app.coronawarn.datadonation.common.persistence.repository.metrics;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

import app.coronawarn.datadonation.common.persistence.domain.metrics.ExposureWindow;
import app.coronawarn.datadonation.common.persistence.domain.metrics.ScanInstance;
import app.coronawarn.datadonation.common.persistence.domain.metrics.TechnicalMetadata;
import app.coronawarn.datadonation.common.persistence.domain.metrics.embeddable.ClientMetadataDetails;
import app.coronawarn.datadonation.common.persistence.domain.metrics.embeddable.CwaVersionMetadata;
import java.time.LocalDate;
import java.time.ZoneId;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Set;
import java.util.Spliterator;
import java.util.stream.Collectors;
import java.util.stream.StreamSupport;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.data.jdbc.DataJdbcTest;

@DataJdbcTest
class ExposureWindowRepositoryTest {

  @Autowired
  private ExposureWindowRepository exposureWindowRepository;

  @Autowired
  private ScanInstanceRepository scanInstanceRepository;

  @AfterEach
  void tearDown() {
    exposureWindowRepository.deleteAll();
  }

  @Test
  void exposureWindowShouldBePersistedCorrectly() {
    LocalDate justADate = LocalDate.now(ZoneId.of("UTC"));
    CwaVersionMetadata cwaVersionMetadata = new CwaVersionMetadata(1, 1, 1);
    ClientMetadataDetails clientMetadata = new ClientMetadataDetails(cwaVersionMetadata, "abc", 2, 2, 3, 1l, 2l);
    TechnicalMetadata technicalMetadata = new TechnicalMetadata(justADate, true, false, true, false);
    Set<ScanInstance> scanInstances = Set.of(new ScanInstance(null, null, 5, 4, 2, null),
        new ScanInstance(null, null, 7, 7, 7, null));
    ExposureWindow exposureMetrics = new ExposureWindow(null, justADate, 1, 1, 1, 2, 2.23, clientMetadata,
        technicalMetadata, scanInstances);

    exposureWindowRepository.save(exposureMetrics);
    ExposureWindow loadedEntity = exposureWindowRepository.findAll().iterator().next();
    assertEquals(loadedEntity.getCallibrationConfidence(), exposureMetrics.getCallibrationConfidence());
    assertEquals(loadedEntity.getClientMetadata(), exposureMetrics.getClientMetadata());
    assertEquals(loadedEntity.getDate(), exposureMetrics.getDate());
    assertEquals(loadedEntity.getInfectiousness(), exposureMetrics.getInfectiousness());
    assertEquals(loadedEntity.getNormalizedTime(), exposureMetrics.getNormalizedTime());
    assertEquals(loadedEntity.getReportType(), exposureMetrics.getReportType());
    assertEquals(loadedEntity.getTechnicalMetadata(), exposureMetrics.getTechnicalMetadata());
    assertEquals(loadedEntity.getTransmissionRiskLevel(), exposureMetrics.getTransmissionRiskLevel());
    assertNotNull(loadedEntity.getId());
    assertScanInstancesDataAreEqual(scanInstances, loadedEntity.getScanInstances());
  }

  @Test
  void verifyScanInstancesAreDeleted() {
    LocalDate justADate = LocalDate.now(ZoneId.of("UTC")).minusDays(5);
    CwaVersionMetadata cwaVersionMetadata = new CwaVersionMetadata(1, 1, 1);
    ClientMetadataDetails clientMetadata = new ClientMetadataDetails(cwaVersionMetadata, "abc", 2, 2, 3, 1l, 2l);
    TechnicalMetadata technicalMetadata = new TechnicalMetadata(justADate, true, false, true, false);
    Set<ScanInstance> scanInstances = Set.of(new ScanInstance(null, null, 5, 4, 2, technicalMetadata),
        new ScanInstance(null, null, 7, 7, 7, technicalMetadata));
    ExposureWindow exposureMetrics = new ExposureWindow(null, justADate, 1, 1, 1, 2, 2.23, clientMetadata,
        technicalMetadata, scanInstances);

    exposureWindowRepository.save(exposureMetrics);
    final List<ScanInstance> savedScanInstances = toList(scanInstanceRepository.findAll().spliterator());
    assertThat(savedScanInstances).isNotEmpty();
    exposureWindowRepository.deleteOlderThan(justADate.plusDays(5));
    scanInstanceRepository.deleteOlderThan(justADate.plusDays(5));

    final List<ScanInstance> shouldBeDeleted = toList(scanInstanceRepository.findAll().spliterator());
    assertThat(shouldBeDeleted).isEmpty();
  }

  private <T> List<T> toList(Spliterator<T> spliterator) {
    return StreamSupport.stream(spliterator, false).collect(Collectors.toList());
  }

  private void assertScanInstancesDataAreEqual(Set<ScanInstance> beforePersistence,
      Set<ScanInstance> afterPersistence) {
    ArrayList<ScanInstance> beforePersistenceScans = new ArrayList<>(beforePersistence);
    ArrayList<ScanInstance> afterPersistenceScans = new ArrayList<>(afterPersistence);
    beforePersistenceScans.sort(Comparator.comparingInt(ScanInstance::getMinimumAttenuation)
        .thenComparingInt(ScanInstance::getTypicalAttenuation).thenComparingInt(ScanInstance::getSecondsSinceLastScan));
    afterPersistenceScans.sort(Comparator.comparingInt(ScanInstance::getMinimumAttenuation)
        .thenComparingInt(ScanInstance::getTypicalAttenuation).thenComparingInt(ScanInstance::getSecondsSinceLastScan));

    for (int i = 0; i < beforePersistenceScans.size(); i++) {
      ScanInstance beforePersistenceScan = beforePersistenceScans.get(i);
      ScanInstance afterPersistenceScan = afterPersistenceScans.get(i);
      if (beforePersistenceScan.getMinimumAttenuation() != afterPersistenceScan.getMinimumAttenuation()
          || beforePersistenceScan.getTypicalAttenuation() != afterPersistenceScan.getTypicalAttenuation()
          || beforePersistenceScan.getSecondsSinceLastScan() != afterPersistenceScan.getSecondsSinceLastScan()) {
        throw new AssertionError("scan instances were not persisted correctly");
      }
    }
  }
}
