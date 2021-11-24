package app.coronawarn.datadonation.services.ppac.android.controller;

import static app.coronawarn.datadonation.common.protocols.internal.ppdd.PPARiskLevel.RISK_LEVEL_HIGH;
import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;

import app.coronawarn.datadonation.common.persistence.domain.metrics.ExposureWindowTestResult;
import app.coronawarn.datadonation.common.persistence.domain.metrics.TechnicalMetadata;
import app.coronawarn.datadonation.common.persistence.domain.metrics.embeddable.ClientMetadataDetails;
import app.coronawarn.datadonation.common.persistence.domain.metrics.embeddable.CwaVersionMetadata;
import app.coronawarn.datadonation.common.persistence.service.PpaDataStorageRequest;
import app.coronawarn.datadonation.common.protocols.internal.ppdd.ExposureRiskMetadata;
import app.coronawarn.datadonation.common.protocols.internal.ppdd.PPAClientMetadataAndroid;
import app.coronawarn.datadonation.common.protocols.internal.ppdd.PPADataAndroid;
import app.coronawarn.datadonation.common.protocols.internal.ppdd.PPADataIOS;
import app.coronawarn.datadonation.common.protocols.internal.ppdd.PPADataRequestAndroid;
import app.coronawarn.datadonation.common.protocols.internal.ppdd.PPADataRequestIOS;
import app.coronawarn.datadonation.common.protocols.internal.ppdd.PPAExposureWindow;
import app.coronawarn.datadonation.common.protocols.internal.ppdd.PPAExposureWindowInfectiousness;
import app.coronawarn.datadonation.common.protocols.internal.ppdd.PPANewExposureWindow;
import app.coronawarn.datadonation.common.protocols.internal.ppdd.PPARiskLevel;
import app.coronawarn.datadonation.common.protocols.internal.ppdd.PPASemanticVersion;
import app.coronawarn.datadonation.common.protocols.internal.ppdd.PPATestResult;
import app.coronawarn.datadonation.common.protocols.internal.ppdd.PPATestResultMetadata;
import app.coronawarn.datadonation.common.protocols.internal.ppdd.PPAUserMetadata;
import app.coronawarn.datadonation.common.utils.TimeUtils;
import app.coronawarn.datadonation.services.ppac.android.attestation.AttestationStatement;
import app.coronawarn.datadonation.services.ppac.config.PpacConfiguration;
import java.time.LocalDate;
import java.util.Collections;
import java.util.List;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.EnumSource;
import org.mockito.InjectMocks;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.beans.factory.annotation.Autowired;

@ExtendWith(MockitoExtension.class)
public class PpaDataRequestAndroidConverterTest
    extends PpaDataRequestAndroidConverter {

  private PpacConfiguration ppacConfig;

  @InjectMocks
  private AttestationStatement attestationStatement;

  @BeforeEach
  public void setup() {
    ppacConfig = new PpacConfiguration();
    //The maximum number of exposure windows to store per submission.
    //672 = 24 hours per day * 0,5 hours per Exposure Window * 14 days
    ppacConfig.setMaxExposureWindowsToRejectSubmission(672);
    ppacConfig.setMaxExposureWindowsToStore(672);
  }

  @Test
  public void convertToExposureMetricsTestRiskLevelUnknownValue() {
    ExposureRiskMetadata exposureRiskMetadata = ExposureRiskMetadata.newBuilder()
        .setPtRiskLevelValue(PPARiskLevel.RISK_LEVEL_UNKNOWN_VALUE).build();

    app.coronawarn.datadonation.common.persistence.domain.metrics.ExposureRiskMetadata dbExposureRiskMetadata =
        convertToExposureMetrics(
            Collections.singletonList(exposureRiskMetadata),
            PPAUserMetadata.getDefaultInstance(),
            TechnicalMetadata.newEmptyInstance(),
            PPAClientMetadataAndroid.getDefaultInstance());

    assertNull(dbExposureRiskMetadata.getPtRiskLevelChanged());
    assertNull(dbExposureRiskMetadata.getPtMostRecentDateAtRiskLevel());
    assertNull(dbExposureRiskMetadata.getPtMostRecentDateChanged());
  }

  @Test
  public void convertToExposureMetricsTest() {
    ExposureRiskMetadata exposureRiskMetadata = ExposureRiskMetadata.newBuilder()
        .setPtRiskLevelValue(PPARiskLevel.RISK_LEVEL_HIGH_VALUE)
        .setPtRiskLevelChangedComparedToPreviousSubmission(true)
        .setPtMostRecentDateAtRiskLevel(0)
        .setPtDateChangedComparedToPreviousSubmission(false)
        .build();
    PPASemanticVersion semanticVersion = PPASemanticVersion.newBuilder().setMajor(2).setMinor(2).setPatch(1).build();

    app.coronawarn.datadonation.common.persistence.domain.metrics.ExposureRiskMetadata dbExposureRiskMetadata =
        convertToExposureMetrics(
            Collections.singletonList(exposureRiskMetadata),
            PPAUserMetadata.getDefaultInstance(),
            TechnicalMetadata.newEmptyInstance(),
            PPAClientMetadataAndroid.newBuilder().setAndroidApiLevel(1)
                .setCwaVersion(semanticVersion).build()
        );

    assertTrue(dbExposureRiskMetadata.getPtRiskLevelChanged());
    assertEquals(LocalDate.of(1970, 1, 1), dbExposureRiskMetadata.getPtMostRecentDateAtRiskLevel());
    assertFalse(dbExposureRiskMetadata.getPtMostRecentDateChanged());
  }

  @ParameterizedTest
  @EnumSource(value = PPATestResult.class,
      names = {"TEST_RESULT_POSITIVE", "TEST_RESULT_NEGATIVE", "TEST_RESULT_RAT_POSITIVE", "TEST_RESULT_RAT_NEGATIVE"})
  public void testConvertToExposureWindowTestResults(PPATestResult ppaTestResults) {
    final Long epochSecondForNow = TimeUtils.getEpochSecondsForNow();
    final PPAExposureWindow ppaExposureWindow = PPAExposureWindow
        .newBuilder()
        .setCalibrationConfidence(1)
        .setInfectiousness(PPAExposureWindowInfectiousness.INFECTIOUSNESS_HIGH)
        .setDate(epochSecondForNow)
        .build();

    final PPANewExposureWindow ppaNewExposureWindow = PPANewExposureWindow
        .newBuilder()
        .setExposureWindow(ppaExposureWindow)
        .build();

    final PPATestResultMetadata ppaTestResultMetadata =
        PPATestResultMetadata.newBuilder()
            .setTestResult(ppaTestResults)
            .setRiskLevelAtTestRegistration(RISK_LEVEL_HIGH)
            .setHoursSinceTestRegistration(5)
            .setHoursSinceHighRiskWarningAtTestRegistration(5)
            .setDaysSinceMostRecentDateAtRiskLevelAtTestRegistration(5)
            .addExposureWindowsAtTestRegistration(ppaNewExposureWindow)
            .addExposureWindowsUntilTestResult(ppaNewExposureWindow)
            .build();

    final PPADataAndroid payload = PPADataAndroid.newBuilder()
        .addTestResultMetadataSet(ppaTestResultMetadata).build();

    PPADataRequestAndroid ppaDataRequestAndroid = PPADataRequestAndroid.newBuilder()
        .setPayload(payload).build();
    // when
    final PpaDataStorageRequest ppaDataStorageRequest = convertToStorageRequest(ppaDataRequestAndroid, ppacConfig,
        attestationStatement);
    assertThat(ppaDataStorageRequest).isNotNull();
    assertThat(ppaDataStorageRequest.getTestResultMetric()).isPresent();
    final List<ExposureWindowTestResult> testResultsMetadata = ppaDataStorageRequest
        .getExposureWindowTestResult().get();
    assertThat(testResultsMetadata.get(0).getTestResult()).isEqualTo(ppaTestResults.getNumber());
    assertThat(testResultsMetadata.get(0).getExposureWindowsAtTestRegistrations().size()).isEqualTo(2);
  }

  @ParameterizedTest
  @EnumSource(value = PPATestResult.class,
      names = {"TEST_RESULT_RAT_PENDING", "TEST_RESULT_UNKNOWN", "TEST_RESULT_PENDING", "TEST_RESULT_RAT_INVALID"})
  public void testConvertToExposureWindowTestResultsFailedBecausePpaTestResult(PPATestResult ppaTestResults) {
    final Long epochSecondForNow = TimeUtils.getEpochSecondsForNow();
    final PPAExposureWindow ppaExposureWindow = PPAExposureWindow
        .newBuilder()
        .setCalibrationConfidence(1)
        .setInfectiousness(PPAExposureWindowInfectiousness.INFECTIOUSNESS_HIGH)
        .setDate(epochSecondForNow)
        .build();

    final PPANewExposureWindow ppaNewExposureWindow = PPANewExposureWindow
        .newBuilder()
        .setExposureWindow(ppaExposureWindow)
        .build();

    final PPATestResultMetadata ppaTestResultMetadata =
        PPATestResultMetadata.newBuilder()
            .setTestResult(ppaTestResults)
            .setRiskLevelAtTestRegistration(RISK_LEVEL_HIGH)
            .setHoursSinceTestRegistration(5)
            .setHoursSinceHighRiskWarningAtTestRegistration(5)
            .setDaysSinceMostRecentDateAtRiskLevelAtTestRegistration(5)
            .addExposureWindowsAtTestRegistration(ppaNewExposureWindow)
            .addExposureWindowsUntilTestResult(ppaNewExposureWindow)
            .build();

    final PPADataAndroid payload = PPADataAndroid.newBuilder()
        .addTestResultMetadataSet(ppaTestResultMetadata).build();

    PPADataRequestAndroid ppaDataRequestIOS = PPADataRequestAndroid.newBuilder()
        .setPayload(payload).build();
    // when
    final PpaDataStorageRequest ppaDataStorageRequest = convertToStorageRequest(ppaDataRequestIOS,
        ppacConfig, attestationStatement);
    assertThat(ppaDataStorageRequest).isNotNull();
    assertThat(ppaDataStorageRequest.getTestResultMetric()).isPresent();
    assertThat(ppaDataStorageRequest.getExposureWindowTestResult()).contains(Collections.emptyList());
  }

  @Override
  protected ClientMetadataDetails convertToClientMetadataDetails(PPAClientMetadataAndroid clientMetadata) {
    PPASemanticVersion cwaVersion = clientMetadata.getCwaVersion();
    CwaVersionMetadata cwaVersionMetadata = new CwaVersionMetadata(cwaVersion.getMajor(), cwaVersion.getMinor(),
        cwaVersion.getPatch());
    return new ClientMetadataDetails(cwaVersionMetadata, clientMetadata.getAppConfigETag(), null, null, null,
        clientMetadata.getAndroidApiLevel(), clientMetadata.getEnfVersion());
  }

  @Override
  protected CwaVersionMetadata convertToCwaVersionMetadata(PPAClientMetadataAndroid clientMetadata) {
    PPASemanticVersion ppaSemanticVersion = clientMetadata.getCwaVersion();
    return new CwaVersionMetadata(ppaSemanticVersion.getMajor(),
        ppaSemanticVersion.getMinor(), ppaSemanticVersion.getPatch());
  }
}
