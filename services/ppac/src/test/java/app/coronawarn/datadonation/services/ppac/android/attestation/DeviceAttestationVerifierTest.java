package app.coronawarn.datadonation.services.ppac.android.attestation;

import static app.coronawarn.datadonation.services.ppac.android.testdata.TestData.*;
import static app.coronawarn.datadonation.services.ppac.android.testdata.TestData.getJwsPayloadValues;
import static app.coronawarn.datadonation.services.ppac.android.testdata.TestData.getJwsPayloadWithNonce;
import static app.coronawarn.datadonation.services.ppac.android.testdata.TestData.getJwsPayloadWithUnacceptedApkCertificateDigestHash;
import static app.coronawarn.datadonation.services.ppac.android.testdata.TestData.getJwsPayloadWrongApkPackageName;
import static app.coronawarn.datadonation.services.ppac.android.testdata.TestData.newAuthenticationObject;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.emptyOrNullString;
import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.not;
import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import app.coronawarn.datadonation.common.persistence.domain.ppac.android.Salt;
import app.coronawarn.datadonation.common.persistence.repository.ppac.android.SaltRepository;
import app.coronawarn.datadonation.services.ppac.android.attestation.errors.ApkCertificateDigestsNotAllowed;
import app.coronawarn.datadonation.services.ppac.android.attestation.errors.ApkPackageNameNotAllowed;
import app.coronawarn.datadonation.services.ppac.android.attestation.errors.BasicEvaluationTypeNotPresent;
import app.coronawarn.datadonation.services.ppac.android.attestation.errors.BasicIntegrityIsRequired;
import app.coronawarn.datadonation.services.ppac.android.attestation.errors.CtsProfileMatchRequired;
import app.coronawarn.datadonation.services.ppac.android.attestation.errors.FailedAttestationHostnameValidation;
import app.coronawarn.datadonation.services.ppac.android.attestation.errors.FailedAttestationTimestampValidation;
import app.coronawarn.datadonation.services.ppac.android.attestation.errors.FailedJwsParsing;
import app.coronawarn.datadonation.services.ppac.android.attestation.errors.HardwareBackedEvaluationTypeNotPresent;
import app.coronawarn.datadonation.services.ppac.android.attestation.errors.MissingMandatoryAuthenticationFields;
import app.coronawarn.datadonation.services.ppac.android.attestation.errors.NonceCouldNotBeVerified;
import app.coronawarn.datadonation.services.ppac.android.attestation.errors.SaltNotValidAnymore;
import app.coronawarn.datadonation.services.ppac.android.attestation.salt.ProdSaltVerificationStrategy;
import app.coronawarn.datadonation.services.ppac.android.attestation.timestamp.ProdTimestampVerificationStrategy;
import app.coronawarn.datadonation.services.ppac.android.testdata.JwsGenerationUtil;
import app.coronawarn.datadonation.services.ppac.android.testdata.TestData;
import app.coronawarn.datadonation.services.ppac.config.PpacConfiguration;
import java.io.IOException;
import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.Arrays;
import java.util.Optional;
import org.apache.http.conn.ssl.DefaultHostnameVerifier;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;
import org.mockito.ArgumentCaptor;


class DeviceAttestationVerifierTest {

  private static final String TEST_NONCE_VALUE = "AAAAAAAAAAAAAAAAAAAAAA==";
  private static final Salt EXPIRED_SALT =
      new Salt("abc", Instant.now().minus(5, ChronoUnit.HOURS).toEpochMilli());
  private static final Salt NOT_EXPIRED_SALT =
      new Salt("def", Instant.now().minus(1, ChronoUnit.HOURS).toEpochMilli());

  private DeviceAttestationVerifier verifier;
  private NonceCalculator defaultNonceCalculator;
  private PpacConfiguration appParameters;

  @BeforeEach
  public void setup() {
    SaltRepository saltRepo = mock(SaltRepository.class);
    when(saltRepo.findById(NOT_EXPIRED_SALT.getSalt())).then((ans) -> Optional.of(NOT_EXPIRED_SALT));
    when(saltRepo.findById(EXPIRED_SALT.getSalt())).thenReturn(Optional.of(EXPIRED_SALT));
    this.appParameters = prepareApplicationParameters();
    this.verifier = newVerifierInstance(saltRepo, appParameters);
    this.defaultNonceCalculator = mock(NonceCalculator.class);
    when(defaultNonceCalculator.calculate(any())).thenReturn(TEST_NONCE_VALUE);
  }

  @ParameterizedTest
  @ValueSource(strings = {"    ", "RANDOM STRING"})
  void verificationShouldFailForInvalidJwsFormat(String invalidSignature) {
    FailedJwsParsing exception = assertThrows(FailedJwsParsing.class, () -> {
      verifier.validate(newAuthenticationObject(invalidSignature, "salt"), defaultNonceCalculator );
    });
    assertThat(exception.getMessage(), is(not(emptyOrNullString())));
  }

  @Test
  void verificationShouldFailForMissingJws() {
    String[] jwsTestParameters = new String[] {null, ""};

    Arrays.asList(jwsTestParameters).forEach(testJws -> {
      MissingMandatoryAuthenticationFields exception =
          assertThrows(MissingMandatoryAuthenticationFields.class, () -> {
            verifier.validate(newAuthenticationObject(testJws, "salt"), defaultNonceCalculator);
          });
      assertThat(exception.getMessage(), is(not(emptyOrNullString())));
    });
  }

  @Test
  void verificationShouldFailForMissingSalt() {
    String[] saltTestParameters = new String[] {null, ""};

    Arrays.asList(saltTestParameters).forEach(testSalt -> {
      MissingMandatoryAuthenticationFields exception =
          assertThrows(MissingMandatoryAuthenticationFields.class, () -> {
            verifier.validate(newAuthenticationObject(getJwsPayloadValues(), testSalt), defaultNonceCalculator);
          });
      assertThat(exception.getMessage(), is(not(emptyOrNullString())));
    });
  }

  @Test
  void verificationShouldFailForExpiredSalt() {
    SaltNotValidAnymore exception = assertThrows(SaltNotValidAnymore.class, () -> {
      this.verifier.validate(newAuthenticationObject(getJwsPayloadValues(), EXPIRED_SALT.getSalt()),
          defaultNonceCalculator);
    });
    assertThat(exception.getMessage(), is(not(emptyOrNullString())));
  }

  @Test
  void shouldPersistNewSaltIfValid() throws IOException {
    SaltRepository saltRepo = mock(SaltRepository.class);
    when(saltRepo.findById(any())).thenReturn(Optional.empty());
    DeviceAttestationVerifier aVerifier = newVerifierInstance(saltRepo, this.appParameters);
    aVerifier.validate(newAuthenticationObject(getJwsPayloadValues(), NOT_EXPIRED_SALT.getSalt()),
        defaultNonceCalculator);

    ArgumentCaptor<String> argument = ArgumentCaptor.forClass(String.class);
    verify(saltRepo, times(1)).persist(argument.capture(), anyLong());
    assertEquals(NOT_EXPIRED_SALT.getSalt(), argument.getValue());
  }

  /**
   * The X509 certificates contained in the header of the JWS sample below are expired thus the
   * system cannot trust the signature.
   *
   * @throws IOException
   */
  @Test
  void verificationShouldFailForExpiredX509Certificates() throws IOException {
    String sample = TestData.loadJwsWithExpiredCertificates();
    FailedAttestationTimestampValidation exception =
        assertThrows(FailedAttestationTimestampValidation.class, () -> {
          verifier.validate(newAuthenticationObject(sample, "salt"), defaultNonceCalculator);
        });
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
    String encodedJws = getJwsPayloadValues();
    this.appParameters.getAndroid().setCertificateHostname("google.test");
    FailedAttestationHostnameValidation exception =
        assertThrows(FailedAttestationHostnameValidation.class, () ->
            verifier.validate(newAuthenticationObject(encodedJws, "salt"), defaultNonceCalculator));
    assertFalse(exception.getMessage().isEmpty());
  }

  @Test
  void verificationShouldFailForAttestationValidityExpiration() throws IOException {
    String encodedJws = getJwsPayloadAttestationValidityExpired();

    FailedAttestationTimestampValidation exception =
        assertThrows(FailedAttestationTimestampValidation.class, () ->
            verifier.validate(newAuthenticationObject(encodedJws, "salt"), defaultNonceCalculator));
    assertFalse(exception.getMessage().isEmpty());
  }

  @Test
  void verificationShouldFailForUnacceptedApkPackageName() throws IOException {
    String encodedJws = getJwsPayloadWrongApkPackageName();

    ApkPackageNameNotAllowed exception =
        assertThrows(ApkPackageNameNotAllowed.class, () ->
            verifier.validate(newAuthenticationObject(encodedJws, "salt"), defaultNonceCalculator));
    assertFalse(exception.getMessage().isEmpty());
  }

  @Test
  void verificationShouldFailForUnacceptedApkCertificateDigestHash() throws IOException {
    String encodedJws = getJwsPayloadWithUnacceptedApkCertificateDigestHash();

    ApkCertificateDigestsNotAllowed exception =
        assertThrows(ApkCertificateDigestsNotAllowed.class, () ->
            verifier.validate(newAuthenticationObject(encodedJws, "salt"), defaultNonceCalculator));
    assertFalse(exception.getMessage().isEmpty());
  }

  @Test
  void verificationShouldFailForBasicIntegrityViolation() throws IOException {
    String encodedJws = getJwsPayloadWithBasicIntegrityViolation();
    this.appParameters.getAndroid().setRequireBasicIntegrity(true);
    BasicIntegrityIsRequired exception =
        assertThrows(BasicIntegrityIsRequired.class, () ->
            verifier.validate(newAuthenticationObject(encodedJws, "salt"), defaultNonceCalculator));
    assertFalse(exception.getMessage().isEmpty());
  }
  
  @Test
  void verificationShouldNotFailForBasicIntegrityViolationWhenCheckDisabled() throws IOException {
    String encodedJws = getJwsPayloadWithBasicIntegrityViolation();
    this.appParameters.getAndroid().setRequireBasicIntegrity(false);
    assertDoesNotThrow(() -> verifier.validate(newAuthenticationObject(encodedJws, "salt"),
        defaultNonceCalculator));
  }
  
  @Test
  void verificationShouldFailForCtsProfileMatchIntegrityViolation() throws IOException {
    String encodedJws = getJwsPayloadWithCtsMatchViolation();
    this.appParameters.getAndroid().setRequireCtsProfileMatch(true);
    CtsProfileMatchRequired exception =
        assertThrows(CtsProfileMatchRequired.class, () ->
            verifier.validate(newAuthenticationObject(encodedJws, "salt"), defaultNonceCalculator));
    assertFalse(exception.getMessage().isEmpty());
  }
  
  @Test
  void verificationShouldNotFailForCtsProfileMatchIntegrityViolationWhenCheckDisabled() throws IOException {
    String encodedJws = getJwsPayloadWithCtsMatchViolation();
    this.appParameters.getAndroid().setRequireCtsProfileMatch(false);
    assertDoesNotThrow(() -> verifier.validate(newAuthenticationObject(encodedJws, "salt"),
        defaultNonceCalculator));
  }
  
  @Test
  void verificationShouldFailForBasicEvaluationTypeRequiredViolation() throws IOException {
    String encodedJws = getJwsPayloadWithEvaluationType("HARDWARE,OTHER");
    this.appParameters.getAndroid().setRequireEvaluationTypeBasic(true);
    BasicEvaluationTypeNotPresent exception =
        assertThrows(BasicEvaluationTypeNotPresent.class, () ->
            verifier.validate(newAuthenticationObject(encodedJws, "salt"), defaultNonceCalculator));
    assertFalse(exception.getMessage().isEmpty());
  }
  
  @Test
  void verificationShouldNotFailForBasicEvaluationTypeRequiredViolationWhenCheckDisabled() throws IOException {
    String encodedJws = getJwsPayloadWithEvaluationType("HARDWARE,OTHER");
    this.appParameters.getAndroid().setRequireEvaluationTypeBasic(false);
    assertDoesNotThrow(() -> verifier.validate(newAuthenticationObject(encodedJws, "salt"),
        defaultNonceCalculator));
  }
  
  @Test
  void verificationShouldFailForHardwareBackedEvaluationTypeRequiredViolation() throws IOException {
    String encodedJws = getJwsPayloadWithEvaluationType("OTHER,BASIC");
    this.appParameters.getAndroid().setRequireEvaluationTypeHardwareBacked(true);
    HardwareBackedEvaluationTypeNotPresent exception =
        assertThrows(HardwareBackedEvaluationTypeNotPresent.class, () ->
            verifier.validate(newAuthenticationObject(encodedJws, "salt"), defaultNonceCalculator));
    assertFalse(exception.getMessage().isEmpty());
  }
  
  @Test
  void verificationShouldNotFailForHardwareBackedEvaluationTypeRequiredViolationWhenCheckDisabled() throws IOException {
    String encodedJws = getJwsPayloadWithEvaluationType("OTHER,BASIC");
    this.appParameters.getAndroid().setRequireEvaluationTypeHardwareBacked(false);
    assertDoesNotThrow(() -> verifier.validate(newAuthenticationObject(encodedJws, "salt"),
        defaultNonceCalculator));
  }
  
  @Test
  void verificationShouldSucceedWhenRequiredEvaluationTypeIsPresent() throws IOException {
    final String encodedJws = getJwsPayloadWithEvaluationType("BASIC,HARDWARE_BACKED");
    this.appParameters.getAndroid().setRequireEvaluationTypeBasic(true);
    this.appParameters.getAndroid().setRequireEvaluationTypeHardwareBacked(true);
    
    assertDoesNotThrow(() -> verifier.validate(newAuthenticationObject(encodedJws, "salt"),
        defaultNonceCalculator));
    
    final String anotherJws = getJwsPayloadWithEvaluationType("HARDWARE_BACKED,BASIC,OTHER");
    assertDoesNotThrow(() -> verifier.validate(newAuthenticationObject(anotherJws, "salt"),
        defaultNonceCalculator));
  }
  
  @Test
  void verificationShouldNotFailIfEvaluationTypeIsEmpty() throws IOException {
    String encodedJws = getJwsPayloadWithEvaluationType("");
    assertDoesNotThrow(() -> verifier.validate(newAuthenticationObject(encodedJws, "salt"),
        defaultNonceCalculator));
  }
  
  @Test
  void verificationShouldSucceedForValidJws() throws IOException {
    String encodedJws = getJwsPayloadValues();
    verifier.validate(newAuthenticationObject(encodedJws, "salt"),defaultNonceCalculator);
  }

  @Test
  @Disabled
  void verificationShouldFailIfNonceIsMissing() throws IOException {
    String encodedJws = getJwsPayloadWithNonce("");

    MissingMandatoryAuthenticationFields exception =
        assertThrows(MissingMandatoryAuthenticationFields.class, () ->
            verifier.validate(newAuthenticationObject(encodedJws, "salt"), defaultNonceCalculator));
    assertFalse(exception.getMessage().isEmpty());
  }

  @Test
  @Disabled
  void verificationShouldFailIfRecalculatedNonceDoesNotMatchReceivedNonce() throws IOException {
    NonceCalculator calculator = NonceCalculator.of("payload-test-string".getBytes());
    String nonce = calculator.calculate("salt");
    String encodedJws = getJwsPayloadWithNonce(nonce);

    NonceCouldNotBeVerified exception =
        assertThrows(NonceCouldNotBeVerified.class, () ->
            verifier.validate(newAuthenticationObject(encodedJws, "salt"), defaultNonceCalculator));
    assertFalse(exception.getMessage().isEmpty());
  }
  
  private static PpacConfiguration prepareApplicationParameters() {
    PpacConfiguration appParameters = new PpacConfiguration();
    PpacConfiguration.Android androidParameters = new PpacConfiguration.Android();
    androidParameters.setCertificateHostname("localhost");
    androidParameters.setAttestationValidity(ATTESTATION_VALIDITY_SECONDS);
    androidParameters.setAllowedApkPackageNames(new String[] {TEST_APK_PACKAGE_NAME});
    androidParameters.setAllowedApkCertificateDigests(
        new String[] {TEST_APK_CERTIFICATE_DIGEST});
    androidParameters.setRequireBasicIntegrity(false);
    androidParameters.setRequireCtsProfileMatch(false);
    androidParameters.setRequireEvaluationTypeBasic(false);
    androidParameters.setRequireEvaluationTypeHardwareBacked(false);
    androidParameters.setDisableApkCertificateDigestsCheck(false);
    androidParameters.setDisableNonceCheck(false);
    appParameters.setAndroid(androidParameters);
    return appParameters;
  }
  
  public static DeviceAttestationVerifier newVerifierInstance(SaltRepository saltRepo, PpacConfiguration appParameters) {
    return new DeviceAttestationVerifier(new DefaultHostnameVerifier(), appParameters,
        new ProdSaltVerificationStrategy(saltRepo, appParameters),
        new TestSignatureVerificationStrategy(JwsGenerationUtil.getTestCertificate()),
        new ProdTimestampVerificationStrategy(appParameters));
  }
}
