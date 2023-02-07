package app.coronawarn.datadonation.services.ppac.android.controller;

import static app.coronawarn.datadonation.common.utils.TimeUtils.getZonedDateTimeFor;
import static app.coronawarn.datadonation.services.ppac.android.attestation.signature.JwsGenerationUtil.getTestCertificate;
import static app.coronawarn.datadonation.services.ppac.android.testdata.TestData.ATTESTATION_VALIDITY_SECONDS;
import static app.coronawarn.datadonation.services.ppac.android.testdata.TestData.TEST_APK_CERTIFICATE_DIGEST;
import static app.coronawarn.datadonation.services.ppac.android.testdata.TestData.TEST_APK_PACKAGE_NAME;
import static app.coronawarn.datadonation.services.ppac.android.testdata.TestData.TEST_CERTIFICATE_HOSTNAME;
import static app.coronawarn.datadonation.services.ppac.android.testdata.TestData.getInvalidExposureRiskMetadata;
import static app.coronawarn.datadonation.services.ppac.android.testdata.TestData.getJwsPayloadValues;
import static app.coronawarn.datadonation.services.ppac.android.testdata.TestData.getJwsPayloadWithBasicIntegrityViolation;
import static app.coronawarn.datadonation.services.ppac.android.testdata.TestData.getJwsPayloadWithCtsMatchViolation;
import static app.coronawarn.datadonation.services.ppac.android.testdata.TestData.getJwsPayloadWithEvaluationType;
import static app.coronawarn.datadonation.services.ppac.android.testdata.TestData.getJwsPayloadWithIntegrityFlagsChecked;
import static app.coronawarn.datadonation.services.ppac.android.testdata.TestData.getJwsPayloadWithNonce;
import static app.coronawarn.datadonation.services.ppac.android.testdata.TestData.getValidAndroidDataPayload;
import static app.coronawarn.datadonation.services.ppac.android.testdata.TestData.getValidClientMetadata;
import static app.coronawarn.datadonation.services.ppac.android.testdata.TestData.getValidExposureRiskMetadata;
import static app.coronawarn.datadonation.services.ppac.android.testdata.TestData.getValidExposureWindow;
import static app.coronawarn.datadonation.services.ppac.android.testdata.TestData.getValidKeySubmissionMetadata;
import static app.coronawarn.datadonation.services.ppac.android.testdata.TestData.getValidTestResultMetadata;
import static app.coronawarn.datadonation.services.ppac.android.testdata.TestData.getValidUserMetadata;
import static app.coronawarn.datadonation.services.ppac.android.testdata.TestData.newAuthenticationObject;
import static app.coronawarn.datadonation.services.ppac.android.testdata.TestData.CardinalityTestData.buildPayloadWithExposureRiskMetrics;
import static app.coronawarn.datadonation.services.ppac.android.testdata.TestData.CardinalityTestData.buildPayloadWithExposureWindowMetrics;
import static app.coronawarn.datadonation.services.ppac.android.testdata.TestData.CardinalityTestData.buildPayloadWithKeySubmission;
import static app.coronawarn.datadonation.services.ppac.android.testdata.TestData.CardinalityTestData.buildPayloadWithScanInstancesMetrics;
import static app.coronawarn.datadonation.services.ppac.android.testdata.TestData.CardinalityTestData.buildPayloadWithTestResults;
import static app.coronawarn.datadonation.services.ppac.logging.PpacErrorCode.APK_CERTIFICATE_MISMATCH;
import static app.coronawarn.datadonation.services.ppac.logging.PpacErrorCode.APK_PACKAGE_NAME_MISMATCH;
import static app.coronawarn.datadonation.services.ppac.logging.PpacErrorCode.BASIC_INTEGRITY_REQUIRED;
import static app.coronawarn.datadonation.services.ppac.logging.PpacErrorCode.CTS_PROFILE_MATCH_REQUIRED;
import static app.coronawarn.datadonation.services.ppac.logging.PpacErrorCode.EVALUATION_TYPE_BASIC_REQUIRED;
import static app.coronawarn.datadonation.services.ppac.logging.PpacErrorCode.EVALUATION_TYPE_HARDWARE_BACKED_REQUIRED;
import static app.coronawarn.datadonation.services.ppac.logging.PpacErrorCode.JWS_SIGNATURE_VERIFICATION_FAILED;
import static app.coronawarn.datadonation.services.ppac.logging.PpacErrorCode.NONCE_MISMATCH;
import static app.coronawarn.datadonation.services.ppac.logging.PpacErrorCode.SALT_REDEEMED;
import static java.time.ZoneOffset.UTC;
import static java.time.ZonedDateTime.now;
import static java.time.temporal.ChronoUnit.HOURS;
import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.boot.test.context.SpringBootTest.WebEnvironment.RANDOM_PORT;
import static org.springframework.http.HttpStatus.BAD_REQUEST;
import static org.springframework.http.HttpStatus.FORBIDDEN;
import static org.springframework.http.HttpStatus.OK;
import static org.springframework.http.HttpStatus.UNAUTHORIZED;

import app.coronawarn.datadonation.common.persistence.domain.ElsOneTimePassword;
import app.coronawarn.datadonation.common.persistence.domain.OneTimePassword;
import app.coronawarn.datadonation.common.persistence.domain.SrsOneTimePassword;
import app.coronawarn.datadonation.common.persistence.domain.ppac.android.SaltData;
import app.coronawarn.datadonation.common.persistence.repository.metrics.ClientMetadataRepository;
import app.coronawarn.datadonation.common.persistence.repository.metrics.ExposureRiskMetadataRepository;
import app.coronawarn.datadonation.common.persistence.repository.metrics.ExposureWindowRepository;
import app.coronawarn.datadonation.common.persistence.repository.metrics.ExposureWindowTestResultsRepository;
import app.coronawarn.datadonation.common.persistence.repository.metrics.KeySubmissionMetadataWithClientMetadataRepository;
import app.coronawarn.datadonation.common.persistence.repository.metrics.KeySubmissionMetadataWithUserMetadataRepository;
import app.coronawarn.datadonation.common.persistence.repository.metrics.SummarizedExposureWindowsWithUserMetadataRepository;
import app.coronawarn.datadonation.common.persistence.repository.metrics.TestResultMetadataRepository;
import app.coronawarn.datadonation.common.persistence.repository.metrics.UserMetadataRepository;
import app.coronawarn.datadonation.common.persistence.repository.ppac.android.SaltRepository;
import app.coronawarn.datadonation.common.persistence.service.AndroidIdService;
import app.coronawarn.datadonation.common.persistence.service.ElsOtpService;
import app.coronawarn.datadonation.common.persistence.service.OtpService;
import app.coronawarn.datadonation.common.persistence.service.SrsOtpService;
import app.coronawarn.datadonation.common.protocols.internal.ppdd.EDUSOneTimePassword;
import app.coronawarn.datadonation.common.protocols.internal.ppdd.EDUSOneTimePasswordRequestAndroid;
import app.coronawarn.datadonation.common.protocols.internal.ppdd.ELSOneTimePassword;
import app.coronawarn.datadonation.common.protocols.internal.ppdd.ELSOneTimePasswordRequestAndroid;
import app.coronawarn.datadonation.common.protocols.internal.ppdd.PPADataAndroid;
import app.coronawarn.datadonation.common.protocols.internal.ppdd.PPADataRequestAndroid;
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
import java.time.Instant;
import java.time.ZonedDateTime;
import java.util.Optional;
import java.util.Set;
import java.util.UUID;
import java.util.function.Consumer;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;
import org.mockito.ArgumentCaptor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.boot.test.mock.mockito.SpyBean;
import org.springframework.context.annotation.Import;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.test.context.junit.jupiter.SpringExtension;

@ExtendWith(SpringExtension.class)
@SpringBootTest(webEnvironment = RANDOM_PORT)
@Import(TestBeanConfig.class)
class AndroidControllerTest {

  @Nested
  class AttestationVerification {

    @Test
    void checkResponseStatusForBasicIntegrityViolation() throws Exception {
      ppacConfiguration.getAndroid().getDat().setRequireBasicIntegrity(true);
      final var response = executor.executePost(buildPayloadWithBasicIntegrityViolation());

      assertThat(response.getStatusCode()).isEqualTo(FORBIDDEN);
      assertThat(response.getBody().getErrorCode()).isEqualTo(BASIC_INTEGRITY_REQUIRED);
    }

    @Test
    void checkResponseStatusForBasicIntegrityViolationButDisabledCheck() throws Exception {
      ppacConfiguration.getAndroid().getDat().setRequireBasicIntegrity(false);
      final var response = executor.executePost(buildPayloadWithBasicIntegrityViolation());

      assertThat(response.getStatusCode()).isNotEqualTo(FORBIDDEN);
    }

    @Test
    void checkResponseStatusForCorrectEvaluationType() throws Exception {
      ppacConfiguration.getAndroid().getDat().setRequireEvaluationTypeHardwareBacked(true);
      ppacConfiguration.getAndroid().getDat().setRequireEvaluationTypeBasic(true);
      final var response = executor.executePost(buildPayloadWithEvaluationType("BASIC,HARDWARE_BACKED"));

      assertThat(response.getStatusCode()).isNotEqualTo(FORBIDDEN);
    }

    @Test
    void checkResponseStatusForCorrectIntegrityFlags() throws Exception {
      ppacConfiguration.getAndroid().getDat().setRequireCtsProfileMatch(true);
      ppacConfiguration.getAndroid().getDat().setRequireBasicIntegrity(true);
      final var response = executor.executePost(buildPayloadWithIntegrityFlagsChecked());

      assertThat(response.getStatusCode()).isNotEqualTo(FORBIDDEN);
    }

    @Test
    void checkResponseStatusForCtsProfileMatchViolation() throws Exception {
      ppacConfiguration.getAndroid().getDat().setRequireCtsProfileMatch(true);
      final var response = executor.executePost(buildPayloadWithCtsMatchViolation());

      assertThat(response.getStatusCode()).isEqualTo(FORBIDDEN);
      assertThat(response.getBody().getErrorCode()).isEqualTo(CTS_PROFILE_MATCH_REQUIRED);
    }

    @Test
    void checkResponseStatusForCtsProfileMatchViolationButDisabledCheck() throws Exception {
      ppacConfiguration.getAndroid().getDat().setRequireCtsProfileMatch(false);
      final var response = executor.executePost(buildPayloadWithCtsMatchViolation());

      assertThat(response.getStatusCode()).isNotEqualTo(FORBIDDEN);
    }

    @Test
    void checkResponseStatusForExpiredSalt() throws Exception {
      final var response = executor.executePost(buildPayloadWithExpiredSalt());

      assertThat(response.getStatusCode()).isEqualTo(FORBIDDEN);
      assertThat(response.getBody().getErrorCode()).isEqualTo(SALT_REDEEMED);
    }

    @Test
    void checkResponseStatusForFailedAttestationTimestampValidation() throws Exception {
      ppacConfiguration.getAndroid().setAttestationValidity(-100);
      final var response = executor.executePost(buildPayloadWithValidMetrics());

      assertThat(response.getStatusCode()).isEqualTo(FORBIDDEN);
      assertThat(response.getBody().getErrorCode()).isEqualTo(SALT_REDEEMED);
    }

    @Test
    void checkResponseStatusForInvalidApkCertificateDigests() throws Exception {
      ppacConfiguration.getAndroid()
          .setAllowedApkCertificateDigests(new String[] { "expected-to-be-9VLvUGV0Gkx24etruEBYikvAtqSQ9iY6rYuKhG" });
      // the JWS default values received below will not have the digest value expected above
      final var response = executor.executePost(buildPayloadWithValidMetrics());

      assertThat(response.getStatusCode()).isEqualTo(FORBIDDEN);
      assertThat(response.getBody().getErrorCode()).isEqualTo(APK_CERTIFICATE_MISMATCH);
    }

    @Test
    void checkResponseStatusForInvalidApkCertificateDigestsAndDisabledCheckConfiguration()
        throws Exception {
      ppacConfiguration.getAndroid().setDisableApkCertificateDigestsCheck(true);
      ppacConfiguration.getAndroid()
          .setAllowedApkCertificateDigests(new String[] { "expected-to-be-9VLvUGV0Gkx24etruEBYikvAtqSQ9iY6rYuKhG" });
      final var response = executor.executePost(buildPayloadWithValidMetrics());

      assertThat(response.getStatusCode()).isNotEqualTo(FORBIDDEN);
    }

    @Test
    void checkResponseStatusForInvalidApkPackageName() throws Exception {
      ppacConfiguration.getAndroid().setAllowedApkPackageNames(new String[] { "package.name.expected" });
      // the JWS default values received below will not have the same package name
      final var response = executor.executePost(buildPayloadWithValidMetrics());
      assertThat(response.getStatusCode()).isEqualTo(FORBIDDEN);
      assertThat(response.getBody().getErrorCode()).isEqualTo(APK_PACKAGE_NAME_MISMATCH);
    }

    @Test
    void checkResponseStatusForInvalidHostname() throws Exception {
      ppacConfiguration.getAndroid().setCertificateHostname("attest.google.com");
      final var response = executor.executePost(buildPayloadWithValidMetrics());

      assertThat(response.getStatusCode()).isEqualTo(FORBIDDEN);
    }

    @Test
    void checkResponseStatusForInvalidJwsParsing() throws Exception {
      final var response = executor.executePost(buildPayloadWithInvalidJwsParsing());

      assertThat(response.getStatusCode()).isEqualTo(UNAUTHORIZED);
      assertThat(response.getBody().getErrorCode()).isEqualTo(JWS_SIGNATURE_VERIFICATION_FAILED);
    }

    @Test
    void checkResponseStatusForInvalidNonces() throws Exception {
      ppacConfiguration.getAndroid().setDisableNonceCheck(false);
      var response = executor.executePost(buildPayloadWithEmptyNonce());
      assertThat(response.getStatusCode()).isEqualTo(BAD_REQUEST);

      response = executor.executePost(buildPayloadWithWrongNonce());
      assertThat(response.getStatusCode()).isEqualTo(FORBIDDEN);
      assertThat(response.getBody().getErrorCode()).isEqualTo(NONCE_MISMATCH);
    }

    @Test
    void checkResponseStatusForInvalidNoncesAndDisabledCheckConfiguration() throws Exception {
      var response = executor.executePost(buildPayloadWithEmptyNonce());
      assertThat(response.getStatusCode()).isNotEqualTo(BAD_REQUEST);

      response = executor.executePost(buildPayloadWithWrongNonce());
      assertThat(response.getStatusCode()).isNotEqualTo(FORBIDDEN);
    }

    @Test
    void checkResponseStatusForMissingJws() throws Exception {
      final var response = executor.executePost(buildPayloadWithMissingJws());
      assertThat(response.getStatusCode()).isEqualTo(BAD_REQUEST);
    }

    @Test
    void checkResponseStatusForMissingRequiredEvaluationTypesButChecksDisabled() throws Exception {
      ppacConfiguration.getAndroid().getDat().setRequireEvaluationTypeHardwareBacked(false);
      ppacConfiguration.getAndroid().getDat().setRequireEvaluationTypeBasic(false);
      final var response = executor.executePost(buildPayloadWithEvaluationType("OTHER,ANOTHER"));

      assertThat(response.getStatusCode()).isNotEqualTo(FORBIDDEN);
    }

    @Test
    void checkResponseStatusForMissingSalt() throws Exception {
      final var response = executor.executePost(buildPayloadWithMissingSalt());
      assertThat(response.getStatusCode()).isEqualTo(BAD_REQUEST);
    }

    @Test
    void checkResponseStatusForRequiredEvaluationTypeBasicViolation() throws Exception {
      ppacConfiguration.getAndroid().getDat().setRequireEvaluationTypeBasic(true);
      final var response = executor.executePost(buildPayloadWithEvaluationType("OTHER"));

      assertThat(response.getStatusCode()).isEqualTo(FORBIDDEN);
      assertThat(response.getBody().getErrorCode()).isEqualTo(EVALUATION_TYPE_BASIC_REQUIRED);
    }

    @Test
    void checkResponseStatusForRequiredEvaluationTypeHardwareBackedViolation() throws Exception {
      ppacConfiguration.getAndroid().getDat().setRequireEvaluationTypeHardwareBacked(true);
      final var response = executor.executePost(buildPayloadWithEvaluationType("OTHER"));

      assertThat(response.getStatusCode()).isEqualTo(FORBIDDEN);
      assertThat(response.getBody().getErrorCode()).isEqualTo(EVALUATION_TYPE_HARDWARE_BACKED_REQUIRED);
    }

    @Test
    void checkResponseStatusForValidNonce() throws Exception {
      ppacConfiguration.getAndroid().setDisableNonceCheck(false);
      final PPADataRequestAndroid test = buildPayloadWithValidNonce();
      final var response = executor.executePost(test);
      assertThat(response.getStatusCode()).isEqualTo(OK);
      assertDataWasSaved();
    }
  }

  @Nested
  class CreateOtpTests {

    private ELSOneTimePasswordRequestAndroid buildElsOtpPayloadWithValidNonce(final String password)
        throws Exception {
      final String jws = getJwsPayloadWithNonce("mFmhph4QE3GTKS0FRNw9UZCxXI7ue+7fGdqGENsfo4g=");
      return ELSOneTimePasswordRequestAndroid.newBuilder()
          .setAuthentication(newAuthenticationObject(jws, NOT_EXPIRED_SALT.getSalt()))
          .setPayload(ELSOneTimePassword.newBuilder().setOtp(password))
          .build();
    }

    private EDUSOneTimePasswordRequestAndroid buildOtpPayloadWithValidNonce(final String password) throws Exception {
      final String jws = getJwsPayloadWithNonce("mFmhph4QE3GTKS0FRNw9UZCxXI7ue+7fGdqGENsfo4g=");
      return EDUSOneTimePasswordRequestAndroid.newBuilder()
          .setAuthentication(newAuthenticationObject(jws, NOT_EXPIRED_SALT.getSalt()))
          .setPayload(EDUSOneTimePassword.newBuilder().setOtp(password))
          .build();
    }

    private SRSOneTimePasswordRequestAndroid buildSrsOtpPayloadWithValidNonce(final String password,
        final byte[] androidId) throws Exception {
      final String jws = getJwsPayloadWithNonce("mFmhph4QE3GTKS0FRNw9UZCxXI7ue+7fGdqGENsfo4g=");
      return SRSOneTimePasswordRequestAndroid.newBuilder()
          .setAuthentication(newAuthenticationObject(jws, NOT_EXPIRED_SALT.getSalt()))
          .setPayload(SRSOneTimePasswordRequestAndroid.SRSOneTimePassword.newBuilder().setOtp(password)
              .setAndroidId(ByteString.copyFrom(androidId)))
          .build();
    }

    @BeforeEach
    void setup() throws Exception {
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

    @Test
    void testLogOtpServiceIsCalled() throws Exception {
      ppacConfiguration.getAndroid().setCertificateHostname("localhost");
      final String password = "8ff92541-792f-4223-9970-bf90bf53b1a1";
      final ArgumentCaptor<ElsOneTimePassword> elsOtpCaptor = ArgumentCaptor.forClass(ElsOneTimePassword.class);
      final ArgumentCaptor<Integer> validityCaptor = ArgumentCaptor.forClass(Integer.class);

      final var response = executor.executeOtpPost(buildElsOtpPayloadWithValidNonce(password));

      assertThat(response.getStatusCode()).isEqualTo(OK);
      verify(elsOtpService, times(1)).createOtp(elsOtpCaptor.capture(), validityCaptor.capture());
      final ElsOneTimePassword cptOtp = elsOtpCaptor.getValue();

      final ZonedDateTime expectedExpirationTime = now(UTC)
          .plusHours(ppacConfiguration.getOtpValidityInHours());
      final ZonedDateTime actualExpirationTime = getZonedDateTimeFor(cptOtp.getExpirationTimestamp());

      assertThat(validityCaptor.getValue()).isEqualTo(ppacConfiguration.getOtpValidityInHours());
      assertThat(actualExpirationTime).isEqualToIgnoringSeconds(expectedExpirationTime);
      assertThat(cptOtp.getPassword()).isEqualTo(password);
      assertThat(cptOtp.getAndroidPpacBasicIntegrity()).isFalse();
      assertThat(cptOtp.getAndroidPpacCtsProfileMatch()).isFalse();
      assertThat(cptOtp.getAndroidPpacEvaluationTypeBasic()).isTrue();
      assertThat(cptOtp.getAndroidPpacEvaluationTypeHardwareBacked()).isFalse();
    }

    @Test
    void testOtpServiceIsCalled() throws Exception {
      ppacConfiguration.getAndroid().getOtp().setRequireBasicIntegrity(false);
      ppacConfiguration.getAndroid().getOtp().setRequireCtsProfileMatch(false);
      ppacConfiguration.getAndroid().getOtp().setRequireEvaluationTypeHardwareBacked(false);
      final String password = "8ff92541-792f-4223-9970-bf90bf53b1a1";
      final ArgumentCaptor<OneTimePassword> otpCaptor = ArgumentCaptor.forClass(OneTimePassword.class);
      final ArgumentCaptor<Integer> validityCaptor = ArgumentCaptor.forClass(Integer.class);

      final var response = executor.executeOtpPost(buildOtpPayloadWithValidNonce(password));

      assertThat(response.getStatusCode()).isEqualTo(OK);
      verify(otpService, times(1)).createOtp(otpCaptor.capture(), validityCaptor.capture());

      final OneTimePassword cptOtp = otpCaptor.getValue();

      final ZonedDateTime expectedExpirationTime = now(UTC).plusHours(ppacConfiguration.getOtpValidityInHours());
      final ZonedDateTime actualExpirationTime = getZonedDateTimeFor(cptOtp.getExpirationTimestamp());

      assertThat(validityCaptor.getValue()).isEqualTo(ppacConfiguration.getOtpValidityInHours());
      assertThat(actualExpirationTime).isEqualToIgnoringSeconds(expectedExpirationTime);
      assertThat(cptOtp.getPassword()).isEqualTo(password);
      assertThat(cptOtp.getAndroidPpacBasicIntegrity()).isFalse();
      assertThat(cptOtp.getAndroidPpacCtsProfileMatch()).isFalse();
      assertThat(cptOtp.getAndroidPpacEvaluationTypeBasic()).isTrue();
      assertThat(cptOtp.getAndroidPpacEvaluationTypeHardwareBacked()).isFalse();
    }

    @Test
    void testResponseIs400WhenOtpIsInvalidUuid() throws Exception {
      final String password = "invalid-uuid";

      final var response = executor.executeOtpPost(buildOtpPayloadWithValidNonce(password));

      assertThat(response.getStatusCode()).isEqualTo(BAD_REQUEST);
    }

    @Test
    void testSrsOtpServiceIsCalled() throws Exception {
      ppacConfiguration.getAndroid().getSrs().setRequireBasicIntegrity(false);
      ppacConfiguration.getAndroid().getSrs().setRequireCtsProfileMatch(false);
      ppacConfiguration.getAndroid().getSrs().setRequireEvaluationTypeHardwareBacked(false);
      ppacConfiguration.getAndroid().setCertificateHostname("localhost");
      final String password = newOtp();
      final ArgumentCaptor<SrsOneTimePassword> srs = ArgumentCaptor.forClass(SrsOneTimePassword.class);
      final ArgumentCaptor<Integer> validityCaptor = ArgumentCaptor.forClass(Integer.class);

      final byte[] androidId = new byte[12]; // for whatever reason, but the Android ID seems to be 12 bytes long
      SecureRandom.getInstanceStrong().nextBytes(androidId);
      var response = executor.executeOtpPost(buildSrsOtpPayloadWithValidNonce(password, androidId));

      final var pepper = ppacConfiguration.getAndroid().pepper();
      final var timeBetweenSubmissionsInDays = ppacConfiguration.getSrsTimeBetweenSubmissionsInDays();
      assertThat(response.getStatusCode()).isEqualTo(OK);
      verify(androidIdService, times(1)).upsertAndroidId(androidId, timeBetweenSubmissionsInDays, pepper);
      verify(androidIdService, times(1)).getAndroidIdByPrimaryKey(AndroidIdService.pepper(androidId, pepper));
      verify(srsOtpService, times(1)).createMinuteOtp(srs.capture(), validityCaptor.capture());
      final SrsOneTimePassword cptOtp = srs.getValue();

      final ZonedDateTime expectedExpirationTime = now(UTC).plusMinutes(ppacConfiguration.getSrsOtpValidityInMinutes());
      final ZonedDateTime actualExpirationTime = getZonedDateTimeFor(cptOtp.getExpirationTimestamp());

      assertThat(validityCaptor.getValue()).isEqualTo(ppacConfiguration.getSrsOtpValidityInMinutes());
      assertThat(actualExpirationTime).isEqualToIgnoringSeconds(expectedExpirationTime);
      assertThat(cptOtp.getPassword()).isEqualTo(password);
      assertThat(cptOtp.getAndroidPpacBasicIntegrity()).isFalse();
      assertThat(cptOtp.getAndroidPpacCtsProfileMatch()).isFalse();
      assertThat(cptOtp.getAndroidPpacEvaluationTypeBasic()).isTrue();
      assertThat(cptOtp.getAndroidPpacEvaluationTypeHardwareBacked()).isFalse();

      // a second request with same androidId is 'rate-limited' and server responds with 403
      response = executor.executeOtpPost(buildSrsOtpPayloadWithValidNonce(newOtp(), androidId), false);
      assertThat(response.getStatusCode()).isEqualTo(FORBIDDEN);
    }
  }

  @Nested
  class MetricsValidation {

    @ParameterizedTest
    @ValueSource(ints = { 2, 3 })
    void checkResponseIsBadRequestForInvalidExposureRiskPayloadCardinality(final Integer cardinality)
        throws Exception {
      final PPADataRequestAndroid payload = buildPayloadWithExposureRiskMetrics(getJwsPayloadValues(),
          NOT_EXPIRED_SALT.getSalt(), cardinality);
      checkResponseStatusForPayloadIsEqualTo(payload, BAD_REQUEST);
    }

    @ParameterizedTest
    @ValueSource(ints = { 4, 5 })
    void checkResponseIsBadRequestForInvalidExposureWindowPayloadCardinality(final Integer cardinality)
        throws Exception {
      ppacConfiguration.setMaxExposureWindowsToRejectSubmission(3);
      final PPADataRequestAndroid payload = buildPayloadWithExposureWindowMetrics(getJwsPayloadValues(),
          NOT_EXPIRED_SALT.getSalt(), cardinality);
      checkResponseStatusForPayloadIsEqualTo(payload, BAD_REQUEST);
    }

    @ParameterizedTest
    @ValueSource(ints = { 4, 5 })
    void checkResponseIsBadRequestForInvalidKeySubmissionCardinality(final Integer cardinality)
        throws Exception {
      final PPADataRequestAndroid payload = buildPayloadWithKeySubmission(getJwsPayloadValues(),
          NOT_EXPIRED_SALT.getSalt(), cardinality);
      checkResponseStatusForPayloadIsEqualTo(payload, BAD_REQUEST);
    }

    @ParameterizedTest
    @ValueSource(ints = { 0, 16, 20 })
    void checkResponseIsBadRequestForInvalidScanInstancesPayloadCardinality(final Integer cardinality)
        throws Exception {
      ppacConfiguration.setMaxExposureWindowsToRejectSubmission(3);
      final PPADataRequestAndroid payload = buildPayloadWithScanInstancesMetrics(getJwsPayloadValues(),
          NOT_EXPIRED_SALT.getSalt(), cardinality);
      checkResponseStatusForPayloadIsEqualTo(payload, BAD_REQUEST);
    }

    @ParameterizedTest
    @ValueSource(ints = { 3, 4 })
    void checkResponseIsBadRequestForInvalidTestResultsCardinality(final Integer cardinality)
        throws Exception {
      final PPADataRequestAndroid payload = buildPayloadWithTestResults(getJwsPayloadValues(),
          NOT_EXPIRED_SALT.getSalt(), cardinality);
      checkResponseStatusForPayloadIsEqualTo(payload, BAD_REQUEST);
    }

    @ParameterizedTest
    @ValueSource(ints = { 1, 10, 15 })
    void checkResponseIsNotBadRequestForInvalidScanInstancesPayloadCardinality(final Integer cardinality)
        throws Exception {
      ppacConfiguration.setMaxExposureWindowsToRejectSubmission(3);
      final PPADataRequestAndroid payload = buildPayloadWithScanInstancesMetrics(getJwsPayloadValues(),
          NOT_EXPIRED_SALT.getSalt(), cardinality);
      checkResponseStatusForPayloadIsNotEqualTo(payload, BAD_REQUEST);
    }

    @ParameterizedTest
    @ValueSource(ints = { 0, 1 })
    void checkResponseIsNotBadRequestForValidExposureRiskPayloadCardinality(final Integer cardinality)
        throws Exception {
      final PPADataRequestAndroid payload = buildPayloadWithExposureRiskMetrics(getJwsPayloadValues(),
          NOT_EXPIRED_SALT.getSalt(), cardinality);
      checkResponseStatusForPayloadIsNotEqualTo(payload, BAD_REQUEST);
    }

    @ParameterizedTest
    @ValueSource(ints = { 1, 2 })
    void checkResponseIsNotBadRequestForValidExposureWindowPayloadCardinality(final Integer cardinality)
        throws Exception {
      ppacConfiguration.setMaxExposureWindowsToRejectSubmission(3);
      final PPADataRequestAndroid payload = buildPayloadWithExposureWindowMetrics(getJwsPayloadValues(),
          NOT_EXPIRED_SALT.getSalt(), cardinality);
      checkResponseStatusForPayloadIsNotEqualTo(payload, BAD_REQUEST);
    }

    @ParameterizedTest
    @ValueSource(ints = { 0, 1, 2, 3 })
    void checkResponseIsNotBadRequestForValidKeySubmissionCardinality(final Integer cardinality)
        throws Exception {
      final PPADataRequestAndroid payload = buildPayloadWithKeySubmission(getJwsPayloadValues(),
          NOT_EXPIRED_SALT.getSalt(), cardinality);
      checkResponseStatusForPayloadIsNotEqualTo(payload, BAD_REQUEST);
    }

    @ParameterizedTest
    @ValueSource(ints = { 0, 1 })
    void checkResponseIsNotBadRequestForValidTestResultsCardinality(final Integer cardinality)
        throws Exception {
      final PPADataRequestAndroid payload = buildPayloadWithTestResults(getJwsPayloadValues(),
          NOT_EXPIRED_SALT.getSalt(), cardinality);
      checkResponseStatusForPayloadIsNotEqualTo(payload, BAD_REQUEST);
    }

    @SuppressWarnings("rawtypes")
    void checkResponseStatusForPayload(final PPADataRequestAndroid invalidPayload,
        final Consumer<ResponseEntity> assertionCall) throws Exception {
      final var response = executor.executePost(invalidPayload);
      assertionCall.accept(response);
    }

    /**
     * @param invalidPayload Invalid payload to test
     * @param statusToCheck  Http status that is expected to be in the response.
     */
    void checkResponseStatusForPayloadIsEqualTo(final PPADataRequestAndroid invalidPayload,
        final HttpStatus statusToCheck) throws Exception {
      checkResponseStatusForPayload(invalidPayload,
          response -> assertThat(response.getStatusCode()).isEqualTo(statusToCheck));
    }

    /**
     * @param invalidPayload Invalid payload to test
     * @param statusToCheck  Http status that is expected not to be in the response.
     */
    void checkResponseStatusForPayloadIsNotEqualTo(final PPADataRequestAndroid invalidPayload,
        final HttpStatus statusToCheck) throws Exception {
      checkResponseStatusForPayload(invalidPayload,
          response -> assertThat(response.getStatusCode()).isNotEqualTo(statusToCheck));
    }

    @Test
    void checkResponseStatusIsOkForInvalidMetrics() throws Exception {
      final var response = executor
          .executePost(buildPayloadWithInvalidExposureRiskDate());
      assertThat(response.getStatusCode()).isEqualTo(BAD_REQUEST);
    }

    @Test
    void checkResponseStatusIsOkForValidMetrics() throws Exception {
      final var response = executor.executePost(buildPayloadWithValidMetrics());
      assertThat(response.getStatusCode()).isEqualTo(OK);
      assertDataWasSaved();
    }
  }

  static final SaltData EXPIRED_SALT = new SaltData("abc", Instant.now().minus(5, HOURS).toEpochMilli());

  static final SaltData NOT_EXPIRED_SALT = new SaltData("def", Instant.now().minus(1, HOURS).toEpochMilli());

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
  private ExposureRiskMetadataRepository exposureRiskMetadataRepo;

  @Autowired
  private ExposureWindowRepository exposureWindowRepo;

  @Autowired
  private TestResultMetadataRepository testResultRepo;

  @Autowired
  private KeySubmissionMetadataWithUserMetadataRepository keySubmissionWithUserMetadataRepo;

  @Autowired
  private KeySubmissionMetadataWithClientMetadataRepository keySubmissionWithClientMetadataRepo;

  @Autowired
  private UserMetadataRepository userMetadataRepo;

  @Autowired
  private ClientMetadataRepository clientMetadataRepo;

  @Autowired
  private SummarizedExposureWindowsWithUserMetadataRepository summarizedExposureWindowsWithUserMetadataRepo;

  @Autowired
  private ExposureWindowTestResultsRepository exposureWindowTestResultsRepo;

  @Autowired
  private RequestExecutor executor;

  @SpyBean
  private ElsOtpService elsOtpService;

  @SpyBean
  private SrsOtpService srsOtpService;

  @SpyBean
  private AndroidIdService androidIdService;

  private void assertDataWasSaved() {
    assertThat(exposureRiskMetadataRepo.findAll()).isNotEmpty();
    assertThat(exposureWindowRepo.findAll()).isNotEmpty();
    assertThat(testResultRepo.findAll()).isNotEmpty();
    assertThat(keySubmissionWithUserMetadataRepo.findAll()).isNotEmpty();
    assertThat(keySubmissionWithClientMetadataRepo.findAll()).isNotEmpty();
    assertThat(userMetadataRepo.findAll()).isNotEmpty();
    assertThat(clientMetadataRepo.findAll()).isNotEmpty();
    assertThat(summarizedExposureWindowsWithUserMetadataRepo.findAll()).isNotEmpty();
    assertThat(exposureWindowTestResultsRepo.findAll()).isNotEmpty();
  }

  private PPADataRequestAndroid buildPayloadWithBasicIntegrityViolation() throws Exception {
    return PPADataRequestAndroid.newBuilder()
        .setAuthentication(newAuthenticationObject(getJwsPayloadWithBasicIntegrityViolation(),
            NOT_EXPIRED_SALT.getSalt()))
        .setPayload(getValidAndroidDataPayload())
        .build();
  }

  private PPADataRequestAndroid buildPayloadWithCtsMatchViolation() throws Exception {
    return PPADataRequestAndroid.newBuilder().setAuthentication(
        newAuthenticationObject(getJwsPayloadWithCtsMatchViolation(), NOT_EXPIRED_SALT.getSalt()))
        .setPayload(getValidAndroidDataPayload())
        .build();
  }

  private PPADataRequestAndroid buildPayloadWithEmptyNonce() throws Exception {
    final String jws = getJwsPayloadWithNonce("");
    return PPADataRequestAndroid.newBuilder()
        .setAuthentication(newAuthenticationObject(jws, NOT_EXPIRED_SALT.getSalt()))
        .setPayload(getValidAndroidDataPayload())
        .build();
  }

  private PPADataRequestAndroid buildPayloadWithEvaluationType(final String evTypeUnderTest)
      throws Exception {
    return PPADataRequestAndroid.newBuilder()
        .setAuthentication(
            newAuthenticationObject(getJwsPayloadWithEvaluationType(evTypeUnderTest), NOT_EXPIRED_SALT.getSalt()))
        .setPayload(getValidAndroidDataPayload())
        .build();
  }

  private PPADataRequestAndroid buildPayloadWithExpiredSalt() throws Exception {
    final String jws = getJwsPayloadValues();
    return PPADataRequestAndroid.newBuilder()
        .setAuthentication(newAuthenticationObject(jws, EXPIRED_SALT.getSalt()))
        .setPayload(getValidAndroidDataPayload())
        .build();
  }

  private PPADataRequestAndroid buildPayloadWithIntegrityFlagsChecked() throws Exception {
    return PPADataRequestAndroid.newBuilder().setAuthentication(
        newAuthenticationObject(getJwsPayloadWithIntegrityFlagsChecked(), NOT_EXPIRED_SALT.getSalt()))
        .setPayload(getValidAndroidDataPayload())
        .build();
  }

  private PPADataRequestAndroid buildPayloadWithInvalidExposureRiskDate() throws Exception {
    final String jws = getJwsPayloadValues();
    return PPADataRequestAndroid.newBuilder()
        .setAuthentication(newAuthenticationObject(jws, NOT_EXPIRED_SALT.getSalt()))
        .setPayload(PPADataAndroid.newBuilder()
            .addAllExposureRiskMetadataSet(Set.of(getInvalidExposureRiskMetadata()))
            .addAllNewExposureWindows(Set.of(getValidExposureWindow()))
            .addAllTestResultMetadataSet(Set.of(getValidTestResultMetadata()))
            .addAllKeySubmissionMetadataSet(Set.of(getValidKeySubmissionMetadata()))
            .setClientMetadata(getValidClientMetadata())
            .setUserMetadata(getValidUserMetadata()))
        .build();
  }

  private PPADataRequestAndroid buildPayloadWithInvalidJwsParsing() throws Exception {
    return PPADataRequestAndroid.newBuilder()
        .setAuthentication(newAuthenticationObject("RANDOM STRING", NOT_EXPIRED_SALT.getSalt()))
        .setPayload(getValidAndroidDataPayload())
        .build();
  }

  private PPADataRequestAndroid buildPayloadWithMissingJws() throws Exception {
    return PPADataRequestAndroid.newBuilder()
        .setAuthentication(newAuthenticationObject("", NOT_EXPIRED_SALT.getSalt()))
        .setPayload(getValidAndroidDataPayload())
        .build();
  }

  private PPADataRequestAndroid buildPayloadWithMissingSalt() throws Exception {
    final String jws = getJwsPayloadValues();
    return PPADataRequestAndroid.newBuilder()
        .setAuthentication(newAuthenticationObject(jws, ""))
        .setPayload(getValidAndroidDataPayload())
        .build();
  }

  private PPADataRequestAndroid buildPayloadWithValidMetrics() throws Exception {
    final String jws = getJwsPayloadValues();
    return PPADataRequestAndroid.newBuilder()
        .setAuthentication(newAuthenticationObject(jws, NOT_EXPIRED_SALT.getSalt()))
        .setPayload(PPADataAndroid.newBuilder()
            .addAllExposureRiskMetadataSet(Set.of(getValidExposureRiskMetadata()))
            .addAllNewExposureWindows(Set.of(getValidExposureWindow()))
            .addAllTestResultMetadataSet(Set.of(getValidTestResultMetadata()))
            .addAllKeySubmissionMetadataSet(Set.of(getValidKeySubmissionMetadata()))
            .setClientMetadata(getValidClientMetadata())
            .setUserMetadata(getValidUserMetadata()))
        .build();
  }

  private PPADataRequestAndroid buildPayloadWithValidNonce() throws Exception {
    final String jws = getJwsPayloadWithNonce("ct40scJZoPw673V4IwXKvoQE9ZrgeI7P/5Ak7sH3Z+U=");
    return PPADataRequestAndroid.newBuilder()
        .setAuthentication(newAuthenticationObject(jws, NOT_EXPIRED_SALT.getSalt()))
        .setPayload(getValidAndroidDataPayload())
        .build();
  }

  private PPADataRequestAndroid buildPayloadWithWrongNonce() throws Exception {
    final String jws = getJwsPayloadWithNonce("AAAA=");
    return PPADataRequestAndroid.newBuilder()
        .setAuthentication(newAuthenticationObject(jws, NOT_EXPIRED_SALT.getSalt()))
        .setPayload(getValidAndroidDataPayload())
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
  }

  @AfterEach
  void tearDown() throws GeneralSecurityException {
    saltRepository.deleteAll();
  }

  public static String newOtp() {
    return UUID.randomUUID().toString();
  }
}
