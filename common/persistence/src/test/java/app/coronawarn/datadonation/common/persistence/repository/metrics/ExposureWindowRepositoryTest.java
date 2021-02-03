package app.coronawarn.datadonation.common.persistence.repository.metrics;

import static org.junit.Assert.assertEquals;
import static org.junit.jupiter.api.Assertions.*;
import java.time.LocalDate;
import java.time.ZoneId;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.data.jdbc.DataJdbcTest;
import app.coronawarn.datadonation.common.persistence.domain.metrics.ClientMetadata;
import app.coronawarn.datadonation.common.persistence.domain.metrics.ExposureRiskMetadata;
import app.coronawarn.datadonation.common.persistence.domain.metrics.ExposureWindow;
import app.coronawarn.datadonation.common.persistence.domain.metrics.TechnicalMetadata;
import app.coronawarn.datadonation.common.persistence.domain.metrics.UserMetadata;

@DataJdbcTest
class ExposureWindowRepositoryTest {

  @Autowired
  private ExposureWindowRepository exposureWindowRepository;

  @AfterEach
  void tearDown() {
    exposureWindowRepository.deleteAll();
  }

  @Test
  void testExposureWindowMetricIsPersisted() {
    LocalDate justADate = LocalDate.now(ZoneId.of("UTC"));
    ClientMetadata clientMetadata = new ClientMetadata(1, 1, 1, "abc", 2, 2, 3, 1, 2);
    TechnicalMetadata technicalMetadata =
        new TechnicalMetadata(justADate, true, false, true, false, false);
    ExposureWindow exposureMetrics =
        new ExposureWindow(null, justADate, 1, 1, 1, 2, 2, clientMetadata, technicalMetadata);

    exposureWindowRepository.save(exposureMetrics);
    ExposureWindow loadedEntity = exposureWindowRepository.findAll().iterator().next();
    assertEquals(loadedEntity.getCallibrationConfidence(),
        exposureMetrics.getCallibrationConfidence());
    assertEquals(loadedEntity.getClientMetadata(), exposureMetrics.getClientMetadata());
    assertEquals(loadedEntity.getDate(), exposureMetrics.getDate());
    assertEquals(loadedEntity.getInfectiousness(), exposureMetrics.getInfectiousness());
    assertEquals(loadedEntity.getNormalizedTime(), exposureMetrics.getNormalizedTime());
    assertEquals(loadedEntity.getReportType(), exposureMetrics.getReportType());
    assertEquals(loadedEntity.getTechnicalMetadata(), exposureMetrics.getTechnicalMetadata());
    assertEquals(loadedEntity.getTransmissionRiskLevel(),
        exposureMetrics.getTransmissionRiskLevel());

  }

}
