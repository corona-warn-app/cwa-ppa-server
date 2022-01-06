package app.coronawarn.datadonation.services.ppac.android.testdata;

import app.coronawarn.datadonation.common.persistence.domain.metrics.ExposureWindow;
import app.coronawarn.datadonation.common.persistence.domain.metrics.KeySubmissionMetadataWithClientMetadata;
import app.coronawarn.datadonation.common.persistence.domain.metrics.KeySubmissionMetadataWithUserMetadata;
import app.coronawarn.datadonation.common.persistence.domain.metrics.TestResultMetadata;
import app.coronawarn.datadonation.common.persistence.service.PpaDataStorageRequest;
import app.coronawarn.datadonation.common.protocols.internal.ppdd.ExposureRiskMetadata;
import app.coronawarn.datadonation.common.protocols.internal.ppdd.PPAAgeGroup;
import app.coronawarn.datadonation.common.protocols.internal.ppdd.PPACAndroid;
import app.coronawarn.datadonation.common.protocols.internal.ppdd.PPACAndroid.Builder;
import app.coronawarn.datadonation.common.protocols.internal.ppdd.PPAClientMetadataAndroid;
import app.coronawarn.datadonation.common.protocols.internal.ppdd.PPADataAndroid;
import app.coronawarn.datadonation.common.protocols.internal.ppdd.PPADataRequestAndroid;
import app.coronawarn.datadonation.common.protocols.internal.ppdd.PPAExposureWindow;
import app.coronawarn.datadonation.common.protocols.internal.ppdd.PPAExposureWindowInfectiousness;
import app.coronawarn.datadonation.common.protocols.internal.ppdd.PPAExposureWindowReportType;
import app.coronawarn.datadonation.common.protocols.internal.ppdd.PPAExposureWindowScanInstance;
import app.coronawarn.datadonation.common.protocols.internal.ppdd.PPAFederalState;
import app.coronawarn.datadonation.common.protocols.internal.ppdd.PPAKeySubmissionMetadata;
import app.coronawarn.datadonation.common.protocols.internal.ppdd.PPALastSubmissionFlowScreen;
import app.coronawarn.datadonation.common.protocols.internal.ppdd.PPANewExposureWindow;
import app.coronawarn.datadonation.common.protocols.internal.ppdd.PPARiskLevel;
import app.coronawarn.datadonation.common.protocols.internal.ppdd.PPASemanticVersion;
import app.coronawarn.datadonation.common.protocols.internal.ppdd.PPATestResult;
import app.coronawarn.datadonation.common.protocols.internal.ppdd.PPATestResultMetadata;
import app.coronawarn.datadonation.common.protocols.internal.ppdd.PPAUserMetadata;
import java.io.IOException;
import java.io.InputStream;
import java.io.Serializable;
import java.time.Instant;
import java.time.LocalDate;
import java.time.ZoneOffset;
import java.time.temporal.ChronoUnit;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Properties;
import java.util.Set;
import java.util.function.Supplier;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import org.apache.commons.lang3.RandomUtils;

public class TestData {

  public static final String TEST_NONCE_VALUE = "AAAAAAAAAAAAAAAAAAAAAA==";
  public static final String TEST_APK_PACKAGE_NAME = "de.rki.coronawarnapp.test";
  public static final String TEST_APK_CERTIFICATE_DIGEST = "9VLvUGV0Gkx24etruEBYikvAtqSQ9iY6rYuKhG+xwKE=";
  // equal to the CN of the test certificate created under src/test/resources/certificates
  public static final String TEST_CERTIFICATE_HOSTNAME = "localhost";
  public static final int ATTESTATION_VALIDITY_SECONDS = 7200;
  
  public static final long FIX_TEST_DATE_FOR_NONCE = LocalDate.of(2021, 1, 1).toEpochDay();

  public static String loadJwsWithExpiredCertificates() throws IOException {
    InputStream fileStream = TestData.class.getResourceAsStream("/jwsSamples/invalid_samples.properties");
    Properties properties = new Properties();
    properties.load(fileStream);
    return (String) properties.get("expiredCertificates");
  }

  public static String getJwsPayloadValues() throws IOException {
    Map<String, Serializable> payloadValues = getJwsPayloadDefaultValue();
    String encodedJws = JwsGenerationUtil.createCompactSerializedJws(payloadValues);
    return encodedJws;
  }

  public static String getJwsPayloadWrongApkPackageName() throws IOException {
    Map<String, Serializable> payloadValues = new HashMap<>(getJwsPayloadDefaultValue());
    payloadValues.put("apkPackageName", "de.rki.wrong.test");
    String encodedJws = JwsGenerationUtil.createCompactSerializedJws(payloadValues);
    return encodedJws;
  }

  public static String getJwsPayloadAttestationValidityExpired() throws IOException {
    Map<String, Serializable> payloadValues = new HashMap<>(getJwsPayloadDefaultValue());
    payloadValues.put("timestampMs", String.valueOf(Instant.now().minusSeconds(8000).toEpochMilli()));
    String encodedJws = JwsGenerationUtil.createCompactSerializedJws(payloadValues);
    return encodedJws;
  }

  public static String getJwsPayloadWithUnacceptedApkCertificateDigestHash() throws IOException {
    Map<String, Serializable> payloadValues = new HashMap<>(getJwsPayloadDefaultValue());
    payloadValues.put("apkCertificateDigestSha256", new String[]{""});
    String encodedJws = JwsGenerationUtil.createCompactSerializedJws(payloadValues);
    return encodedJws;
  }

  public static String getJwsPayloadWithBasicIntegrityViolation() throws IOException {
    Map<String, Serializable> payloadValues = new HashMap<>(getJwsPayloadDefaultValue());
    payloadValues.put("basicIntegrity", false);
    String encodedJws = JwsGenerationUtil.createCompactSerializedJws(payloadValues);
    return encodedJws;
  }

  public static String getJwsPayloadWithEvaluationType(String evaluationType) throws IOException {
    Map<String, Serializable> payloadValues = new HashMap<>(getJwsPayloadDefaultValue());
    payloadValues.put("evaluationType", evaluationType);
    String encodedJws = JwsGenerationUtil.createCompactSerializedJws(payloadValues);
    return encodedJws;
  }

  public static String getJwsPayloadWithIntegrityFlagsChecked() throws IOException {
    Map<String, Serializable> payloadValues = new HashMap<>(getJwsPayloadDefaultValue());
    payloadValues.put("ctsProfileMatch", true);
    payloadValues.put("basicIntegrity", true);
    String encodedJws = JwsGenerationUtil.createCompactSerializedJws(payloadValues);
    return encodedJws;
  }

  public static String getJwsPayloadWithCtsMatchViolation() throws IOException {
    Map<String, Serializable> payloadValues = new HashMap<>(getJwsPayloadDefaultValue());
    payloadValues.put("ctsProfileMatch", false);
    String encodedJws = JwsGenerationUtil.createCompactSerializedJws(payloadValues);
    return encodedJws;
  }

  public static String getJwsPayloadWithNonce(String nonce) throws IOException {
    Map<String, Serializable> payloadValues = new HashMap<>(getJwsPayloadDefaultValue());
    payloadValues.put("nonce", nonce);
    String encodedJws = JwsGenerationUtil.createCompactSerializedJws(payloadValues);
    return encodedJws;
  }

  public static PPACAndroid newAuthenticationObject(String jws, String salt) {
    Builder builder = PPACAndroid.newBuilder();
    if (jws != null) {
      builder.setSafetyNetJws(jws);
    }
    if (salt != null) {
      builder.setSalt(salt);
    }
    return builder.build();
  }

  private static Map<String, Serializable> getJwsPayloadDefaultValue() throws IOException {
    return Map.of("nonce", TEST_NONCE_VALUE, "timestampMs",
        String.valueOf(Instant.now().minusSeconds(500).toEpochMilli()), "apkPackageName", TEST_APK_PACKAGE_NAME,
        "apkDigestSha256", "9oiqOMQAZfBgCnI0jyN7TgPAQNSSxWrjh14f0eXpB3U=", "ctsProfileMatch", "false",
        "apkCertificateDigestSha256", new String[]{TEST_APK_CERTIFICATE_DIGEST}, "basicIntegrity", "false", "advice",
        "RESTORE_TO_FACTORY_ROM,LOCK_BOOTLOADER", "evaluationType", "BASIC");
  }

  public static ExposureRiskMetadata getValidExposureRiskMetadata() {
    return ExposureRiskMetadata.newBuilder().setRiskLevel(PPARiskLevel.RISK_LEVEL_HIGH)
        .setMostRecentDateAtRiskLevel(FIX_TEST_DATE_FOR_NONCE)
        .setRiskLevelChangedComparedToPreviousSubmission(true).build();
  }

  public static ExposureRiskMetadata getInvalidExposureRiskMetadata() {
    return ExposureRiskMetadata.newBuilder().setRiskLevel(PPARiskLevel.RISK_LEVEL_HIGH)
        .setMostRecentDateAtRiskLevel(LocalDate.of(1969, 1, 1).atStartOfDay(ZoneOffset.UTC).toEpochSecond())
        .setRiskLevelChangedComparedToPreviousSubmission(true).build();
  }

  public static PPANewExposureWindow getInvalidExposureWindow() {
    return PPANewExposureWindow.newBuilder().setExposureWindow(
        PPAExposureWindow.newBuilder().setCalibrationConfidence(2).setDate(LocalDate.now().toEpochDay())).build();
  }

  public static PPANewExposureWindow getValidExposureWindow() {
    return PPANewExposureWindow.newBuilder()
        .setExposureWindow(
            PPAExposureWindow.newBuilder().setCalibrationConfidence(2).setDate(FIX_TEST_DATE_FOR_NONCE)
                .setInfectiousness(PPAExposureWindowInfectiousness.INFECTIOUSNESS_HIGH)
                .setReportType(PPAExposureWindowReportType.REPORT_TYPE_CONFIRMED_TEST)
                .addAllScanInstances(Set.of(PPAExposureWindowScanInstance.newBuilder().setMinAttenuation(1)
                    .setSecondsSinceLastScan(3).setTypicalAttenuation(4).build())))
        .build();
  }

  public static PPANewExposureWindow getValidExposureWindow(Set<PPAExposureWindowScanInstance> scanInstances) {
    return PPANewExposureWindow.newBuilder()
        .setExposureWindow(PPAExposureWindow.newBuilder().setCalibrationConfidence(2)
            .setDate(LocalDate.now().toEpochDay())
            .setInfectiousness(PPAExposureWindowInfectiousness.INFECTIOUSNESS_HIGH)
            .setReportType(PPAExposureWindowReportType.REPORT_TYPE_CONFIRMED_TEST).addAllScanInstances(scanInstances))
        .build();
  }

  public static PPATestResultMetadata getValidTestResultMetadata() {
    return PPATestResultMetadata.newBuilder().setDaysSinceMostRecentDateAtRiskLevelAtTestRegistration(2)
        .setHoursSinceHighRiskWarningAtTestRegistration(2).setHoursSinceTestRegistration(3)
        .setRiskLevelAtTestRegistration(PPARiskLevel.RISK_LEVEL_HIGH).setTestResult(PPATestResult.TEST_RESULT_NEGATIVE)
        .addAllExposureWindowsAtTestRegistration(Set.of(getValidExposureWindow()))
        .addAllExposureWindowsUntilTestResult(Set.of(getValidExposureWindow()))
        .build();
  }

  public static PPAKeySubmissionMetadata getValidKeySubmissionMetadata() {
    return PPAKeySubmissionMetadata.newBuilder().setAdvancedConsentGiven(true)
        .setDaysSinceMostRecentDateAtRiskLevelAtTestRegistration(4).setHoursSinceHighRiskWarningAtTestRegistration(3)
        .setHoursSinceTestRegistration(2)
        .setLastSubmissionFlowScreen(PPALastSubmissionFlowScreen.SUBMISSION_FLOW_SCREEN_SYMPTOM_ONSET)
        .setSubmitted(true).setSubmittedAfterCancel(false).setSubmittedAfterSymptomFlow(false)
        .setSubmittedInBackground(true).setSubmittedWithTeleTAN(false)
        .setSubmittedAfterRapidAntigenTest(false).build();
  }

  public static PPAClientMetadataAndroid getValidClientMetadata() {
    return PPAClientMetadataAndroid.newBuilder().setAndroidApiLevel(3).setAppConfigETag("etag")
        .setCwaVersion(PPASemanticVersion.newBuilder().setMajor(1).setMinor(2).setPatch(4).build()).setEnfVersion(4)
        .build();
  }

  public static PPAUserMetadata getValidUserMetadata() {
    return PPAUserMetadata.newBuilder().setAdministrativeUnit(3).setAgeGroup(PPAAgeGroup.AGE_GROUP_30_TO_59)
        .setFederalState(PPAFederalState.FEDERAL_STATE_BE).build();
  }

  public static PPADataAndroid getValidAndroidDataPayload() {
    return PPADataAndroid.newBuilder().addAllExposureRiskMetadataSet(Set.of(TestData.getValidExposureRiskMetadata()))
        .addAllNewExposureWindows(Set.of(TestData.getValidExposureWindow()))
        .addAllTestResultMetadataSet(Set.of(TestData.getValidTestResultMetadata()))
        .addAllKeySubmissionMetadataSet(Set.of(TestData.getValidKeySubmissionMetadata()))
        .setClientMetadata(TestData.getValidClientMetadata()).setUserMetadata(TestData.getValidUserMetadata()).build();
  }

  public static PpaDataStorageRequest getStorageRequestWithInvalidExposureWindow() {
    return new PpaDataStorageRequest(MetricsMockData.getExposureRiskMetadata(),
        List.of(new ExposureWindow(null, null, null, null, null, null, null, null, null, Set.of())),
        MetricsMockData.getTestResultMetric(), MetricsMockData.getKeySubmissionWithClientMetadata(),
        MetricsMockData.getKeySubmissionWithUserMetadata(), MetricsMockData.getUserMetadata(),
        MetricsMockData.getClientMetadata(),
        MetricsMockData.getExposureWindowTestResults(),
        MetricsMockData.getSummarizedExposureWindowsWithUserMetadata());
  }

  public static PpaDataStorageRequest getStorageRequestWithInvalidExposureRisk() {
    return new PpaDataStorageRequest(
        new app.coronawarn.datadonation.common.persistence.domain.metrics.ExposureRiskMetadata(null, null,
            null, null,
            null, null, null, null, null, null, null, null),
        MetricsMockData.getExposureWindow(), MetricsMockData.getTestResultMetric(),
        MetricsMockData.getKeySubmissionWithClientMetadata(), MetricsMockData.getKeySubmissionWithUserMetadata(),
        MetricsMockData.getUserMetadata(), MetricsMockData.getClientMetadata(),
        MetricsMockData.getExposureWindowTestResults(),
        MetricsMockData.getSummarizedExposureWindowsWithUserMetadata());
  }

  public static PpaDataStorageRequest getStorageRequestWithInvalidUserMetadata() {
    return new PpaDataStorageRequest(MetricsMockData.getExposureRiskMetadata(), MetricsMockData.getExposureWindow(),
        MetricsMockData.getTestResultMetric(), MetricsMockData.getKeySubmissionWithClientMetadata(),
        List.of(new KeySubmissionMetadataWithUserMetadata(null, null, null, null, false, null, null, null, null,
            null, null, null, null, null)),
        MetricsMockData.getUserMetadata(), MetricsMockData.getClientMetadata(),
        MetricsMockData.getExposureWindowTestResults(),
        MetricsMockData.getSummarizedExposureWindowsWithUserMetadata());
  }

  public static PpaDataStorageRequest getStorageRequestWithInvalidClientMetadata() {
    return new PpaDataStorageRequest(MetricsMockData.getExposureRiskMetadata(), MetricsMockData.getExposureWindow(),
        MetricsMockData.getTestResultMetric(),
        List.of(new KeySubmissionMetadataWithClientMetadata(null, null, null, null, null, null, null,
            false, null, null)),
        MetricsMockData.getKeySubmissionWithUserMetadata(), MetricsMockData.getUserMetadata(),
        MetricsMockData.getClientMetadata(),
        MetricsMockData.getExposureWindowTestResults(),
        MetricsMockData.getSummarizedExposureWindowsWithUserMetadata());
  }

  public static PpaDataStorageRequest getStorageRequestWithInvalidTestResults() {
    return new PpaDataStorageRequest(MetricsMockData.getExposureRiskMetadata(), MetricsMockData.getExposureWindow(),
        List.of(new TestResultMetadata(null, null, null, null, null, null, null,
            null, null, null, null, null)),
        MetricsMockData.getKeySubmissionWithClientMetadata(), MetricsMockData.getKeySubmissionWithUserMetadata(),
        MetricsMockData.getUserMetadata(), MetricsMockData.getClientMetadata(),
        MetricsMockData.getExposureWindowTestResults(),
        MetricsMockData.getSummarizedExposureWindowsWithUserMetadata());
  }

  public static class CardinalityTestData {

    public static PPADataRequestAndroid buildPayloadWithExposureRiskMetrics(String jws, String salt,
        Integer numberOFElements) throws IOException {
      return PPADataRequestAndroid
          .newBuilder().setAuthentication(newAuthenticationObject(jws, salt)).setPayload(PPADataAndroid
              .newBuilder().addAllExposureRiskMetadataSet(setOfElements(numberOFElements,
                  // for each element make sure to set a random value to a field to avoid
                  // element duplication in sets
                  () -> ExposureRiskMetadata.newBuilder()
                      .setRiskLevelValue(RandomUtils.nextInt(0, 3))
                      .setMostRecentDateAtRiskLevel(RandomUtils.nextLong(0,
                          Instant.now().plus(10, ChronoUnit.DAYS).getEpochSecond()))
                      .build()))
              .addAllNewExposureWindows(Set.of(TestData.getValidExposureWindow()))
              .addAllTestResultMetadataSet(Set.of(TestData.getValidTestResultMetadata()))
              .addAllKeySubmissionMetadataSet(Set.of(TestData.getValidKeySubmissionMetadata()))
              .setClientMetadata(TestData.getValidClientMetadata())
              .setUserMetadata(TestData.getValidUserMetadata()))
          .build();
    }

    public static PPADataRequestAndroid buildPayloadWithExposureWindowMetrics(String jws, String salt,
        Integer numberOFElements) {
      return PPADataRequestAndroid.newBuilder().setAuthentication(newAuthenticationObject(jws, salt))
          .setPayload(PPADataAndroid
              .newBuilder().addAllExposureRiskMetadataSet(
                  Set.of(TestData.getValidExposureRiskMetadata()))
              .addAllNewExposureWindows(setOfElements(numberOFElements, () -> PPANewExposureWindow.newBuilder()
                  // for each element make sure to set a random value to a field to avoid
                  // element duplication in sets
                  .setNormalizedTime(RandomUtils.nextDouble()).setTransmissionRiskLevel(RandomUtils.nextInt(0, 3))
                  .setExposureWindow(PPAExposureWindow.newBuilder()
                      .addAllScanInstances(setOfElements(2,
                          () -> PPAExposureWindowScanInstance.newBuilder().setMinAttenuation(RandomUtils.nextInt())
                              .setSecondsSinceLastScan(RandomUtils.nextInt()).build()))
                      .build())
                  .build()))
              .addAllTestResultMetadataSet(Set.of(TestData.getValidTestResultMetadata()))
              .addAllKeySubmissionMetadataSet(Set.of(TestData.getValidKeySubmissionMetadata()))
              .setClientMetadata(TestData.getValidClientMetadata()).setUserMetadata(TestData.getValidUserMetadata()))
          .build();
    }

    public static PPADataRequestAndroid buildPayloadWithTestResults(String jws, String salt, Integer numberOfElements) {
      return PPADataRequestAndroid.newBuilder().setAuthentication(newAuthenticationObject(jws, salt))
          .setPayload(
              PPADataAndroid.newBuilder().addAllExposureRiskMetadataSet(Set.of(TestData.getValidExposureRiskMetadata()))
                  .addAllNewExposureWindows(Set.of(TestData.getValidExposureWindow()))
                  .addAllTestResultMetadataSet(setOfElements(numberOfElements,
                      // for each element make sure to set a random value to a field to avoid
                      // element duplication in sets
                      () -> PPATestResultMetadata
                          .newBuilder().setDaysSinceMostRecentDateAtRiskLevelAtTestRegistration(RandomUtils.nextInt())
                          .setHoursSinceHighRiskWarningAtTestRegistration(RandomUtils.nextInt()).build()))
                  .addAllKeySubmissionMetadataSet(Set.of(TestData.getValidKeySubmissionMetadata()))
                  .setClientMetadata(TestData.getValidClientMetadata())
                  .setUserMetadata(TestData.getValidUserMetadata()))
          .build();
    }

    public static PPADataRequestAndroid buildPayloadWithKeySubmission(String jws, String salt,
        Integer numberOfElements) {
      return PPADataRequestAndroid.newBuilder().setAuthentication(newAuthenticationObject(jws, salt))
          .setPayload(
              PPADataAndroid.newBuilder().addAllExposureRiskMetadataSet(Set.of(TestData.getValidExposureRiskMetadata()))
                  .addAllNewExposureWindows(Set.of(TestData.getValidExposureWindow()))
                  .addAllTestResultMetadataSet(Set.of(TestData.getValidTestResultMetadata()))
                  .addAllKeySubmissionMetadataSet(setOfElements(numberOfElements,
                      // for each element make sure to set a random value to a field to avoid
                      // element duplication in sets
                      () -> PPAKeySubmissionMetadata.newBuilder()
                          .setHoursSinceHighRiskWarningAtTestRegistration(RandomUtils.nextInt())
                          .setDaysSinceMostRecentDateAtRiskLevelAtTestRegistration(RandomUtils.nextInt()).build()))
                  .setClientMetadata(TestData.getValidClientMetadata())
                  .setUserMetadata(TestData.getValidUserMetadata()))
          .build();
    }

    public static PPADataRequestAndroid buildPayloadWithScanInstancesMetrics(String jws, String salt,
        Integer numberOfElements) {
      return PPADataRequestAndroid.newBuilder().setAuthentication(newAuthenticationObject(jws, salt))
          .setPayload(
              PPADataAndroid.newBuilder().addAllExposureRiskMetadataSet(Set.of(TestData.getValidExposureRiskMetadata()))
                  .addAllNewExposureWindows(Set.of(TestData.getValidExposureWindow(setOfElements(numberOfElements,
                      () -> PPAExposureWindowScanInstance.newBuilder().setMinAttenuation(RandomUtils.nextInt())
                          .setSecondsSinceLastScan(RandomUtils.nextInt()).build()))))
                  .addAllTestResultMetadataSet(Set.of(TestData.getValidTestResultMetadata()))
                  .addAllKeySubmissionMetadataSet(Set.of(TestData.getValidKeySubmissionMetadata()))
                  .setClientMetadata(TestData.getValidClientMetadata())
                  .setUserMetadata(TestData.getValidUserMetadata()))
          .build();
    }

    private static <T> Set<T> setOfElements(int numberOfElements, Supplier<T> supplier) {
      return Stream.generate(supplier).limit(numberOfElements).collect(Collectors.toSet());
    }
  }
}
