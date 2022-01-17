package app.coronawarn.datadonation.common.persistence.domain.metrics;

import static org.assertj.core.api.Assertions.assertThat;

import app.coronawarn.datadonation.common.persistence.domain.metrics.embeddable.ClientMetadataDetails;
import app.coronawarn.datadonation.common.persistence.domain.metrics.embeddable.CwaVersionMetadata;
import java.time.LocalDate;
import java.time.ZoneId;
import java.util.Set;
import org.assertj.core.api.AssertionsForClassTypes;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

public class ExposureWindowTest {

  static private final LocalDate date = LocalDate.now(ZoneId.of("UTC"));
  static private final CwaVersionMetadata cwaVersion = new CwaVersionMetadata(1, 1, 1);
  static private final ClientMetadataDetails clientMetadata = new ClientMetadataDetails(cwaVersion, "abc",
      2, 2, 3, 1L, 2L);
  static private final TechnicalMetadata technicalMetadata = new TechnicalMetadata(date, true, false, true, false);
  static private final Set<ScanInstance> scanInstances = Set.of(new ScanInstance(null, null, 5, 4, 2, null),
      new ScanInstance(null, null, 7, 7, 7, null));
  ExposureWindow exposureWindow = generateExposureWindow(date, 1, 1, 1, 1, 1.0, clientMetadata, technicalMetadata,
      scanInstances);

  @Nested
  @DisplayName("testEquals")
  class TestEquals {

    @Test
    void testEqualsSelf() {
      assertThat(exposureWindow).isEqualTo(exposureWindow);
    }

    @Test
    void testEqualsEquivalent() {
      ExposureWindow equivalentExposureWindow = generateExposureWindow(date, 1, 1, 1, 1, 1.0, clientMetadata,
          technicalMetadata, scanInstances);
      assertThat(exposureWindow).isEqualTo(equivalentExposureWindow);
    }

    @Test
    void testEqualsObjectOfDifferentClass() {
      AssertionsForClassTypes.assertThat(exposureWindow).isNotEqualTo("String");
    }

    @Test
    void testEqualsOnCalibrationConfidence() {
      ExposureWindow noCalibrationConfidence = generateExposureWindow(date, 1, 1, null, 1, 1.0, clientMetadata,
          technicalMetadata, scanInstances);
      ExposureWindow differentCalibrationConfidence = generateExposureWindow(date, 1, 1, 2, 1, 1.0, clientMetadata,
          technicalMetadata, scanInstances);

      assertThat(noCalibrationConfidence).isNotEqualTo(exposureWindow);
      assertThat(exposureWindow)
          .isNotEqualTo(noCalibrationConfidence)
          .isNotEqualTo(differentCalibrationConfidence);
    }

    @Test
    void testEqualsOnClientMetadata() {
      ExposureWindow noClientMetadata = generateExposureWindow(date, 1, 1, 1, 1, 1.0, null, technicalMetadata,
          scanInstances);
      CwaVersionMetadata cwaVersionMetadata = new CwaVersionMetadata(0, 0, 0);
      ClientMetadataDetails differentCMD = new ClientMetadataDetails(cwaVersionMetadata, "000", 0, 0, 0, 0L, 0L);
      ExposureWindow differentClientMetadata = generateExposureWindow(date, 1, 1, 1, 1, 1.0, differentCMD,
          technicalMetadata, scanInstances);

      assertThat(noClientMetadata).isNotEqualTo(exposureWindow);
      assertThat(exposureWindow)
          .isNotEqualTo(noClientMetadata)
          .isNotEqualTo(differentClientMetadata);
    }

    @Test
    void testEqualsOnDate() {
      ExposureWindow noDate = generateExposureWindow(null, 1, 1, 1, 1, 1.0, clientMetadata, technicalMetadata,
          scanInstances);
      ExposureWindow differentDate = generateExposureWindow(date.minusDays(1), 1, 1, 1, 1, 1.0, clientMetadata,
          technicalMetadata, scanInstances);

      assertThat(noDate).isNotEqualTo(exposureWindow);
      assertThat(exposureWindow)
          .isNotEqualTo(noDate)
          .isNotEqualTo(differentDate);
    }

    @Test
    void testEqualsOnInfectiousness() {
      ExposureWindow noInfectiousness = generateExposureWindow(date, 1, null, 1, 1, 1.0, clientMetadata,
          technicalMetadata, scanInstances);
      ExposureWindow differentInfectiousness = generateExposureWindow(date, 1, 2, 1, 1, 1.0, clientMetadata,
          technicalMetadata, scanInstances);

      assertThat(noInfectiousness).isNotEqualTo(exposureWindow);
      assertThat(exposureWindow)
          .isNotEqualTo(noInfectiousness)
          .isNotEqualTo(differentInfectiousness);
    }

    @Test
    void testEqualsOnNormalizedTime() {
      ExposureWindow noNormalizedTime = generateExposureWindow(date, 1, 1, 1, 1, null, clientMetadata,
          technicalMetadata, scanInstances);
      ExposureWindow differentNormalizedTime = generateExposureWindow(date, 1, 1, 1, 1, 1.1, clientMetadata,
          technicalMetadata, scanInstances);

      assertThat(noNormalizedTime).isNotEqualTo(exposureWindow);
      assertThat(exposureWindow)
          .isNotEqualTo(noNormalizedTime)
          .isNotEqualTo(differentNormalizedTime);
    }

    @Test
    void testEqualsOnReportType() {
      ExposureWindow noReportType = generateExposureWindow(date, null, 1, 1, 1, 1.0, clientMetadata, technicalMetadata,
          scanInstances);
      ExposureWindow differentReportType = generateExposureWindow(date, 2, 1, 1, 1, 1.0, clientMetadata,
          technicalMetadata, scanInstances);

      assertThat(noReportType).isNotEqualTo(exposureWindow);
      assertThat(exposureWindow)
          .isNotEqualTo(noReportType)
          .isNotEqualTo(differentReportType);
    }

    @Test
    void testEqualsOnScanInstances() {
      Set<ScanInstance> alteredScanInstances = Set.of(new ScanInstance(null, null, 4, 4, 2, null),
          new ScanInstance(null, null, 3, 7, 7, null));
      ExposureWindow differentScanInstances = generateExposureWindow(date, 1, 1, 1, 1, 1.0, clientMetadata,
          technicalMetadata, alteredScanInstances);

      assertThat(exposureWindow).isNotEqualTo(differentScanInstances);
    }

    @Test
    void testEqualsOnTechnicalMetadata() {
      ExposureWindow noTechnicalMetadata = generateExposureWindow(date, 1, 1, 1, 1, 1.0, clientMetadata, null,
          scanInstances);
      TechnicalMetadata alteredTechnicalMetadata = new TechnicalMetadata(date.minusDays(1), true, false, true, true);
      ExposureWindow differentTechnicalMetadata = generateExposureWindow(date, 1, 1, 1, 1, 1.0, clientMetadata,
          alteredTechnicalMetadata, scanInstances);

      assertThat(noTechnicalMetadata).isNotEqualTo(exposureWindow);
      assertThat(exposureWindow)
          .isNotEqualTo(noTechnicalMetadata)
          .isNotEqualTo(differentTechnicalMetadata);
    }

    @Test
    void testEqualsOnTransmissionRiskLevel() {
      ExposureWindow noReportType = generateExposureWindow(date, 1, 1, 1, null, 1.0, clientMetadata, technicalMetadata,
          scanInstances);
      ExposureWindow differentReportType = generateExposureWindow(date, 1, 1, 1, 2, 1.0, clientMetadata,
          technicalMetadata, scanInstances);

      assertThat(noReportType).isNotEqualTo(exposureWindow);
      assertThat(exposureWindow)
          .isNotEqualTo(noReportType)
          .isNotEqualTo(differentReportType);
    }

  }

  private ExposureWindow generateExposureWindow(LocalDate date, Integer reportType, Integer infectiousness,
      Integer calibrationConfidence, Integer transmissionRiskLevel, Double normalizedTime, ClientMetadataDetails cmd,
      TechnicalMetadata tmd, Set<ScanInstance> scanInstances) {
    return new ExposureWindow(null, date, reportType, infectiousness, calibrationConfidence, transmissionRiskLevel,
        normalizedTime, cmd, tmd, scanInstances);
  }
}
