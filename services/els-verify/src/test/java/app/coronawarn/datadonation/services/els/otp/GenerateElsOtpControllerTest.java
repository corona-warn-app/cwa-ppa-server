package app.coronawarn.datadonation.services.els.otp;

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
@ActiveProfiles("generate-els-otp")
@DirtiesContext
class GenerateElsOtpControllerTest {

  private static final Logger logger = LoggerFactory.getLogger(GenerateElsOtpControllerTest.class);

  @Autowired
  GenerateElsOtpController generateElsOtpController;

  @Test
  void testElsOtpsAreCreated() {
    final int numberOfInvocations = 15;
    final int validityInHours = 5;

    final Collection<OtpTestGenerationResponse> responses = generateElsOtpController
        .generateElsOtp(numberOfInvocations, validityInHours).getBody();
    final ZonedDateTime expected = ZonedDateTime.now(ZoneOffset.UTC).plusHours(validityInHours);

    assert responses != null;
    assertThat(responses.size()).isEqualTo(numberOfInvocations);

    if (expected.getSecond() < 1) {
      // chance is high, that the test was started at second 59, but execution time was too long and now we're in a
      // different minute, so let's skip the assertions!
      logger.warn("skipping: {} - 'testElsOtpsAreCreated'", this);
      return;
    }
    for (final OtpTestGenerationResponse response : responses) {
      assertThat(expected).isEqualToIgnoringSeconds(response.getExpirationDate());
    }
  }
}
