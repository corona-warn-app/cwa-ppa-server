

package app.coronawarn.datadonation.services.ppac.android.controller;

import app.coronawarn.datadonation.common.protocols.internal.ppdd.PpaDataRequestAndroid.PPADataRequestAndroid;
import java.net.URI;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.RequestEntity;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;

import static app.coronawarn.datadonation.common.config.UrlConstants.ANDROID;
import static app.coronawarn.datadonation.common.config.UrlConstants.DATA;

/**
 * RequestExecutor executes requests against the diagnosis key submission endpoint and holds a various methods for test
 * request generation.
 */
public class RequestExecutor {

  private static final URI ANDROID_URL = URI.create(ANDROID + DATA);

  private final TestRestTemplate testRestTemplate;

  public RequestExecutor(TestRestTemplate testRestTemplate) {
    this.testRestTemplate = testRestTemplate;
  }

  public ResponseEntity<Void> execute(HttpMethod method, RequestEntity<PPADataRequestAndroid> requestEntity) {
    return testRestTemplate.exchange(ANDROID_URL, method, requestEntity, Void.class);
  }

  public ResponseEntity<Void> executePost( PPADataRequestAndroid body, HttpHeaders headers) {
    return execute(HttpMethod.POST, new RequestEntity<>(body, headers, HttpMethod.POST, ANDROID_URL));
  }

  public ResponseEntity<Void> executePost(PPADataRequestAndroid body) {
    return executePost(body, buildDefaultHeader());
  }

  private HttpHeaders buildDefaultHeader() {
    return new HttpHeaders();
  }
}
