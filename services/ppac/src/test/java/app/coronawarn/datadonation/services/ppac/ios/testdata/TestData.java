package app.coronawarn.datadonation.services.ppac.ios.testdata;

import static app.coronawarn.datadonation.common.protocols.internal.ppdd.PPALastSubmissionFlowScreen.SUBMISSION_FLOW_SCREEN_OTHER;
import static app.coronawarn.datadonation.common.protocols.internal.ppdd.PPARiskLevel.RISK_LEVEL_HIGH;
import static app.coronawarn.datadonation.common.protocols.internal.ppdd.PPATestResult.TEST_RESULT_POSITIVE;
import static app.coronawarn.datadonation.common.utils.TimeUtils.getEpochSecondsForNow;

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
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.time.LocalDateTime;
import java.time.OffsetDateTime;
import java.time.ZoneOffset;
import java.time.format.DateTimeFormatter;
import java.util.Arrays;
import java.util.UUID;
import org.bouncycastle.util.encoders.Base64;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.testcontainers.shaded.com.fasterxml.jackson.core.JsonProcessingException;
import org.testcontainers.shaded.com.fasterxml.jackson.databind.ObjectMapper;

public final class TestData {

  public static String buildBase64String(final int length) {
    final char[] keyChars = new char[length];
    Arrays.fill(keyChars, 'A');
    final String key = new String(keyChars);
    return Base64.toBase64String(key.getBytes(Charset.defaultCharset())).substring(key.length() - length,
        key.length() + 1) + "=";
  }

  public static DeviceToken buildDeviceToken(final String deviceToken) {
    MessageDigest digest = null;
    try {
      digest = MessageDigest.getInstance("SHA-256");
    } catch (final NoSuchAlgorithmException e) {
      e.printStackTrace();
    }
    return new DeviceToken(digest.digest(deviceToken.getBytes(StandardCharsets.UTF_8)), getEpochSecondsForNow());
  }

  public static EDUSOneTimePasswordRequestIOS buildEdusOneTimePasswordPayload(final String apiToken,
      final String deviceToken, final String otp) {
    final PPACIOS authIos = PPACIOS.newBuilder().setApiToken(apiToken).setDeviceToken(deviceToken).build();
    final EDUSOneTimePassword edusOneTimePassword = EDUSOneTimePassword.newBuilder().setOtp(otp).build();
    return EDUSOneTimePasswordRequestIOS.newBuilder().setAuthentication(authIos).setPayload(edusOneTimePassword)
        .build();
  }

  public static PPADataRequestIOS buildInvalidPPADataRequestIosPayload() {
    final PPACIOS authIos = PPACIOS.newBuilder().setApiToken("apiToken").setDeviceToken("deviceToken").build();
    final PPADataIOS metrics = PPADataIOS.newBuilder().build();
    return PPADataRequestIOS.newBuilder().setAuthentication(authIos).setPayload(metrics).build();
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

    final PPAExposureWindow ppaExposureWindow = PPAExposureWindow
        .newBuilder()
        .setCalibrationConfidence(1)
        .setInfectiousness(PPAExposureWindowInfectiousness.INFECTIOUSNESS_HIGH)
        .setDate(epochSecondForNow)
        .build();
    final ExposureRiskMetadata exposureRiskMetadataSrc = ExposureRiskMetadata.newBuilder()
        .setDateChangedComparedToPreviousSubmission(true)
        .setMostRecentDateAtRiskLevel(epochSecondForNow)
        .setRiskLevel(RISK_LEVEL_HIGH)
        .setRiskLevelChangedComparedToPreviousSubmission(true)
        .build();

    final PPANewExposureWindow ppaNewExposureWindow = PPANewExposureWindow
        .newBuilder()
        .setExposureWindow(ppaExposureWindow)
        .build();

    final PPAKeySubmissionMetadata ppaKeySubmissionMetadata = PPAKeySubmissionMetadata.newBuilder()
        .setAdvancedConsentGiven(true)
        .setDaysSinceMostRecentDateAtRiskLevelAtTestRegistration(5)
        .setHoursSinceHighRiskWarningAtTestRegistration(5)
        .setHoursSinceTestRegistration(5)
        .setLastSubmissionFlowScreen(SUBMISSION_FLOW_SCREEN_OTHER)
        .setSubmittedAfterSymptomFlow(true)
        .build();

    final PPATestResultMetadata ppaTestResultMetadata = PPATestResultMetadata.newBuilder()
        .setTestResult(TEST_RESULT_POSITIVE)
        .setRiskLevelAtTestRegistration(RISK_LEVEL_HIGH)
        .setHoursSinceTestRegistration(5)
        .setHoursSinceHighRiskWarningAtTestRegistration(5)
        .setDaysSinceMostRecentDateAtRiskLevelAtTestRegistration(5)
        .addExposureWindowsAtTestRegistration(ppaNewExposureWindow)
        .addExposureWindowsUntilTestResult(ppaNewExposureWindow)
        .build();

    return PPADataIOS.newBuilder()
        .addTestResultMetadataSet(ppaTestResultMetadata)
        .addNewExposureWindows(ppaNewExposureWindow)
        .addExposureRiskMetadataSet(exposureRiskMetadataSrc)
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
    return UUID.randomUUID().toString();
  }

  private static HttpHeaders getHttpHeaders(final Boolean skipApiTokenExpiration) {
    final HttpHeaders httpHeaders = new HttpHeaders();
    httpHeaders.setContentType(MediaType.valueOf("application/x-protobuf"));
    httpHeaders.set("cwa-ppac-ios-accept-api-token", skipApiTokenExpiration.toString());
    return httpHeaders;
  }

  public static String jsonify(final PerDeviceDataResponse data) {
    try {
      return new ObjectMapper().writeValueAsString(data);
    } catch (final JsonProcessingException e) {
      e.printStackTrace();
    }
    return null;
  }

  public static ResponseEntity<OtpCreationResponse> postOtpCreationRequest(
      final EDUSOneTimePasswordRequestIOS otpRequest,
      final TestRestTemplate testRestTemplate,
      final String url,
      final Boolean skipApiTokenExpiration) {
    final HttpHeaders httpHeaders = getHttpHeaders(skipApiTokenExpiration);
    return testRestTemplate.exchange(url, HttpMethod.POST, new HttpEntity<>(otpRequest, httpHeaders),
        OtpCreationResponse.class);
  }

  public static ResponseEntity<OtpCreationResponse> postOtpCreationRequest(
      final ELSOneTimePasswordRequestIOS otpRequest, final TestRestTemplate testRestTemplate, final String url,
      final Boolean skipApiTokenExpiration) {
    final HttpHeaders httpHeaders = getHttpHeaders(skipApiTokenExpiration);
    return testRestTemplate.exchange(url, HttpMethod.POST, new HttpEntity<>(otpRequest, httpHeaders),
        OtpCreationResponse.class);
  }

  public static ResponseEntity<OtpCreationResponse> postOtpCreationRequest(
      final SRSOneTimePasswordRequestIOS otpRequest, final TestRestTemplate testRestTemplate, final String url,
      final Boolean skipApiTokenExpiration) {
    final HttpHeaders httpHeaders = getHttpHeaders(skipApiTokenExpiration);
    httpHeaders.set("cwa-fake", "0");
    return testRestTemplate.exchange(url, HttpMethod.POST, new HttpEntity<>(otpRequest, httpHeaders),
        OtpCreationResponse.class);
  }

  public static ResponseEntity<DataSubmissionResponse> postSubmission(final PPADataRequestIOS ppaDataRequestIOS,
      final TestRestTemplate testRestTemplate, final String url, final Boolean skipApiTokenExpiration) {
    final HttpHeaders httpHeaders = getHttpHeaders(skipApiTokenExpiration);
    return testRestTemplate.exchange(url, HttpMethod.POST, new HttpEntity<>(ppaDataRequestIOS, httpHeaders),
        DataSubmissionResponse.class);
  }

  public static ResponseEntity<DataSubmissionResponse> postSurvey(
      final EDUSOneTimePasswordRequestIOS edusOneTimePasswordRequestIOS, final TestRestTemplate testRestTemplate,
      final String url, final Boolean skipApiTokenExpiration) {
    final HttpHeaders httpHeaders = getHttpHeaders(skipApiTokenExpiration);
    return testRestTemplate.exchange(url, HttpMethod.POST, new HttpEntity<>(edusOneTimePasswordRequestIOS, httpHeaders),
        DataSubmissionResponse.class);
  }
}
