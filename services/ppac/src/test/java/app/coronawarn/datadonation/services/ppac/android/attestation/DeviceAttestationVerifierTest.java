package app.coronawarn.datadonation.services.ppac.android.attestation;

import static app.coronawarn.datadonation.services.ppac.android.testdata.TestData.*;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.emptyOrNullString;
import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.not;
import static org.junit.Assert.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.*;

import java.io.IOException;
import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.Arrays;
import java.util.Optional;
import app.coronawarn.datadonation.services.ppac.android.attestation.errors.ApkCertificateDigestsNotAllowed;
import app.coronawarn.datadonation.services.ppac.android.attestation.errors.ApkPackageNameNotAllowed;
import app.coronawarn.datadonation.services.ppac.android.attestation.errors.FailedAttestationHostnameValidation;
import org.apache.http.conn.ssl.DefaultHostnameVerifier;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;
import org.mockito.ArgumentCaptor;
import app.coronawarn.datadonation.common.persistence.domain.android.Salt;
import app.coronawarn.datadonation.common.persistence.repository.android.SaltRepository;
import app.coronawarn.datadonation.common.protocols.AuthAndroid;
import app.coronawarn.datadonation.common.protocols.AuthAndroid.Builder;
import app.coronawarn.datadonation.services.ppac.android.attestation.errors.FailedAttestationTimestampValidation;
import app.coronawarn.datadonation.services.ppac.android.attestation.errors.FailedJwsParsing;
import app.coronawarn.datadonation.services.ppac.android.attestation.errors.MissingMandatoryAuthenticationFields;
import app.coronawarn.datadonation.services.ppac.android.attestation.errors.SaltNotValidAnymore;
import app.coronawarn.datadonation.services.ppac.android.testdata.JwsGenerationUtil;
import app.coronawarn.datadonation.services.ppac.android.testdata.TestData;
import app.coronawarn.datadonation.services.ppac.config.PpacConfiguration;

class DeviceAttestationVerifierTest {

  private static final int ATTESTATION_VALIDITY_SECONDS = 7200;
  private static final Salt EXPIRED_SALT =
      new Salt("abc", Instant.now().minus(5, ChronoUnit.HOURS).toEpochMilli());
  private static final Salt NOT_EXPIRED_SALT =
      new Salt("def", Instant.now().minus(1, ChronoUnit.HOURS).toEpochMilli());

  private DeviceAttestationVerifier verifier;

  @BeforeEach
  public void setup() {
    SaltRepository saltRepo = mock(SaltRepository.class);
    when(saltRepo.findById(any())).then((ans) -> Optional.of(NOT_EXPIRED_SALT));
    this.verifier = newVerifierInstance(saltRepo);
  }

  @ParameterizedTest
  @ValueSource(strings = {"    ", "RANDOM STRING"})
  void verificationShouldFailForInvalidJwsFormat(String invalidSignature) {
    FailedJwsParsing exception = assertThrows(FailedJwsParsing.class,
        () -> verifier.validate(newAuthenticationObject(invalidSignature, "salt")));
    assertThat(exception.getMessage(), is(not(emptyOrNullString())));
  }

  @Test
  void verificationShouldFailForMissingJws() {
    String[] jwsTestParameters = new String[]{null, ""};

    Arrays.asList(jwsTestParameters).forEach(testJws -> {
      MissingMandatoryAuthenticationFields exception =
          assertThrows(MissingMandatoryAuthenticationFields.class, () ->
              verifier.validate(newAuthenticationObject(testJws, "salt"))
          );
      assertThat(exception.getMessage(), is(not(emptyOrNullString())));
    });
  }

  @Test
  void verificationShouldFailForMissingSalt() {
    String[] saltTestParameters = new String[]{null, ""};

    Arrays.asList(saltTestParameters).forEach(testSalt -> {
      MissingMandatoryAuthenticationFields exception =
          assertThrows(MissingMandatoryAuthenticationFields.class, () ->
              verifier.validate(newAuthenticationObject(getJwsPayloadValues(), testSalt))
          );
      assertThat(exception.getMessage(), is(not(emptyOrNullString())));
    });
  }

  @Test
  void verificationShouldFailForExpiredSalt() {
    SaltRepository saltRepo = mock(SaltRepository.class);
    when(saltRepo.findById(EXPIRED_SALT.getSalt())).thenReturn(Optional.of(EXPIRED_SALT));
    DeviceAttestationVerifier aVerifier = newVerifierInstance(saltRepo);
    SaltNotValidAnymore exception = assertThrows(SaltNotValidAnymore.class, () ->
        aVerifier.validate(newAuthenticationObject(getJwsPayloadValues(), EXPIRED_SALT.getSalt())));
    assertThat(exception.getMessage(), is(not(emptyOrNullString())));
  }

  @Test
  void shouldPersistNewSaltIfValid() throws IOException {
    SaltRepository saltRepo = mock(SaltRepository.class);
    when(saltRepo.findById(any())).thenReturn(Optional.empty());
    DeviceAttestationVerifier aVerifier = newVerifierInstance(saltRepo);
    aVerifier.validate(newAuthenticationObject(getJwsPayloadValues(), NOT_EXPIRED_SALT.getSalt()));

    ArgumentCaptor<String> argument = ArgumentCaptor.forClass(String.class);
    verify(saltRepo, times(1)).persist(argument.capture(), anyLong());
    assertEquals(NOT_EXPIRED_SALT.getSalt(), argument.getValue());
  }

  /**
   * The X509 certificates contained in the header of the JWS sample below are expired thus the system cannot trust the
   * signature.
   *
   * @throws IOException
   */
  @Test
  void verificationShouldFailForExpiredX509Certificates() throws IOException {
    String sample = TestData.loadJwsWithExpiredCertificates();
    FailedAttestationTimestampValidation exception =
        assertThrows(FailedAttestationTimestampValidation.class, () ->
            verifier.validate(newAuthenticationObject(sample, "salt")));
    assertFalse(exception.getMessage().isEmpty());
  }

  /**
   * The Authentication Object used for testing is generated based on the cert resources stored in
   * /resources/certificates. To test the hostname mismatch we have to override the hostname attribute while creating
   * the newVerifierInstance.
   *
   * @throws IOException
   */
  @Test
  void verificationShouldFailForCertificateHostnameMismatch() throws IOException {
    SaltRepository saltRepo = mock(SaltRepository.class);
    String encodedJws = getJwsPayloadValues();
    this.verifier = newVerifierInstance(saltRepo, "google.test");
    FailedAttestationHostnameValidation exception =
        assertThrows(FailedAttestationHostnameValidation.class, () ->
            verifier.validate(newAuthenticationObject(encodedJws, "salt")));
    assertFalse(exception.getMessage().isEmpty());
  }

  @Test
  void verificationShouldFailForAttestationValidityExpiration() throws IOException {
    String encodedJws = getJwsPayloadAttestationValidityExpired();

    FailedAttestationTimestampValidation exception =
        assertThrows(FailedAttestationTimestampValidation.class, () ->
            verifier.validate(newAuthenticationObject(encodedJws, "salt")));
    assertFalse(exception.getMessage().isEmpty());
  }

  @Test
  void verificationShouldFailForUnacceptedApkPackageName() throws IOException {
    String encodedJws = getJwsPayloadWrongApkPackageName();

    ApkPackageNameNotAllowed exception =
        assertThrows(ApkPackageNameNotAllowed.class, () ->
            verifier.validate(newAuthenticationObject(encodedJws, "salt")));
    assertFalse(exception.getMessage().isEmpty());
  }

  @Test
  void verificationShouldFailForUnacceptedApkCertificateDigestHash() throws IOException {
    String encodedJws = getJwsPayloadWithUnacceptedApkCertificateDigestHash();

    ApkCertificateDigestsNotAllowed exception =
        assertThrows(ApkCertificateDigestsNotAllowed.class, () ->
            verifier.validate(newAuthenticationObject(encodedJws, "salt")));
    assertFalse(exception.getMessage().isEmpty());
  }

  @Test
  void verificationShouldSucceedForValidJws() throws IOException {
    String encodedJws = getJwsPayloadValues();
    verifier.validate(newAuthenticationObject(encodedJws, "salt"));
  }

  private AuthAndroid newAuthenticationObject(String jws, String salt) {
    Builder builder = AuthAndroid.newBuilder();
    if (jws != null) {
      builder.setSafetyNetJwsResult(jws);
    }
    if (salt != null) {
      builder.setSalt(salt);
    }
    return builder.build();
  }

  private DeviceAttestationVerifier newVerifierInstance(SaltRepository saltRepo) {
    return newVerifierInstance(saltRepo, "localhost");
  }

  private DeviceAttestationVerifier newVerifierInstance(SaltRepository saltRepo, String hostname) {
    PpacConfiguration appParameters = new PpacConfiguration();
    PpacConfiguration.Android androidParameters = new PpacConfiguration.Android();
    androidParameters.setCertificateHostname(hostname);
    androidParameters.setAttestationValidity(ATTESTATION_VALIDITY_SECONDS);
    androidParameters.setAllowedApkPackageNames(new String[]{"de.rki.coronawarnapp.test"});
    androidParameters.setAllowedApkCertificateDigests(
        new String[]{"9VLvUGV0Gkx24etruEBYikvAtqSQ9iY6rYuKhG+xwKE="});
    appParameters.setAndroid(androidParameters);
    return new DeviceAttestationVerifier(new DefaultHostnameVerifier(), appParameters, saltRepo,
        new TestSignatureVerificationStrategy(JwsGenerationUtil.getTestCertificate()));
  }
}
