package app.coronawarn.datadonation.services.ppac.android.attestation;

import static app.coronawarn.datadonation.services.ppac.android.testdata.TestData.ATTESTATION_VALIDITY_SECONDS;
import static app.coronawarn.datadonation.services.ppac.android.testdata.TestData.TEST_APK_CERTIFICATE_DIGEST;
import static app.coronawarn.datadonation.services.ppac.android.testdata.TestData.TEST_APK_PACKAGE_NAME;
import static app.coronawarn.datadonation.services.ppac.android.testdata.TestData.getJwsPayloadAttestationValidityExpired;
import static app.coronawarn.datadonation.services.ppac.android.testdata.TestData.getJwsPayloadValues;
import static app.coronawarn.datadonation.services.ppac.android.testdata.TestData.getJwsPayloadWithBasicIntegrityViolation;
import static app.coronawarn.datadonation.services.ppac.android.testdata.TestData.getJwsPayloadWithCtsMatchViolation;
import static app.coronawarn.datadonation.services.ppac.android.testdata.TestData.getJwsPayloadWithEvaluationType;
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
import app.coronawarn.datadonation.services.ppac.android.attestation.signature.TestSignatureVerificationStrategy;
import app.coronawarn.datadonation.services.ppac.android.attestation.timestamp.ProdTimestampVerificationStrategy;
import app.coronawarn.datadonation.services.ppac.android.testdata.TestData;
import app.coronawarn.datadonation.services.ppac.commons.PpacScenario;
import app.coronawarn.datadonation.services.ppac.config.PpacConfiguration;
import app.coronawarn.datadonation.services.ppac.config.PpacConfiguration.Android.Dat;
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
  private static final SaltData EXPIRED_SALT_DATA = new SaltData("abc",
      Instant.now().minus(5, ChronoUnit.HOURS).toEpochMilli());
  private static final SaltData NOT_EXPIRED_SALT_DATA = new SaltData("def",
      Instant.now().minus(1, ChronoUnit.HOURS).toEpochMilli());

  public static DeviceAttestationVerifier newVerifierInstance(final SaltRepository saltRepo,
      final PpacConfiguration appParameters) throws Exception {
    return new DeviceAttestationVerifier(new DefaultHostnameVerifier(), appParameters,
        new ProdSaltVerificationStrategy(saltRepo, appParameters),
        new TestSignatureVerificationStrategy(),
        new ProdTimestampVerificationStrategy(appParameters),
        new PpacAndroidIntegrityValidator(appParameters));
  }

  private static PpacConfiguration prepareApplicationParameters() {
    final PpacConfiguration appParameters = new PpacConfiguration();
    final PpacConfiguration.Android androidParameters = new PpacConfiguration.Android();
    androidParameters.setCertificateHostname("localhost");
    androidParameters.setAttestationValidity(ATTESTATION_VALIDITY_SECONDS);
    androidParameters.setAllowedApkPackageNames(new String[] { TEST_APK_PACKAGE_NAME });
    androidParameters.setAllowedApkCertificateDigests(
        new String[] { TEST_APK_CERTIFICATE_DIGEST });

    final Dat dataParameters = new Dat();
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

  private DeviceAttestationVerifier verifier;

  private NonceCalculator defaultNonceCalculator;

  private PpacConfiguration appParameters;

  @BeforeEach
  public void setup() throws Exception {
    final SaltRepository saltRepo = mock(SaltRepository.class);
    when(saltRepo.findById(NOT_EXPIRED_SALT_DATA.getSalt())).then(ans -> Optional.of(NOT_EXPIRED_SALT_DATA));
    when(saltRepo.findById(EXPIRED_SALT_DATA.getSalt())).thenReturn(Optional.of(EXPIRED_SALT_DATA));
    appParameters = prepareApplicationParameters();
    verifier = newVerifierInstance(saltRepo, appParameters);
    defaultNonceCalculator = mock(NonceCalculator.class);
    when(defaultNonceCalculator.calculate(any())).thenReturn(TEST_NONCE_VALUE);
  }

  @Test
  void shouldPersistNewSaltIfValid() throws Exception {
    final SaltRepository saltRepo = mock(SaltRepository.class);
    when(saltRepo.findById(any())).thenReturn(Optional.empty());
    final DeviceAttestationVerifier aVerifier = newVerifierInstance(saltRepo, appParameters);
    aVerifier.validate(newAuthenticationObject(getJwsPayloadValues(), NOT_EXPIRED_SALT_DATA.getSalt()),
        defaultNonceCalculator, PpacScenario.PPA);

    final ArgumentCaptor<String> argument = ArgumentCaptor.forClass(String.class);
    verify(saltRepo, times(1)).persist(argument.capture(), anyLong());
    assertEquals(NOT_EXPIRED_SALT_DATA.getSalt(), argument.getValue());
  }

  @Test
  void verificationShouldFailForAttestationValidityExpiration() throws Exception {
    final String encodedJws = getJwsPayloadAttestationValidityExpired();
    final PPACAndroid ppacAndroid = newAuthenticationObject(encodedJws, "salt");
    final FailedAttestationTimestampValidation exception = assertThrows(FailedAttestationTimestampValidation.class,
        () -> verifier.validate(ppacAndroid, defaultNonceCalculator, PpacScenario.PPA));
    assertFalse(exception.getMessage().isEmpty());
  }

  @Test
  void verificationShouldFailForBasicEvaluationTypeRequiredViolation() throws Exception {
    final String encodedJws = getJwsPayloadWithEvaluationType("HARDWARE,OTHER");
    appParameters.getAndroid().getDat().setRequireEvaluationTypeBasic(true);
    final PPACAndroid ppacAndroid = newAuthenticationObject(encodedJws, "salt");
    final BasicEvaluationTypeNotPresent exception = assertThrows(BasicEvaluationTypeNotPresent.class,
        () -> verifier.validate(ppacAndroid, defaultNonceCalculator, PpacScenario.PPA));
    assertFalse(exception.getMessage().isEmpty());
  }

  @Test
  void verificationShouldFailForBasicIntegrityViolation() throws Exception {
    final String encodedJws = getJwsPayloadWithBasicIntegrityViolation();
    appParameters.getAndroid().getDat().setRequireBasicIntegrity(true);
    final PPACAndroid ppacAndroid = newAuthenticationObject(encodedJws, "salt");
    final BasicIntegrityIsRequired exception = assertThrows(BasicIntegrityIsRequired.class,
        () -> verifier.validate(ppacAndroid, defaultNonceCalculator, PpacScenario.PPA));
    assertFalse(exception.getMessage().isEmpty());
  }

  /**
   * The Authentication Object used for testing is generated based on the cert resources stored in
   * /resources/certificates. To test the hostname mismatch we have to override the hostname attribute while creating
   * the newVerifierInstance.
   *
   * @throws Exception
   */
  @Test
  void verificationShouldFailForCertificateHostnameMismatch() throws Exception {
    final String encodedJws = getJwsPayloadValues();
    appParameters.getAndroid().setCertificateHostname("google.test");
    final PPACAndroid ppacAndroid = newAuthenticationObject(encodedJws, "salt");
    final FailedAttestationHostnameValidation exception = assertThrows(FailedAttestationHostnameValidation.class,
        () -> verifier.validate(ppacAndroid, defaultNonceCalculator, PpacScenario.PPA));
    assertFalse(exception.getMessage().isEmpty());
  }

  @Test
  void verificationShouldFailForCtsProfileMatchIntegrityViolation() throws Exception {
    final String encodedJws = getJwsPayloadWithCtsMatchViolation();
    appParameters.getAndroid().getDat().setRequireCtsProfileMatch(true);
    final PPACAndroid ppacAndroid = newAuthenticationObject(encodedJws, "salt");
    final CtsProfileMatchRequired exception = assertThrows(CtsProfileMatchRequired.class,
        () -> verifier.validate(ppacAndroid, defaultNonceCalculator, PpacScenario.PPA));
    assertFalse(exception.getMessage().isEmpty());
  }

  @Test
  void verificationShouldFailForExpiredSalt() throws Exception {
    final PPACAndroid ppacAndroid = newAuthenticationObject(getJwsPayloadValues(), EXPIRED_SALT_DATA.getSalt());
    final SaltNotValidAnymore exception = assertThrows(SaltNotValidAnymore.class,
        () -> verifier.validate(ppacAndroid, defaultNonceCalculator, PpacScenario.PPA));
    assertThat(exception.getMessage(), is(not(emptyOrNullString())));
  }

  /**
   * The X509 certificates contained in the header of the JWS sample below are expired thus the system cannot trust the
   * signature.
   *
   * @throws Exception
   */
  @Test
  void verificationShouldFailForExpiredX509Certificates() throws Exception {
    final String sample = TestData.loadJwsWithExpiredCertificates();
    final PPACAndroid ppacAndroid = newAuthenticationObject(sample, "salt");
    final FailedAttestationTimestampValidation exception = assertThrows(FailedAttestationTimestampValidation.class,
        () -> verifier.validate(ppacAndroid, defaultNonceCalculator, PpacScenario.PPA));
    assertFalse(exception.getMessage().isEmpty());
  }

  @Test
  void verificationShouldFailForHardwareBackedEvaluationTypeRequiredViolation() throws Exception {
    final String encodedJws = getJwsPayloadWithEvaluationType("OTHER,BASIC");
    appParameters.getAndroid().getDat().setRequireEvaluationTypeHardwareBacked(true);
    final PPACAndroid ppacAndroid = newAuthenticationObject(encodedJws, "salt");
    final HardwareBackedEvaluationTypeNotPresent exception = assertThrows(HardwareBackedEvaluationTypeNotPresent.class,
        () -> verifier.validate(ppacAndroid, defaultNonceCalculator, PpacScenario.PPA));
    assertFalse(exception.getMessage().isEmpty());
  }

  @ParameterizedTest
  @ValueSource(strings = { "    ", "RANDOM STRING" })
  void verificationShouldFailForInvalidJwsFormat(final String invalidSignature) {
    final PPACAndroid ppacAndroid = newAuthenticationObject(invalidSignature, "salt");
    final FailedJwsParsing exception = assertThrows(FailedJwsParsing.class,
        () -> verifier.validate(ppacAndroid, defaultNonceCalculator, PpacScenario.PPA));
    assertThat(exception.getMessage(), is(not(emptyOrNullString())));
  }

  @Test
  void verificationShouldFailForMissingJws() {
    final String[] jwsTestParameters = { null, "" };

    Arrays.asList(jwsTestParameters).forEach(testJws -> {
      final PPACAndroid ppacAndroid = newAuthenticationObject(testJws, "salt");
      final MissingMandatoryAuthenticationFields exception = assertThrows(MissingMandatoryAuthenticationFields.class,
          () -> verifier.validate(ppacAndroid, defaultNonceCalculator, PpacScenario.PPA));
      assertThat(exception.getMessage(), is(not(emptyOrNullString())));
    });
  }

  @Test
  void verificationShouldFailForMissingSalt() throws Exception {
    final String[] saltTestParameters = { null, "" };

    Arrays.asList(saltTestParameters).forEach(testSalt -> {
      try {
        final PPACAndroid ppacAndroid = newAuthenticationObject(getJwsPayloadValues(), testSalt);
        final MissingMandatoryAuthenticationFields exception = assertThrows(MissingMandatoryAuthenticationFields.class,
            () -> verifier.validate(ppacAndroid, defaultNonceCalculator,
                PpacScenario.PPA));
        assertThat(exception.getMessage(), is(not(emptyOrNullString())));
      } catch (final Exception e) {
        fail(e);
      }
    });
  }

  @Test
  void verificationShouldFailForUnacceptedApkCertificateDigestHash() throws Exception {
    final String encodedJws = getJwsPayloadWithUnacceptedApkCertificateDigestHash();
    final PPACAndroid ppacAndroid = newAuthenticationObject(encodedJws, "salt");
    final ApkCertificateDigestsNotAllowed exception = assertThrows(ApkCertificateDigestsNotAllowed.class,
        () -> verifier.validate(ppacAndroid, defaultNonceCalculator, PpacScenario.PPA));
    assertFalse(exception.getMessage().isEmpty());
  }

  @Test
  void verificationShouldFailForUnacceptedApkPackageName() throws Exception {
    final String encodedJws = getJwsPayloadWrongApkPackageName();
    final PPACAndroid ppacAndroid = newAuthenticationObject(encodedJws, "salt");
    final ApkPackageNameNotAllowed exception = assertThrows(ApkPackageNameNotAllowed.class,
        () -> verifier.validate(ppacAndroid, defaultNonceCalculator, PpacScenario.PPA));
    assertFalse(exception.getMessage().isEmpty());
  }

  @Test
  void verificationShouldFailIfNonceIsMissing() throws Exception {
    final String encodedJws = getJwsPayloadWithNonce("");
    final PPACAndroid ppacAndroid = newAuthenticationObject(encodedJws, "salt");
    final MissingMandatoryAuthenticationFields exception = assertThrows(MissingMandatoryAuthenticationFields.class,
        () -> verifier.validate(ppacAndroid, defaultNonceCalculator, PpacScenario.PPA));
    assertFalse(exception.getMessage().isEmpty());
  }

  @Test
  void verificationShouldFailIfRecalculatedNonceDoesNotMatchReceivedNonce() throws Exception {
    final NonceCalculator calculator = NonceCalculator.of("payload-test-string".getBytes());
    final String nonce = calculator.calculate("salt");
    final String encodedJws = getJwsPayloadWithNonce(nonce);
    final PPACAndroid ppacAndroid = newAuthenticationObject(encodedJws, "salt");
    final NonceCouldNotBeVerified exception = assertThrows(NonceCouldNotBeVerified.class,
        () -> verifier.validate(ppacAndroid, defaultNonceCalculator, PpacScenario.PPA));
    assertFalse(exception.getMessage().isEmpty());
  }

  @Test
  void verificationShouldNotFailForBasicEvaluationTypeRequiredViolationWhenCheckDisabled() throws Exception {
    final String encodedJws = getJwsPayloadWithEvaluationType("HARDWARE,OTHER");
    appParameters.getAndroid().getDat().setRequireEvaluationTypeBasic(false);
    assertDoesNotThrow(() -> verifier.validate(newAuthenticationObject(encodedJws, "salt"),
        defaultNonceCalculator, PpacScenario.PPA));
  }

  @Test
  void verificationShouldNotFailForBasicIntegrityViolationWhenCheckDisabled() throws Exception {
    final String encodedJws = getJwsPayloadWithBasicIntegrityViolation();
    appParameters.getAndroid().getDat().setRequireBasicIntegrity(false);
    final PPACAndroid ppacAndroid = newAuthenticationObject(encodedJws, "salt");
    assertDoesNotThrow(() -> verifier.validate(ppacAndroid, defaultNonceCalculator, PpacScenario.PPA));
  }

  @Test
  void verificationShouldNotFailForCtsProfileMatchIntegrityViolationWhenCheckDisabled() throws Exception {
    final String encodedJws = getJwsPayloadWithCtsMatchViolation();
    appParameters.getAndroid().getDat().setRequireCtsProfileMatch(false);
    assertDoesNotThrow(() -> verifier.validate(newAuthenticationObject(encodedJws, "salt"),
        defaultNonceCalculator, PpacScenario.PPA));
  }

  @Test
  void verificationShouldNotFailForHardwareBackedEvaluationTypeRequiredViolationWhenCheckDisabled() throws Exception {
    final String encodedJws = getJwsPayloadWithEvaluationType("OTHER,BASIC");
    appParameters.getAndroid().getDat().setRequireEvaluationTypeHardwareBacked(false);
    assertDoesNotThrow(() -> verifier.validate(newAuthenticationObject(encodedJws, "salt"),
        defaultNonceCalculator, PpacScenario.PPA));
  }

  @Test
  void verificationShouldNotFailIfEvaluationTypeIsEmpty() throws Exception {
    final String encodedJws = getJwsPayloadWithEvaluationType("");
    assertDoesNotThrow(() -> verifier.validate(newAuthenticationObject(encodedJws, "salt"),
        defaultNonceCalculator, PpacScenario.PPA));
  }

  @Test
  void verificationShouldSucceedForValidJws() throws Exception {
    final String encodedJws = getJwsPayloadValues();
    verifier.validate(newAuthenticationObject(encodedJws, "salt"), defaultNonceCalculator, PpacScenario.PPA);
  }

  @Test
  void verificationShouldSucceedWhenRequiredEvaluationTypeIsPresent() throws Exception {
    final String encodedJws = getJwsPayloadWithEvaluationType("BASIC,HARDWARE_BACKED");
    appParameters.getAndroid().getDat().setRequireEvaluationTypeBasic(true);
    appParameters.getAndroid().getDat().setRequireEvaluationTypeHardwareBacked(true);

    assertDoesNotThrow(() -> verifier.validate(newAuthenticationObject(encodedJws, "salt"),
        defaultNonceCalculator, PpacScenario.PPA));

    final String anotherJws = getJwsPayloadWithEvaluationType("HARDWARE_BACKED,BASIC,OTHER");
    assertDoesNotThrow(() -> verifier.validate(newAuthenticationObject(anotherJws, "salt"),
        defaultNonceCalculator, PpacScenario.PPA));
  }
}
