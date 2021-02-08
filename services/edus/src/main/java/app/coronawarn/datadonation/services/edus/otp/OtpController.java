package app.coronawarn.datadonation.services.edus.otp;

import static app.coronawarn.datadonation.common.config.UrlConstants.OTP;
import static app.coronawarn.datadonation.common.config.UrlConstants.SURVEY;

import app.coronawarn.datadonation.common.persistence.domain.OneTimePassword;
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
public class OtpController {

  /**
   * The route to the Event-driven User Surveys endpoint (version agnostic).
   */
  private static final Logger logger = LoggerFactory.getLogger(OtpController.class);

  private final OtpService otpService;

  public OtpController(OtpService otpService) {
    this.otpService = otpService;
  }

  /**
   * Handling of OTP-Redemption.
   *
   * @param otpRequest Request that contains the OTP that shall be redeemed.
   * @return Response that contains the redeemed OTP.
   */
  @PostMapping(value = OTP)
  public ResponseEntity<OtpResponse> redeemOtp(@Valid @RequestBody OtpRequest otpRequest) {
    OneTimePassword otp = otpService.getOtp(otpRequest.getOtp());
    boolean wasRedeemed = otpService.getOtpStatus(otp).equals(OtpState.REDEEMED);

    OtpState otpState = otpService.redeemOtp(otp);
    HttpStatus httpStatus;

    if (otpState.equals(OtpState.REDEEMED) && !wasRedeemed) {
      httpStatus = HttpStatus.OK;
      logger.info("OTP redeemed successfully.");
    } else {
      httpStatus = HttpStatus.BAD_REQUEST;
      logger.warn("OTP could not be redeemed.");
    }

    return new ResponseEntity<>(new OtpResponse(otpRequest.getOtp(), otpState), httpStatus);
  }
}
