package app.coronawarn.datadonation.services.ppac.android.testdata;

import app.coronawarn.datadonation.common.persistence.domain.metrics.ExposureWindow;
import app.coronawarn.datadonation.common.persistence.domain.metrics.KeySubmissionMetadataWithClientMetadata;
import app.coronawarn.datadonation.common.persistence.domain.metrics.KeySubmissionMetadataWithUserMetadata;
import app.coronawarn.datadonation.common.persistence.domain.metrics.TestResultMetadata;
import app.coronawarn.datadonation.common.persistence.repository.ppac.android.SaltRepository;
import app.coronawarn.datadonation.common.persistence.service.PpaDataStorageRequest;
import app.coronawarn.datadonation.common.protocols.internal.ppdd.ExposureRiskMetadata;
import app.coronawarn.datadonation.common.protocols.internal.ppdd.PPAAgeGroup;
import app.coronawarn.datadonation.common.protocols.internal.ppdd.PPAClientMetadataAndroid;
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
import app.coronawarn.datadonation.common.protocols.internal.ppdd.PpacAndroid.PPACAndroid;
import app.coronawarn.datadonation.common.protocols.internal.ppdd.PpacAndroid.PPACAndroid.Builder;
import app.coronawarn.datadonation.services.ppac.android.attestation.DeviceAttestationVerifier;
import app.coronawarn.datadonation.services.ppac.android.attestation.TestSignatureVerificationStrategy;
import app.coronawarn.datadonation.services.ppac.config.PpacConfiguration;
import org.apache.http.conn.ssl.DefaultHostnameVerifier;
import java.io.IOException;
import java.io.InputStream;
import java.io.Serializable;
import java.time.Instant;
import java.time.LocalDate;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Properties;
import java.util.Set;

public class TestData {

  private static final int ATTESTATION_VALIDITY_SECONDS = 7200;

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

  public static DeviceAttestationVerifier newVerifierInstance(SaltRepository saltRepo) {
    return newVerifierInstance(saltRepo, "localhost");
  }

  public static DeviceAttestationVerifier newVerifierInstance(SaltRepository saltRepo, String hostname) {
    PpacConfiguration appParameters = new PpacConfiguration();
    PpacConfiguration.Android androidParameters = new PpacConfiguration.Android();
    androidParameters.setCertificateHostname(hostname);
    androidParameters.setAttestationValidity(ATTESTATION_VALIDITY_SECONDS);
    androidParameters.setAllowedApkPackageNames(new String[] {"de.rki.coronawarnapp.test"});
    androidParameters.setAllowedApkCertificateDigests(
        new String[] {"9VLvUGV0Gkx24etruEBYikvAtqSQ9iY6rYuKhG+xwKE="});
    androidParameters.setRequireBasicIntegrity(false);
    androidParameters.setRequireCtsProfileMatch(false);
    androidParameters.setRequireEvaluationTypeBasic(false);
    androidParameters.setRequireEvaluationTypeHardwareBacked(false);
    appParameters.setAndroid(androidParameters);
    return new DeviceAttestationVerifier(new DefaultHostnameVerifier(), appParameters, saltRepo,
        new TestSignatureVerificationStrategy(JwsGenerationUtil.getTestCertificate()));
  }

  private static Map<String, Serializable> getJwsPayloadDefaultValue() throws IOException {
    return Map.of(
        "nonce", "AAAAAAAAAAAAAAAAAAAAAA==",
        "timestampMs", String.valueOf(Instant.now().minusSeconds(500).toEpochMilli()),
        "apkPackageName", "de.rki.coronawarnapp.test", "apkDigestSha256",
        "9oiqOMQAZfBgCnI0jyN7TgPAQNSSxWrjh14f0eXpB3U=", "ctsProfileMatch", "false",
        "apkCertificateDigestSha256", new String[]{"9VLvUGV0Gkx24etruEBYikvAtqSQ9iY6rYuKhG+xwKE="},
        "basicIntegrity", "false", "advice", "RESTORE_TO_FACTORY_ROM,LOCK_BOOTLOADER",
        "evaluationType", "BASIC");
  }

  public static ExposureRiskMetadata getValidExposureRiskMetadata() {
    return ExposureRiskMetadata.newBuilder()
        .setRiskLevel(PPARiskLevel.RISK_LEVEL_HIGH)
        .setMostRecentDateAtRiskLevel(LocalDate.now().toEpochDay())
        .setRiskLevelChangedComparedToPreviousSubmission(true)
        .build();
  }

  public static PPANewExposureWindow getInvalidExposureWindow() {
    return PPANewExposureWindow.newBuilder()
        .setExposureWindow(PPAExposureWindow.newBuilder()
            .setCalibrationConfidence(2)
            .setDate(LocalDate.now().toEpochDay()))
            .build();
  }
  
  public static PPANewExposureWindow getValidExposureWindow() {
    return PPANewExposureWindow.newBuilder()
        .setExposureWindow(PPAExposureWindow.newBuilder()
            .setCalibrationConfidence(2)
            .setDate(LocalDate.now().toEpochDay())
            .setInfectiousness(PPAExposureWindowInfectiousness.INFECTIOUSNESS_HIGH)
            .setReportType(PPAExposureWindowReportType.REPORT_TYPE_CONFIRMED_TEST)
            .addAllScanInstances(Set.of(PPAExposureWindowScanInstance.newBuilder().setMinAttenuation(1)
                .setSecondsSinceLastScan(3).setTypicalAttenuation(4).build()))).build();
  }

  public static PPATestResultMetadata getValidTestResultMetadata() {
    return PPATestResultMetadata.newBuilder()
        .setDaysSinceMostRecentDateAtRiskLevelAtTestRegistration(2)
        .setHoursSinceHighRiskWarningAtTestRegistration(2).setHoursSinceTestRegistration(3)
        .setRiskLevelAtTestRegistration(PPARiskLevel.RISK_LEVEL_HIGH)
        .setTestResult(PPATestResult.TEST_RESULT_NEGATIVE).build();
  }

  public static PPAKeySubmissionMetadata getValidKeySubmissionMetadata() {
    return PPAKeySubmissionMetadata.newBuilder()
        .setAdvancedConsentGiven(true)
        .setDaysSinceMostRecentDateAtRiskLevelAtTestRegistration(4)
        .setHoursSinceHighRiskWarningAtTestRegistration(3)
        .setHoursSinceTestRegistration(2)
        .setLastSubmissionFlowScreen(
            PPALastSubmissionFlowScreen.SUBMISSION_FLOW_SCREEN_SYMPTOM_ONSET)
        .setSubmitted(true).setSubmittedAfterCancel(false).setSubmittedAfterSymptomFlow(false)
        .setSubmittedInBackground(true)
        .setSubmittedWithTeleTAN(false)
        .build();
  }

  public static PPAClientMetadataAndroid getValidClientMetadata() {
    return PPAClientMetadataAndroid.newBuilder().setAndroidApiLevel(3).setAppConfigETag("etag")
        .setCwaVersion(PPASemanticVersion.newBuilder().setMajor(1).setMinor(2).setPatch(4).build())
        .setEnfVersion(4).build();
  }

  public static PPAUserMetadata getValidUserMetadata() {
    return PPAUserMetadata.newBuilder()
        .setAdministrativeUnit(3)
        .setAgeGroup(PPAAgeGroup.AGE_GROUP_30_TO_59)
        .setFederalState(PPAFederalState.FEDERAL_STATE_BE)
        .build();
  }

  public static PpaDataStorageRequest getStorageRequestWithInvalidExposureWindow() {
    return new PpaDataStorageRequest(MetricsMockData.getExposureRiskMetadata(),
        List.of(new ExposureWindow(null, null, null, null, null, null, null, null, null)),
        MetricsMockData.getTestResultMetric(), MetricsMockData.getKeySubmissionWithClientMetadata(),
        MetricsMockData.getKeySubmissionWithUserMetadata(), 
        MetricsMockData.getUserMetadata(), MetricsMockData.getClientMetadata());
  }

  public static PpaDataStorageRequest getStorageRequestWithInvalidExposureRisk() {
    return new PpaDataStorageRequest(
        new app.coronawarn.datadonation.common.persistence.domain.metrics.ExposureRiskMetadata(null,
            null, null, null, null, null, null),
        MetricsMockData.getExposureWindow(), MetricsMockData.getTestResultMetric(),
        MetricsMockData.getKeySubmissionWithClientMetadata(),
        MetricsMockData.getKeySubmissionWithUserMetadata(),
        MetricsMockData.getUserMetadata(), MetricsMockData.getClientMetadata());
  }
  
  public static PpaDataStorageRequest getStorageRequestWithInvalidUserMetadata() {
    return new PpaDataStorageRequest(MetricsMockData.getExposureRiskMetadata(),
        MetricsMockData.getExposureWindow(), MetricsMockData.getTestResultMetric(),
        MetricsMockData.getKeySubmissionWithClientMetadata(),
        new KeySubmissionMetadataWithUserMetadata(null, null, null, null, null, null, null, null,
            null, null),
        MetricsMockData.getUserMetadata(), MetricsMockData.getClientMetadata());
  }

  public static PpaDataStorageRequest getStorageRequestWithInvalidClientMetadata() {
    return new PpaDataStorageRequest(
        MetricsMockData.getExposureRiskMetadata(), MetricsMockData.getExposureWindow(),
        MetricsMockData.getTestResultMetric(), new KeySubmissionMetadataWithClientMetadata(null,
            null, null, null, null, null, null, null, null),
        MetricsMockData.getKeySubmissionWithUserMetadata(),
        MetricsMockData.getUserMetadata(), MetricsMockData.getClientMetadata());
  }

  public static PpaDataStorageRequest getStorageRequestWithInvalidTestResults() {
    return new PpaDataStorageRequest(MetricsMockData.getExposureRiskMetadata(),
        MetricsMockData.getExposureWindow(),
        new TestResultMetadata(null, null, null, null, null, null, null, null),
        MetricsMockData.getKeySubmissionWithClientMetadata(),
        MetricsMockData.getKeySubmissionWithUserMetadata(),
        MetricsMockData.getUserMetadata(), MetricsMockData.getClientMetadata());
  }
}
