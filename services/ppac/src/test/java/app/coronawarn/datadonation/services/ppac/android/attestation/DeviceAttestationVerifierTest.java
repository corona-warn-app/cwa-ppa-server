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
import static org.junit.jupiter.api.Assertions.fail;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import app.coronawarn.datadonation.common.persistence.domain.ppac.android.SaltData;
import app.coronawarn.datadonation.common.persistence.repository.ppac.android.SaltRepository;
import app.coronawarn.datadonation.common.protocols.internal.ppdd.PPACAndroid;
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
import app.coronawarn.datadonation.services.ppac.commons.PpacScenario;
import app.coronawarn.datadonation.services.ppac.config.PpacConfiguration;
import app.coronawarn.datadonation.services.ppac.config.PpacConfiguration.Android.Dat;
import java.io.IOException;
import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.Arrays;
import java.util.Optional;
import org.apache.http.conn.ssl.DefaultHostnameVerifier;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;
import org.mockito.ArgumentCaptor;


class DeviceAttestationVerifierTest {

  private static final String TEST_NONCE_VALUE = "AAAAAAAAAAAAAAAAAAAAAA==";
  private static final SaltData EXPIRED_SALT_DATA =
      new SaltData("abc", Instant.now().minus(5, ChronoUnit.HOURS).toEpochMilli());
  private static final SaltData NOT_EXPIRED_SALT_DATA =
      new SaltData("def", Instant.now().minus(1, ChronoUnit.HOURS).toEpochMilli());

  private DeviceAttestationVerifier verifier;
  private NonceCalculator defaultNonceCalculator;
  private PpacConfiguration appParameters;

  @BeforeEach
  public void setup() {
    SaltRepository saltRepo = mock(SaltRepository.class);
    when(saltRepo.findById(NOT_EXPIRED_SALT_DATA.getSalt())).then((ans) -> Optional.of(NOT_EXPIRED_SALT_DATA));
    when(saltRepo.findById(EXPIRED_SALT_DATA.getSalt())).thenReturn(Optional.of(EXPIRED_SALT_DATA));
    this.appParameters = prepareApplicationParameters();
    this.verifier = newVerifierInstance(saltRepo, appParameters);
    this.defaultNonceCalculator = mock(NonceCalculator.class);
    when(defaultNonceCalculator.calculate(any())).thenReturn(TEST_NONCE_VALUE);
  }

  @ParameterizedTest
  @ValueSource(strings = {"    ", "RANDOM STRING"})
  void verificationShouldFailForInvalidJwsFormat(String invalidSignature) {
    PPACAndroid ppacAndroid = newAuthenticationObject(invalidSignature, "salt");
    FailedJwsParsing exception = assertThrows(FailedJwsParsing.class, () ->
        verifier.validate(ppacAndroid, defaultNonceCalculator, PpacScenario.PPA));
    assertThat(exception.getMessage(), is(not(emptyOrNullString())));
  }

  @Test
  void verificationShouldFailForMissingJws() {
    String[] jwsTestParameters = new String[] {null, ""};

    Arrays.asList(jwsTestParameters).forEach(testJws -> {
      PPACAndroid ppacAndroid = newAuthenticationObject(testJws, "salt");
      MissingMandatoryAuthenticationFields exception =
          assertThrows(MissingMandatoryAuthenticationFields.class, () ->
              verifier.validate(ppacAndroid, defaultNonceCalculator, PpacScenario.PPA));
      assertThat(exception.getMessage(), is(not(emptyOrNullString())));
    });
  }

  @Test
  void verificationShouldFailForMissingSalt() {
    String[] saltTestParameters = new String[]{null, ""};

    Arrays.asList(saltTestParameters).forEach(testSalt -> {
      try {
        PPACAndroid ppacAndroid = newAuthenticationObject(getJwsPayloadValues(), testSalt);
        MissingMandatoryAuthenticationFields exception =
            assertThrows(MissingMandatoryAuthenticationFields.class, () ->
                verifier.validate(ppacAndroid, defaultNonceCalculator,
                    PpacScenario.PPA));
        assertThat(exception.getMessage(), is(not(emptyOrNullString())));
      } catch (IOException e) {
        fail(e.getMessage());
      }
    });
  }

  @Test
  void verificationShouldFailForExpiredSalt() throws IOException {
    PPACAndroid ppacAndroid = newAuthenticationObject(getJwsPayloadValues(), EXPIRED_SALT_DATA.getSalt());
    SaltNotValidAnymore exception = assertThrows(SaltNotValidAnymore.class, () ->
        this.verifier.validate(ppacAndroid, defaultNonceCalculator, PpacScenario.PPA));
    assertThat(exception.getMessage(), is(not(emptyOrNullString())));
  }

  @Test
  void shouldPersistNewSaltIfValid() throws IOException {
    SaltRepository saltRepo = mock(SaltRepository.class);
    when(saltRepo.findById(any())).thenReturn(Optional.empty());
    DeviceAttestationVerifier aVerifier = newVerifierInstance(saltRepo, this.appParameters);
    aVerifier.validate(newAuthenticationObject(getJwsPayloadValues(), NOT_EXPIRED_SALT_DATA.getSalt()),
        defaultNonceCalculator, PpacScenario.PPA);

    ArgumentCaptor<String> argument = ArgumentCaptor.forClass(String.class);
    verify(saltRepo, times(1)).persist(argument.capture(), anyLong());
    assertEquals(NOT_EXPIRED_SALT_DATA.getSalt(), argument.getValue());
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
    PPACAndroid ppacAndroid = newAuthenticationObject(sample, "salt");
    FailedAttestationTimestampValidation exception =
        assertThrows(FailedAttestationTimestampValidation.class, () ->
            verifier.validate(ppacAndroid, defaultNonceCalculator, PpacScenario.PPA));
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
    PPACAndroid ppacAndroid = newAuthenticationObject(encodedJws, "salt");
    FailedAttestationHostnameValidation exception =
        assertThrows(FailedAttestationHostnameValidation.class, () ->
            verifier.validate(ppacAndroid, defaultNonceCalculator, PpacScenario.PPA));
    assertFalse(exception.getMessage().isEmpty());
  }

  @Test
  void verificationShouldFailForAttestationValidityExpiration() throws IOException {
    String encodedJws = getJwsPayloadAttestationValidityExpired();
    PPACAndroid ppacAndroid = newAuthenticationObject(encodedJws, "salt");
    FailedAttestationTimestampValidation exception =
        assertThrows(FailedAttestationTimestampValidation.class, () ->
            verifier.validate(ppacAndroid, defaultNonceCalculator, PpacScenario.PPA));
    assertFalse(exception.getMessage().isEmpty());
  }

  @Test
  void verificationShouldFailForUnacceptedApkPackageName() throws IOException {
    String encodedJws = getJwsPayloadWrongApkPackageName();
    PPACAndroid ppacAndroid = newAuthenticationObject(encodedJws, "salt");
    ApkPackageNameNotAllowed exception =
        assertThrows(ApkPackageNameNotAllowed.class, () ->
            verifier.validate(ppacAndroid, defaultNonceCalculator, PpacScenario.PPA));
    assertFalse(exception.getMessage().isEmpty());
  }

  @Test
  void verificationShouldFailForUnacceptedApkCertificateDigestHash() throws IOException {
    String encodedJws = getJwsPayloadWithUnacceptedApkCertificateDigestHash();
    PPACAndroid ppacAndroid = newAuthenticationObject(encodedJws, "salt");
    ApkCertificateDigestsNotAllowed exception =
        assertThrows(ApkCertificateDigestsNotAllowed.class, () ->
            verifier.validate(ppacAndroid, defaultNonceCalculator, PpacScenario.PPA));
    assertFalse(exception.getMessage().isEmpty());
  }

  @Test
  void verificationShouldFailForBasicIntegrityViolation() throws IOException {
    String encodedJws = getJwsPayloadWithBasicIntegrityViolation();
    this.appParameters.getAndroid().getDat().setRequireBasicIntegrity(true);
    PPACAndroid ppacAndroid = newAuthenticationObject(encodedJws, "salt");
    BasicIntegrityIsRequired exception =
        assertThrows(BasicIntegrityIsRequired.class, () ->
            verifier.validate(ppacAndroid, defaultNonceCalculator, PpacScenario.PPA));
    assertFalse(exception.getMessage().isEmpty());
  }

  @Test
  void verificationShouldNotFailForBasicIntegrityViolationWhenCheckDisabled() throws IOException {
    String encodedJws = getJwsPayloadWithBasicIntegrityViolation();
    this.appParameters.getAndroid().getDat().setRequireBasicIntegrity(false);
    PPACAndroid ppacAndroid = newAuthenticationObject(encodedJws, "salt");
    assertDoesNotThrow(() -> verifier.validate(ppacAndroid, defaultNonceCalculator, PpacScenario.PPA));
  }

  @Test
  void verificationShouldFailForCtsProfileMatchIntegrityViolation() throws IOException {
    String encodedJws = getJwsPayloadWithCtsMatchViolation();
    this.appParameters.getAndroid().getDat().setRequireCtsProfileMatch(true);
    PPACAndroid ppacAndroid = newAuthenticationObject(encodedJws, "salt");
    CtsProfileMatchRequired exception =
        assertThrows(CtsProfileMatchRequired.class, () ->
            verifier.validate(ppacAndroid, defaultNonceCalculator, PpacScenario.PPA));
    assertFalse(exception.getMessage().isEmpty());
  }

  @Test
  void verificationShouldNotFailForCtsProfileMatchIntegrityViolationWhenCheckDisabled() throws IOException {
    String encodedJws = getJwsPayloadWithCtsMatchViolation();
    this.appParameters.getAndroid().getDat().setRequireCtsProfileMatch(false);
    assertDoesNotThrow(() -> verifier.validate(newAuthenticationObject(encodedJws, "salt"),
        defaultNonceCalculator, PpacScenario.PPA));
  }

  @Test
  void verificationShouldFailForBasicEvaluationTypeRequiredViolation() throws IOException {
    String encodedJws = getJwsPayloadWithEvaluationType("HARDWARE,OTHER");
    this.appParameters.getAndroid().getDat().setRequireEvaluationTypeBasic(true);
    PPACAndroid ppacAndroid = newAuthenticationObject(encodedJws, "salt");
    BasicEvaluationTypeNotPresent exception =
        assertThrows(BasicEvaluationTypeNotPresent.class, () ->
            verifier.validate(ppacAndroid, defaultNonceCalculator, PpacScenario.PPA));
    assertFalse(exception.getMessage().isEmpty());
  }

  @Test
  void verificationShouldNotFailForBasicEvaluationTypeRequiredViolationWhenCheckDisabled() throws IOException {
    String encodedJws = getJwsPayloadWithEvaluationType("HARDWARE,OTHER");
    this.appParameters.getAndroid().getDat().setRequireEvaluationTypeBasic(false);
    assertDoesNotThrow(() -> verifier.validate(newAuthenticationObject(encodedJws, "salt"),
        defaultNonceCalculator, PpacScenario.PPA));
  }

  @Test
  void verificationShouldFailForHardwareBackedEvaluationTypeRequiredViolation() throws IOException {
    String encodedJws = getJwsPayloadWithEvaluationType("OTHER,BASIC");
    this.appParameters.getAndroid().getDat().setRequireEvaluationTypeHardwareBacked(true);
    PPACAndroid ppacAndroid = newAuthenticationObject(encodedJws, "salt");
    HardwareBackedEvaluationTypeNotPresent exception =
        assertThrows(HardwareBackedEvaluationTypeNotPresent.class, () ->
            verifier.validate(ppacAndroid, defaultNonceCalculator, PpacScenario.PPA));
    assertFalse(exception.getMessage().isEmpty());
  }

  @Test
  void verificationShouldNotFailForHardwareBackedEvaluationTypeRequiredViolationWhenCheckDisabled() throws IOException {
    String encodedJws = getJwsPayloadWithEvaluationType("OTHER,BASIC");
    this.appParameters.getAndroid().getDat().setRequireEvaluationTypeHardwareBacked(false);
    assertDoesNotThrow(() -> verifier.validate(newAuthenticationObject(encodedJws, "salt"),
        defaultNonceCalculator, PpacScenario.PPA));
  }

  @Test
  void verificationShouldSucceedWhenRequiredEvaluationTypeIsPresent() throws IOException {
    final String encodedJws = getJwsPayloadWithEvaluationType("BASIC,HARDWARE_BACKED");
    this.appParameters.getAndroid().getDat().setRequireEvaluationTypeBasic(true);
    this.appParameters.getAndroid().getDat().setRequireEvaluationTypeHardwareBacked(true);

    assertDoesNotThrow(() -> verifier.validate(newAuthenticationObject(encodedJws, "salt"),
        defaultNonceCalculator, PpacScenario.PPA));

    String anotherJws = getJwsPayloadWithEvaluationType("HARDWARE_BACKED,BASIC,OTHER");
    assertDoesNotThrow(() -> verifier.validate(newAuthenticationObject(anotherJws, "salt"),
        defaultNonceCalculator, PpacScenario.PPA));
  }

  @Test
  void verificationShouldNotFailIfEvaluationTypeIsEmpty() throws IOException {
    String encodedJws = getJwsPayloadWithEvaluationType("");
    assertDoesNotThrow(() -> verifier.validate(newAuthenticationObject(encodedJws, "salt"),
        defaultNonceCalculator, PpacScenario.PPA));
  }

  @Test
  void verificationShouldSucceedForValidJws() throws IOException {
    String encodedJws = getJwsPayloadValues();
    verifier.validate(newAuthenticationObject(encodedJws, "salt"), defaultNonceCalculator, PpacScenario.PPA);
  }

  @Test
  void verificationShouldFailIfNonceIsMissing() throws IOException {
    String encodedJws = getJwsPayloadWithNonce("");
    PPACAndroid ppacAndroid = newAuthenticationObject(encodedJws, "salt");
    MissingMandatoryAuthenticationFields exception =
        assertThrows(MissingMandatoryAuthenticationFields.class, () ->
            verifier.validate(ppacAndroid, defaultNonceCalculator, PpacScenario.PPA));
    assertFalse(exception.getMessage().isEmpty());
  }

  @Test
  void verificationShouldFailIfRecalculatedNonceDoesNotMatchReceivedNonce() throws IOException {
    NonceCalculator calculator = NonceCalculator.of("payload-test-string".getBytes());
    String nonce = calculator.calculate("salt");
    String encodedJws = getJwsPayloadWithNonce(nonce);
    PPACAndroid ppacAndroid = newAuthenticationObject(encodedJws, "salt");
    NonceCouldNotBeVerified exception =
        assertThrows(NonceCouldNotBeVerified.class, () ->
            verifier.validate(ppacAndroid, defaultNonceCalculator, PpacScenario.PPA));
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

    Dat dataParameters = new Dat();
    dataParameters.setRequireBasicIntegrity(false);
    dataParameters.setRequireCtsProfileMatch(false);
    dataParameters.setRequireEvaluationTypeBasic(false);
    dataParameters.setRequireEvaluationTypeHardwareBacked(false);
    androidParameters.setDat(dataParameters);

    androidParameters.setDisableApkCertificateDigestsCheck(false);
    androidParameters.setDisableNonceCheck(false);
    appParameters.setAndroid(androidParameters);
    return appParameters;
  }

  public static DeviceAttestationVerifier newVerifierInstance(SaltRepository saltRepo, PpacConfiguration appParameters) {
    return new DeviceAttestationVerifier(new DefaultHostnameVerifier(), appParameters,
        new ProdSaltVerificationStrategy(saltRepo, appParameters),
        new TestSignatureVerificationStrategy(JwsGenerationUtil.getTestCertificate()),
        new ProdTimestampVerificationStrategy(appParameters),
        new PpacAndroidIntegrityValidator(appParameters));
  }
}
