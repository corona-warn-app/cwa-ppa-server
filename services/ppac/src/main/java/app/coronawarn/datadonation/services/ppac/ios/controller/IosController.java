package app.coronawarn.datadonation.services.ppac.ios.controller;

import static app.coronawarn.datadonation.common.config.UrlConstants.DATA;
import static app.coronawarn.datadonation.common.config.UrlConstants.IOS;
import static app.coronawarn.datadonation.common.config.UrlConstants.LOG;
import static app.coronawarn.datadonation.common.config.UrlConstants.OTP;
import static app.coronawarn.datadonation.common.config.UrlConstants.SRS;

import app.coronawarn.datadonation.common.config.SecurityLogger;
import app.coronawarn.datadonation.common.persistence.domain.ElsOneTimePassword;
import app.coronawarn.datadonation.common.persistence.domain.OneTimePassword;
import app.coronawarn.datadonation.common.persistence.service.ElsOtpService;
import app.coronawarn.datadonation.common.persistence.service.OtpCreationResponse;
import app.coronawarn.datadonation.common.persistence.service.OtpService;
import app.coronawarn.datadonation.common.persistence.service.PpaDataService;
import app.coronawarn.datadonation.common.persistence.service.PpaDataStorageRequest;
import app.coronawarn.datadonation.common.protocols.internal.ppdd.EDUSOneTimePasswordRequestIOS;
import app.coronawarn.datadonation.common.protocols.internal.ppdd.ELSOneTimePasswordRequestIOS;
import app.coronawarn.datadonation.common.protocols.internal.ppdd.PPADataRequestIOS;
import app.coronawarn.datadonation.common.protocols.internal.ppdd.SRSOneTimePasswordRequestIOS;
import app.coronawarn.datadonation.services.ppac.commons.PpacScenario;
import app.coronawarn.datadonation.services.ppac.config.PpacConfiguration;
import app.coronawarn.datadonation.services.ppac.ios.controller.validation.ValidEdusOneTimePasswordRequestIos;
import app.coronawarn.datadonation.services.ppac.ios.controller.validation.ValidPpaDataRequestIosPayload;
import app.coronawarn.datadonation.services.ppac.ios.verification.PpacProcessor;
import io.micrometer.core.annotation.Timed;
import java.time.ZonedDateTime;
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

  private final PpacProcessor ppacProcessor;
  private final OtpService otpService;
  private final ElsOtpService elsOtpService;
  private final PpaDataRequestIosConverter converter;
  private final PpaDataService ppaDataService;
  private final PpacConfiguration ppacConfiguration;
  private final SecurityLogger securityLogger;

  IosController(PpacConfiguration ppacConfiguration, PpacProcessor ppacProcessor, OtpService otpService,
      ElsOtpService elsOtpService,
      PpaDataRequestIosConverter converter, PpaDataService ppaDataService, SecurityLogger securityLogger) {
    this.ppacConfiguration = ppacConfiguration;
    this.ppacProcessor = ppacProcessor;
    this.otpService = otpService;
    this.elsOtpService = elsOtpService;
    this.converter = converter;
    this.ppaDataService = ppaDataService;
    this.securityLogger = securityLogger;
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
  @Timed(description = "Time spent handling Ios data submission.")
  public ResponseEntity<Object> submitData(
      @RequestHeader(value = "cwa-ppac-ios-accept-api-token", required = false) boolean ignoreApiTokenAlreadyIssued,
      @ValidPpaDataRequestIosPayload @RequestBody PPADataRequestIOS ppaDataRequestIos) {
    ppacProcessor.validate(ppaDataRequestIos.getAuthentication(), ignoreApiTokenAlreadyIssued,
        PpacScenario.PPA);
    securityLogger.successIos(DATA);
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
        PpacScenario.EDUS);
    securityLogger.successIos(OTP);
    ZonedDateTime expirationTime = otpService
        .createOtp(new OneTimePassword(otpRequest.getPayload().getOtp()),
            ppacConfiguration.getOtpValidityInHours());
    return ResponseEntity.status(HttpStatus.OK).body(new OtpCreationResponse(expirationTime));
  }

  /**
   * Entry point for triggering incoming ELS otp creation requests requests.
   *
   * @param elsOtpRequest               The unmarshalled protocol buffers otp creation payload.
   * @return An empty response body.
   */
  @PostMapping(value = LOG, consumes = "application/x-protobuf")
  public ResponseEntity<Object> submitElsOtp(
      @ValidEdusOneTimePasswordRequestIos @RequestBody ELSOneTimePasswordRequestIOS elsOtpRequest) {
    ZonedDateTime expirationTime = elsOtpService
        .createOtp(new ElsOneTimePassword(elsOtpRequest.getPayload().getOtp()),
            ppacConfiguration.getOtpValidityInHours());
    securityLogger.successIos(LOG);
    return ResponseEntity.status(HttpStatus.OK).body(new OtpCreationResponse(expirationTime));
  }

  /**
   * Handles OTP creation requests for Self-Report Submissions (SRS).
   */
  @PostMapping(value = SRS, consumes = "application/x-protobuf")
  public ResponseEntity<Object> submitSrsOtp(
          @RequestHeader(value = "cwa-ppac-ios-accept-api-token", required = false) boolean ignoreApiTokenAlreadyIssued,
      @ValidEdusOneTimePasswordRequestIos @RequestBody SRSOneTimePasswordRequestIOS srsOtpRequest) {
    ppacProcessor.validate(srsOtpRequest.getAuthentication(), ignoreApiTokenAlreadyIssued, PpacScenario.SRS);
    securityLogger.successIos(LOG);
    ZonedDateTime expirationTime = otpService
            .createOtp(new OneTimePassword(srsOtpRequest.getPayload().getOtp()),
                    ppacConfiguration.getOtpValidityInHours());
    return ResponseEntity.status(HttpStatus.OK).body(new OtpCreationResponse(expirationTime));
  }
}
