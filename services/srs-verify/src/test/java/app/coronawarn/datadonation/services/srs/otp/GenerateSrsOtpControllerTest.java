package app.coronawarn.datadonation.services.srs.otp;

import static org.assertj.core.api.Assertions.assertThat;

import app.coronawarn.datadonation.common.persistence.service.OtpTestGenerationResponse;
import java.time.ZoneOffset;
import java.time.ZonedDateTime;
import java.util.List;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.ActiveProfiles;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@ActiveProfiles("generate-srs-otp")
@DirtiesContext
class GenerateSrsOtpControllerTest {

  @Autowired
  GenerateSrsOtpController generateSrsOtpController;

  @Test
  void testSrsOtpsAreCreated() {
    final int numberOfInvocations = 15;
    final int validityInMinutes = 5;

    final List<OtpTestGenerationResponse> responses = generateSrsOtpController
        .generateSrsOtp(numberOfInvocations, validityInMinutes)
        .getBody();

    assert responses != null;
    assertThat(responses.size()).isEqualTo(numberOfInvocations);

    for (final OtpTestGenerationResponse response : responses) {
      assertThat(ZonedDateTime.now(ZoneOffset.UTC).plusMinutes(validityInMinutes))
          .isEqualToIgnoringSeconds(response.getExpirationDate());
    }
  }
}
