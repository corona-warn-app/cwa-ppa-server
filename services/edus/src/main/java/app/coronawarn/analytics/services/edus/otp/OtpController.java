package app.coronawarn.analytics.services.edus.otp;

import app.coronawarn.analytics.common.persistence.repository.OtpDataRepository;
import java.time.LocalDate;
import java.time.ZoneOffset;
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
   * Checks if requested otp exists in the database and is valid.
   *
   * @param otp String unique id
   * @return true if otp exists and not expired
   */
  public boolean checkOtpIsValid(String otp) {
    AtomicBoolean isValid = new AtomicBoolean(false);

    dataRepository.findById(otp).ifPresent(otpData -> {
      if (otpData.getExpirationDate().isAfter(LocalDate.now(ZoneOffset.UTC))) {
        isValid.set(true);
      }
    });
    return isValid.get();
  }
}
