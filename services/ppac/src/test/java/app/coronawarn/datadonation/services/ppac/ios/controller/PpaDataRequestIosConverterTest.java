package app.coronawarn.datadonation.services.ppac.ios.controller;

import static app.coronawarn.datadonation.common.protocols.internal.ppdd.PPALastSubmissionFlowScreen.SUBMISSION_FLOW_SCREEN_OTHER;
import static app.coronawarn.datadonation.common.protocols.internal.ppdd.PPALastSubmissionFlowScreen.SUBMISSION_FLOW_SCREEN_OTHER_VALUE;
import static app.coronawarn.datadonation.common.protocols.internal.ppdd.PPARiskLevel.RISK_LEVEL_HIGH;
import static app.coronawarn.datadonation.common.protocols.internal.ppdd.PPARiskLevel.RISK_LEVEL_HIGH_VALUE;
import static app.coronawarn.datadonation.common.protocols.internal.ppdd.PPATestResult.TEST_RESULT_POSITIVE;
import static app.coronawarn.datadonation.common.protocols.internal.ppdd.PPATestResult.TEST_RESULT_POSITIVE_VALUE;
import static org.assertj.core.api.Assertions.assertThat;

import app.coronawarn.datadonation.common.persistence.domain.metrics.ExposureWindow;
import app.coronawarn.datadonation.common.persistence.domain.metrics.ExposureWindowTestResult;
import app.coronawarn.datadonation.common.persistence.domain.metrics.KeySubmissionMetadataWithClientMetadata;
import app.coronawarn.datadonation.common.persistence.domain.metrics.KeySubmissionMetadataWithUserMetadata;
import app.coronawarn.datadonation.common.persistence.domain.metrics.TestResultMetadata;
import app.coronawarn.datadonation.common.persistence.service.PpaDataStorageRequest;
import app.coronawarn.datadonation.common.protocols.internal.ppdd.ExposureRiskMetadata;
import app.coronawarn.datadonation.common.protocols.internal.ppdd.PPADataIOS;
import app.coronawarn.datadonation.common.protocols.internal.ppdd.PPADataRequestIOS;
import app.coronawarn.datadonation.common.protocols.internal.ppdd.PPAExposureWindow;
import app.coronawarn.datadonation.common.protocols.internal.ppdd.PPAExposureWindowInfectiousness;
import app.coronawarn.datadonation.common.protocols.internal.ppdd.PPAKeySubmissionMetadata;
import app.coronawarn.datadonation.common.protocols.internal.ppdd.PPANewExposureWindow;
import app.coronawarn.datadonation.common.protocols.internal.ppdd.PPATestResult;
import app.coronawarn.datadonation.common.protocols.internal.ppdd.PPATestResultMetadata;
import app.coronawarn.datadonation.common.protocols.internal.ppdd.TriStateBoolean;
import app.coronawarn.datadonation.common.utils.TimeUtils;
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

@ExtendWith(MockitoExtension.class)
public class PpaDataRequestIosConverterTest {

  @InjectMocks
  private PpaDataRequestIosConverter underTest;

  private PpacConfiguration ppacConfig;

  @BeforeEach
  public void setup() {
    ppacConfig = new PpacConfiguration();
    ppacConfig.setMaxExposureWindowsToRejectSubmission(672);
    ppacConfig.setMaxExposureWindowsToStore(672);
  }

  @Test
  public void testConvertToExposureWindow() {
    final Long epochSecondForNow = TimeUtils.getEpochSecondsForNow();
    LocalDate now = TimeUtils.getLocalDateFor(epochSecondForNow);
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

    final PPADataIOS payload = PPADataIOS.newBuilder()
        .addNewExposureWindows(ppaNewExposureWindow).build();

    PPADataRequestIOS ppaDataRequestIOS = PPADataRequestIOS.newBuilder()
        .setPayload(payload).build();
    // when
    final PpaDataStorageRequest ppaDataStorageRequest = underTest
        .convertToStorageRequest(ppaDataRequestIOS, ppacConfig);
    assertThat(ppaDataStorageRequest).isNotNull();
    assertThat(ppaDataStorageRequest.getExposureWindowsMetric()).isPresent();
    final ExposureWindow exposureWindow = ppaDataStorageRequest.getExposureWindowsMetric().get().iterator().next();
    assertThat(exposureWindow.getDate()).isEqualTo(now);
    assertThat(exposureWindow.getCallibrationConfidence()).isEqualTo(1);
    assertThat(exposureWindow.getInfectiousness()).isEqualTo(PPAExposureWindowInfectiousness.INFECTIOUSNESS_HIGH_VALUE);
  }

  @Test
  public void testConvertExposureRiskMetaData() {

    final Long epochSecondForNow = TimeUtils.getEpochSecondsForNow();
    LocalDate now = TimeUtils.getLocalDateFor(epochSecondForNow);
    final ExposureRiskMetadata exposureRiskMetadataSrc = ExposureRiskMetadata.newBuilder()
        .setDateChangedComparedToPreviousSubmission(true)
        .setMostRecentDateAtRiskLevel(epochSecondForNow)
        .setRiskLevel(RISK_LEVEL_HIGH)
        .setRiskLevelChangedComparedToPreviousSubmission(true)
        .build();

    final PPADataIOS payload = PPADataIOS.newBuilder()
        .addExposureRiskMetadataSet(exposureRiskMetadataSrc).build();

    PPADataRequestIOS ppaDataRequestIOS = PPADataRequestIOS.newBuilder()
        .setPayload(payload).build();
    // when
    final PpaDataStorageRequest ppaDataStorageRequest = underTest
        .convertToStorageRequest(ppaDataRequestIOS, ppacConfig);
    assertThat(ppaDataStorageRequest).isNotNull();
    assertThat(ppaDataStorageRequest.getExposureRiskMetric()).isPresent();
    final app.coronawarn.datadonation.common.persistence.domain.metrics.ExposureRiskMetadata exposureRiskMetaData = ppaDataStorageRequest
        .getExposureRiskMetric().get();
    assertThat(exposureRiskMetaData.getMostRecentDateChanged()).isTrue();
    assertThat(exposureRiskMetaData.getRiskLevel()).isEqualTo(RISK_LEVEL_HIGH.getNumber());
    assertThat(exposureRiskMetaData.getMostRecentDateAtRiskLevel()).isEqualTo(now);
  }

  @Test
  public void testConvertToTestResultMetrics() {
    final Long epochSecondForNow = TimeUtils.getEpochSecondsForNow();
    LocalDate now = TimeUtils.getLocalDateFor(epochSecondForNow);
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
            .setTestResult(TEST_RESULT_POSITIVE)
            .setRiskLevelAtTestRegistration(RISK_LEVEL_HIGH)
            .setHoursSinceTestRegistration(5)
            .setHoursSinceHighRiskWarningAtTestRegistration(5)
            .setDaysSinceMostRecentDateAtRiskLevelAtTestRegistration(5)
            .build();

    final PPADataIOS payload = PPADataIOS.newBuilder()
        .addTestResultMetadataSet(ppaTestResultMetadata).build();

    PPADataRequestIOS ppaDataRequestIOS = PPADataRequestIOS.newBuilder()
        .setPayload(payload).build();
    // when
    final PpaDataStorageRequest ppaDataStorageRequest = underTest
        .convertToStorageRequest(ppaDataRequestIOS, ppacConfig);
    assertThat(ppaDataStorageRequest).isNotNull();
    assertThat(ppaDataStorageRequest.getTestResultMetric()).isPresent();
    final TestResultMetadata testResultMetadata = ppaDataStorageRequest
        .getTestResultMetric().get();
    assertThat(testResultMetadata.getTestResult()).isEqualTo(TEST_RESULT_POSITIVE_VALUE);
    assertThat(testResultMetadata.getRiskLevelAtTestRegistration()).isEqualTo(RISK_LEVEL_HIGH_VALUE);
    assertThat(testResultMetadata.getHoursSinceTestRegistration()).isEqualTo(5);
    assertThat(testResultMetadata.getHoursSinceHighRiskWarningAtTestRegistration()).isEqualTo(5);
    assertThat(testResultMetadata.getDaysSinceMostRecentDateAtRiskLevelAtTestRegistration()).isEqualTo(5);
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

    final PPADataIOS payload = PPADataIOS.newBuilder()
        .addTestResultMetadataSet(ppaTestResultMetadata).build();

    PPADataRequestIOS ppaDataRequestIOS = PPADataRequestIOS.newBuilder()
        .setPayload(payload).build();
    // when
    final PpaDataStorageRequest ppaDataStorageRequest = underTest
        .convertToStorageRequest(ppaDataRequestIOS, ppacConfig);
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

    final PPADataIOS payload = PPADataIOS.newBuilder()
        .addTestResultMetadataSet(ppaTestResultMetadata).build();

    PPADataRequestIOS ppaDataRequestIOS = PPADataRequestIOS.newBuilder()
        .setPayload(payload).build();
    // when
    final PpaDataStorageRequest ppaDataStorageRequest = underTest
        .convertToStorageRequest(ppaDataRequestIOS, ppacConfig);
    assertThat(ppaDataStorageRequest).isNotNull();
    assertThat(ppaDataStorageRequest.getTestResultMetric()).isPresent();
    assertThat(ppaDataStorageRequest.getExposureWindowTestResult()).contains(Collections.emptyList());
  }

  @Test
  public void testConvertToKeySubmissionMetrics() {
    // given
    final PPAKeySubmissionMetadata ppaKeySubmissionMetadata =
        PPAKeySubmissionMetadata.newBuilder()
            .setAdvancedConsentGiven(true)
            .setDaysSinceMostRecentDateAtRiskLevelAtTestRegistration(5)
            .setHoursSinceHighRiskWarningAtTestRegistration(5)
            .setHoursSinceTestRegistration(5)
            .setLastSubmissionFlowScreen(SUBMISSION_FLOW_SCREEN_OTHER)
            .setSubmittedAfterSymptomFlow(true)
            .setSubmittedWithCheckIns(TriStateBoolean.TSB_TRUE)
            .build();

    final PPADataIOS payload = PPADataIOS.newBuilder()
        .addKeySubmissionMetadataSet(ppaKeySubmissionMetadata).build();

    PPADataRequestIOS ppaDataRequestIOS = PPADataRequestIOS.newBuilder()
        .setPayload(payload).build();
    // when
    final PpaDataStorageRequest ppaDataStorageRequest = underTest
        .convertToStorageRequest(ppaDataRequestIOS, ppacConfig);

    // then
    assertThat(ppaDataStorageRequest).isNotNull();
    assertThat(ppaDataStorageRequest.getKeySubmissionWithUserMetadata()).isPresent();
    final List<KeySubmissionMetadataWithUserMetadata> keySubmissions = ppaDataStorageRequest
        .getKeySubmissionWithUserMetadata().get();
    final List<KeySubmissionMetadataWithClientMetadata> clientMetadatas = ppaDataStorageRequest
        .getKeySubmissionWithClientMetadata().get();
    for (KeySubmissionMetadataWithUserMetadata keySubmission : keySubmissions) {
      assertThat(keySubmission.getDaysSinceMostRecentDateAtRiskLevelAtTestRegistration()).isEqualTo(5);
      assertThat(keySubmission.getHoursSinceHighRiskWarningAtTestRegistration()).isEqualTo(5);
      assertThat(keySubmission.getHoursSinceTestRegistration()).isEqualTo(5);
    }

    for (KeySubmissionMetadataWithClientMetadata clientMetadata : clientMetadatas) {
      assertThat(clientMetadata.getAdvancedConsentGiven()).isTrue();
      assertThat(clientMetadata.getLastSubmissionFlowScreen()).isEqualTo(SUBMISSION_FLOW_SCREEN_OTHER_VALUE);
      assertThat(clientMetadata.getSubmittedAfterSymptomFlow()).isTrue();
      assertThat(clientMetadata.getSubmittedWithCheckIns()).isTrue();
    }
  }

  @Test
  public void testConvertExposureRiskMetaData_emptyExposureMetrics() {
    final PPADataIOS payload = PPADataIOS.newBuilder()
        .addAllExposureRiskMetadataSet(Collections.emptyList())
        .addAllNewExposureWindows(Collections.emptyList())
        .addAllKeySubmissionMetadataSet(Collections.emptyList())
        .addAllTestResultMetadataSet(Collections.emptyList()).build();

    PPADataRequestIOS ppaDataRequestIOS = PPADataRequestIOS.newBuilder()
        .setPayload(payload).build();
    // when
    final PpaDataStorageRequest ppaDataStorageRequest = underTest
        .convertToStorageRequest(ppaDataRequestIOS, ppacConfig);
    assertThat(ppaDataStorageRequest).isNotNull();
    assertThat(ppaDataStorageRequest.getExposureRiskMetric()).isNotPresent();
    assertThat(ppaDataStorageRequest.getKeySubmissionWithClientMetadata()).isNotPresent();
    assertThat(ppaDataStorageRequest.getKeySubmissionWithUserMetadata()).isNotPresent();
    assertThat(ppaDataStorageRequest.getTestResultMetric()).isNotPresent();
    assertThat(ppaDataStorageRequest.getExposureWindowsMetric()).isNotPresent();
  }
}
