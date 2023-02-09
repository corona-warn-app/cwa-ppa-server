package app.coronawarn.datadonation.services.ppac.android.controller;

import static app.coronawarn.datadonation.common.config.UrlConstants.ANDROID;
import static app.coronawarn.datadonation.common.config.UrlConstants.DATA;
import static app.coronawarn.datadonation.common.config.UrlConstants.LOG;
import static app.coronawarn.datadonation.common.config.UrlConstants.OTP;
import static app.coronawarn.datadonation.common.config.UrlConstants.SRS;

import app.coronawarn.datadonation.common.persistence.domain.ElsOneTimePassword;
import app.coronawarn.datadonation.common.persistence.domain.OneTimePassword;
import app.coronawarn.datadonation.common.persistence.domain.SrsOneTimePassword;
import app.coronawarn.datadonation.common.persistence.service.AndroidIdService;
import app.coronawarn.datadonation.common.persistence.service.OtpCreationResponse;
import app.coronawarn.datadonation.common.persistence.service.PpaDataStorageRequest;
import app.coronawarn.datadonation.common.protocols.internal.ppdd.EDUSOneTimePassword;
import app.coronawarn.datadonation.common.protocols.internal.ppdd.EDUSOneTimePasswordRequestAndroid;
import app.coronawarn.datadonation.common.protocols.internal.ppdd.ELSOneTimePassword;
import app.coronawarn.datadonation.common.protocols.internal.ppdd.ELSOneTimePasswordRequestAndroid;
import app.coronawarn.datadonation.common.protocols.internal.ppdd.PPACAndroid;
import app.coronawarn.datadonation.common.protocols.internal.ppdd.PPADataRequestAndroid;
import app.coronawarn.datadonation.common.protocols.internal.ppdd.SRSOneTimePasswordRequestAndroid;
import app.coronawarn.datadonation.services.ppac.android.attestation.AndroidIdVerificationStrategy;
import app.coronawarn.datadonation.services.ppac.android.attestation.AttestationStatement;
import app.coronawarn.datadonation.services.ppac.android.attestation.DeviceAttestationVerifier;
import app.coronawarn.datadonation.services.ppac.android.attestation.NonceCalculator;
import app.coronawarn.datadonation.services.ppac.android.attestation.SrsRateLimitVerificationStrategy;
import app.coronawarn.datadonation.services.ppac.android.controller.validation.PpaDataRequestAndroidValidator;
import app.coronawarn.datadonation.services.ppac.android.controller.validation.ValidAndroidOneTimePasswordRequest;
import app.coronawarn.datadonation.services.ppac.commons.AbstractController;
import app.coronawarn.datadonation.services.ppac.commons.PpacScenario;
import com.google.api.client.json.webtoken.JsonWebSignature;
import io.micrometer.core.annotation.Timed;
import java.time.ZonedDateTime;
import java.util.Base64;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.util.StopWatch;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.context.request.async.DeferredResult;

@RestController
@RequestMapping(ANDROID)
@Validated
public class AndroidController extends AbstractController {

  private static final Logger LOGGER = LoggerFactory.getLogger(AndroidController.class);

  @Autowired
  private DeviceAttestationVerifier deviceAttestationVerifier;

  @Autowired
  private AndroidIdVerificationStrategy androidIdVerificationStrategy;

  @Autowired
  private SrsRateLimitVerificationStrategy srsRateLimitVerificationStrategy;

  @Autowired
  private AndroidIdService androidIdService;

  @Autowired
  private PpaDataRequestAndroidConverter converter;

  @Autowired
  private PpaDataRequestAndroidValidator androidRequestValidator;

  public AndroidController(final AndroidDelayManager delayManager) {
    super(delayManager);
  }

  private ElsOneTimePassword createElsOneTimePassword(final PPACAndroid ppac,
      final ELSOneTimePassword elsOneTimePassword) {
    final AttestationStatement attestationStatement = getAttestationStatement(ppac);
    final ElsOneTimePassword otp = new ElsOneTimePassword(elsOneTimePassword.getOtp());
    setOtpFields(attestationStatement, otp);
    return otp;
  }

  private OneTimePassword createOneTimePassword(final PPACAndroid ppac, final EDUSOneTimePassword payload) {
    final AttestationStatement attestationStatement = getAttestationStatement(ppac);
    final OneTimePassword otp = new OneTimePassword(payload.getOtp());
    setOtpFields(attestationStatement, otp);
    return otp;
  }

  private SrsOneTimePassword createSrsOneTimePassword(final PPACAndroid ppac,
      final SRSOneTimePasswordRequestAndroid.SRSOneTimePassword payload) {
    final AttestationStatement attestationStatement = getAttestationStatement(ppac);
    final SrsOneTimePassword srsOneTimePassword = new SrsOneTimePassword(payload.getOtp());
    setOtpFields(attestationStatement, srsOneTimePassword);
    return srsOneTimePassword;
  }

  private AttestationStatement getAttestationStatement(final PPACAndroid ppac) {
    final JsonWebSignature jsonWebSignature = deviceAttestationVerifier.parseJws(ppac.getSafetyNetJws());
    return (AttestationStatement) jsonWebSignature
        .getPayload();
  }

  private void setOtpFields(final AttestationStatement attestationStatement, final OneTimePassword otp) {
    otp.setAndroidPpacBasicIntegrity(attestationStatement.hasBasicIntegrity());
    otp.setAndroidPpacCtsProfileMatch(attestationStatement.isCtsProfileMatch());
    otp.setAndroidPpacEvaluationTypeBasic(attestationStatement.getEvaluationType().contains("BASIC"));
    otp.setAndroidPpacEvaluationTypeHardwareBacked(
        attestationStatement.getEvaluationType().contains("HARDWARE_BACKED"));
  }

  /**
   * Handles diagnosis key submission requests.
   *
   * @param ppaDataRequest The unmarshalled protocol buffers submission payload.
   * @return An empty response body.
   */
  @PostMapping(value = DATA)
  @Timed(value = DATA, description = "Time spent handling Android data submission.")
  public DeferredResult<ResponseEntity<OtpCreationResponse>> submitData(
      @RequestBody final PPADataRequestAndroid ppaDataRequest) {

    final StopWatch stopWatch = start();
    if (LOGGER.isDebugEnabled()) {
      LOGGER.debug("Request received (base64): {}", Base64.getEncoder().encodeToString(ppaDataRequest.toByteArray()));
    }
    androidRequestValidator.validate(ppaDataRequest.getPayload(),
        ppacConfiguration.getMaxExposureWindowsToRejectSubmission());
    final AttestationStatement attestationStatement = deviceAttestationVerifier.validate(
        ppaDataRequest.getAuthentication(), NonceCalculator.of(ppaDataRequest.getPayload().toByteArray()),
        PpacScenario.PPA);
    securityLogger.successAndroid(DATA);
    final PpaDataStorageRequest dataToStore = converter.convertToStorageRequest(ppaDataRequest, ppacConfiguration,
        attestationStatement);
    ppaDataService.store(dataToStore);

    return deferredResult(ZonedDateTime.now(), stopWatch, HttpStatus.NO_CONTENT);
  }

  /**
   * Handles OTP creation requests for error log sharing (ELS).
   *
   * @param elsOtpRequest The unmarshalled protocol buffers log otp creation payload.
   * @return An empty response body.
   */
  @PostMapping(value = LOG, consumes = "application/x-protobuf", produces = "application/json")
  @Timed(value = LOG, description = "Time spent handling Android Error-Log-Sharing OTP request.")
  public DeferredResult<ResponseEntity<OtpCreationResponse>> submitElsOtp(
      @ValidAndroidOneTimePasswordRequest @RequestBody final ELSOneTimePasswordRequestAndroid elsOtpRequest) {

    final StopWatch stopWatch = start();
    final PPACAndroid ppac = elsOtpRequest.getAuthentication();
    final ELSOneTimePassword payload = elsOtpRequest.getPayload();
    deviceAttestationVerifier.validate(ppac, NonceCalculator.of(payload.toByteArray()), PpacScenario.LOG);
    securityLogger.successAndroid(LOG);
    final ElsOneTimePassword logOtp = createElsOneTimePassword(ppac, payload);
    final ZonedDateTime expirationTime = elsOtpService.createOtp(logOtp, ppacConfiguration.getOtpValidityInHours());

    return deferredResult(expirationTime, stopWatch);
  }

  /**
   * Handles otp creation requests.
   *
   * @param otpRequest The unmarshalled protocol buffers otp creation payload.
   * @return An empty response body.
   */
  @PostMapping(value = OTP, consumes = "application/x-protobuf", produces = "application/json")
  @Timed(value = OTP, description = "Time spent handling Android Event-Driven-User-Survey OTP request.")
  public DeferredResult<ResponseEntity<OtpCreationResponse>> submitOtp(
      @ValidAndroidOneTimePasswordRequest @RequestBody final EDUSOneTimePasswordRequestAndroid otpRequest) {

    final StopWatch stopWatch = start();
    final PPACAndroid ppac = otpRequest.getAuthentication();
    final EDUSOneTimePassword payload = otpRequest.getPayload();
    deviceAttestationVerifier.validate(ppac, NonceCalculator.of(payload.toByteArray()), PpacScenario.EDUS);
    securityLogger.successAndroid(OTP);
    final OneTimePassword otp = createOneTimePassword(ppac, payload);
    final ZonedDateTime expirationTime = otpService.createOtp(otp, ppacConfiguration.getOtpValidityInHours());

    return deferredResult(expirationTime, stopWatch);
  }

  /**
   * Handles OTP creation requests for Self-Report Submissions (SRS).
   */
  @PostMapping(value = SRS, consumes = "application/x-protobuf", produces = "application/json", headers = {
      "cwa-fake=0" })
  @Timed(value = SRS, description = "Time spent handling Android Self-Report-Submission OTP request.")
  public DeferredResult<ResponseEntity<OtpCreationResponse>> submitSrsOtp(
      @RequestHeader(value = "cwa-ppac-android-accept-android-id", required = false) final boolean acceptAndroidId,
      @ValidAndroidOneTimePasswordRequest @RequestBody final SRSOneTimePasswordRequestAndroid srsOtpRequest) {

    final StopWatch stopWatch = start();
    final PPACAndroid ppac = srsOtpRequest.getAuthentication();
    final SRSOneTimePasswordRequestAndroid.SRSOneTimePassword payload = srsOtpRequest.getPayload();
    deviceAttestationVerifier.validate(ppac, NonceCalculator.of(payload.toByteArray()), PpacScenario.SRS);
    androidIdVerificationStrategy.validateAndroidId(payload.getAndroidId().toByteArray());
    srsRateLimitVerificationStrategy.validateSrsRateLimit(payload.getAndroidId().toByteArray(), acceptAndroidId);
    securityLogger.successAndroid(SRS);
    // store Android ID
    androidIdService.upsertAndroidId(payload.getAndroidId().toByteArray(),
        ppacConfiguration.getSrsTimeBetweenSubmissionsInDays(), ppacConfiguration.getAndroid().pepper());
    final SrsOneTimePassword srsOtp = createSrsOneTimePassword(ppac, payload);
    final ZonedDateTime expirationTime = srsOtpService.createMinuteOtp(srsOtp,
        ppacConfiguration.getSrsOtpValidityInMinutes());

    return deferredResult(expirationTime, stopWatch);
  }
}
