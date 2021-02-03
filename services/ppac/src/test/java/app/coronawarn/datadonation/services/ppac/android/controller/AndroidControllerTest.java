package app.coronawarn.datadonation.services.ppac.android.controller;

import static app.coronawarn.datadonation.services.ppac.android.testdata.TestData.getJwsPayloadValues;
import static app.coronawarn.datadonation.services.ppac.android.testdata.TestData.newAuthenticationObject;
import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.http.HttpStatus.OK;
import static org.springframework.http.HttpStatus.UNAUTHORIZED;

import app.coronawarn.datadonation.common.persistence.domain.android.Salt;
import app.coronawarn.datadonation.common.protocols.internal.ppdd.PPADataAndroid;
import app.coronawarn.datadonation.common.protocols.internal.ppdd.PpaDataRequestAndroid.PPADataRequestAndroid;
import app.coronawarn.datadonation.services.ppac.android.attestation.SignatureVerificationStrategy;
import app.coronawarn.datadonation.services.ppac.android.testdata.JwsGenerationUtil;
import java.io.IOException;
import java.security.GeneralSecurityException;
import java.time.Instant;
import java.time.temporal.ChronoUnit;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import org.junit.jupiter.api.TestInstance.Lifecycle;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.ResponseEntity;
import org.springframework.test.context.junit.jupiter.SpringExtension;

@ExtendWith(SpringExtension.class)
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@TestInstance(Lifecycle.PER_CLASS)
class AndroidControllerTest {

  private static final Salt NOT_EXPIRED_SALT =
      new Salt("def", Instant.now().minus(1, ChronoUnit.HOURS).toEpochMilli());

  @MockBean
  private SignatureVerificationStrategy signatureVerificationStrategy;

  @Autowired
  private RequestExecutor executor;

  @BeforeEach
  void setup() throws GeneralSecurityException {
    when(signatureVerificationStrategy.verifySignature(any())).thenReturn(JwsGenerationUtil.getTestCertificate());
  }

  @Test
  void checkResponseStatusForValidParameters() throws IOException {
    ResponseEntity<Void> actResponse = executor.executePost(buildPayload());
    assertThat(actResponse.getStatusCode()).isEqualTo(OK);
  }

  @Test
  @Disabled
  void checkResponseStatusForInvalidPayload() throws IOException {
    ResponseEntity<Void> actResponse = executor.executePost(buildPayload());
    assertThat(actResponse.getStatusCode()).isEqualTo(UNAUTHORIZED);
  }

  @Test
  @Disabled
  void checkResponseStatusForInvalidAuthentication() throws IOException {
    ResponseEntity<Void> actResponse = executor.executePost(buildPayload());
    assertThat(actResponse.getStatusCode()).isEqualTo(OK);
  }

  private PPADataRequestAndroid buildPayload() throws IOException {
    String jws = getJwsPayloadValues();
    return PPADataRequestAndroid.newBuilder()
        .setAuthentication(newAuthenticationObject(jws, NOT_EXPIRED_SALT.getSalt()))
        .setPayload(PPADataAndroid.newBuilder().build())
        .build();
  }

}
