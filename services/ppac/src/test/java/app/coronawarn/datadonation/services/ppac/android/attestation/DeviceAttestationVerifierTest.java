package app.coronawarn.datadonation.services.ppac.android.attestation;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.emptyOrNullString;
import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.not;
import static org.junit.Assert.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import java.io.IOException;
import java.io.Serializable;
import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.Arrays;
import java.util.Map;
import java.util.Optional;
import org.apache.http.conn.ssl.DefaultHostnameVerifier;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;
import org.mockito.ArgumentCaptor;
import app.coronawarn.datadonation.common.persistence.domain.android.Salt;
import app.coronawarn.datadonation.common.persistence.repository.android.SaltRepository;
import app.coronawarn.datadonation.common.protocols.internal.ppdd.PpacAndroid.PPACAndroid;
import app.coronawarn.datadonation.common.protocols.internal.ppdd.PpacAndroid.PPACAndroid.Builder;
import app.coronawarn.datadonation.services.ppac.android.attestation.errors.FailedAttestationTimestampValidation;
import app.coronawarn.datadonation.services.ppac.android.attestation.errors.FailedJwsParsing;
import app.coronawarn.datadonation.services.ppac.android.attestation.errors.MissingMandatoryAuthenticationFields;
import app.coronawarn.datadonation.services.ppac.android.attestation.errors.SaltNotValidAnymore;
import app.coronawarn.datadonation.services.ppac.android.testdata.JwsGenerationUtil;
import app.coronawarn.datadonation.services.ppac.android.testdata.TestData;
import app.coronawarn.datadonation.services.ppac.config.PpacConfiguration;


class DeviceAttestationVerifierTest {

  private static final String TEST_NONCE_VALUE = "AAAAAAAAAAAAAAAAAAAAAA==";
  private static final int ATTESTATION_VALIDITY_SECONDS = 7200;
  private static final Salt EXPIRED_SALT =
      new Salt("abc", Instant.now().minus(5, ChronoUnit.HOURS).toEpochMilli());
  private static final Salt NOT_EXPIRED_SALT =
      new Salt("def", Instant.now().minus(1, ChronoUnit.HOURS).toEpochMilli());

  private DeviceAttestationVerifier verifier;
  private NonceCalculator defaultNonceCalculator;

  @BeforeEach
  public void setup() {
    SaltRepository saltRepo = mock(SaltRepository.class);
    when(saltRepo.findById(any())).then((ans) -> Optional.of(NOT_EXPIRED_SALT));
    this.verifier = newVerifierInstance(saltRepo);
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
            verifier.validate(newAuthenticationObject(getJwsPayloadValue(), testSalt), defaultNonceCalculator);
          });
      assertThat(exception.getMessage(), is(not(emptyOrNullString())));
    });
  }

  @Test
  void verificationShouldFailForExpiredSalt() {
    SaltRepository saltRepo = mock(SaltRepository.class);
    when(saltRepo.findById(EXPIRED_SALT.getSalt())).thenReturn(Optional.of(EXPIRED_SALT));
    DeviceAttestationVerifier aVerifier = newVerifierInstance(saltRepo);
    SaltNotValidAnymore exception = assertThrows(SaltNotValidAnymore.class, () -> {
      aVerifier.validate(newAuthenticationObject(getJwsPayloadValue(), EXPIRED_SALT.getSalt()), defaultNonceCalculator);
    });
    assertThat(exception.getMessage(), is(not(emptyOrNullString())));
  }

  @Test
  void shouldPersistNewSaltIfValid() throws IOException {
    SaltRepository saltRepo = mock(SaltRepository.class);
    when(saltRepo.findById(any())).thenReturn(Optional.empty());
    DeviceAttestationVerifier aVerifier = newVerifierInstance(saltRepo);
    aVerifier.validate(newAuthenticationObject(getJwsPayloadValue(), NOT_EXPIRED_SALT.getSalt()), defaultNonceCalculator);

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

  @Test
  @Disabled("No sample JWS available yet")
  void verificationShouldFailForCertificateHostnameMismatch() throws IOException {
    // TODO
  }

  @Test
  @Disabled("No sample JWS available yet")
  void verificationShouldFailForAttestationValidityExpiration() throws IOException {
    // TODO
  }

  @Test
  @Disabled("No sample JWS available yet")
  void verificationShouldFailForUnacceptedApkPackageName() throws IOException {
    // TODO
  }

  @Test
  @Disabled("No sample JWS available yet")
  void verificationShouldFailForUnacceptedApkCertificateDigestHash() throws IOException {
    // TODO
  }

  @Test
  @Disabled("JWS created below not correct yet")
  void verificationShouldSucceedForValidJws() throws IOException {
    String encodedJws = getJwsPayloadValue();
    verifier.validate(newAuthenticationObject(encodedJws, "salt"), defaultNonceCalculator);
  }

  private String getJwsPayloadValue() throws IOException {
    Map<String, Serializable> payloadValues = Map.of(
        "nonce", TEST_NONCE_VALUE,
        "timestampMs", String.valueOf(Instant.now().minusSeconds(500).toEpochMilli()),
        "apkPackageName", "de.rki.coronawarnapp.test", "apkDigestSha256",
        "9oiqOMQAZfBgCnI0jyN7TgPAQNSSxWrjh14f0eXpB3U=", "ctsProfileMatch", "false",
        "apkCertificateDigestSha256", new String[] {"9VLvUGV0Gkx24etruEBYikvAtqSQ9iY6rYuKhG+xwKE="},
        "basicIntegrity", "false", "advice", "RESTORE_TO_FACTORY_ROM,LOCK_BOOTLOADER",
        "evaluationType", "BASIC");
    String encodedJws = JwsGenerationUtil.createCompactSerializedJws(payloadValues);
    return encodedJws;
  }

  private PPACAndroid newAuthenticationObject(String jws, String salt) {
    Builder builder = PPACAndroid.newBuilder();
    if (jws != null) {
      builder.setSafetyNetJws(jws);
    }
    if (salt != null) {
      builder.setSalt(salt);
    }
    return builder.build();
  }

  private DeviceAttestationVerifier newVerifierInstance(SaltRepository saltRepo) {
    PpacConfiguration appParameters = new PpacConfiguration();
    PpacConfiguration.Android androidParameters = new PpacConfiguration.Android();
    androidParameters.setCertificateHostname("localhost");
    androidParameters.setAttestationValidity(ATTESTATION_VALIDITY_SECONDS);
    androidParameters.setAllowedApkPackageNames(new String[] {"de.rki.coronawarnapp.test"});
    androidParameters.setAllowedApkCertificateDigests(
        new String[] {"9VLvUGV0Gkx24etruEBYikvAtqSQ9iY6rYuKhG+xwKE="});
    appParameters.setAndroid(androidParameters);
    return new DeviceAttestationVerifier(new DefaultHostnameVerifier(), appParameters, saltRepo,
        new TestSignatureVerificationStrategy(JwsGenerationUtil.getTestCertificate()));
  }
}
