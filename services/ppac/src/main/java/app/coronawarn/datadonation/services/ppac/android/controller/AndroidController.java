package app.coronawarn.datadonation.services.ppac.android.controller;

import static app.coronawarn.datadonation.common.config.UrlConstants.OTP;

import app.coronawarn.datadonation.common.config.UrlConstants;
import app.coronawarn.datadonation.common.persistence.domain.OneTimePassword;
import app.coronawarn.datadonation.common.persistence.service.OtpService;
import app.coronawarn.datadonation.common.persistence.service.PpaDataService;
import app.coronawarn.datadonation.common.persistence.service.PpaDataStorageRequest;
import app.coronawarn.datadonation.common.protocols.internal.ppdd.EdusOtpRequestAndroid.EDUSOneTimePasswordRequestAndroid;
import app.coronawarn.datadonation.common.protocols.internal.ppdd.PpaDataRequestAndroid.PPADataRequestAndroid;
import app.coronawarn.datadonation.services.ppac.android.attestation.DeviceAttestationVerifier;
import app.coronawarn.datadonation.services.ppac.android.attestation.NonceCalculator;
import app.coronawarn.datadonation.services.ppac.config.PpacConfiguration;
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

  AndroidController(DeviceAttestationVerifier attestationVerifier, PpaDataService ppaDataService,
      PpacConfiguration ppacConfiguration, OtpService otpService) {
    this.ppacConfiguration = ppacConfiguration;
    this.attestationVerifier = attestationVerifier;
    this.ppaDataService = ppaDataService;
    this.otpService = otpService;
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

    PpaDataStorageRequest dataStorageRequest =
        PpaDataRequestConverter.convertToStorageRequest(ppaDataRequest);

    ppaDataService.store(dataStorageRequest);
    return ResponseEntity.status(HttpStatus.NO_CONTENT).build();
  }

  /**
   * Handles otp creation requests.
   *
   * @param otpRequest The unmarshalled protocol buffers otp creation payload.
   * @return An empty response body.
   */
  @PostMapping(value = OTP, consumes = "application/x-protobuf")
  public ResponseEntity<Void> submitOtp(
      @RequestBody EDUSOneTimePasswordRequestAndroid otpRequest) {
    attestationVerifier.validate(otpRequest.getAuthentication(),
        NonceCalculator.of(otpRequest.getPayload()));
    otpService.createOtp(new OneTimePassword(otpRequest.getPayload().getOtp()),
        ppacConfiguration.getOtpValidityInHours());
    return ResponseEntity.noContent().build();
  }
}
