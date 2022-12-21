package app.coronawarn.datadonation.services.edus.otp;

import static org.assertj.core.api.Assertions.assertThat;

import app.coronawarn.datadonation.common.persistence.service.OtpTestGenerationResponse;
import java.time.ZoneOffset;
import java.time.ZonedDateTime;
import java.util.Collection;
import org.junit.jupiter.api.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.ActiveProfiles;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@ActiveProfiles("generate-otp")
@DirtiesContext
class GenerateOtpControllerTest {

  private static final Logger logger = LoggerFactory.getLogger(GenerateOtpControllerTest.class);

  @Autowired
  GenerateOtpController generateOtpController;

  @Test
  void testOtpsAreCreated() {
    final int numberOfInvocations = 15;
    final int validityInHours = 5;

    final ZonedDateTime expected = ZonedDateTime.now(ZoneOffset.UTC).plusHours(validityInHours);
    final Collection<OtpTestGenerationResponse> responses = generateOtpController
        .generateOtp(numberOfInvocations, validityInHours).getBody();

    assert responses != null;
    assertThat(responses.size()).isEqualTo(numberOfInvocations);

    if (ZonedDateTime.now(ZoneOffset.UTC).getMinute() != expected.getMinute()) {
      // we are not within the same minute anymore :-(
      logger.warn("skipping: {} - 'testElsOtpsAreCreated'", this);
      return;
    }

    for (final OtpTestGenerationResponse response : responses) {
      assertThat(expected).isEqualToIgnoringSeconds(response.getExpirationDate());
    }
  }
}
