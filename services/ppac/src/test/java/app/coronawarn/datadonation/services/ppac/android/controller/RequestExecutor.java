package app.coronawarn.datadonation.services.ppac.android.controller;

import static app.coronawarn.datadonation.common.config.UrlConstants.ANDROID;
import static app.coronawarn.datadonation.common.config.UrlConstants.DATA;
import static app.coronawarn.datadonation.common.config.UrlConstants.DELETE_SALT;
import static app.coronawarn.datadonation.common.config.UrlConstants.LOG;
import static app.coronawarn.datadonation.common.config.UrlConstants.OTP;
import static app.coronawarn.datadonation.common.config.UrlConstants.SRS;
import static org.springframework.http.MediaType.valueOf;

import app.coronawarn.datadonation.common.persistence.service.OtpCreationResponse;
import app.coronawarn.datadonation.common.protocols.internal.ppdd.EDUSOneTimePasswordRequestAndroid;
import app.coronawarn.datadonation.common.protocols.internal.ppdd.ELSOneTimePasswordRequestAndroid;
import app.coronawarn.datadonation.common.protocols.internal.ppdd.PPADataRequestAndroid;
import app.coronawarn.datadonation.common.protocols.internal.ppdd.SRSOneTimePasswordRequestAndroid;
import app.coronawarn.datadonation.services.ppac.commons.web.DataSubmissionResponse;
import java.net.URI;
import java.util.HashMap;
import java.util.Map;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.RequestEntity;
import org.springframework.http.ResponseEntity;
import org.springframework.web.util.UriComponents;
import org.springframework.web.util.UriComponentsBuilder;

/**
 * RequestExecutor executes requests against the diagnosis key submission endpoint and holds a various methods for test
 * request generation.
 */
public class RequestExecutor {

  private static final URI ANDROID_DATA_URL = URI.create(ANDROID + DATA);
  private static final URI ANDROID_ELS_OTP_URL = URI.create(ANDROID + LOG);
  private static final URI ANDROID_OTP_URL = URI.create(ANDROID + OTP);
  private static final URI ANDROID_SRS_OTP_URL = URI.create(ANDROID + SRS);

  private final TestRestTemplate testRestTemplate;

  public RequestExecutor(final TestRestTemplate testRestTemplate) {
    this.testRestTemplate = testRestTemplate;
  }

  public static HttpHeaders buildDefaultHeader() {
    return new HttpHeaders();
  }

  ResponseEntity<DataSubmissionResponse> execute(final HttpMethod method,
      final RequestEntity<PPADataRequestAndroid> requestEntity) {
    return testRestTemplate.exchange(ANDROID_DATA_URL, method, requestEntity, DataSubmissionResponse.class);
  }

  public ResponseEntity<String> executeDelete(final String saltToBeDeleted) {
    return executeDelete(saltToBeDeleted, buildDefaultHeader());
  }

  ResponseEntity<String> executeDelete(final String salt, final HttpHeaders headers) {
    return executeForSalt(HttpMethod.DELETE, new HttpEntity<>(headers), salt);
  }

  ResponseEntity<OtpCreationResponse> executeElsOtp(final HttpMethod method,
      final RequestEntity<ELSOneTimePasswordRequestAndroid> requestEntity) {
    return testRestTemplate.exchange(ANDROID_ELS_OTP_URL, method, requestEntity, OtpCreationResponse.class);
  }

  ResponseEntity<String> executeForSalt(final HttpMethod method, final HttpEntity<Object> requestEntity,
      final String salt) {
    final Map<String, String> urlParams = new HashMap<>();
    urlParams.put("salt", salt);
    final UriComponents uri = UriComponentsBuilder.fromUriString(DELETE_SALT).buildAndExpand(urlParams);

    return testRestTemplate.exchange(uri.encode().toUri(), method, requestEntity, String.class);
  }

  ResponseEntity<OtpCreationResponse> executeOtp(final HttpMethod method,
      final RequestEntity<EDUSOneTimePasswordRequestAndroid> requestEntity) {
    return testRestTemplate.exchange(ANDROID_OTP_URL, method, requestEntity, OtpCreationResponse.class);
  }

  public ResponseEntity<OtpCreationResponse> executeOtpPost(final EDUSOneTimePasswordRequestAndroid body) {
    return executeOtpPost(body, buildDefaultHeader());
  }

  ResponseEntity<OtpCreationResponse> executeOtpPost(final EDUSOneTimePasswordRequestAndroid body,
      final HttpHeaders headers) {
    return executeOtp(HttpMethod.POST, new RequestEntity<>(body, headers, HttpMethod.POST, ANDROID_OTP_URL));
  }

  public ResponseEntity<OtpCreationResponse> executeOtpPost(final ELSOneTimePasswordRequestAndroid body) {
    return executeOtpPost(body, buildDefaultHeader());
  }

  ResponseEntity<OtpCreationResponse> executeOtpPost(final ELSOneTimePasswordRequestAndroid body,
      final HttpHeaders headers) {
    return executeElsOtp(HttpMethod.POST, new RequestEntity<>(body, headers, HttpMethod.POST, ANDROID_ELS_OTP_URL));
  }

  public ResponseEntity<OtpCreationResponse> executeOtpPost(final SRSOneTimePasswordRequestAndroid body) {
    return executeOtpPost(body, buildDefaultHeader());
  }

  public ResponseEntity<OtpCreationResponse> executeOtpPost(final SRSOneTimePasswordRequestAndroid body,
      final boolean acceptId) {
    var header = buildDefaultHeader();
    header.set("cwa-ppac-android-accept-android-id", acceptId ? "1" : "0");
    return executeOtpPost(body, header);
  }

  ResponseEntity<OtpCreationResponse> executeOtpPost(final SRSOneTimePasswordRequestAndroid body,
      final HttpHeaders headers) {
    headers.setContentType(valueOf("application/x-protobuf"));
    headers.set("cwa-fake", "0");
    return executeSrsOtp(HttpMethod.POST, new RequestEntity<>(body, headers, HttpMethod.POST, ANDROID_SRS_OTP_URL));
  }

  public ResponseEntity<DataSubmissionResponse> executePost(final PPADataRequestAndroid body) {
    return executePost(body, buildDefaultHeader());
  }

  ResponseEntity<DataSubmissionResponse> executePost(final PPADataRequestAndroid body, final HttpHeaders headers) {
    return execute(HttpMethod.POST, new RequestEntity<>(body, headers, HttpMethod.POST, ANDROID_DATA_URL));
  }

  ResponseEntity<OtpCreationResponse> executeSrsOtp(final HttpMethod method,
      final RequestEntity<SRSOneTimePasswordRequestAndroid> requestEntity) {
    return testRestTemplate.exchange(ANDROID_SRS_OTP_URL, method, requestEntity, OtpCreationResponse.class);
  }
}
