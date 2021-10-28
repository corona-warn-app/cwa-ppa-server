package app.coronawarn.datadonation.services.ppac.android.controller;

import static app.coronawarn.datadonation.services.ppac.android.testdata.TestData.getJwsPayloadValues;
import static app.coronawarn.datadonation.services.ppac.android.testdata.TestData.getJwsPayloadWithBasicIntegrityViolation;
import static app.coronawarn.datadonation.services.ppac.android.testdata.TestData.getJwsPayloadWithCtsMatchViolation;
import static app.coronawarn.datadonation.services.ppac.android.testdata.TestData.getJwsPayloadWithEvaluationType;
import static app.coronawarn.datadonation.services.ppac.android.testdata.TestData.getJwsPayloadWithIntegrityFlagsChecked;
import static app.coronawarn.datadonation.services.ppac.android.testdata.TestData.getJwsPayloadWithNonce;
import static app.coronawarn.datadonation.services.ppac.android.testdata.TestData.getValidAndroidDataPayload;
import static app.coronawarn.datadonation.services.ppac.android.testdata.TestData.newAuthenticationObject;
import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.http.HttpStatus.BAD_REQUEST;
import static org.springframework.http.HttpStatus.FORBIDDEN;
import static org.springframework.http.HttpStatus.NO_CONTENT;
import static org.springframework.http.HttpStatus.OK;
import static org.springframework.http.HttpStatus.UNAUTHORIZED;

import app.coronawarn.datadonation.common.persistence.domain.ElsOneTimePassword;
import app.coronawarn.datadonation.common.persistence.domain.OneTimePassword;
import app.coronawarn.datadonation.common.persistence.domain.ppac.android.Salt;
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
import app.coronawarn.datadonation.common.persistence.service.ElsOtpService;
import app.coronawarn.datadonation.common.persistence.service.OtpCreationResponse;
import app.coronawarn.datadonation.common.persistence.service.OtpService;
import app.coronawarn.datadonation.common.protocols.internal.ppdd.EDUSOneTimePassword;
import app.coronawarn.datadonation.common.protocols.internal.ppdd.EDUSOneTimePasswordRequestAndroid;
import app.coronawarn.datadonation.common.protocols.internal.ppdd.ELSOneTimePassword;
import app.coronawarn.datadonation.common.protocols.internal.ppdd.ELSOneTimePasswordRequestAndroid;
import app.coronawarn.datadonation.common.protocols.internal.ppdd.PPADataAndroid;
import app.coronawarn.datadonation.common.protocols.internal.ppdd.PPADataRequestAndroid;
import app.coronawarn.datadonation.common.utils.TimeUtils;
import app.coronawarn.datadonation.services.ppac.android.attestation.signature.SignatureVerificationStrategy;
import app.coronawarn.datadonation.services.ppac.android.testdata.JwsGenerationUtil;
import app.coronawarn.datadonation.services.ppac.android.testdata.TestData;
import app.coronawarn.datadonation.services.ppac.android.testdata.TestData.CardinalityTestData;
import app.coronawarn.datadonation.services.ppac.commons.web.DataSubmissionResponse;
import app.coronawarn.datadonation.services.ppac.config.PpacConfiguration;
import app.coronawarn.datadonation.services.ppac.config.PpacConfiguration.Android.Dat;
import app.coronawarn.datadonation.services.ppac.config.TestBeanConfig;
import app.coronawarn.datadonation.services.ppac.logging.PpacErrorCode;
import java.io.IOException;
import java.security.GeneralSecurityException;
import java.time.Instant;
import java.time.ZoneOffset;
import java.time.ZonedDateTime;
import java.time.temporal.ChronoUnit;
import java.util.Optional;
import java.util.Set;
import java.util.function.Consumer;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Disabled;
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
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@Import(TestBeanConfig.class)
class AndroidControllerTest {

  private static final Salt EXPIRED_SALT =
      new Salt("abc", Instant.now().minus(5, ChronoUnit.HOURS).toEpochMilli());

  private static final Salt NOT_EXPIRED_SALT =
      new Salt("def", Instant.now().minus(1, ChronoUnit.HOURS).toEpochMilli());

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

  @BeforeEach
  void setup() throws GeneralSecurityException {
    prepareDefaultAppConfiguration();
  }

  @AfterEach
  void tearDown() throws GeneralSecurityException {
    saltRepository.deleteAll();
  }

  @SpyBean
  private ElsOtpService elsOtpService;

  @Nested
  class AttestationVerification {

    @Test
    @Disabled("Nonce keeps changing whenever tests are re-run due to unkown variable in the calculation."
        + "Test is disabled until we can figure out the variable component.")
    void checkResponseStatusForValidNonce() throws IOException {
      ppacConfiguration.getAndroid().setDisableNonceCheck(false);
      ResponseEntity<DataSubmissionResponse> actResponse = executor.executePost(buildPayloadWithValidNonce());
      assertThat(actResponse.getStatusCode()).isEqualTo(NO_CONTENT);
      assertDataWasSaved();
    }

    @Test
    void checkResponseStatusForInvalidNonces() throws IOException {
      ppacConfiguration.getAndroid().setDisableNonceCheck(false);
      ResponseEntity<DataSubmissionResponse> actResponse = executor.executePost(buildPayloadWithEmptyNonce());
      assertThat(actResponse.getStatusCode()).isEqualTo(BAD_REQUEST);

      actResponse = executor.executePost(buildPayloadWithWrongNonce());
      assertThat(actResponse.getStatusCode()).isEqualTo(FORBIDDEN);
      assertThat(actResponse.getBody().getErrorCode()).isEqualTo(PpacErrorCode.NONCE_MISMATCH);
    }

    @Test
    void checkResponseStatusForInvalidNoncesAndDisabledCheckConfiguration() throws IOException {
      ResponseEntity<DataSubmissionResponse> actResponse =
          executor.executePost(buildPayloadWithEmptyNonce());
      assertThat(actResponse.getStatusCode()).isNotEqualTo(BAD_REQUEST);

      actResponse = executor.executePost(buildPayloadWithWrongNonce());
      assertThat(actResponse.getStatusCode()).isNotEqualTo(FORBIDDEN);
    }

    @Test
    void checkResponseStatusForInvalidApkPackageName() throws IOException {
      ppacConfiguration.getAndroid()
          .setAllowedApkPackageNames(new String[] {"package.name.expected"});
      // the JWS default values received below will not have the same package name
      ResponseEntity<DataSubmissionResponse> actResponse =
          executor.executePost(buildPayloadWithValidMetrics());
      assertThat(actResponse.getStatusCode()).isEqualTo(FORBIDDEN);
      assertThat(actResponse.getBody().getErrorCode()).isEqualTo(PpacErrorCode.APK_PACKAGE_NAME_MISMATCH);
    }

    @Test
    void checkResponseStatusForInvalidApkCertificateDigests() throws IOException {
      ppacConfiguration.getAndroid().setAllowedApkCertificateDigests(
          new String[]{"expected-to-be-9VLvUGV0Gkx24etruEBYikvAtqSQ9iY6rYuKhG"});
      //the JWS default values received below will not have the digest value expected above
      ResponseEntity<DataSubmissionResponse> actResponse = executor.executePost(buildPayloadWithValidMetrics());

      assertThat(actResponse.getStatusCode()).isEqualTo(FORBIDDEN);
      assertThat(actResponse.getBody().getErrorCode()).isEqualTo(PpacErrorCode.APK_CERTIFICATE_MISMATCH);
    }

    @Test
    void checkResponseStatusForInvalidApkCertificateDigestsAndDisabledCheckConfiguration()
        throws IOException {
      ppacConfiguration.getAndroid().setDisableApkCertificateDigestsCheck(true);
      ppacConfiguration.getAndroid().setAllowedApkCertificateDigests(
          new String[] {"expected-to-be-9VLvUGV0Gkx24etruEBYikvAtqSQ9iY6rYuKhG"});
      ResponseEntity<DataSubmissionResponse> actResponse =
          executor.executePost(buildPayloadWithValidMetrics());

      assertThat(actResponse.getStatusCode()).isNotEqualTo(FORBIDDEN);
    }

    @Test
    void checkResponseStatusForFailedAttestationTimestampValidation() throws IOException {
      ppacConfiguration.getAndroid().setAttestationValidity(-100);
      ResponseEntity<DataSubmissionResponse> actResponse =
          executor.executePost(buildPayloadWithValidMetrics());

      assertThat(actResponse.getStatusCode()).isEqualTo(FORBIDDEN);
      assertThat(actResponse.getBody().getErrorCode()).isEqualTo(PpacErrorCode.SALT_REDEEMED);
    }

    @Test
    void checkResponseStatusForInvalidHostname() throws IOException {
      ppacConfiguration.getAndroid().setCertificateHostname("attest.google.com");
      ResponseEntity<DataSubmissionResponse> actResponse =
          executor.executePost(buildPayloadWithValidMetrics());

      assertThat(actResponse.getStatusCode()).isEqualTo(FORBIDDEN);
    }

    @Test
    void checkResponseStatusForMissingSalt() throws IOException {
      ResponseEntity<DataSubmissionResponse> actResponse =
          executor.executePost(buildPayloadWithMissingSalt());
      assertThat(actResponse.getStatusCode()).isEqualTo(BAD_REQUEST);
    }

    @Test
    void checkResponseStatusForExpiredSalt() throws IOException {
      ResponseEntity<DataSubmissionResponse> actResponse =
          executor.executePost(buildPayloadWithExpiredSalt());
      assertThat(actResponse.getStatusCode()).isEqualTo(FORBIDDEN);
      assertThat(actResponse.getBody().getErrorCode()).isEqualTo(PpacErrorCode.SALT_REDEEMED);
    }

    @Test
    void checkResponseStatusForMissingJws() throws IOException {
      ResponseEntity<DataSubmissionResponse> actResponse =
          executor.executePost(buildPayloadWithMissingJws());
      assertThat(actResponse.getStatusCode()).isEqualTo(BAD_REQUEST);
    }

    @Test
    void checkResponseStatusForInvalidJwsParsing() throws IOException {
      ResponseEntity<DataSubmissionResponse> actResponse =
          executor.executePost(buildPayloadWithInvalidJwsParsing());
      assertThat(actResponse.getStatusCode()).isEqualTo(UNAUTHORIZED);
      assertThat(actResponse.getBody().getErrorCode()).isEqualTo(PpacErrorCode.JWS_SIGNATURE_VERIFICATION_FAILED);
    }

    @Test
    void checkResponseStatusForBasicIntegrityViolation() throws IOException {
      ppacConfiguration.getAndroid().getDat().setRequireBasicIntegrity(true);
      ResponseEntity<DataSubmissionResponse> actResponse =
          executor.executePost(buildPayloadWithBasicIntegrityViolation());

      assertThat(actResponse.getStatusCode()).isEqualTo(FORBIDDEN);
      assertThat(actResponse.getBody().getErrorCode()).isEqualTo(PpacErrorCode.BASIC_INTEGRITY_REQUIRED);
    }

    @Test
    void checkResponseStatusForBasicIntegrityViolationButDisabledCheck() throws IOException {
      ppacConfiguration.getAndroid().getDat().setRequireBasicIntegrity(false);
      ResponseEntity<DataSubmissionResponse> actResponse =
          executor.executePost(buildPayloadWithBasicIntegrityViolation());

      assertThat(actResponse.getStatusCode()).isNotEqualTo(FORBIDDEN);
    }

    @Test
    void checkResponseStatusForCtsProfileMatchViolation() throws IOException {
      ppacConfiguration.getAndroid().getDat().setRequireCtsProfileMatch(true);
      ResponseEntity<DataSubmissionResponse> actResponse =
          executor.executePost(buildPayloadWithCtsMatchViolation());

      assertThat(actResponse.getStatusCode()).isEqualTo(FORBIDDEN);
      assertThat(actResponse.getBody().getErrorCode()).isEqualTo(PpacErrorCode.CTS_PROFILE_MATCH_REQUIRED);
    }

    @Test
    void checkResponseStatusForCtsProfileMatchViolationButDisabledCheck() throws IOException {
      ppacConfiguration.getAndroid().getDat().setRequireCtsProfileMatch(false);
      ResponseEntity<DataSubmissionResponse> actResponse =
          executor.executePost(buildPayloadWithCtsMatchViolation());

      assertThat(actResponse.getStatusCode()).isNotEqualTo(FORBIDDEN);
    }

    @Test
    void checkResponseStatusForCorrectIntegrityFlags() throws IOException {
      ppacConfiguration.getAndroid().getDat().setRequireCtsProfileMatch(true);
      ppacConfiguration.getAndroid().getDat().setRequireBasicIntegrity(true);
      ResponseEntity<DataSubmissionResponse> actResponse =
          executor.executePost(buildPayloadWithIntegrityFlagsChecked());

      assertThat(actResponse.getStatusCode()).isNotEqualTo(FORBIDDEN);
    }

    @Test
    void checkResponseStatusForRequiredEvaluationTypeBasicViolation() throws IOException {
      ppacConfiguration.getAndroid().getDat().setRequireEvaluationTypeBasic(true);
      ResponseEntity<DataSubmissionResponse> actResponse =
          executor.executePost(buildPayloadWithEvaluationType("OTHER"));

      assertThat(actResponse.getStatusCode()).isEqualTo(FORBIDDEN);
      assertThat(actResponse.getBody().getErrorCode()).isEqualTo(PpacErrorCode.EVALUATION_TYPE_BASIC_REQUIRED);
    }

    @Test
    void checkResponseStatusForRequiredEvaluationTypeHardwareBackedViolation() throws IOException {
      ppacConfiguration.getAndroid().getDat().setRequireEvaluationTypeHardwareBacked(true);
      ResponseEntity<DataSubmissionResponse> actResponse =
          executor.executePost(buildPayloadWithEvaluationType("OTHER"));

      assertThat(actResponse.getStatusCode()).isEqualTo(FORBIDDEN);
      assertThat(actResponse.getBody().getErrorCode()).isEqualTo(PpacErrorCode.EVALUATION_TYPE_HARDWARE_BACKED_REQUIRED);
    }

    @Test
    void checkResponseStatusForMissingRequiredEvaluationTypesButChecksDisabled() throws IOException {
      ppacConfiguration.getAndroid().getDat().setRequireEvaluationTypeHardwareBacked(false);
      ppacConfiguration.getAndroid().getDat().setRequireEvaluationTypeBasic(false);
      ResponseEntity<DataSubmissionResponse> actResponse =
          executor.executePost(buildPayloadWithEvaluationType("OTHER,ANOTHER"));

      assertThat(actResponse.getStatusCode()).isNotEqualTo(FORBIDDEN);
    }

    @Test
    void checkResponseStatusForCorrectEvaluationType() throws IOException {
      ppacConfiguration.getAndroid().getDat().setRequireEvaluationTypeHardwareBacked(true);
      ppacConfiguration.getAndroid().getDat().setRequireEvaluationTypeBasic(true);
      ResponseEntity<DataSubmissionResponse> actResponse =
          executor.executePost(buildPayloadWithEvaluationType("BASIC,HARDWARE_BACKED"));

      assertThat(actResponse.getStatusCode()).isNotEqualTo(FORBIDDEN);
    }
  }

  @Nested
  class MetricsValidation {

    @ParameterizedTest
    @ValueSource(ints = {0, 1})
    void checkResponseIsNotBadRequestForValidExposureRiskPayloadCardinality(Integer cardinality)
        throws IOException {
      PPADataRequestAndroid payload = CardinalityTestData.buildPayloadWithExposureRiskMetrics(
          getJwsPayloadValues(), NOT_EXPIRED_SALT.getSalt(), cardinality);
      checkResponseStatusForPayloadIsNotEqualTo(payload, BAD_REQUEST);
    }

    @ParameterizedTest
    @ValueSource(ints = {2, 3})
    void checkResponseIsBadRequestForInvalidExposureRiskPayloadCardinality(Integer cardinality)
        throws IOException {
      PPADataRequestAndroid payload = CardinalityTestData.buildPayloadWithExposureRiskMetrics(
          getJwsPayloadValues(), NOT_EXPIRED_SALT.getSalt(), cardinality);
      checkResponseStatusForPayloadIsEqualTo(payload, BAD_REQUEST);
    }

    @ParameterizedTest
    @ValueSource(ints = {4,5})
    void checkResponseIsBadRequestForInvalidExposureWindowPayloadCardinality(Integer cardinality) throws IOException {
      ppacConfiguration.setMaxExposureWindowsToRejectSubmission(3);
      PPADataRequestAndroid payload = CardinalityTestData.buildPayloadWithExposureWindowMetrics(
          getJwsPayloadValues(), NOT_EXPIRED_SALT.getSalt(), cardinality);
      checkResponseStatusForPayloadIsEqualTo(payload, BAD_REQUEST);
    }

    @ParameterizedTest
    @ValueSource(ints = {1,2})
    void checkResponseIsNotBadRequestForValidExposureWindowPayloadCardinality(Integer cardinality) throws IOException {
      ppacConfiguration.setMaxExposureWindowsToRejectSubmission(3);
      PPADataRequestAndroid payload = CardinalityTestData.buildPayloadWithExposureWindowMetrics(
          getJwsPayloadValues(), NOT_EXPIRED_SALT.getSalt(), cardinality);
      checkResponseStatusForPayloadIsNotEqualTo(payload, BAD_REQUEST);
    }

    @ParameterizedTest
    @ValueSource(ints = {0, 16, 20})
    void checkResponseIsBadRequestForInvalidScanInstancesPayloadCardinality(Integer cardinality)
        throws IOException {
      ppacConfiguration.setMaxExposureWindowsToRejectSubmission(3);
      PPADataRequestAndroid payload = CardinalityTestData.buildPayloadWithScanInstancesMetrics(
          getJwsPayloadValues(), NOT_EXPIRED_SALT.getSalt(), cardinality);
      checkResponseStatusForPayloadIsEqualTo(payload, BAD_REQUEST);
    }

    @ParameterizedTest
    @ValueSource(ints = {1, 10, 15})
    void checkResponseIsNotBadRequestForInvalidScanInstancesPayloadCardinality(Integer cardinality)
        throws IOException {
      ppacConfiguration.setMaxExposureWindowsToRejectSubmission(3);
      PPADataRequestAndroid payload = CardinalityTestData.buildPayloadWithScanInstancesMetrics(
          getJwsPayloadValues(), NOT_EXPIRED_SALT.getSalt(), cardinality);
      checkResponseStatusForPayloadIsNotEqualTo(payload, BAD_REQUEST);
    }

    @ParameterizedTest
    @ValueSource(ints = {3, 4})
    void checkResponseIsBadRequestForInvalidTestResultsCardinality(Integer cardinality)
        throws IOException {
      PPADataRequestAndroid payload = CardinalityTestData.buildPayloadWithTestResults(
          getJwsPayloadValues(), NOT_EXPIRED_SALT.getSalt(), cardinality);
      checkResponseStatusForPayloadIsEqualTo(payload, BAD_REQUEST);
    }

    @ParameterizedTest
    @ValueSource(ints = {0, 1})
    void checkResponseIsNotBadRequestForValidTestResultsCardinality(Integer cardinality)
        throws IOException {
      PPADataRequestAndroid payload = CardinalityTestData.buildPayloadWithTestResults(
          getJwsPayloadValues(), NOT_EXPIRED_SALT.getSalt(), cardinality);
      checkResponseStatusForPayloadIsNotEqualTo(payload, BAD_REQUEST);
    }

    @ParameterizedTest
    @ValueSource(ints = {3, 4})
    void checkResponseIsBadRequestForInvalidKeySubmissionCardinality(Integer cardinality)
        throws IOException {
      PPADataRequestAndroid payload = CardinalityTestData.buildPayloadWithKeySubmission(
          getJwsPayloadValues(), NOT_EXPIRED_SALT.getSalt(), cardinality);
      checkResponseStatusForPayloadIsEqualTo(payload, BAD_REQUEST);
    }

    @ParameterizedTest
    @ValueSource(ints = {0, 1})
    void checkResponseIsNotBadRequestForValidKeySubmissionCardinality(Integer cardinality)
        throws IOException {
      PPADataRequestAndroid payload = CardinalityTestData.buildPayloadWithKeySubmission(
          getJwsPayloadValues(), NOT_EXPIRED_SALT.getSalt(), cardinality);
      checkResponseStatusForPayloadIsNotEqualTo(payload, BAD_REQUEST);
    }

    @Test
    void checkResponseStatusIsOkForValidMetrics() throws IOException {
      ResponseEntity<DataSubmissionResponse> actResponse = executor.executePost(buildPayloadWithValidMetrics());
      assertThat(actResponse.getStatusCode()).isEqualTo(NO_CONTENT);
      assertDataWasSaved();
    }

    @Test
    void checkResponseStatusIsOkForInvalidMetrics() throws IOException {
      ResponseEntity<DataSubmissionResponse> actResponse = executor.executePost(buildPayloadWithInvalidExposureRiskDate());
      assertThat(actResponse.getStatusCode()).isEqualTo(BAD_REQUEST);
    }

    /**
     * @param invalidPayload  Invalid payload to test
     * @param statusToCheck Http status that is expected to be in the response.
     */
    void checkResponseStatusForPayloadIsEqualTo(PPADataRequestAndroid invalidPayload,
        HttpStatus statusToCheck) throws IOException {
      checkResponseStatusForPayload(invalidPayload,
          (actResponse) -> assertThat(actResponse.getStatusCode()).isEqualTo(statusToCheck));
    }

    /**
     * @param invalidPayload  Invalid payload to test
     * @param statusToCheck Http status that is expected not to be in the response.
     */
    void checkResponseStatusForPayloadIsNotEqualTo(PPADataRequestAndroid invalidPayload,
         HttpStatus statusToCheck) throws IOException {
      checkResponseStatusForPayload(invalidPayload,
          (actResponse) -> assertThat(actResponse.getStatusCode()).isNotEqualTo(statusToCheck));
    }

    @SuppressWarnings("rawtypes")
    void checkResponseStatusForPayload(PPADataRequestAndroid invalidPayload,
        Consumer<ResponseEntity> assertionCall) throws IOException {
      ResponseEntity<DataSubmissionResponse> actResponse = executor.executePost(invalidPayload);
      assertionCall.accept(actResponse);
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
      Dat dat = new Dat();
      dat.setRequireCtsProfileMatch(false);
      dat.setRequireBasicIntegrity(false);
      dat.setRequireEvaluationTypeBasic(false);
      dat.setRequireEvaluationTypeHardwareBacked(false);
      ppacConfiguration.getAndroid().setDat(dat);

      when(saltRepo.findById(any())).then((ans) -> Optional.of(NOT_EXPIRED_SALT));
      when(signatureVerificationStrategy.verifySignature(any())).thenReturn(JwsGenerationUtil.getTestCertificate());
    }

    @Test
    void testOtpServiceIsCalled() throws IOException {
      ppacConfiguration.getAndroid().getOtp().setRequireBasicIntegrity(false);
      ppacConfiguration.getAndroid().getOtp().setRequireCtsProfileMatch(false);
      ppacConfiguration.getAndroid().getOtp().setRequireEvaluationTypeHardwareBacked(false);
      String password = "8ff92541-792f-4223-9970-bf90bf53b1a1";
      ArgumentCaptor<OneTimePassword> otpCaptor = ArgumentCaptor.forClass(OneTimePassword.class);
      ArgumentCaptor<Integer> validityCaptor = ArgumentCaptor.forClass(Integer.class);

      ResponseEntity<OtpCreationResponse> actResponse =
          executor.executeOtpPost(buildOtpPayloadWithValidNonce(password));

      assertThat(actResponse.getStatusCode()).isEqualTo(OK);
      verify(otpService, times(1)).createOtp(otpCaptor.capture(), validityCaptor.capture());

      OneTimePassword cptOtp = otpCaptor.getValue();

      ZonedDateTime expectedExpirationTime = ZonedDateTime.now(ZoneOffset.UTC)
          .plusHours(ppacConfiguration.getOtpValidityInHours());
      ZonedDateTime actualExpirationTime = TimeUtils.getZonedDateTimeFor(cptOtp.getExpirationTimestamp());

      assertThat(validityCaptor.getValue()).isEqualTo(ppacConfiguration.getOtpValidityInHours());
      assertThat(actualExpirationTime).isEqualToIgnoringSeconds(expectedExpirationTime);
      assertThat(cptOtp.getPassword()).isEqualTo(password);
      assertThat(cptOtp.getAndroidPpacBasicIntegrity()).isFalse();
      assertThat(cptOtp.getAndroidPpacCtsProfileMatch()).isFalse();
      assertThat(cptOtp.getAndroidPpacEvaluationTypeBasic()).isTrue();
      assertThat(cptOtp.getAndroidPpacEvaluationTypeHardwareBacked()).isFalse();
    }

    @Test
    void testLogOtpServiceIsCalled() throws IOException {
      ppacConfiguration.getAndroid().setCertificateHostname("localhost");
      String password = "8ff92541-792f-4223-9970-bf90bf53b1a1";
      ArgumentCaptor<ElsOneTimePassword> elsOtpCaptor = ArgumentCaptor.forClass(ElsOneTimePassword.class);
      ArgumentCaptor<Integer> validityCaptor = ArgumentCaptor.forClass(Integer.class);

      ResponseEntity<OtpCreationResponse> actResponse =
          executor.executeOtpPost(buildElsOtpPayloadWithValidNonce(password));

      assertThat(actResponse.getStatusCode()).isEqualTo(OK);
      verify(elsOtpService, times(1)).createOtp(elsOtpCaptor.capture(), validityCaptor.capture());
      ElsOneTimePassword cptOtp = elsOtpCaptor.getValue();

      ZonedDateTime expectedExpirationTime = ZonedDateTime.now(ZoneOffset.UTC)
          .plusHours(ppacConfiguration.getOtpValidityInHours());
      ZonedDateTime actualExpirationTime = TimeUtils.getZonedDateTimeFor(cptOtp.getExpirationTimestamp());

      assertThat(validityCaptor.getValue()).isEqualTo(ppacConfiguration.getOtpValidityInHours());
      assertThat(actualExpirationTime).isEqualToIgnoringSeconds(expectedExpirationTime);
      assertThat(cptOtp.getPassword()).isEqualTo(password);
      assertThat(cptOtp.getAndroidPpacBasicIntegrity()).isFalse();
      assertThat(cptOtp.getAndroidPpacCtsProfileMatch()).isFalse();
      assertThat(cptOtp.getAndroidPpacEvaluationTypeBasic()).isTrue();
      assertThat(cptOtp.getAndroidPpacEvaluationTypeHardwareBacked()).isFalse();
    }

    @Test
    void testResponseIs400WhenOtpIsInvalidUuid() throws IOException {
      String password = "invalid-uuid";

      ResponseEntity<OtpCreationResponse> actResponse = executor.executeOtpPost(buildOtpPayloadWithValidNonce(
          password));

      assertThat(actResponse.getStatusCode()).isEqualTo(BAD_REQUEST);
    }

    private EDUSOneTimePasswordRequestAndroid buildOtpPayloadWithValidNonce(String password) throws IOException {
      String jws = getJwsPayloadWithNonce("mFmhph4QE3GTKS0FRNw9UZCxXI7ue+7fGdqGENsfo4g=");
      return EDUSOneTimePasswordRequestAndroid.newBuilder()
          .setAuthentication(newAuthenticationObject(jws, NOT_EXPIRED_SALT.getSalt()))
          .setPayload(EDUSOneTimePassword.newBuilder().setOtp(password))
          .build();
    }

    private ELSOneTimePasswordRequestAndroid buildElsOtpPayloadWithValidNonce(String password) throws IOException {
      String jws = getJwsPayloadWithNonce("mFmhph4QE3GTKS0FRNw9UZCxXI7ue+7fGdqGENsfo4g=");
      return ELSOneTimePasswordRequestAndroid.newBuilder()
          .setAuthentication(newAuthenticationObject(jws, NOT_EXPIRED_SALT.getSalt()))
          .setPayload(ELSOneTimePassword.newBuilder().setOtp(password))
          .build();
    }
  }

  private void assertDataWasSaved() {
    assertThat(exposureRiskMetadataRepo.findAll()).isNotEmpty();
    assertThat(exposureWindowRepo.findAll()).isNotEmpty();
    assertThat(testResultRepo.findAll()).isNotEmpty();
    assertThat(keySubmissionWithUserMetadataRepo.findAll()).isNotEmpty();
    assertThat(keySubmissionWithClientMetadataRepo.findAll()).isNotEmpty();
    assertThat(userMetadataRepo.findAll()).isNotEmpty();
    assertThat(clientMetadataRepo.findAll()).isNotEmpty();
  }

  private PPADataRequestAndroid buildPayloadWithMissingSalt() throws IOException {
    String jws = getJwsPayloadValues();
    return PPADataRequestAndroid.newBuilder()
        .setAuthentication(newAuthenticationObject(jws, ""))
        .setPayload(getValidAndroidDataPayload())
        .build();
  }

  private PPADataRequestAndroid buildPayloadWithExpiredSalt() throws IOException {
    String jws = getJwsPayloadValues();
    return PPADataRequestAndroid.newBuilder()
        .setAuthentication(newAuthenticationObject(jws, EXPIRED_SALT.getSalt()))
        .setPayload(getValidAndroidDataPayload())
        .build();
  }

  private PPADataRequestAndroid buildPayloadWithMissingJws() throws IOException {
    return PPADataRequestAndroid.newBuilder()
        .setAuthentication(newAuthenticationObject("", NOT_EXPIRED_SALT.getSalt()))
        .setPayload(getValidAndroidDataPayload())
        .build();
  }

  private PPADataRequestAndroid buildPayloadWithInvalidJwsParsing() throws IOException {
    return PPADataRequestAndroid.newBuilder()
        .setAuthentication(newAuthenticationObject("RANDOM STRING", NOT_EXPIRED_SALT.getSalt()))
        .setPayload(getValidAndroidDataPayload())
        .build();
  }

  private PPADataRequestAndroid buildPayloadWithBasicIntegrityViolation() throws IOException {
    return PPADataRequestAndroid.newBuilder()
        .setAuthentication(newAuthenticationObject(getJwsPayloadWithBasicIntegrityViolation(),
            NOT_EXPIRED_SALT.getSalt()))
        .setPayload(getValidAndroidDataPayload()).build();
  }

  private PPADataRequestAndroid buildPayloadWithIntegrityFlagsChecked() throws IOException {
    return PPADataRequestAndroid.newBuilder().setAuthentication(
        newAuthenticationObject(getJwsPayloadWithIntegrityFlagsChecked(), NOT_EXPIRED_SALT.getSalt()))
        .setPayload(getValidAndroidDataPayload()).build();
  }

  private PPADataRequestAndroid buildPayloadWithCtsMatchViolation() throws IOException {
    return PPADataRequestAndroid.newBuilder().setAuthentication(
        newAuthenticationObject(getJwsPayloadWithCtsMatchViolation(), NOT_EXPIRED_SALT.getSalt()))
        .setPayload(getValidAndroidDataPayload()).build();
  }

  private PPADataRequestAndroid buildPayloadWithEvaluationType(String evTypeUnderTest)
      throws IOException {
    return PPADataRequestAndroid.newBuilder()
        .setAuthentication(newAuthenticationObject(
            getJwsPayloadWithEvaluationType(evTypeUnderTest), NOT_EXPIRED_SALT.getSalt()))
        .setPayload(getValidAndroidDataPayload()).build();
  }

  private PPADataRequestAndroid buildPayloadWithValidNonce() throws IOException {
    String jws = getJwsPayloadWithNonce("+MtwU0sy/aFUvXuzQ7WKkMyILO1dwBu8Ys/SuOpWcZ8=");
    return PPADataRequestAndroid.newBuilder()
        .setAuthentication(newAuthenticationObject(jws, NOT_EXPIRED_SALT.getSalt()))
        .setPayload(getValidAndroidDataPayload())
        .build();
  }

  private PPADataRequestAndroid buildPayloadWithEmptyNonce() throws IOException {
    String jws = getJwsPayloadWithNonce("");
    return PPADataRequestAndroid.newBuilder()
        .setAuthentication(newAuthenticationObject(jws, NOT_EXPIRED_SALT.getSalt()))
        .setPayload(getValidAndroidDataPayload())
        .build();
  }

  private PPADataRequestAndroid buildPayloadWithWrongNonce() throws IOException {
    String jws = getJwsPayloadWithNonce("AAAA=");
    return PPADataRequestAndroid.newBuilder()
        .setAuthentication(newAuthenticationObject(jws, NOT_EXPIRED_SALT.getSalt()))
        .setPayload(getValidAndroidDataPayload())
        .build();
  }

  private PPADataRequestAndroid buildPayloadWithValidMetrics() throws IOException {
    String jws = getJwsPayloadValues();
    return PPADataRequestAndroid.newBuilder()
        .setAuthentication(newAuthenticationObject(jws, NOT_EXPIRED_SALT.getSalt()))
        .setPayload(PPADataAndroid.newBuilder()
            .addAllExposureRiskMetadataSet(Set.of(TestData.getValidExposureRiskMetadata()))
            .addAllNewExposureWindows(Set.of(TestData.getValidExposureWindow()))
            .addAllTestResultMetadataSet(Set.of(TestData.getValidTestResultMetadata()))
            .addAllKeySubmissionMetadataSet(Set.of(TestData.getValidKeySubmissionMetadata()))
            .setClientMetadata(TestData.getValidClientMetadata())
            .setUserMetadata(TestData.getValidUserMetadata()))
        .build();
  }

  private PPADataRequestAndroid buildPayloadWithInvalidExposureRiskDate() throws IOException {
    String jws = getJwsPayloadValues();
    return PPADataRequestAndroid.newBuilder()
        .setAuthentication(newAuthenticationObject(jws, NOT_EXPIRED_SALT.getSalt()))
        .setPayload(PPADataAndroid.newBuilder()
            .addAllExposureRiskMetadataSet(Set.of(TestData.getInvalidExposureRiskMetadata()))
            .addAllNewExposureWindows(Set.of(TestData.getValidExposureWindow()))
            .addAllTestResultMetadataSet(Set.of(TestData.getValidTestResultMetadata()))
            .addAllKeySubmissionMetadataSet(Set.of(TestData.getValidKeySubmissionMetadata()))
            .setClientMetadata(TestData.getValidClientMetadata())
            .setUserMetadata(TestData.getValidUserMetadata()))
        .build();
  }
  /**
   * The default configuration values are alligned with the test JWS that are created inside
   * the tests. See {@link JwsGenerationUtil} and {@link TestData#getJwsPayloadDefaultValue} for
   * more details on how the mock JWS are created. As an example, the certificate hostname
   * configuration below is alligned with test certificates used to build the JWS signatures
   * (they were created with CN=localhost).
   */
  private void prepareDefaultAppConfiguration() throws GeneralSecurityException {
    ppacConfiguration.getAndroid().setAllowedApkCertificateDigests(
        new String[]{TestData.TEST_APK_CERTIFICATE_DIGEST});
    ppacConfiguration.getAndroid().setAttestationValidity(TestData.ATTESTATION_VALIDITY_SECONDS);

    Dat androidDataFlags = new Dat();
    androidDataFlags.setRequireBasicIntegrity(false);
    androidDataFlags.setRequireCtsProfileMatch(false);
    androidDataFlags.setRequireEvaluationTypeHardwareBacked(false);
    androidDataFlags.setRequireEvaluationTypeBasic(false);
    ppacConfiguration.getAndroid().setDat(androidDataFlags);
    ppacConfiguration.getAndroid().setCertificateHostname(TestData.TEST_CERTIFICATE_HOSTNAME);
    ppacConfiguration.getAndroid().setDisableNonceCheck(true);
    ppacConfiguration.getAndroid().setDisableApkCertificateDigestsCheck(false);
    ppacConfiguration.getAndroid()
        .setAllowedApkPackageNames(new String[] {TestData.TEST_APK_PACKAGE_NAME});

    saltRepository.persist(EXPIRED_SALT.getSalt(), EXPIRED_SALT.getCreatedAt());
    saltRepository.persist(NOT_EXPIRED_SALT.getSalt(), NOT_EXPIRED_SALT.getCreatedAt());
    when(signatureVerificationStrategy.verifySignature(any())).thenReturn(JwsGenerationUtil.getTestCertificate());
  }
}
