package app.coronawarn.datadonation.services.ppac.android.controller;

import static app.coronawarn.datadonation.services.ppac.android.attestation.signature.JwsGenerationUtil.getTestCertificate;
import static app.coronawarn.datadonation.services.ppac.android.controller.AndroidControllerTest.EXPIRED_SALT;
import static app.coronawarn.datadonation.services.ppac.android.controller.AndroidControllerTest.NOT_EXPIRED_SALT;
import static app.coronawarn.datadonation.services.ppac.android.controller.AndroidControllerTest.newOtp;
import static app.coronawarn.datadonation.services.ppac.android.testdata.TestData.ATTESTATION_VALIDITY_SECONDS;
import static app.coronawarn.datadonation.services.ppac.android.testdata.TestData.TEST_APK_CERTIFICATE_DIGEST;
import static app.coronawarn.datadonation.services.ppac.android.testdata.TestData.TEST_APK_PACKAGE_NAME;
import static app.coronawarn.datadonation.services.ppac.android.testdata.TestData.TEST_CERTIFICATE_HOSTNAME;
import static app.coronawarn.datadonation.services.ppac.android.testdata.TestData.getJwsPayloadWithNonce;
import static app.coronawarn.datadonation.services.ppac.android.testdata.TestData.newAuthenticationObject;
import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.boot.test.context.SpringBootTest.WebEnvironment.RANDOM_PORT;
import static org.springframework.http.HttpStatus.FORBIDDEN;
import static org.springframework.http.HttpStatus.OK;

import app.coronawarn.datadonation.common.persistence.repository.ppac.android.SaltRepository;
import app.coronawarn.datadonation.common.persistence.service.AndroidIdService;
import app.coronawarn.datadonation.common.persistence.service.OtpService;
import app.coronawarn.datadonation.common.persistence.service.SrsOtpService;
import app.coronawarn.datadonation.common.protocols.internal.ppdd.SRSOneTimePasswordRequestAndroid;
import app.coronawarn.datadonation.services.ppac.android.attestation.signature.JwsGenerationUtil;
import app.coronawarn.datadonation.services.ppac.android.attestation.signature.SignatureVerificationStrategy;
import app.coronawarn.datadonation.services.ppac.android.testdata.TestData;
import app.coronawarn.datadonation.services.ppac.config.PpacConfiguration;
import app.coronawarn.datadonation.services.ppac.config.PpacConfiguration.Android.Dat;
import app.coronawarn.datadonation.services.ppac.config.TestBeanConfig;
import com.google.protobuf.ByteString;
import java.security.GeneralSecurityException;
import java.security.SecureRandom;
import java.util.Optional;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.boot.test.mock.mockito.SpyBean;
import org.springframework.context.annotation.Import;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit.jupiter.SpringExtension;

@ExtendWith(SpringExtension.class)
@SpringBootTest(webEnvironment = RANDOM_PORT)
@Import(TestBeanConfig.class)
@ActiveProfiles("test")
class AndroidControllerProfileTest {

  @MockBean
  private SignatureVerificationStrategy signatureVerificationStrategy;

  @SpyBean
  private OtpService otpService;

  @SpyBean
  private PpaDataRequestAndroidConverter androidStorageConverter;

  @Autowired
  private PpacConfiguration ppacConfiguration;

  @Autowired
  private SaltRepository saltRepository;

  @Autowired
  private RequestExecutor executor;

  @SpyBean
  private SrsOtpService srsOtpService;

  @SpyBean
  private AndroidIdService androidIdService;

  private SRSOneTimePasswordRequestAndroid buildSrsOtpPayloadWithValidNonce(final String password,
      final byte[] androidId) throws Exception {
    final String jws = getJwsPayloadWithNonce("mFmhph4QE3GTKS0FRNw9UZCxXI7ue+7fGdqGENsfo4g=");
    return SRSOneTimePasswordRequestAndroid.newBuilder()
        .setAuthentication(newAuthenticationObject(jws, NOT_EXPIRED_SALT.getSalt()))
        .setPayload(SRSOneTimePasswordRequestAndroid.SRSOneTimePassword.newBuilder().setOtp(password)
            .setAndroidId(ByteString.copyFrom(androidId)))
        .build();
  }

  /**
   * The default configuration values are alligned with the test JWS that are created inside the tests. See
   * {@link JwsGenerationUtil} and {@link TestData#getJwsPayloadDefaultValue} for more details on how the mock JWS are
   * created. As an example, the certificate hostname configuration below is alligned with test certificates used to
   * build the JWS signatures (they were created with CN=localhost).
   */
  private void prepareDefaultAppConfiguration() throws Exception {
    ppacConfiguration.getAndroid().setAllowedApkCertificateDigests(new String[] { TEST_APK_CERTIFICATE_DIGEST });
    ppacConfiguration.getAndroid().setAttestationValidity(ATTESTATION_VALIDITY_SECONDS);

    final Dat androidDataFlags = new Dat();
    androidDataFlags.setRequireBasicIntegrity(false);
    androidDataFlags.setRequireCtsProfileMatch(false);
    androidDataFlags.setRequireEvaluationTypeHardwareBacked(false);
    androidDataFlags.setRequireEvaluationTypeBasic(false);
    ppacConfiguration.getAndroid().setDat(androidDataFlags);
    ppacConfiguration.getAndroid().setCertificateHostname(TEST_CERTIFICATE_HOSTNAME);
    ppacConfiguration.getAndroid().setDisableNonceCheck(true);
    ppacConfiguration.getAndroid().setDisableApkCertificateDigestsCheck(false);
    ppacConfiguration.getAndroid().setAllowedApkPackageNames(new String[] { TEST_APK_PACKAGE_NAME });

    saltRepository.persist(EXPIRED_SALT.getSalt(), EXPIRED_SALT.getCreatedAt());
    saltRepository.persist(NOT_EXPIRED_SALT.getSalt(), NOT_EXPIRED_SALT.getCreatedAt());
    when(signatureVerificationStrategy.verifySignature(any())).thenReturn(getTestCertificate());
  }

  @BeforeEach
  void setup() throws Exception {
    prepareDefaultAppConfiguration();

    final SaltRepository saltRepo = mock(SaltRepository.class);

    ppacConfiguration.getAndroid().setAllowedApkPackageNames(new String[] { "de.rki.coronawarnapp.test" });
    ppacConfiguration.getAndroid().setAllowedApkCertificateDigests(
        new String[] { "9VLvUGV0Gkx24etruEBYikvAtqSQ9iY6rYuKhG+xwKE=" });
    ppacConfiguration.getAndroid().setAttestationValidity(7200);
    final Dat dat = new Dat();
    dat.setRequireCtsProfileMatch(false);
    dat.setRequireBasicIntegrity(false);
    dat.setRequireEvaluationTypeBasic(false);
    dat.setRequireEvaluationTypeHardwareBacked(false);
    ppacConfiguration.getAndroid().setDat(dat);

    when(saltRepo.findById(any())).then(ans -> Optional.of(NOT_EXPIRED_SALT));
    when(signatureVerificationStrategy.verifySignature(any())).thenReturn(getTestCertificate());
  }

  @AfterEach
  void tearDown() throws GeneralSecurityException {
    saltRepository.deleteAll();
  }

  @Test
  void testSrsUpsertAndroidId() throws Exception {
    ppacConfiguration.getAndroid().getSrs().setRequireBasicIntegrity(false);
    ppacConfiguration.getAndroid().getSrs().setRequireCtsProfileMatch(false);
    ppacConfiguration.getAndroid().getSrs().setRequireEvaluationTypeHardwareBacked(false);
    ppacConfiguration.getAndroid().setCertificateHostname("localhost");

    final byte[] androidId = new byte[12]; // for whatever reason, but the Android ID seems to be 12 bytes long
    SecureRandom.getInstanceStrong().nextBytes(androidId);
    var response = executor.executeOtpPost(buildSrsOtpPayloadWithValidNonce(newOtp(), androidId));

    final var pepper = ppacConfiguration.getAndroid().pepper();
    final var timeBetweenSubmissionsInDays = ppacConfiguration.getSrsTimeBetweenSubmissionsInDays();
    assertThat(response.getStatusCode()).isEqualTo(OK);

    // second request with same androidId, will result in an 'update' execution
    response = executor.executeOtpPost(buildSrsOtpPayloadWithValidNonce(newOtp(), androidId), true);
    assertThat(response.getStatusCode()).isEqualTo(OK);

    verify(androidIdService, times(2)).upsertAndroidId(androidId, timeBetweenSubmissionsInDays, pepper);
    verify(androidIdService, times(2)).getAndroidIdByPrimaryKey(AndroidIdService.pepper(androidId, pepper));

    response = executor.executeOtpPost(buildSrsOtpPayloadWithValidNonce(newOtp(), androidId), false);
    assertThat(response.getStatusCode()).isEqualTo(FORBIDDEN);
  }
}
