package app.coronawarn.datadonation.services.ppac.android.controller;

import static app.coronawarn.datadonation.common.config.UrlConstants.ANDROID;
import static app.coronawarn.datadonation.common.config.UrlConstants.DATA;
import static app.coronawarn.datadonation.common.config.UrlConstants.LOG;
import static app.coronawarn.datadonation.common.config.UrlConstants.OTP;
import static app.coronawarn.datadonation.common.config.UrlConstants.SRS;

import app.coronawarn.datadonation.common.config.SecurityLogger;
import app.coronawarn.datadonation.common.persistence.domain.ElsOneTimePassword;
import app.coronawarn.datadonation.common.persistence.domain.OneTimePassword;
import app.coronawarn.datadonation.common.persistence.domain.SrsOneTimePassword;
import app.coronawarn.datadonation.common.persistence.service.AndroidIdService;
import app.coronawarn.datadonation.common.persistence.service.ElsOtpService;
import app.coronawarn.datadonation.common.persistence.service.OtpCreationResponse;
import app.coronawarn.datadonation.common.persistence.service.OtpService;
import app.coronawarn.datadonation.common.persistence.service.PpaDataService;
import app.coronawarn.datadonation.common.persistence.service.PpaDataStorageRequest;
import app.coronawarn.datadonation.common.protocols.internal.ppdd.EDUSOneTimePassword;
import app.coronawarn.datadonation.common.protocols.internal.ppdd.EDUSOneTimePasswordRequestAndroid;
import app.coronawarn.datadonation.common.protocols.internal.ppdd.ELSOneTimePassword;
import app.coronawarn.datadonation.common.protocols.internal.ppdd.ELSOneTimePasswordRequestAndroid;
import app.coronawarn.datadonation.common.protocols.internal.ppdd.PPACAndroid;
import app.coronawarn.datadonation.common.protocols.internal.ppdd.PPADataRequestAndroid;
import app.coronawarn.datadonation.common.protocols.internal.ppdd.SRSOneTimePassword;
import app.coronawarn.datadonation.common.protocols.internal.ppdd.SRSOneTimePasswordRequestAndroid;
import app.coronawarn.datadonation.services.ppac.android.attestation.AttestationStatement;
import app.coronawarn.datadonation.services.ppac.android.attestation.DeviceAttestationVerifier;
import app.coronawarn.datadonation.services.ppac.android.attestation.ElsDeviceAttestationVerifier;
import app.coronawarn.datadonation.services.ppac.android.attestation.NonceCalculator;
import app.coronawarn.datadonation.services.ppac.android.attestation.SrsDeviceAttestationVerifier;
import app.coronawarn.datadonation.services.ppac.android.controller.validation.PpaDataRequestAndroidValidator;
import app.coronawarn.datadonation.services.ppac.android.controller.validation.ValidAndroidOneTimePasswordRequest;
import app.coronawarn.datadonation.services.ppac.commons.PpacScenario;
import app.coronawarn.datadonation.services.ppac.config.PpacConfiguration;
import com.google.api.client.json.webtoken.JsonWebSignature;
import io.micrometer.core.annotation.Timed;
import java.time.ZonedDateTime;
import java.util.Base64;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping(ANDROID)
@Validated
public class AndroidController {

  private static final Logger LOGGER = LoggerFactory.getLogger(AndroidController.class);

  private final PpacConfiguration ppacConfiguration;
  private final DeviceAttestationVerifier attestationVerifier;
  private ElsDeviceAttestationVerifier elsAttestationVerifier;
  private SrsDeviceAttestationVerifier srsAttestationVerifier;
  private final PpaDataService ppaDataService;
  private final OtpService otpService;
  private ElsOtpService elsOtpService;
  private AndroidIdService androidIdService;
  private final PpaDataRequestAndroidConverter converter;
  private final PpaDataRequestAndroidValidator androidRequestValidator;
  private final SecurityLogger securityLogger;

  AndroidController(@Qualifier("deviceAttestationVerifier") DeviceAttestationVerifier attestationVerifier,
      PpaDataService ppaDataService,
      PpacConfiguration ppacConfiguration, OtpService otpService,
      PpaDataRequestAndroidConverter converter,
      PpaDataRequestAndroidValidator androidRequestValidator,
      SecurityLogger securityLogger) {
    this.ppacConfiguration = ppacConfiguration;
    this.attestationVerifier = attestationVerifier;
    this.ppaDataService = ppaDataService;
    this.otpService = otpService;
    this.converter = converter;
    this.androidRequestValidator = androidRequestValidator;
    this.securityLogger = securityLogger;
  }

  @Autowired
  public void setElsAttestationVerifier(ElsDeviceAttestationVerifier elsAttestationVerifier) {
    this.elsAttestationVerifier = elsAttestationVerifier;
  }

  @Autowired
  public void setSrsAttestationVerifier(SrsDeviceAttestationVerifier srsAttestationVerifier) {
    this.srsAttestationVerifier = srsAttestationVerifier;
  }

  @Autowired
  public void setElsOtpService(ElsOtpService elsOtpService) {
    this.elsOtpService = elsOtpService;
  }

  @Autowired
  public void setAndroidIdService(AndroidIdService androidIdService) {
    this.androidIdService = androidIdService;
  }

  /**
   * Handles diagnosis key submission requests.
   *
   * @param ppaDataRequest The unmarshalled protocol buffers submission payload.
   * @return An empty response body.
   */
  @PostMapping(value = DATA)
  @Timed(description = "Time spent handling Android data submission.")
  public ResponseEntity<Void> submitData(
      @RequestBody PPADataRequestAndroid ppaDataRequest) {

    if (LOGGER.isDebugEnabled()) {
      LOGGER.debug("Request received (base64): {}",
          Base64.getEncoder().encodeToString(ppaDataRequest.toByteArray()));
    }

    androidRequestValidator.validate(ppaDataRequest.getPayload(),
        ppacConfiguration.getMaxExposureWindowsToRejectSubmission());

    AttestationStatement attestationStatement = attestationVerifier
        .validate(ppaDataRequest.getAuthentication(), NonceCalculator.of(ppaDataRequest.getPayload().toByteArray()),
            PpacScenario.PPA);
    securityLogger.successAndroid(DATA);
    final PpaDataStorageRequest dataToStore = this.converter.convertToStorageRequest(ppaDataRequest, ppacConfiguration,
        attestationStatement);
    ppaDataService.store(dataToStore);

    return ResponseEntity.status(HttpStatus.NO_CONTENT).build();
  }

  /**
   * Handles otp creation requests.
   *
   * @param otpRequest The unmarshalled protocol buffers otp creation payload.
   * @return An empty response body.
   */
  @PostMapping(value = OTP, consumes = "application/x-protobuf", produces = "application/json")
  public ResponseEntity<OtpCreationResponse> submitOtp(
      @ValidAndroidOneTimePasswordRequest @RequestBody EDUSOneTimePasswordRequestAndroid otpRequest) {
    PPACAndroid ppac = otpRequest.getAuthentication();
    EDUSOneTimePassword payload = otpRequest.getPayload();

    attestationVerifier.validate(ppac, NonceCalculator.of(payload.toByteArray()), PpacScenario.EDUS);
    securityLogger.successAndroid(OTP);
    OneTimePassword otp = createOneTimePassword(ppac, payload);

    ZonedDateTime expirationTime = otpService.createOtp(otp, ppacConfiguration.getOtpValidityInHours());
    return ResponseEntity.status(HttpStatus.OK).body(new OtpCreationResponse(expirationTime));
  }

  /**
   * Handles OTP creation requests for error log sharing (ELS).
   *
   * @param elsOtpRequest The unmarshalled protocol buffers log otp creation payload.
   * @return An empty response body.
   */
  @PostMapping(value = LOG, consumes = "application/x-protobuf", produces = "application/json")
  public ResponseEntity<OtpCreationResponse> submitElsOtp(
      @ValidAndroidOneTimePasswordRequest @RequestBody ELSOneTimePasswordRequestAndroid elsOtpRequest) {
    PPACAndroid ppac = elsOtpRequest.getAuthentication();
    ELSOneTimePassword payload = elsOtpRequest.getPayload();
    elsAttestationVerifier.validate(ppac, NonceCalculator.of(payload.toByteArray()), PpacScenario.LOG);
    securityLogger.successAndroid(LOG);
    ElsOneTimePassword logOtp = createElsOneTimePassword(ppac, payload);
    ZonedDateTime expirationTime = elsOtpService.createOtp(logOtp, ppacConfiguration.getOtpValidityInHours());
    return ResponseEntity.status(HttpStatus.OK).body(new OtpCreationResponse(expirationTime));
  }

  /**
   * Handles OTP creation requests for Self-Report Submissions (SRS).
   */
  @PostMapping(value = SRS, consumes = "application/x-protobuf", produces = "application/json")
  public ResponseEntity<OtpCreationResponse> submitSrsOtp(
      @ValidAndroidOneTimePasswordRequest @RequestBody SRSOneTimePasswordRequestAndroid srsOtpRequest) {
    PPACAndroid ppac = srsOtpRequest.getAuthentication();
    SRSOneTimePassword payload = srsOtpRequest.getPayload();
    srsAttestationVerifier.validate(ppac, NonceCalculator.of(payload.toByteArray()), PpacScenario.SRS);
    securityLogger.successAndroid(SRS);
    // store Android ID
    // FIXME: if persistence fails, we have to return error code 500.
    // We can this by throwing a respective exception during persistence.
    // Problem is that there is no explicit exceptions thrown, and it's unclear if we can catch any at all.
    // The existing code uses exception handlers on top of Optionals
    // Those exception handlers decide which error code to return, and also performs the logging.
    // But we can maybe just use an exception without a handler, and get the same result.
    // Only question is if the logging then also works, because we would just rethrow the exception.
    // See AndroidIdUpsertError for an example.
    androidIdService.upsertAndroidId(ppac, ppacConfiguration.getSrsTimeBetweenSubmissionsInDays(),
        ppacConfiguration.getAndroid().pepper());
    final SrsOneTimePassword srsOtp = createSrsOneTimePassword(ppac, payload);
    final ZonedDateTime expirationTime = otpService.createMinuteOtp(srsOtp,
        ppacConfiguration.getSrsOtpValidityInMinutes());
    return ResponseEntity.status(HttpStatus.OK).body(new OtpCreationResponse(expirationTime));
  }

  private OneTimePassword createOneTimePassword(PPACAndroid ppac, EDUSOneTimePassword payload) {
    AttestationStatement attestationStatement = getAttestationStatement(ppac);
    OneTimePassword otp = new OneTimePassword(payload.getOtp());
    setOtpFields(attestationStatement, otp);
    return otp;
  }

  private void setOtpFields(AttestationStatement attestationStatement, OneTimePassword otp) {
    otp.setAndroidPpacBasicIntegrity(attestationStatement.hasBasicIntegrity());
    otp.setAndroidPpacCtsProfileMatch(attestationStatement.isCtsProfileMatch());
    otp.setAndroidPpacEvaluationTypeBasic(attestationStatement.getEvaluationType().contains("BASIC"));
    otp.setAndroidPpacEvaluationTypeHardwareBacked(
        attestationStatement.getEvaluationType().contains("HARDWARE_BACKED"));
  }

  private ElsOneTimePassword createElsOneTimePassword(PPACAndroid ppac, ELSOneTimePassword elsOneTimePassword) {
    AttestationStatement attestationStatement = getAttestationStatement(ppac);
    ElsOneTimePassword otp = new ElsOneTimePassword(elsOneTimePassword.getOtp());
    setOtpFields(attestationStatement, otp);
    return otp;
  }

  private SrsOneTimePassword createSrsOneTimePassword(PPACAndroid ppac, SRSOneTimePassword srsOneTimePassword) {
    AttestationStatement attestationStatement = getAttestationStatement(ppac);
    SrsOneTimePassword otp = new SrsOneTimePassword(srsOneTimePassword.getOtp());
    setOtpFields(attestationStatement, otp);
    return otp;
  }

  private AttestationStatement getAttestationStatement(PPACAndroid ppac) {
    JsonWebSignature jsonWebSignature = attestationVerifier.parseJws(ppac.getSafetyNetJws());
    return (AttestationStatement) jsonWebSignature
        .getPayload();
  }
}
