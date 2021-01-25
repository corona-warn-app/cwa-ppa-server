package app.coronawarn.datadonation.services.edus.otp;

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
@RequestMapping("/version/v1")
@Validated
@ControllerAdvice
public class OtpController {

  /**
   * The route to the Event-driven User Surveys endpoint (version agnostic).
   */
  public static final String VALIDATION_ROUTE = "/otp/validate";
  public static final String REDEMPTION_ROUTE = "/otp/redeem";
  private static final Logger logger = LoggerFactory.getLogger(OtpController.class);

  private final OtpService otpService;

  public OtpController(OtpService otpService) {
    this.otpService = otpService;
  }

  /**
   * Handling of Event-driven User Surveys (EDUS).
   *
   * @param otpRequest The application/json payload.
   * @return An empty response body.
   */
  @PostMapping(value = VALIDATION_ROUTE)
  public ResponseEntity<OtpResponse> submitData(@Valid @RequestBody OtpRequest otpRequest) {
    final OtpState otpState = otpService.checkOtpIsValid(otpRequest.getOtp());
    if (otpState.equals(OtpState.VALID)) {
      return new ResponseEntity<>(
          new OtpResponse(otpRequest.getOtp(), otpState),
          HttpStatus.OK);
    }
    return new ResponseEntity<>(
        new OtpResponse(otpRequest.getOtp(), otpState),
        HttpStatus.BAD_REQUEST);
  }

  /**
   * Handling of OTP-Redemption.
   *
   * @param otpRequest Request that contains the OTP that shall be redeemed.
   * @return Response that contains the redeemed OTP.
   */
  @PostMapping(value = REDEMPTION_ROUTE)
  public ResponseEntity<OtpResponse> redeemOtp(@RequestBody OtpRequest otpRequest) {
    String otpID = otpRequest.getOtp();
    OtpState otpState = otpService.redeemOtp(otpRequest.getOtp());
    if (otpState.equals(OtpState.VALID)) {
      return new ResponseEntity<>(new OtpResponse(otpID, otpState),
          HttpStatus.OK);
    }
    return new ResponseEntity<>(new OtpResponse(otpID, otpState),
        HttpStatus.BAD_REQUEST);
  }
}
