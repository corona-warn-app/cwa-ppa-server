package app.coronawarn.datadonation.services.ppac.android.attestation;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.*;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.*;
import java.io.IOException;
import java.util.List;
import java.util.Map;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;
import org.mockito.stubbing.Answer;
import org.apache.http.conn.ssl.DefaultHostnameVerifier;
import app.coronawarn.datadonation.common.persistence.repository.android.SaltRepository;
import app.coronawarn.datadonation.common.protocols.AuthAndroid;
import app.coronawarn.datadonation.services.ppac.android.attestation.errors.FailedJwsParsing;
import app.coronawarn.datadonation.services.ppac.android.attestation.errors.FailedSignatureVerification;
import app.coronawarn.datadonation.services.ppac.android.testdata.JwsGenerationUtil;
import app.coronawarn.datadonation.services.ppac.android.testdata.TestData;
import app.coronawarn.datadonation.services.ppac.config.PpacConfiguration;


class DeviceAttestationVerifierTest {

  private DeviceAttestationVerifier verifier;

  @BeforeEach
  public void setup() {
    SaltRepository saltRepo = mock(SaltRepository.class);
    List<String> salts = List.of("abc", "def");
    when(saltRepo.findById(any())).then(lookupSalt(salts) );
    this.verifier = newVerifierInstance(mock(SaltRepository.class));
  }
  
  @ParameterizedTest
  @ValueSource(strings = {"", "   ", "RANDOM STRING"})
  void verificationShouldFailForInvalidJwsFormat(String invalidSignature) {
    FailedJwsParsing exception = assertThrows(FailedJwsParsing.class, () -> {
      verifier.validate(newAuthenticationObject(invalidSignature));
    });
    assertThat(exception.getMessage(), is(not(emptyOrNullString())));
  }
  
  /**
   *  The X509 certificates contained in the header of the JWS sample below are expired thus the system
   *  cannot trust the signature.
   * @throws IOException 
   */
  @Test
  void verificationShouldFailForExpiredX509Certificates() throws IOException {
    String sample = TestData.loadJwsWithExpiredCertificates();
    FailedSignatureVerification exception = assertThrows(FailedSignatureVerification.class, () -> {
      verifier.validate(newAuthenticationObject(sample));
    });
    assertFalse(exception.getMessage().isEmpty());
  }
  
  @Test
  @Disabled("No sample JWS available yet")
  void verificationShouldFailForCertificateHostnameMismatch() throws IOException {
    //TODO
  }
  
  @Test
  @Disabled("No sample JWS available yet")
  void verificationShouldFailForAttestationValidityExpiration() throws IOException {
    //TODO
  }
  
  @Test
  @Disabled("No sample JWS available yet")
  void verificationShouldFailForUnacceptedApkPackageName() throws IOException {
    //TODO
  }
  
  @Test
  @Disabled("No sample JWS available yet")
  void verificationShouldFailForUnacceptedApkCertificateDigestHash() throws IOException {
    //TODO
  }
  
  @Test
  @Disabled("JWS created below not correct yet")
  void verificationShouldSucceedForValidJws() throws IOException {
     Map<String, String> payloadValues = Map.of("nonce","AAAAAAAAAAAAAAAAAAAAAA==",
         "timestampMs", "1608558363702",
         "apkPackageName", "de.rki.coronawarnapp.test",
         "apkDigestSha256", "9oiqOMQAZfBgCnI0jyN7TgPAQNSSxWrjh14f0eXpB3U=",
         "ctsProfileMatch", "false",
         //"apkCertificateDigestSha256","[\"9VLvUGV0Gkx24etruEBYikvAtqSQ9iY6rYuKhG+xwKE=\"]",
         "basicIntegrity","false",
         "advice", "RESTORE_TO_FACTORY_ROM,LOCK_BOOTLOADER",
         "evaluationType", "BASIC");
     String encodedJws = JwsGenerationUtil.createCompactSerializedJws(payloadValues);
     verifier.validate(newAuthenticationObject(encodedJws));
  }
  
  private AuthAndroid newAuthenticationObject(String jws) {
    return AuthAndroid.newBuilder().setSafetyNetJwsResult(jws).build();
  }
  
  private DeviceAttestationVerifier newVerifierInstance(SaltRepository saltRepo) {
    PpacConfiguration appParameters = new PpacConfiguration();
    PpacConfiguration.Android androidParameters = new PpacConfiguration.Android();
    androidParameters.setCertificateHostname("attest.android.com");
    androidParameters.setAttestationValidity(7200);
    appParameters.setAndroid(androidParameters);
    return new DeviceAttestationVerifier(new DefaultHostnameVerifier(), appParameters, saltRepo);
  }
  
  private Answer<?> lookupSalt(List<String> salts) {
    return (argument) -> salts.stream().filter( s -> s.equals(argument)).findAny();
  }
}