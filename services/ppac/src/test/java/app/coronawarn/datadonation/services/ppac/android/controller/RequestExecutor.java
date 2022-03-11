package app.coronawarn.datadonation.services.ppac.android.controller;

import static app.coronawarn.datadonation.common.config.UrlConstants.ANDROID;
import static app.coronawarn.datadonation.common.config.UrlConstants.DATA;
import static app.coronawarn.datadonation.common.config.UrlConstants.DELETE_SALT;
import static app.coronawarn.datadonation.common.config.UrlConstants.LOG;
import static app.coronawarn.datadonation.common.config.UrlConstants.OTP;

import app.coronawarn.datadonation.common.persistence.service.OtpCreationResponse;
import app.coronawarn.datadonation.common.protocols.internal.ppdd.EDUSOneTimePasswordRequestAndroid;
import app.coronawarn.datadonation.common.protocols.internal.ppdd.ELSOneTimePasswordRequestAndroid;
import app.coronawarn.datadonation.common.protocols.internal.ppdd.PPADataRequestAndroid;
import app.coronawarn.datadonation.services.ppac.commons.web.DataSubmissionResponse;
import java.net.URI;
import java.util.HashMap;
import java.util.Map;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.RequestEntity;
import org.springframework.http.ResponseEntity;
import org.springframework.web.util.UriComponentsBuilder;

/**
 * RequestExecutor executes requests against the diagnosis key submission endpoint and holds a various methods for test
 * request generation.
 */
public class RequestExecutor {

  private static final URI DELETE_SALT_URL = URI.create(DELETE_SALT);
  private static final URI ANDROID_DATA_URL = URI.create(ANDROID + DATA);
  private static final URI ANDROID_OTP_URL = URI.create(ANDROID + OTP);
  private static final URI ANDROID_ELS_OTP_URL = URI.create(ANDROID + LOG);

  private final TestRestTemplate testRestTemplate;

  public RequestExecutor(TestRestTemplate testRestTemplate) {
    this.testRestTemplate = testRestTemplate;
  }

  public ResponseEntity<DataSubmissionResponse> execute(HttpMethod method,
      RequestEntity<PPADataRequestAndroid> requestEntity) {
    return testRestTemplate.exchange(ANDROID_DATA_URL, method, requestEntity, DataSubmissionResponse.class);
  }

  public ResponseEntity<String> executeForSalt(HttpMethod method,
      RequestEntity<String> requestEntity, String salt) {
    Map<String, String> urlParams = new HashMap<>();
    urlParams.put("salt", salt);
    UriComponentsBuilder builder = UriComponentsBuilder.fromUriString(DELETE_SALT);
    return testRestTemplate.exchange(builder.buildAndExpand(urlParams).toUri(), method, requestEntity, String.class);
  }

  public ResponseEntity<OtpCreationResponse> executeOtp(HttpMethod method,
      RequestEntity<EDUSOneTimePasswordRequestAndroid> requestEntity) {
    return testRestTemplate.exchange(ANDROID_OTP_URL, method, requestEntity, OtpCreationResponse.class);
  }

  public ResponseEntity<OtpCreationResponse> executeElsOtp(HttpMethod method,
      RequestEntity<ELSOneTimePasswordRequestAndroid> requestEntity) {
    return testRestTemplate.exchange(ANDROID_ELS_OTP_URL, method, requestEntity, OtpCreationResponse.class);
  }

  public ResponseEntity<DataSubmissionResponse> executePost(PPADataRequestAndroid body, HttpHeaders headers) {
    return execute(HttpMethod.POST,
        new RequestEntity<>(body, headers, HttpMethod.POST, ANDROID_DATA_URL));
  }

  public ResponseEntity<String> executeDelete(String salt, HttpHeaders headers) {
    return executeForSalt(HttpMethod.DELETE,
        new RequestEntity(salt, headers, HttpMethod.DELETE, DELETE_SALT_URL), salt);
  }

  public ResponseEntity<OtpCreationResponse> executeOtpPost(EDUSOneTimePasswordRequestAndroid body,
      HttpHeaders headers) {
    return executeOtp(HttpMethod.POST,
        new RequestEntity<>(body, headers, HttpMethod.POST, ANDROID_OTP_URL));
  }

  public ResponseEntity<OtpCreationResponse> executeOtpPost(ELSOneTimePasswordRequestAndroid body,
      HttpHeaders headers) {
    return executeElsOtp(HttpMethod.POST,
        new RequestEntity<>(body, headers, HttpMethod.POST, ANDROID_ELS_OTP_URL));
  }

  public ResponseEntity<DataSubmissionResponse> executePost(PPADataRequestAndroid body) {
    return executePost(body, buildDefaultHeader());
  }

  public ResponseEntity<OtpCreationResponse> executeOtpPost(EDUSOneTimePasswordRequestAndroid body) {
    return executeOtpPost(body, buildDefaultHeader());
  }

  public ResponseEntity<OtpCreationResponse> executeOtpPost(ELSOneTimePasswordRequestAndroid body) {
    return executeOtpPost(body, buildDefaultHeader());
  }

  public ResponseEntity<String> executeDelete(String saltToBeDeleted) {
    return executeDelete(saltToBeDeleted, buildDefaultHeader());
  }

  private HttpHeaders buildDefaultHeader() {
    return new HttpHeaders();
  }
}
