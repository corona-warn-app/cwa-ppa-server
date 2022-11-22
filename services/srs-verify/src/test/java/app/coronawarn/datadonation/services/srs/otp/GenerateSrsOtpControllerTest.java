package app.coronawarn.datadonation.services.srs.otp;

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
@ActiveProfiles("generate-srs-otp")
@DirtiesContext
class GenerateSrsOtpControllerTest {

  private static final Logger logger = LoggerFactory.getLogger(GenerateSrsOtpControllerTest.class);

  @Autowired
  GenerateSrsOtpController generateSrsOtpController;

  @Test
  void testSrsOtpsAreCreated() {
    final int numberOfInvocations = 15;
    final int validityInMinutes = 5;

    final ZonedDateTime expected = ZonedDateTime.now(ZoneOffset.UTC).plusMinutes(validityInMinutes);
    final Collection<OtpTestGenerationResponse> responses = generateSrsOtpController
        .generateSrsOtp(numberOfInvocations, validityInMinutes).getBody();

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
