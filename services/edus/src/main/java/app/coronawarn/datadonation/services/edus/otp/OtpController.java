package app.coronawarn.datadonation.services.edus.otp;

import static app.coronawarn.datadonation.common.config.UrlConstants.OTP;
import static app.coronawarn.datadonation.common.config.UrlConstants.SURVEY;

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
    OtpState otpState = otpService.redeemOtp(otpRequest.getOtp());
    return createOtpStateResponseEntity(otpRequest.getOtp(), otpState);
  }

  private ResponseEntity<OtpResponse> createOtpStateResponseEntity(String otp, OtpState otpState) {
    HttpStatus httpStatus = HttpStatus.BAD_REQUEST;
    if (OtpState.VALID.equals(otpState)) {
      httpStatus = HttpStatus.OK;
    }
    return new ResponseEntity<>(new OtpResponse(otp, otpState), httpStatus);
  }
}
