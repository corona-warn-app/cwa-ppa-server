package app.coronawarn.datadonation.services.ppac.ios.testdata;

import app.coronawarn.datadonation.common.persistence.domain.DeviceToken;
import app.coronawarn.datadonation.common.persistence.service.OtpCreationResponse;
import app.coronawarn.datadonation.common.protocols.internal.ppdd.EdusOtp.EDUSOneTimePassword;
import app.coronawarn.datadonation.common.protocols.internal.ppdd.EdusOtpRequestIos.EDUSOneTimePasswordRequestIOS;
import app.coronawarn.datadonation.common.protocols.internal.ppdd.*;
import app.coronawarn.datadonation.common.protocols.internal.ppdd.PpaDataRequestIos.PPADataRequestIOS;
import app.coronawarn.datadonation.common.protocols.internal.ppdd.PpacIos.PPACIOS;
import app.coronawarn.datadonation.common.utils.TimeUtils;
import app.coronawarn.datadonation.services.ppac.commons.web.DataSubmissionResponse;
import app.coronawarn.datadonation.services.ppac.ios.client.domain.PerDeviceDataResponse;
import org.bouncycastle.util.encoders.Base64;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.http.*;
import org.testcontainers.shaded.com.fasterxml.jackson.core.JsonProcessingException;
import org.testcontainers.shaded.com.fasterxml.jackson.databind.ObjectMapper;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.time.LocalDate;
import java.time.OffsetDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Arrays;
import java.util.UUID;

import static app.coronawarn.datadonation.common.protocols.internal.ppdd.PPALastSubmissionFlowScreen.SUBMISSION_FLOW_SCREEN_OTHER;
import static app.coronawarn.datadonation.common.protocols.internal.ppdd.PPARiskLevel.RISK_LEVEL_HIGH;
import static app.coronawarn.datadonation.common.protocols.internal.ppdd.PPATestResult.TEST_RESULT_POSITIVE;
import static app.coronawarn.datadonation.common.utils.TimeUtils.getEpochSecondForNow;

public final class TestData {

  public static PerDeviceDataResponse buildIosDeviceData(OffsetDateTime lastUpdated, boolean valid) {
    PerDeviceDataResponse data = new PerDeviceDataResponse();
    if (valid) {
      data.setBit0(true);
      data.setBit1(false);
    } else {
      data.setBit0(true);
      data.setBit1(true);
    }
    data.setLastUpdated(lastUpdated.format(DateTimeFormatter.ofPattern("yyyy-MM")));
    return data;
  }

  public static ResponseEntity<DataSubmissionResponse> postSurvey(
      EDUSOneTimePasswordRequestIOS edusOneTimePasswordRequestIOS, TestRestTemplate testRestTemplate, String url,
      Boolean skipApiTokenExpiration) {
    HttpHeaders httpHeaders = new HttpHeaders();
    httpHeaders.setContentType(MediaType.valueOf("application/x-protobuf"));
    httpHeaders.set("cwa-ppac-ios-accept-api-token", skipApiTokenExpiration.toString());
    return testRestTemplate.exchange(url, HttpMethod.POST,
        new HttpEntity<>(edusOneTimePasswordRequestIOS, httpHeaders), DataSubmissionResponse.class);
  }

  public static ResponseEntity<DataSubmissionResponse> postSubmission(
      PPADataRequestIOS ppaDataRequestIOS, TestRestTemplate testRestTemplate, String url,
      Boolean skipApiTokenExpiration) {

    HttpHeaders httpHeaders = new HttpHeaders();
    httpHeaders.setContentType(MediaType.valueOf("application/x-protobuf"));
    httpHeaders.set("cwa-ppac-ios-accept-api-token", skipApiTokenExpiration.toString());
    return testRestTemplate.exchange(url, HttpMethod.POST,
        new HttpEntity<>(ppaDataRequestIOS, httpHeaders), DataSubmissionResponse.class);
  }

  public static ResponseEntity<OtpCreationResponse> postOtpCreationRequest(
      EDUSOneTimePasswordRequestIOS otpRequest,
      TestRestTemplate testRestTemplate,
      String url,
      Boolean skipApiTokenExpiration) {
    HttpHeaders httpHeaders = new HttpHeaders();
    httpHeaders.setContentType(MediaType.valueOf("application/x-protobuf"));
    httpHeaders.set("cwa-ppac-ios-accept-api-token", skipApiTokenExpiration.toString());
    return testRestTemplate
        .exchange(url, HttpMethod.POST, new HttpEntity<>(otpRequest, httpHeaders), OtpCreationResponse.class);
  }

  public static PPADataRequestIOS buildInvalidPPADataRequestIosPayload() {
    PPACIOS authIos = PPACIOS.newBuilder().setApiToken("apiToken").setDeviceToken("deviceToken").build();
    PPADataIOS metrics = PPADataIOS.newBuilder().build();
    return PPADataRequestIOS.newBuilder().setAuthentication(authIos).setPayload(metrics).build();

  }

  public static PPADataRequestIOS buildPPADataRequestIosPayload(String apiToken, String deviceToken,
      boolean withPayload) {
    PPACIOS authIos = PPACIOS.newBuilder().setApiToken(apiToken).setDeviceToken(deviceToken).build();

    if (withPayload) {
      return PPADataRequestIOS.newBuilder().setAuthentication(authIos).setPayload(buildIosPayload()).build();
    }
    return PPADataRequestIOS.newBuilder().setAuthentication(authIos).setPayload(PPADataIOS.newBuilder().build())
        .build();
  }

  public static EDUSOneTimePasswordRequestIOS buildEdusOneTimePasswordPayload(String apiToken, String deviceToken,
      String otp) {
    PPACIOS authIos = PPACIOS.newBuilder().setApiToken(apiToken).setDeviceToken(deviceToken).build();
    final EDUSOneTimePassword edusOneTimePassword = EDUSOneTimePassword.newBuilder().setOtp(otp).build();
    return EDUSOneTimePasswordRequestIOS.newBuilder().setAuthentication(authIos).setPayload(
        edusOneTimePassword)
        .build();
  }

  private static PPADataIOS buildIosPayload() {
    final Long epochSecondForNow = TimeUtils.getEpochSecondForNow();
    LocalDate now = TimeUtils.getLocalDateFor(epochSecondForNow);

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

    final PPAKeySubmissionMetadata ppaKeySubmissionMetadata =
        PPAKeySubmissionMetadata.newBuilder()
            .setAdvancedConsentGiven(true)
            .setDaysSinceMostRecentDateAtRiskLevelAtTestRegistration(5)
            .setHoursSinceHighRiskWarningAtTestRegistration(5)
            .setHoursSinceTestRegistration(5)
            .setLastSubmissionFlowScreen(SUBMISSION_FLOW_SCREEN_OTHER)
            .setSubmittedAfterSymptomFlow(true)
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
        .addTestResultMetadataSet(ppaTestResultMetadata)
        .addNewExposureWindows(ppaNewExposureWindow)
        .addExposureRiskMetadataSet(exposureRiskMetadataSrc)
        .addKeySubmissionMetadataSet(ppaKeySubmissionMetadata).build();
    return payload;
  }

  public static String buildUuid() {
    return UUID.randomUUID().toString();
  }

  public static String buildBase64String(int length) {
    char[] keyChars = new char[length];
    Arrays.fill(keyChars, 'A');
    String key = new String(keyChars);
    return Base64.toBase64String(key.getBytes(Charset.defaultCharset()))
        .substring(key.length() - length, key.length() + 1) + "=";
  }

  public static String jsonify(PerDeviceDataResponse data) {
    ObjectMapper objectMapper = new ObjectMapper();
    String result = null;
    try {
      result = objectMapper.writeValueAsString(data);
    } catch (JsonProcessingException e) {
      e.printStackTrace();
    }
    return result;
  }

  public static DeviceToken buildDeviceToken(String deviceToken) {
    MessageDigest digest = null;
    try {
      digest = MessageDigest.getInstance("SHA-256");
    } catch (NoSuchAlgorithmException e) {
      e.printStackTrace();
    }
    return new DeviceToken(digest.digest(deviceToken.getBytes(StandardCharsets.UTF_8)),
        getEpochSecondForNow());
  }
}
