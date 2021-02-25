package app.coronawarn.datadonation.services.ppac.android.controller;

import app.coronawarn.datadonation.common.config.UrlConstants;
import app.coronawarn.datadonation.common.persistence.domain.OneTimePassword;
import app.coronawarn.datadonation.common.persistence.service.OtpCreationResponse;
import app.coronawarn.datadonation.common.persistence.service.OtpService;
import app.coronawarn.datadonation.common.persistence.service.PpaDataService;
import app.coronawarn.datadonation.common.persistence.service.PpaDataStorageRequest;
import app.coronawarn.datadonation.common.protocols.internal.ppdd.EDUSOneTimePassword;
import app.coronawarn.datadonation.common.protocols.internal.ppdd.EDUSOneTimePasswordRequestAndroid;
import app.coronawarn.datadonation.common.protocols.internal.ppdd.PPACAndroid;
import app.coronawarn.datadonation.common.protocols.internal.ppdd.PPADataRequestAndroid;
import app.coronawarn.datadonation.services.ppac.android.attestation.AttestationStatement;
import app.coronawarn.datadonation.services.ppac.android.attestation.DeviceAttestationVerifier;
import app.coronawarn.datadonation.services.ppac.android.attestation.NonceCalculator;
import app.coronawarn.datadonation.services.ppac.android.attestation.salt.ProdSaltVerificationStrategy;
import app.coronawarn.datadonation.services.ppac.android.controller.validation.PpaDataRequestAndroidValidator;
import app.coronawarn.datadonation.services.ppac.android.controller.validation.ValidEdusOneTimePasswordRequestAndroid;
import app.coronawarn.datadonation.services.ppac.commons.PpacScenario;
import app.coronawarn.datadonation.services.ppac.config.PpacConfiguration;
import com.google.api.client.json.webtoken.JsonWebSignature;
import java.time.ZonedDateTime;
import org.apache.commons.codec.binary.Base64;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping(UrlConstants.ANDROID)
@Validated
public class AndroidController {

  private static final Logger logger = LoggerFactory.getLogger(ProdSaltVerificationStrategy.class);

  private final PpacConfiguration ppacConfiguration;
  private final DeviceAttestationVerifier attestationVerifier;
  private final PpaDataService ppaDataService;
  private final OtpService otpService;
  private final PpaDataRequestAndroidConverter converter;
  private final PpaDataRequestAndroidValidator androidRequestValidator;

  AndroidController(DeviceAttestationVerifier attestationVerifier, PpaDataService ppaDataService,
      PpacConfiguration ppacConfiguration, OtpService otpService, PpaDataRequestAndroidConverter converter,
      PpaDataRequestAndroidValidator androidRequestValidator) {
    this.ppacConfiguration = ppacConfiguration;
    this.attestationVerifier = attestationVerifier;
    this.ppaDataService = ppaDataService;
    this.otpService = otpService;
    this.converter = converter;
    this.androidRequestValidator = androidRequestValidator;
  }

  /**
   * Handles diagnosis key submission requests.
   *
   * @param ppaDataRequest The unmarshalled protocol buffers submission payload.
   * @return An empty response body.
   */
  @PostMapping(value = UrlConstants.DATA)
  public ResponseEntity<Void> submitData(
      @RequestBody PPADataRequestAndroid ppaDataRequest) {

    logger.debug("Request received (base64): " + Base64.encodeBase64String(ppaDataRequest.toByteArray()));

    androidRequestValidator.validate(ppaDataRequest.getPayload(),
        ppacConfiguration.getMaxExposureWindowsToRejectSubmission());

    AttestationStatement attestationStatement = attestationVerifier
        .validate(ppaDataRequest.getAuthentication(), NonceCalculator.of(ppaDataRequest.getPayload().toByteArray()),
            PpacScenario.PPA);
    final PpaDataStorageRequest dataToStore =
        this.converter.convertToStorageRequest(ppaDataRequest, ppacConfiguration, attestationStatement);
    ppaDataService.store(dataToStore);

    return ResponseEntity.status(HttpStatus.NO_CONTENT).build();
  }

  /**
   * Handles otp creation requests.
   *
   * @param otpRequest The unmarshalled protocol buffers otp creation payload.
   * @return An empty response body.
   */
  @PostMapping(value = UrlConstants.OTP, consumes = "application/x-protobuf", produces = "application/json")
  public ResponseEntity<OtpCreationResponse> submitOtp(
      @ValidEdusOneTimePasswordRequestAndroid @RequestBody EDUSOneTimePasswordRequestAndroid otpRequest) {
    PPACAndroid ppac = otpRequest.getAuthentication();
    EDUSOneTimePassword payload = otpRequest.getPayload();

    attestationVerifier.validate(ppac, NonceCalculator.of(payload.toByteArray()), PpacScenario.EDUS);

    OneTimePassword otp = createOneTimePassword(ppac, payload);

    ZonedDateTime expirationTime = otpService.createOtp(otp, ppacConfiguration.getOtpValidityInHours());
    return ResponseEntity.status(HttpStatus.OK).body(new OtpCreationResponse(expirationTime));
  }

  private OneTimePassword createOneTimePassword(PPACAndroid ppac, EDUSOneTimePassword payload) {
    JsonWebSignature jsonWebSignature = attestationVerifier.parseJws(ppac.getSafetyNetJws());
    AttestationStatement attestationStatement = (AttestationStatement) jsonWebSignature
        .getPayload();

    OneTimePassword otp = new OneTimePassword(payload.getOtp());
    otp.setAndroidPpacBasicIntegrity(attestationStatement.hasBasicIntegrity());
    otp.setAndroidPpacCtsProfileMatch(attestationStatement.isCtsProfileMatch());
    otp.setAndroidPpacEvaluationTypeBasic(attestationStatement.getEvaluationType().contains("BASIC"));
    otp.setAndroidPpacEvaluationTypeHardwareBacked(
        attestationStatement.getEvaluationType().contains("HARDWARE_BACKED"));
    return otp;
  }
}
