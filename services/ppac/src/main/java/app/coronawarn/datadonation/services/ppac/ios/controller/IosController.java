package app.coronawarn.datadonation.services.ppac.ios.controller;

import static app.coronawarn.datadonation.common.config.UrlConstants.DATA;
import static app.coronawarn.datadonation.common.config.UrlConstants.IOS;
import static app.coronawarn.datadonation.common.config.UrlConstants.LOG;
import static app.coronawarn.datadonation.common.config.UrlConstants.OTP;
import static app.coronawarn.datadonation.common.config.UrlConstants.SRS;

import app.coronawarn.datadonation.common.persistence.domain.ElsOneTimePassword;
import app.coronawarn.datadonation.common.persistence.domain.OneTimePassword;
import app.coronawarn.datadonation.common.persistence.domain.SrsOneTimePassword;
import app.coronawarn.datadonation.common.persistence.service.OtpCreationResponse;
import app.coronawarn.datadonation.common.persistence.service.PpaDataStorageRequest;
import app.coronawarn.datadonation.common.protocols.internal.ppdd.EDUSOneTimePasswordRequestIOS;
import app.coronawarn.datadonation.common.protocols.internal.ppdd.ELSOneTimePasswordRequestIOS;
import app.coronawarn.datadonation.common.protocols.internal.ppdd.PPADataRequestIOS;
import app.coronawarn.datadonation.common.protocols.internal.ppdd.SRSOneTimePasswordRequestIOS;
import app.coronawarn.datadonation.services.ppac.commons.AbstractController;
import app.coronawarn.datadonation.services.ppac.commons.PpacScenario;
import app.coronawarn.datadonation.services.ppac.ios.controller.validation.ValidOneTimePasswordRequestIos;
import app.coronawarn.datadonation.services.ppac.ios.controller.validation.ValidPpaDataRequestIosPayload;
import app.coronawarn.datadonation.services.ppac.ios.verification.PpacProcessor;
import io.micrometer.core.annotation.Timed;
import java.time.ZonedDateTime;
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
@RequestMapping(IOS)
@Validated
public class IosController extends AbstractController {

  @Autowired
  private PpacProcessor ppacProcessor;

  @Autowired
  private PpaDataRequestIosConverter converter;

  public IosController(final IosDelayManager delayManager) {
    super(delayManager);
  }

  /**
   * Entry point for validating incoming data submission requests.
   *
   * @param ppaDataRequestIos The unmarshalled protocol buffers submission payload.
   * @param acceptApiToken    flag to indicate whether the ApiToken should be validated against the last updated time
   *                          from the per-device Data.
   * @return An empty response body.
   */
  @PostMapping(value = DATA)
  @Timed(value = DATA, description = "Time spent handling Ios data submission.")
  public DeferredResult<ResponseEntity<OtpCreationResponse>> submitData(
      @RequestHeader(value = "cwa-ppac-ios-accept-api-token", required = false) final boolean acceptApiToken,
      @ValidPpaDataRequestIosPayload @RequestBody final PPADataRequestIOS ppaDataRequestIos) {

    final StopWatch stopWatch = start();
    ppacProcessor.validate(ppaDataRequestIos.getAuthentication(), acceptApiToken, PpacScenario.PPA);
    securityLogger.successIos(DATA);
    final PpaDataStorageRequest ppaDataStorageRequest = converter.convertToStorageRequest(ppaDataRequestIos);
    ppaDataService.store(ppaDataStorageRequest);

    return deferredResult(ZonedDateTime.now(), stopWatch, HttpStatus.NO_CONTENT);
  }

  /**
   * Entry point for triggering incoming ELS otp creation requests requests.
   *
   * @param elsOtpRequest The unmarshalled protocol buffers otp creation payload.
   * @return An empty response body.
   */
  @PostMapping(value = LOG, consumes = "application/x-protobuf", produces = "application/json")
  @Timed(value = LOG, description = "Time spent handling iOS Error-Log-Sharing OTP request.")
  public DeferredResult<ResponseEntity<OtpCreationResponse>> submitElsOtp(
      @ValidOneTimePasswordRequestIos @RequestBody final ELSOneTimePasswordRequestIOS elsOtpRequest) {

    final StopWatch stopWatch = start();
    final ZonedDateTime expirationTime = elsOtpService.createOtp(
        new ElsOneTimePassword(elsOtpRequest.getPayload().getOtp()), ppacConfiguration.getOtpValidityInHours());
    securityLogger.successIos(LOG);

    return deferredResult(expirationTime, stopWatch);
  }

  /**
   * Entry point for triggering incoming otp creation requests requests.
   *
   * @param acceptApiToken flag to indicate whether the ApiToken should be validated against the last updated time from
   *                       the per-device Data.
   * @param otpRequest     The unmarshalled protocol buffers otp creation payload.
   * @return An empty response body.
   */
  @PostMapping(value = OTP, consumes = "application/x-protobuf", produces = "application/json")
  @Timed(value = OTP, description = "Time spent handling iOS Event-Driven-User-Survey OTP request.")
  public DeferredResult<ResponseEntity<OtpCreationResponse>> submitOtp(
      @RequestHeader(value = "cwa-ppac-ios-accept-api-token", required = false) final boolean acceptApiToken,
      @ValidOneTimePasswordRequestIos @RequestBody final EDUSOneTimePasswordRequestIOS otpRequest) {

    final StopWatch stopWatch = start();
    ppacProcessor.validate(otpRequest.getAuthentication(), acceptApiToken, PpacScenario.EDUS);
    securityLogger.successIos(OTP);
    final ZonedDateTime expirationTime = otpService.createOtp(new OneTimePassword(otpRequest.getPayload().getOtp()),
        ppacConfiguration.getOtpValidityInHours());

    return deferredResult(expirationTime, stopWatch);
  }

  /**
   * Handles OTP creation requests for Self-Report Submissions (SRS).
   */
  @PostMapping(value = SRS, consumes = "application/x-protobuf", produces = "application/json", headers = {
      "cwa-fake=0" })
  @Timed(value = SRS, description = "Time spent handling iOS Self-Report-Submission OTP request.")
  public DeferredResult<ResponseEntity<OtpCreationResponse>> submitSrsOtp(
      @RequestHeader(value = "cwa-ppac-ios-accept-api-token", required = false) final boolean acceptApiToken,
      @ValidOneTimePasswordRequestIos @RequestBody final SRSOneTimePasswordRequestIOS srsOtpRequest) {

    final StopWatch stopWatch = start();
    ppacProcessor.validate(srsOtpRequest.getAuthentication(), acceptApiToken, PpacScenario.SRS);
    securityLogger.successIos(LOG);
    final ZonedDateTime expirationTime = srsOtpService.createMinuteOtp(
        new SrsOneTimePassword(srsOtpRequest.getPayload().getOtp()), ppacConfiguration.getSrsOtpValidityInMinutes());

    return deferredResult(expirationTime, stopWatch);
  }
}
