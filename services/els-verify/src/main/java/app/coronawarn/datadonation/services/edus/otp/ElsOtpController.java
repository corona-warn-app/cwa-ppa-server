package app.coronawarn.datadonation.services.edus.otp;

import static app.coronawarn.datadonation.common.config.UrlConstants.LOG;
import static app.coronawarn.datadonation.common.config.UrlConstants.SURVEY;
import static java.lang.Boolean.TRUE;

import app.coronawarn.datadonation.common.persistence.domain.ElsOneTimePassword;
import app.coronawarn.datadonation.common.persistence.domain.OneTimePassword;
import app.coronawarn.datadonation.common.persistence.service.ElsOtpService;
import app.coronawarn.datadonation.common.persistence.service.OtpState;
import javax.validation.Valid;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping(SURVEY)
@Validated
@ControllerAdvice
public class ElsOtpController {

  /**
   * The route to the Event-driven User Surveys endpoint (version agnostic).
   */
  private static final Logger logger = LoggerFactory.getLogger(ElsOtpController.class);

  private final ElsOtpService elsOtpService;

  public ElsOtpController(ElsOtpService elsOtpService) {
    this.elsOtpService = elsOtpService;
  }

  /**
   * Handling of ELS-Redemption.
   *
   * @param elsOtpRedemptionRequest Request that contains the ELS that shall be redeemed.
   * @return Response that contains the redeemed ELS.
   */
  @PostMapping(value = LOG)
  public ResponseEntity<ElsOtpRedemptionResponse> redeemElsOtp(
      @Valid @RequestBody ElsOtpRedemptionRequest elsOtpRedemptionRequest) {
    ElsOneTimePassword otp = elsOtpService.getOtp(elsOtpRedemptionRequest.getEls());
    boolean wasRedeemed = elsOtpService.getOtpStatus(otp).equals(OtpState.REDEEMED);

    OtpState otpState = elsOtpService.redeemOtp(otp);
    HttpStatus httpStatus;

    if (otpState.equals(OtpState.REDEEMED) && !wasRedeemed) {
      httpStatus = HttpStatus.OK;
      otpState = OtpState.VALID;
      logger.info("ELS OTP redeemed successfully.");
    } else {
      httpStatus = HttpStatus.BAD_REQUEST;
      logger.warn("ELS OTP could not be redeemed.");
    }

    return new ResponseEntity<>(new ElsOtpRedemptionResponse(elsOtpRedemptionRequest.getEls(), otpState,
        calculateStrongClientIntegrityCheck(otp)),
        httpStatus);
  }

  static boolean calculateStrongClientIntegrityCheck(OneTimePassword otp) {
    return isOtpFromIosDevice(otp) || isOtpFromValidAndroidDevice(otp);
  }

  static boolean isOtpFromIosDevice(OneTimePassword otp) {
    return otp.getAndroidPpacBasicIntegrity() == null
        && otp.getAndroidPpacCtsProfileMatch() == null
        && otp.getAndroidPpacEvaluationTypeBasic() == null
        && otp.getAndroidPpacEvaluationTypeHardwareBacked() == null;
  }

  static boolean isOtpFromValidAndroidDevice(OneTimePassword otp) {
    return TRUE.equals(otp.getAndroidPpacBasicIntegrity())
        && TRUE.equals(otp.getAndroidPpacCtsProfileMatch())
        && TRUE.equals(otp.getAndroidPpacEvaluationTypeHardwareBacked());
  }
}
