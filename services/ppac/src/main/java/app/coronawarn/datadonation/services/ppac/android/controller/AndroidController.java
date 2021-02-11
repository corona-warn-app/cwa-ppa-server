package app.coronawarn.datadonation.services.ppac.android.controller;

import app.coronawarn.datadonation.common.config.UrlConstants;
import app.coronawarn.datadonation.common.persistence.service.PpaDataService;
import app.coronawarn.datadonation.common.persistence.service.PpaDataStorageRequest;
import app.coronawarn.datadonation.common.protocols.internal.ppdd.ExposureRiskMetadata;
import app.coronawarn.datadonation.common.protocols.internal.ppdd.PPAClientMetadataAndroid;
import app.coronawarn.datadonation.common.protocols.internal.ppdd.PPADataAndroid;
import app.coronawarn.datadonation.common.protocols.internal.ppdd.PPAKeySubmissionMetadata;
import app.coronawarn.datadonation.common.protocols.internal.ppdd.PPANewExposureWindow;
import app.coronawarn.datadonation.common.protocols.internal.ppdd.PPATestResultMetadata;
import app.coronawarn.datadonation.common.protocols.internal.ppdd.PPAUserMetadata;
import app.coronawarn.datadonation.common.protocols.internal.ppdd.PpaDataRequestAndroid.PPADataRequestAndroid;
import app.coronawarn.datadonation.services.ppac.android.attestation.DeviceAttestationVerifier;
import app.coronawarn.datadonation.services.ppac.android.attestation.NonceCalculator;
import java.util.List;
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

  private static final Logger logger = LoggerFactory.getLogger(AndroidController.class);

  private final PpacConfiguration ppacConfiguration;
  private final DeviceAttestationVerifier attestationVerifier;
  private final PpaDataService ppaDataService;
  private final OtpService otpService;
  private final PpaDataRequestAndroidConverter converter;

  AndroidController(DeviceAttestationVerifier attestationVerifier, PpaDataService ppaDataService,
      PpacConfiguration ppacConfiguration, OtpService otpService, PpaDataRequestAndroidConverter converter) {
    this.ppacConfiguration = ppacConfiguration;
    this.attestationVerifier = attestationVerifier;
    this.ppaDataService = ppaDataService;
    this.otpService = otpService;
    this.converter = converter;
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

    attestationVerifier.validate(ppaDataRequest.getAuthentication(),
        NonceCalculator.of(ppaDataRequest.getPayload()));
    final PpaDataStorageRequest dataToStore = this.converter.convertToStorageRequest(ppaDataRequest);
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
      @ValidEdusOneTimePasswordRequestAndroid @RequestBody EDUSOneTimePasswordRequestAndroid otpRequest) {
    PPACAndroid ppac = otpRequest.getAuthentication();
    EDUSOneTimePassword payload = otpRequest.getPayload();

    attestationVerifier.validate(ppac, NonceCalculator.of(payload));

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
