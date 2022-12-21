package app.coronawarn.datadonation.services.ppac.ios.testdata;

import static app.coronawarn.datadonation.common.protocols.internal.ppdd.PPALastSubmissionFlowScreen.SUBMISSION_FLOW_SCREEN_OTHER;
import static app.coronawarn.datadonation.common.protocols.internal.ppdd.PPARiskLevel.RISK_LEVEL_HIGH;
import static app.coronawarn.datadonation.common.protocols.internal.ppdd.PPATestResult.TEST_RESULT_POSITIVE;
import static app.coronawarn.datadonation.common.utils.TimeUtils.getEpochSecondsForNow;
import static java.nio.charset.Charset.defaultCharset;
import static java.nio.charset.StandardCharsets.UTF_8;
import static java.security.MessageDigest.getInstance;
import static java.util.UUID.randomUUID;
import static org.bouncycastle.util.encoders.Base64.toBase64String;
import static org.springframework.http.HttpMethod.POST;

import app.coronawarn.datadonation.common.persistence.domain.DeviceToken;
import app.coronawarn.datadonation.common.persistence.service.OtpCreationResponse;
import app.coronawarn.datadonation.common.protocols.internal.ppdd.EDUSOneTimePassword;
import app.coronawarn.datadonation.common.protocols.internal.ppdd.EDUSOneTimePasswordRequestIOS;
import app.coronawarn.datadonation.common.protocols.internal.ppdd.ELSOneTimePasswordRequestIOS;
import app.coronawarn.datadonation.common.protocols.internal.ppdd.ExposureRiskMetadata;
import app.coronawarn.datadonation.common.protocols.internal.ppdd.PPACIOS;
import app.coronawarn.datadonation.common.protocols.internal.ppdd.PPADataIOS;
import app.coronawarn.datadonation.common.protocols.internal.ppdd.PPADataRequestIOS;
import app.coronawarn.datadonation.common.protocols.internal.ppdd.PPAExposureWindow;
import app.coronawarn.datadonation.common.protocols.internal.ppdd.PPAExposureWindowInfectiousness;
import app.coronawarn.datadonation.common.protocols.internal.ppdd.PPAKeySubmissionMetadata;
import app.coronawarn.datadonation.common.protocols.internal.ppdd.PPANewExposureWindow;
import app.coronawarn.datadonation.common.protocols.internal.ppdd.PPATestResultMetadata;
import app.coronawarn.datadonation.common.protocols.internal.ppdd.SRSOneTimePasswordRequestIOS;
import app.coronawarn.datadonation.common.utils.TimeUtils;
import app.coronawarn.datadonation.services.ppac.commons.web.DataSubmissionResponse;
import app.coronawarn.datadonation.services.ppac.ios.client.domain.PerDeviceDataResponse;
import java.security.NoSuchAlgorithmException;
import java.time.LocalDateTime;
import java.time.OffsetDateTime;
import java.time.ZoneOffset;
import java.time.format.DateTimeFormatter;
import java.util.Arrays;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.testcontainers.shaded.com.fasterxml.jackson.core.JsonProcessingException;
import org.testcontainers.shaded.com.fasterxml.jackson.databind.ObjectMapper;

public final class TestData {

  public static String buildBase64String(final int length) {
    final char[] keyChars = new char[length];
    Arrays.fill(keyChars, 'A');
    final String key = new String(keyChars);
    return toBase64String(key.getBytes(defaultCharset())).substring(key.length() - length, key.length() + 1) + "=";
  }

  public static DeviceToken buildDeviceToken(final String deviceToken) throws NoSuchAlgorithmException {
    return new DeviceToken(getInstance("SHA-256").digest(deviceToken.getBytes(UTF_8)), getEpochSecondsForNow());
  }

  public static EDUSOneTimePasswordRequestIOS buildEdusOneTimePasswordPayload(final String apiToken,
      final String deviceToken, final String otp) {
    final PPACIOS authIos = PPACIOS.newBuilder().setApiToken(apiToken).setDeviceToken(deviceToken).build();
    final EDUSOneTimePassword payload = EDUSOneTimePassword.newBuilder().setOtp(otp).build();
    return EDUSOneTimePasswordRequestIOS.newBuilder().setAuthentication(authIos).setPayload(payload)        .build();
  }

  public static PPADataRequestIOS buildInvalidPPADataRequestIosPayload() {
    final PPACIOS authIos = PPACIOS.newBuilder().setApiToken("apiToken").setDeviceToken("deviceToken").build();
    final PPADataIOS payload = PPADataIOS.newBuilder().build();
    return PPADataRequestIOS.newBuilder().setAuthentication(authIos).setPayload(payload).build();
  }

  public static PerDeviceDataResponse buildIosDeviceData(final OffsetDateTime lastUpdated, final boolean valid) {
    final PerDeviceDataResponse data = new PerDeviceDataResponse();
    if (valid) {
      data.setBit0(true);
      data.setBit1(false);
    } else {
      data.setBit0(true);
      data.setBit1(true);
    }
    final LocalDateTime utcBasedTime = lastUpdated.atZoneSameInstant(ZoneOffset.UTC).toLocalDateTime();
    data.setLastUpdated(utcBasedTime.format(DateTimeFormatter.ofPattern("yyyy-MM")));
    return data;
  }

  private static PPADataIOS buildIosPayload() {
    final Long epochSecondForNow = TimeUtils.getEpochSecondsForNow();

    final PPAExposureWindow ppaExposureWindow = PPAExposureWindow.newBuilder().setCalibrationConfidence(1)
        .setInfectiousness(PPAExposureWindowInfectiousness.INFECTIOUSNESS_HIGH).setDate(epochSecondForNow).build();
    final ExposureRiskMetadata exposureRiskMetadataSrc = ExposureRiskMetadata.newBuilder()
        .setDateChangedComparedToPreviousSubmission(true).setMostRecentDateAtRiskLevel(epochSecondForNow)
        .setRiskLevel(RISK_LEVEL_HIGH).setRiskLevelChangedComparedToPreviousSubmission(true).build();

    final PPANewExposureWindow ppaNewExposureWindow = PPANewExposureWindow.newBuilder()
        .setExposureWindow(ppaExposureWindow).build();

    final PPAKeySubmissionMetadata ppaKeySubmissionMetadata = PPAKeySubmissionMetadata.newBuilder()
        .setAdvancedConsentGiven(true).setDaysSinceMostRecentDateAtRiskLevelAtTestRegistration(5)
        .setHoursSinceHighRiskWarningAtTestRegistration(5).setHoursSinceTestRegistration(5)
        .setLastSubmissionFlowScreen(SUBMISSION_FLOW_SCREEN_OTHER).setSubmittedAfterSymptomFlow(true).build();

    final PPATestResultMetadata ppaTestResultMetadata = PPATestResultMetadata.newBuilder()
        .setTestResult(TEST_RESULT_POSITIVE).setRiskLevelAtTestRegistration(RISK_LEVEL_HIGH)
        .setHoursSinceTestRegistration(5).setHoursSinceHighRiskWarningAtTestRegistration(5)
        .setDaysSinceMostRecentDateAtRiskLevelAtTestRegistration(5)
        .addExposureWindowsAtTestRegistration(ppaNewExposureWindow)
        .addExposureWindowsUntilTestResult(ppaNewExposureWindow).build();

    return PPADataIOS.newBuilder().addTestResultMetadataSet(ppaTestResultMetadata)
        .addNewExposureWindows(ppaNewExposureWindow).addExposureRiskMetadataSet(exposureRiskMetadataSrc)
        .addKeySubmissionMetadataSet(ppaKeySubmissionMetadata).build();
  }

  public static PPADataRequestIOS buildPPADataRequestIosPayload(final String apiToken, final String deviceToken,
      final boolean withPayload) {
    final PPACIOS authIos = PPACIOS.newBuilder().setApiToken(apiToken).setDeviceToken(deviceToken).build();

    if (withPayload) {
      return PPADataRequestIOS.newBuilder().setAuthentication(authIos).setPayload(buildIosPayload()).build();
    }
    return PPADataRequestIOS.newBuilder().setAuthentication(authIos).setPayload(PPADataIOS.newBuilder().build())
        .build();
  }

  public static String buildUuid() {
    return randomUUID().toString();
  }

  private static HttpHeaders getHttpHeaders(final Boolean skipApiTokenExpiration) {
    final HttpHeaders httpHeaders = new HttpHeaders();
    httpHeaders.setContentType(MediaType.valueOf("application/x-protobuf"));
    httpHeaders.set("cwa-ppac-ios-accept-api-token", skipApiTokenExpiration.toString());
    return httpHeaders;
  }

  public static String jsonify(final PerDeviceDataResponse data) throws JsonProcessingException {
      return new ObjectMapper().writeValueAsString(data);
  }

  public static ResponseEntity<OtpCreationResponse> postOtpCreationRequest(final EDUSOneTimePasswordRequestIOS payload,
      final TestRestTemplate rest, final String url, final Boolean skipApiTokenExpiration) {
    final HttpHeaders httpHeaders = getHttpHeaders(skipApiTokenExpiration);
    return rest.exchange(url, POST, new HttpEntity<>(payload, httpHeaders), OtpCreationResponse.class);
  }

  public static ResponseEntity<OtpCreationResponse> postOtpCreationRequest(final ELSOneTimePasswordRequestIOS payload,
      final TestRestTemplate rest, final String url, final Boolean skipApiTokenExpiration) {
    final HttpHeaders httpHeaders = getHttpHeaders(skipApiTokenExpiration);
    return rest.exchange(url, POST, new HttpEntity<>(payload, httpHeaders), OtpCreationResponse.class);
  }

  public static ResponseEntity<OtpCreationResponse> postOtpCreationRequest(final SRSOneTimePasswordRequestIOS payload,
      final TestRestTemplate rest, final String url, final Boolean skipApiTokenExpiration) {
    final HttpHeaders httpHeaders = getHttpHeaders(skipApiTokenExpiration);
    httpHeaders.set("cwa-fake", "0");
    return rest.exchange(url, POST, new HttpEntity<>(payload, httpHeaders), OtpCreationResponse.class);
  }

  public static ResponseEntity<OtpCreationResponse> postSubmission(final PPADataRequestIOS payload,
      final TestRestTemplate rest, final String url, final Boolean skipApiTokenExpiration) {
    return rest.exchange(url, POST,
        new HttpEntity<>(payload, getHttpHeaders(skipApiTokenExpiration)), OtpCreationResponse.class);
  }

  public static ResponseEntity<OtpCreationResponse> postSurvey(final EDUSOneTimePasswordRequestIOS payload,
      final TestRestTemplate rest, final String url, final Boolean skipApiTokenExpiration) {
    return rest.exchange(url, POST, new HttpEntity<>(payload, getHttpHeaders(skipApiTokenExpiration)),
        OtpCreationResponse.class);
  }

  public static ResponseEntity<DataSubmissionResponse> postErrSubmission(final PPADataRequestIOS payload,
      final TestRestTemplate rest, final String url, final Boolean skipApiTokenExpiration) {
    return rest.exchange(url, POST, new HttpEntity<>(payload, getHttpHeaders(skipApiTokenExpiration)),
        DataSubmissionResponse.class);
  }

  public static ResponseEntity<DataSubmissionResponse> postErrSurvey(final EDUSOneTimePasswordRequestIOS payload,
      final TestRestTemplate rest, final String url, final Boolean skipApiTokenExpiration) {
    return rest.exchange(url, POST, new HttpEntity<>(payload, getHttpHeaders(skipApiTokenExpiration)),
        DataSubmissionResponse.class);
  }
}
