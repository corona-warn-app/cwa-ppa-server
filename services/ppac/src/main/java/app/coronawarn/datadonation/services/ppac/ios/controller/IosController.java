package app.coronawarn.datadonation.services.ppac.ios.controller;

import static app.coronawarn.datadonation.common.config.UrlConstants.DATA;
import static app.coronawarn.datadonation.common.config.UrlConstants.IOS;
import static app.coronawarn.datadonation.common.config.UrlConstants.OTP;

import app.coronawarn.datadonation.common.persistence.domain.OneTimePassword;
import app.coronawarn.datadonation.common.persistence.service.OtpCreationResponse;
import app.coronawarn.datadonation.common.persistence.service.OtpService;
import app.coronawarn.datadonation.common.persistence.service.PpaDataService;
import app.coronawarn.datadonation.common.persistence.service.PpaDataStorageRequest;
import app.coronawarn.datadonation.common.protocols.internal.ppdd.EdusOtpRequestIos.EDUSOneTimePasswordRequestIOS;
import app.coronawarn.datadonation.common.protocols.internal.ppdd.PpaDataRequestIos.PPADataRequestIOS;
import app.coronawarn.datadonation.services.ppac.config.PpacConfiguration;
import app.coronawarn.datadonation.services.ppac.ios.controller.validation.ValidEdusOneTimePasswordRequestIos;
import app.coronawarn.datadonation.services.ppac.ios.controller.validation.ValidPpaDataRequestIosPayload;
import app.coronawarn.datadonation.services.ppac.ios.verification.PpacIosScenario;
import app.coronawarn.datadonation.services.ppac.ios.verification.PpacProcessor;
import java.time.ZonedDateTime;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping(IOS)
@Validated
public class IosController {

  private static final Logger logger = LoggerFactory.getLogger(IosController.class);

  private final PpacConfiguration ppacConfiguration;
  private final PpacProcessor ppacProcessor;
  private final OtpService otpService;
  private final PpaDataRequestIosConverter converter;
  private final PpaDataService ppaDataService;

  IosController(PpacConfiguration ppacConfiguration, PpacProcessor ppacProcessor, OtpService otpService,
      PpaDataRequestIosConverter converter, PpaDataService ppaDataService) {
    this.ppacConfiguration = ppacConfiguration;
    this.ppacProcessor = ppacProcessor;
    this.otpService = otpService;
    this.converter = converter;
    this.ppaDataService = ppaDataService;
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
    ppacProcessor.validate(ppaDataRequestIos.getAuthentication(), ignoreApiTokenAlreadyIssued,
        PpacIosScenario.PPA);
    final PpaDataStorageRequest ppaDataStorageRequest = this.converter.convertToStorageRequest(ppaDataRequestIos);
    ppaDataService.store(ppaDataStorageRequest);
    return ResponseEntity.noContent().build();
  }

  /**
   * Entry point for triggering incoming otp creation requests requests.
   *
   * @param ignoreApiTokenAlreadyIssued flag to indicate whether the ApiToken should be validated against the last
   *                                    updated time from the per-device Data.
   * @param otpRequest                  The unmarshalled protocol buffers otp creation payload.
   * @return An empty response body.
   */
  @PostMapping(value = OTP, consumes = "application/x-protobuf")
  public ResponseEntity<Object> submitOtp(
      @RequestHeader(value = "cwa-ppac-ios-accept-api-token", required = false) boolean ignoreApiTokenAlreadyIssued,
      @ValidEdusOneTimePasswordRequestIos @RequestBody EDUSOneTimePasswordRequestIOS otpRequest) {
    ppacProcessor.validate(otpRequest.getAuthentication(), ignoreApiTokenAlreadyIssued,
        PpacIosScenario.EDUS);
    ZonedDateTime expirationTime = otpService
        .createOtp(new OneTimePassword(otpRequest.getPayload().getOtp()),
            ppacConfiguration.getOtpValidityInHours());
    return ResponseEntity.status(HttpStatus.OK).body(new OtpCreationResponse(expirationTime));
  }
}
