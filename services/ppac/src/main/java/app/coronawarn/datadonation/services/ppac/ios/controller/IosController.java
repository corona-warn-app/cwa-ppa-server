package app.coronawarn.datadonation.services.ppac.ios.controller;

import app.coronawarn.datadonation.common.protocols.internal.ppdd.EdusOtpRequestIos.EDUSOneTimePasswordRequestIOS;
import app.coronawarn.datadonation.common.protocols.internal.ppdd.PpaDataRequestIos.PPADataRequestIOS;
import app.coronawarn.datadonation.services.ppac.ios.controller.validation.ValidPpaDataRequestIosPayload;
import app.coronawarn.datadonation.services.ppac.ios.verification.PpacIosScenario;
import app.coronawarn.datadonation.services.ppac.ios.verification.PpacProcessor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import static app.coronawarn.datadonation.common.config.UrlConstants.*;

@RestController
@RequestMapping(IOS)
@Validated
public class IosController {

  private static final Logger logger = LoggerFactory.getLogger(IosController.class);
  private final PpacProcessor ppacProcessor;

  IosController(PpacProcessor ppacProcessor) {
    this.ppacProcessor = ppacProcessor;
  }

  /**
   * Entry point for validating incoming data submission requests.
   *
   * @param ppaDataRequestIos           The unmarshalled protocol buffers submission payload.
   * @param ignoreApiTokenAlreadyIssued flag to indicate whether the ApiToken should be validated against the last
   *                                    updated time from the per-device Data.
   * @return An empty response body.
   */
  @PostMapping(value = DATA, consumes = "application/x-protobuf")
  public ResponseEntity<Object> submitData(
      @RequestHeader(value = "cwa-ppac-ios-accept-api-token", required = false) boolean ignoreApiTokenAlreadyIssued,
      @ValidPpaDataRequestIosPayload @RequestBody PPADataRequestIOS ppaDataRequestIos) {
    ppacProcessor.validate(ppaDataRequestIos.getAuthentication(), ignoreApiTokenAlreadyIssued, PpacIosScenario.PPA);
    return ResponseEntity.noContent().build();
  }

  /**
   * Entry point for triggering incoming otp creation requests requests.
   * <p>
   * //TODO
   */
  @PostMapping(value = OTP, consumes = "application/x-protobuf")
  public ResponseEntity<Object> submitOtp(
      @RequestHeader(value = "cwa-ppac-ios-accept-api-token", required = false) boolean ignoreApiTokenAlreadyIssued,
      @ValidPpaDataRequestIosPayload @RequestBody EDUSOneTimePasswordRequestIOS otpRequest) {
    // TODO ENUM
    ppacProcessor.validate(otpRequest.getAuthentication(), ignoreApiTokenAlreadyIssued, PpacIosScenario.EDUS);
    // new OneTimePassword(oneTimePassword.getOtp(), expirationTime)
    return ResponseEntity.noContent().build();
  }
}
