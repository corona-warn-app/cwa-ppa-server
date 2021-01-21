package app.coronawarn.analytics.services.edus.otp;

import app.coronawarn.analytics.common.persistence.domain.OtpData;
import app.coronawarn.analytics.common.persistence.repository.OtpDataRepository;
import java.util.Date;
import java.util.Optional;
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
public class OtpRedemptionController {

  /**
   * The route to the otp redemption endpoint (version agnostic).
   */
  public static final String REDEMPTION_ROUTE = "x"; // TODO: PATH TBD
  private static final Logger logger = LoggerFactory.getLogger(OtpRedemptionController.class);

  OtpDataRepository dataRepository;

  public OtpRedemptionController(OtpDataRepository dataRepository) {
    this.dataRepository = dataRepository;
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
    Optional<OtpData> otp = dataRepository.findById(otpID);
    boolean isValid = false;
    if (otp.isPresent()) {
      isValid = otp.get().getExpirationDate().after(new Date());
      dataRepository.deleteById(otpID);
    } else {
      logger.warn("no result"); // TODO: Log Message
    }
    return new ResponseEntity<>(new OtpResponse(otpID, isValid),
        isValid ? HttpStatus.OK : HttpStatus.INTERNAL_SERVER_ERROR);
  }
}
