package app.coronawarn.datadonation.services.edus.otp;

import app.coronawarn.datadonation.common.persistence.service.OtpTestGenerationResponse;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.ActiveProfiles;
import java.time.ZoneOffset;
import java.time.ZonedDateTime;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@ActiveProfiles("generate-els-otp")
@DirtiesContext
public class GenerateElsOtpControllerTest {

  @Autowired
  GenerateElsOtpController generateElsOtpController;

  @Test
  void testElsOtpsAreCreated() {
    int numberOfInvocations = 15;
    int validityInHours = 5;

    List<OtpTestGenerationResponse> responses = generateElsOtpController
        .generateElsOtp(numberOfInvocations, validityInHours)
        .getBody();

    assert responses != null;
    assertThat(responses.size()).isEqualTo(numberOfInvocations);

    for (OtpTestGenerationResponse response : responses) {
      assertThat(ZonedDateTime.now(ZoneOffset.UTC).plusHours(validityInHours))
          .isEqualToIgnoringSeconds(response.getExpirationDate());
    }
  }
}