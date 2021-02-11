package app.coronawarn.datadonation.services.ppac.android.controller;

import static app.coronawarn.datadonation.services.ppac.android.testdata.TestData.getJwsPayloadValues;
import static app.coronawarn.datadonation.services.ppac.android.testdata.TestData.getJwsPayloadWithNonce;
import static app.coronawarn.datadonation.services.ppac.android.testdata.TestData.newAuthenticationObject;
import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.only;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.http.HttpStatus.BAD_REQUEST;
import static org.springframework.http.HttpStatus.FORBIDDEN;
import static org.springframework.http.HttpStatus.NO_CONTENT;
import static org.springframework.http.HttpStatus.OK;
import static org.springframework.http.HttpStatus.UNAUTHORIZED;

import app.coronawarn.datadonation.common.persistence.domain.OneTimePassword;
import app.coronawarn.datadonation.common.persistence.domain.ppac.android.Salt;
import app.coronawarn.datadonation.common.persistence.repository.ppac.android.SaltRepository;
import app.coronawarn.datadonation.common.persistence.service.OtpCreationResponse;
import app.coronawarn.datadonation.common.persistence.service.OtpService;
import app.coronawarn.datadonation.common.protocols.internal.ppdd.EdusOtp.EDUSOneTimePassword;
import app.coronawarn.datadonation.common.protocols.internal.ppdd.EdusOtpRequestAndroid.EDUSOneTimePasswordRequestAndroid;
import app.coronawarn.datadonation.common.protocols.internal.ppdd.PPADataAndroid;
import app.coronawarn.datadonation.common.protocols.internal.ppdd.PpaDataRequestAndroid.PPADataRequestAndroid;
import app.coronawarn.datadonation.services.ppac.android.attestation.NonceCalculator;
import app.coronawarn.datadonation.services.ppac.android.attestation.SignatureVerificationStrategy;
import app.coronawarn.datadonation.services.ppac.android.testdata.JwsGenerationUtil;
import app.coronawarn.datadonation.services.ppac.config.PpacConfiguration;
import app.coronawarn.datadonation.services.ppac.config.TestBeanConfig;
import app.coronawarn.datadonation.services.ppac.config.TestWebSecurityConfig;
import java.io.IOException;
import java.security.GeneralSecurityException;
import java.time.Instant;
import java.time.ZoneOffset;
import java.time.ZonedDateTime;
import java.time.temporal.ChronoUnit;
import java.util.Optional;
import java.util.UUID;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.boot.test.mock.mockito.SpyBean;
import org.springframework.context.annotation.Import;
import org.springframework.http.ResponseEntity;
import org.springframework.test.context.junit.jupiter.SpringExtension;

@ExtendWith(SpringExtension.class)
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@Import({TestWebSecurityConfig.class, TestBeanConfig.class})
class AndroidControllerTest {

  private static final Salt EXPIRED_SALT =
      new Salt("abc", Instant.now().minus(5, ChronoUnit.HOURS).toEpochMilli());

  private static final Salt NOT_EXPIRED_SALT =
      new Salt("def", Instant.now().minus(1, ChronoUnit.HOURS).toEpochMilli());

  private static final String TEST_NONCE_VALUE = "AAAAAAAAAAAAAAAAAAAAAA==";

  @MockBean
  private SignatureVerificationStrategy signatureVerificationStrategy;

  @Autowired
  private RequestExecutor executor;

  @MockBean
  private NonceCalculator nonceCalculator;

  @Autowired
  private PpacConfiguration ppacConfiguration;

  @SpyBean
  private OtpService otpService;

  @Nested
  class MockedSignatureVerificationStrategy {

    @BeforeEach
    void setup() throws GeneralSecurityException {
      SaltRepository saltRepo = mock(SaltRepository.class);

      ppacConfiguration.getAndroid().setAllowedApkPackageNames(new String[]{"de.rki.coronawarnapp.test"});
      ppacConfiguration.getAndroid().setAllowedApkCertificateDigests(
          new String[]{"9VLvUGV0Gkx24etruEBYikvAtqSQ9iY6rYuKhG+xwKE="});
      ppacConfiguration.getAndroid().setAttestationValidity(7200);

      when(saltRepo.findById(any())).then((ans) -> Optional.of(NOT_EXPIRED_SALT));
      when(signatureVerificationStrategy.verifySignature(any())).thenReturn(JwsGenerationUtil.getTestCertificate());
    }

    @Test
    void checkResponseStatusForValidNonce() throws IOException {
      ppacConfiguration.getAndroid().setCertificateHostname("localhost");
      ResponseEntity<Void> actResponse = executor.executePost(buildPayloadWithValidNonce());
      assertThat(actResponse.getStatusCode()).isEqualTo(NO_CONTENT);
    }

    @Test
    void checkResponseStatusForInvalidNonce() throws IOException {
      when(nonceCalculator.calculate(any())).thenReturn(TEST_NONCE_VALUE);
      ResponseEntity<Void> actResponse = executor.executePost(buildPayload());
      assertThat(actResponse.getStatusCode()).isEqualTo(FORBIDDEN);
    }

    @Test
    void checkResponseStatusForInvalidApkPackageName() throws IOException {
      ppacConfiguration.getAndroid().setCertificateHostname("localhost");
      ppacConfiguration.getAndroid().setAllowedApkPackageNames(new String[]{"de.rki.coronawarnapp.wrong"});
      ResponseEntity<Void> actResponse = executor.executePost(buildPayloadWithValidNonce());
      assertThat(actResponse.getStatusCode()).isEqualTo(FORBIDDEN);
    }

    @Test
    void checkResponseStatusForApkCertificateDigestsNotAllowed() throws IOException {
      ppacConfiguration.getAndroid().setCertificateHostname("localhost");
      ppacConfiguration.getAndroid().setAllowedApkCertificateDigests(
          new String[]{"9VLvUGV0Gkx24etruEBYikvAtqSQ9iY6rYuKhG-wrong"});
      ResponseEntity<Void> actResponse = executor.executePost(buildPayloadWithValidNonce());
      assertThat(actResponse.getStatusCode()).isEqualTo(FORBIDDEN);
    }

    @Test
    void checkResponseStatusForFailedAttestationTimestampValidation() throws IOException {
      ppacConfiguration.getAndroid().setCertificateHostname("localhost");
      ppacConfiguration.getAndroid().setAttestationValidity(-100);
      ResponseEntity<Void> actResponse = executor.executePost(buildPayloadWithValidNonce());
      assertThat(actResponse.getStatusCode()).isEqualTo(FORBIDDEN);
    }

    @Test
    void checkResponseStatusForInvalidHostname() throws IOException {
      ResponseEntity<Void> actResponse = executor.executePost(buildPayload());
      assertThat(actResponse.getStatusCode()).isEqualTo(FORBIDDEN);
    }

    @Test
    void checkResponseStatusForMissingSalt() throws IOException {
      ResponseEntity<Void> actResponse = executor.executePost(buildPayloadWithMissingSalt());
      assertThat(actResponse.getStatusCode()).isEqualTo(BAD_REQUEST);
    }

    @Test
    void checkResponseStatusForExpiredSalt() throws IOException {
      ResponseEntity<Void> actResponse = executor.executePost(buildPayloadWithExpiredSalt());
      assertThat(actResponse.getStatusCode()).isEqualTo(FORBIDDEN);
    }

    @Test
    void checkResponseStatusForMissingJws() throws IOException {
      ResponseEntity<Void> actResponse = executor.executePost(buildPayloadWithMissingJws());
      assertThat(actResponse.getStatusCode()).isEqualTo(BAD_REQUEST);
    }

    @Test
    void checkResponseStatusForInvalidJwsParsing() throws IOException {
      ResponseEntity<Void> actResponse = executor.executePost(buildPayloadWithInvalidJwsParsing());
      assertThat(actResponse.getStatusCode()).isEqualTo(UNAUTHORIZED);
    }

  }

  @Nested
  class CreateOtpTests {

    @BeforeEach
    void setup() throws GeneralSecurityException {
      SaltRepository saltRepo = mock(SaltRepository.class);

      ppacConfiguration.getAndroid().setAllowedApkPackageNames(new String[]{"de.rki.coronawarnapp.test"});
      ppacConfiguration.getAndroid().setAllowedApkCertificateDigests(
          new String[]{"9VLvUGV0Gkx24etruEBYikvAtqSQ9iY6rYuKhG+xwKE="});
      ppacConfiguration.getAndroid().setAttestationValidity(7200);

      when(saltRepo.findById(any())).then((ans) -> Optional.of(NOT_EXPIRED_SALT));
      when(signatureVerificationStrategy.verifySignature(any())).thenReturn(JwsGenerationUtil.getTestCertificate());
    }

    @Test
    void testOtpServiceIsCalled() throws IOException {
      ppacConfiguration.getAndroid().setCertificateHostname("localhost");
      String password = UUID.randomUUID().toString();
      ArgumentCaptor<OneTimePassword> otpCaptor = ArgumentCaptor.forClass(OneTimePassword.class);
      ArgumentCaptor<Integer> validityCaptor = ArgumentCaptor.forClass(Integer.class);

      ResponseEntity<OtpCreationResponse> actResponse = executor.executeOtpPost(buildOtpPayloadWithValidNonce(
          password));

      verify(otpService, times(1)).createOtp(otpCaptor.capture(), validityCaptor.capture());
      assertThat(actResponse.getStatusCode()).isEqualTo(OK);
      OneTimePassword cptOtp = otpCaptor.getValue();

      assertThat(validityCaptor.getValue()).isEqualTo(ppacConfiguration.getOtpValidityInHours());
      assertThat(cptOtp.getExpirationTimestamp()).isEqualTo(ZonedDateTime.now(ZoneOffset.UTC).plusHours(ppacConfiguration.getOtpValidityInHours()).toEpochSecond());
      assertThat(cptOtp.getPassword()).isEqualTo(password);
      assertThat(cptOtp.getAndroidPpacBasicIntegrity()).isFalse();
      assertThat(cptOtp.getAndroidPpacCtsProfileMatch()).isFalse();
      assertThat(cptOtp.getAndroidPpacEvaluationTypeBasic()).isTrue();
      assertThat(cptOtp.getAndroidPpacEvaluationTypeHardwareBacked()).isFalse();
    }

    @Test
    void testResponseIs400WhenOtpIsInvalidUuid() throws IOException {
      ppacConfiguration.getAndroid().setCertificateHostname("localhost");
      String password = "invalid-uuid";
      ArgumentCaptor<OneTimePassword> otpCaptor = ArgumentCaptor.forClass(OneTimePassword.class);
      ArgumentCaptor<Integer> validityCaptor = ArgumentCaptor.forClass(Integer.class);

      ResponseEntity<OtpCreationResponse> actResponse = executor.executeOtpPost(buildOtpPayloadWithValidNonce(
          password));

      assertThat(actResponse.getStatusCode()).isEqualTo(BAD_REQUEST);
    }

    private EDUSOneTimePasswordRequestAndroid buildOtpPayloadWithValidNonce(String password) throws IOException {
      String jws = getJwsPayloadWithNonce("eLJTzrT+rTJgxlADK+puUXf8FdODPugHhtRSVSd4jr4=");
      return EDUSOneTimePasswordRequestAndroid.newBuilder()
          .setAuthentication(newAuthenticationObject(jws, NOT_EXPIRED_SALT.getSalt()))
          .setPayload(EDUSOneTimePassword.newBuilder().setOtp(password))
          .build();
    }
  }

  @Test
  void checkResponseStatusForInvalidSignature() throws IOException {
    ResponseEntity<Void> actResponse = executor.executePost(buildPayload());
    assertThat(actResponse.getStatusCode()).isEqualTo(UNAUTHORIZED);
  }

  private PPADataRequestAndroid buildPayload() throws IOException {
    String jws = getJwsPayloadValues();
    return PPADataRequestAndroid.newBuilder()
        .setAuthentication(newAuthenticationObject(jws, NOT_EXPIRED_SALT.getSalt()))
        .setPayload(PPADataAndroid.newBuilder().build())
        .build();
  }

  private PPADataRequestAndroid buildPayloadWithMissingSalt() throws IOException {
    String jws = getJwsPayloadValues();
    return PPADataRequestAndroid.newBuilder()
        .setAuthentication(newAuthenticationObject(jws, ""))
        .setPayload(PPADataAndroid.newBuilder().build())
        .build();
  }

  private PPADataRequestAndroid buildPayloadWithExpiredSalt() throws IOException {
    String jws = getJwsPayloadValues();
    return PPADataRequestAndroid.newBuilder()
        .setAuthentication(newAuthenticationObject(jws, EXPIRED_SALT.getSalt()))
        .setPayload(PPADataAndroid.newBuilder().build())
        .build();
  }

  private PPADataRequestAndroid buildPayloadWithMissingJws() throws IOException {
    return PPADataRequestAndroid.newBuilder()
        .setAuthentication(newAuthenticationObject("", NOT_EXPIRED_SALT.getSalt()))
        .setPayload(PPADataAndroid.newBuilder().build())
        .build();
  }

  private PPADataRequestAndroid buildPayloadWithInvalidJwsParsing() throws IOException {
    return PPADataRequestAndroid.newBuilder()
        .setAuthentication(newAuthenticationObject("RANDOM STRING", NOT_EXPIRED_SALT.getSalt()))
        .setPayload(PPADataAndroid.newBuilder().build())
        .build();
  }

  private PPADataRequestAndroid buildPayloadWithValidNonce() throws IOException {
    String jws = getJwsPayloadWithNonce("eLJTzrT+rTJgxlADK+puUXf8FdODPugHhtRSVSd4jr4=");
    return PPADataRequestAndroid.newBuilder()
        .setAuthentication(newAuthenticationObject(jws, NOT_EXPIRED_SALT.getSalt()))
        .setPayload(PPADataAndroid.newBuilder().build())
        .build();
  }
}
