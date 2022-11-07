package app.coronawarn.datadonation.services.srs.otp;

import static app.coronawarn.datadonation.common.config.UrlConstants.SRS;
import static app.coronawarn.datadonation.common.config.UrlConstants.SRS_VERIFY;

import app.coronawarn.datadonation.common.persistence.domain.SrsOneTimePassword;
import app.coronawarn.datadonation.common.persistence.service.OtpTestGenerationResponse;
import app.coronawarn.datadonation.common.persistence.service.SrsOtpService;
import java.time.ZonedDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Profile;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping(SRS_VERIFY)
@Profile("generate-srs-otp")
public class GenerateSrsOtpController {

  /**
   * The route to the Self-Report Submission endpoint for SRS OTP testing.
   */
  private static final Logger LOGGER = LoggerFactory.getLogger(GenerateSrsOtpController.class);

  @Autowired
  private final SrsOtpService srsOtpService;

  public GenerateSrsOtpController(final SrsOtpService srsOtpService) {
    this.srsOtpService = srsOtpService;
    LOGGER.warn("DON'T USE PROFILE 'generate-srs-otp' IN PRODUCTION ENVIRONMENT!");
  }

  /**
   * Generate a list of SRS OPTs for testing purpose.
   *
   * @return Response that contains a list with new generated SRS OPTs.
   */
  @GetMapping(value = SRS + "/{number}/{validity}")
  public ResponseEntity<List<OtpTestGenerationResponse>> generateSrsOtp(
      @PathVariable(name = "number") final Integer number, @PathVariable("validity") final Integer validity) {
    final List<OtpTestGenerationResponse> generatedOtps = new ArrayList<>();
    for (int i = 0; i < number; i++) {
      final String password = UUID.randomUUID().toString();
      final ZonedDateTime expirationTime = srsOtpService.createOtp(new SrsOneTimePassword(password),
          validity);
      final OtpTestGenerationResponse otpTestGenerationResponse = new OtpTestGenerationResponse(expirationTime,
          password);
      generatedOtps.add(otpTestGenerationResponse);
    }
    return ResponseEntity.status(HttpStatus.OK).body(generatedOtps);
  }
}
