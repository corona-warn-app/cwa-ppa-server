package app.coronawarn.datadonation.services.ppac.ios.testdata;

import static app.coronawarn.datadonation.services.ppac.utils.TimeUtils.getEpochSecondForNow;

import app.coronawarn.datadonation.common.persistence.domain.DeviceToken;
import app.coronawarn.datadonation.common.protocols.internal.ppdd.PPADataIOS;
import app.coronawarn.datadonation.common.protocols.internal.ppdd.PpaDataRequestIos.PPADataRequestIOS;
import app.coronawarn.datadonation.common.protocols.internal.ppdd.PpacIos.PPACIOS;
import app.coronawarn.datadonation.services.ppac.ios.client.domain.PerDeviceDataResponse;
import app.coronawarn.datadonation.services.ppac.ios.verification.DataSubmissionResponse;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.time.OffsetDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Base64;
import java.util.UUID;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.testcontainers.shaded.com.fasterxml.jackson.core.JsonProcessingException;
import org.testcontainers.shaded.com.fasterxml.jackson.databind.ObjectMapper;

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

  public static ResponseEntity<DataSubmissionResponse> postSubmission(
      PPADataRequestIOS ppaDataRequestIOS, TestRestTemplate testRestTemplate, String url,
      Boolean skipApiTokenExpiration) {

    HttpHeaders httpHeaders = new HttpHeaders();
    httpHeaders.setContentType(MediaType.valueOf("application/x-protobuf"));
    httpHeaders.set("cwa-ppac-ios-accept-api-token", skipApiTokenExpiration.toString());
    return testRestTemplate.exchange(url, HttpMethod.POST,
        new HttpEntity<>(ppaDataRequestIOS, httpHeaders), DataSubmissionResponse.class);
  }

  public static PPADataRequestIOS buildInvalidPPADataRequestIosPayload() {
    PPACIOS authIos = PPACIOS.newBuilder().setApiToken("apiToken").setDeviceToken("deviceToken").build();
    PPADataIOS metrics = PPADataIOS.newBuilder().build();
    return PPADataRequestIOS.newBuilder().setAuthentication(authIos).setPayload(metrics).build();

  }

  public static PPADataRequestIOS buildPPADataRequestIosPayload(String apiToken, String deviceToken) {
    PPACIOS authIos = PPACIOS.newBuilder().setApiToken(apiToken).setDeviceToken(deviceToken).build();
    PPADataIOS metrics = PPADataIOS.newBuilder().build();
    return PPADataRequestIOS.newBuilder().setAuthentication(authIos).setPayload(metrics).build();
  }

  public static String buildUuid() {
    return UUID.randomUUID().toString();
  }

  public static String buildBase64String(int length) {
    String key = "thisIsAReallyLongDeviceToken";
    return Base64.getEncoder().encodeToString(key.getBytes(Charset.defaultCharset()))
        .substring(key.length() - length, key.length());
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
