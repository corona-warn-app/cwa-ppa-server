package app.coronawarn.datadonation.services.edus.otp;

import static app.coronawarn.datadonation.common.config.UrlConstants.OTP;
import static app.coronawarn.datadonation.common.config.UrlConstants.SURVEY;

import app.coronawarn.datadonation.common.persistence.domain.OneTimePassword;
import app.coronawarn.datadonation.common.persistence.service.OtpService;
import app.coronawarn.datadonation.common.persistence.service.OtpTestGenerationResponse;
import java.time.ZonedDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.annotation.Profile;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping(SURVEY)
@Profile("generate-otp")
public class GenerateOtpController {

  /**
   * The route to the Event-driven User Surveys endpoint for OTP testing.
   */
  private static final Logger logger = LoggerFactory.getLogger(GenerateOtpController.class);

  private OtpService otpService;

  public GenerateOtpController(OtpService otpService) {
    this.otpService = otpService;
    logger.warn("DON'T USE PROFILE 'generate-otp' IN PRODUCTION ENVIRONMENT!");
  }

  /**
   * Generate a list of OTPs for testing purpose.
   *
   * @return Response that contains a list with new generated OTPs.
   */
  @GetMapping(value = OTP + "/{number}/{validity}")
  public ResponseEntity<List<OtpTestGenerationResponse>> generateOtp(
      @PathVariable(name = "number") Integer number, @PathVariable("validity") Integer validity) {
    List<OtpTestGenerationResponse> generatedOtps = new ArrayList<>();
    for (int i = 0; i < number; i++) {
      String password = UUID.randomUUID().toString();
      ZonedDateTime expirationTime = otpService.createOtp(new OneTimePassword(password),
          validity);
      OtpTestGenerationResponse otpTestGenerationResponse =
          new OtpTestGenerationResponse(expirationTime, password);
      generatedOtps.add(otpTestGenerationResponse);
    }
    return ResponseEntity.status(HttpStatus.OK).body(generatedOtps);
  }
}
