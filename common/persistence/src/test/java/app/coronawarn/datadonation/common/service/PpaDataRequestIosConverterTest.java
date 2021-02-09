package app.coronawarn.datadonation.common.service;

import static app.coronawarn.datadonation.common.protocols.internal.ppdd.PPALastSubmissionFlowScreen.SUBMISSION_FLOW_SCREEN_OTHER;
import static app.coronawarn.datadonation.common.protocols.internal.ppdd.PPARiskLevel.RISK_LEVEL_HIGH;
import static app.coronawarn.datadonation.common.protocols.internal.ppdd.PPARiskLevel.RISK_LEVEL_HIGH_VALUE;
import static app.coronawarn.datadonation.common.protocols.internal.ppdd.PPATestResult.TEST_RESULT_POSITIVE;
import static app.coronawarn.datadonation.common.protocols.internal.ppdd.PPATestResult.TEST_RESULT_POSITIVE_VALUE;
import static org.assertj.core.api.Assertions.assertThat;

import app.coronawarn.datadonation.common.persistence.domain.metrics.KeySubmissionMetadataWithUserMetadata;
import app.coronawarn.datadonation.common.persistence.domain.metrics.TestResultMetadata;
import app.coronawarn.datadonation.common.persistence.service.PpaDataRequestIosConverter;
import app.coronawarn.datadonation.common.persistence.service.PpaDataStorageRequest;
import app.coronawarn.datadonation.common.protocols.internal.ppdd.ExposureRiskMetadata;
import app.coronawarn.datadonation.common.protocols.internal.ppdd.PPADataIOS;
import app.coronawarn.datadonation.common.protocols.internal.ppdd.PPAKeySubmissionMetadata;
import app.coronawarn.datadonation.common.protocols.internal.ppdd.PPATestResultMetadata;
import app.coronawarn.datadonation.common.protocols.internal.ppdd.PpaDataRequestIos.PPADataRequestIOS;
import app.coronawarn.datadonation.common.utils.TimeUtils;
import java.time.LocalDate;
import java.util.Collections;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
public class PpaDataRequestIosConverterTest {

  @InjectMocks
  private PpaDataRequestIosConverter underTest;

  @Test
  public void testConvertExposureRiskMetaData() {

    final Long epochSecondForNow = TimeUtils.getEpochSecondForNow();
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
    final PpaDataStorageRequest ppaDataStorageRequest = underTest.convertToStorageRequest(ppaDataRequestIOS);
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
    final PpaDataStorageRequest ppaDataStorageRequest = underTest.convertToStorageRequest(ppaDataRequestIOS);
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

  @Test
  public void testConvertToKeySubmissionMetrics() {
    final PPAKeySubmissionMetadata ppaKeySubmissionMetadata =
        PPAKeySubmissionMetadata.newBuilder()
            .setAdvancedConsentGiven(true)
            .setDaysSinceMostRecentDateAtRiskLevelAtTestRegistration(5)
            .setHoursSinceHighRiskWarningAtTestRegistration(5)
            .setHoursSinceTestRegistration(5)
            .setLastSubmissionFlowScreen(SUBMISSION_FLOW_SCREEN_OTHER)
            .setSubmittedAfterSymptomFlow(true)
            .build();

    final PPADataIOS payload = PPADataIOS.newBuilder()
        .addKeySubmissionMetadataSet(ppaKeySubmissionMetadata).build();

    PPADataRequestIOS ppaDataRequestIOS = PPADataRequestIOS.newBuilder()
        .setPayload(payload).build();
    // when
    final PpaDataStorageRequest ppaDataStorageRequest = underTest.convertToStorageRequest(ppaDataRequestIOS);
    assertThat(ppaDataStorageRequest).isNotNull();
    assertThat(ppaDataStorageRequest.getKeySubmissionWithUserMetadata()).isPresent();
    final KeySubmissionMetadataWithUserMetadata keySubmission = ppaDataStorageRequest
        .getKeySubmissionWithUserMetadata().get();

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
    final PpaDataStorageRequest ppaDataStorageRequest = underTest.convertToStorageRequest(ppaDataRequestIOS);
    assertThat(ppaDataStorageRequest).isNotNull();
    assertThat(ppaDataStorageRequest.getExposureRiskMetric()).isNotPresent();
    assertThat(ppaDataStorageRequest.getKeySubmissionWithClientMetadata()).isNotPresent();
    assertThat(ppaDataStorageRequest.getKeySubmissionWithUserMetadata()).isNotPresent();
    assertThat(ppaDataStorageRequest.getTestResultMetric()).isNotPresent();
    assertThat(ppaDataStorageRequest.getExposureWindowsMetric()).isNotPresent();
  }
}
