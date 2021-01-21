package app.coronawarn.analytics.services.edus.otp;

import app.coronawarn.analytics.common.persistence.repository.OtpDataRepository;
import java.util.Date;
import java.util.concurrent.atomic.AtomicBoolean;
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
  public static final String EDUS_ROUTE = "/edus/data";
  public static final String REDEMPTION_ROUTE = "TBD"; // TODO: route
  private static final Logger logger = LoggerFactory.getLogger(OtpController.class);

  OtpDataRepository dataRepository;

  public OtpController(OtpDataRepository dataRepository) {
    this.dataRepository = dataRepository;
  }

  /**
   * Handling of Event-driven User Surveys (EDUS).
   *
   * @param otpRequest The application/json payload.
   * @return An empty response body.
   */
  @PostMapping(value = EDUS_ROUTE)
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
    if (isValid) {
      dataRepository.deleteById(otpID);
    } else {
      logger.warn("placeholder"); // TODO: Log Message
    }
    return new ResponseEntity<>(new OtpResponse(otpID, isValid),
        isValid ? HttpStatus.OK : HttpStatus.INTERNAL_SERVER_ERROR);
  }


  /**
   * Checks if requested otp exists in the database and is valid.
   *
   * @param otp String unique id
   * @return true if otp exists and not expired
   */
  public boolean checkOtpIsValid(String otp) {
    AtomicBoolean isValid = new AtomicBoolean(false);

    dataRepository.findById(otp).ifPresent(otpData -> {
      if (otpData.getExpirationDate().after(new Date())) {
        isValid.set(true);
      }
    });
    return isValid.get();
  }
}
