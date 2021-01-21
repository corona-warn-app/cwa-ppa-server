package app.coronawarn.analytics.services.edus.otp;

import app.coronawarn.analytics.common.persistence.repository.OneTimePasswordRepository;
import java.time.LocalDate;
import java.time.ZoneOffset;
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
@RequestMapping("/version/v1/otp")
@Validated
@ControllerAdvice
public class OtpController {

  /**
   * The route to the Event-driven User Surveys endpoint (version agnostic).
   */
  public static final String VALIDATION_ROUTE = "/validate";
  public static final String REDEMPTION_ROUTE = "/redeem";
  private static final Logger logger = LoggerFactory.getLogger(OtpController.class);

  OneTimePasswordRepository dataRepository;

  public OtpController(OneTimePasswordRepository dataRepository) {
    this.dataRepository = dataRepository;
  }

  /**
   * Handling of Event-driven User Surveys (EDUS).
   *
   * @param otpRequest The application/json payload.
   * @return An empty response body.
   */
  @PostMapping(value = VALIDATION_ROUTE)
  public ResponseEntity<OtpResponse> submitData(@RequestBody OtpRequest otpRequest) {
    return new ResponseEntity<OtpResponse>(new OtpResponse(otpRequest.getOtp(), checkOtpIsValid(otpRequest.getOtp())),
        HttpStatus.OK);
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
    boolean isValid = checkOtpIsValid(otpID);
    dataRepository.deleteById(otpID);
    return new ResponseEntity<>(new OtpResponse(otpID, isValid),
        HttpStatus.OK);
  }


  /**
   * Checks if requested otp exists in the database and is valid.
   *
   * @param otp String unique id
   * @return true if otp exists and not expired
   */
  public boolean checkOtpIsValid(String otp) {
    return dataRepository.findById(otp).filter(otpData ->
        otpData.getExpirationDate().isAfter(LocalDate.now(ZoneOffset.UTC))).isPresent();
  }
}
