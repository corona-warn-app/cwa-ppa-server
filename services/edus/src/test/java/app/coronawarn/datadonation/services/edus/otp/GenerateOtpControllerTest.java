package app.coronawarn.datadonation.services.edus.otp;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import app.coronawarn.datadonation.common.persistence.service.OtpService;
import app.coronawarn.datadonation.common.persistence.service.OtpTestGenerationResponse;
import java.time.ZoneOffset;
import java.time.ZonedDateTime;
import java.util.List;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.annotation.DirtiesContext;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@DirtiesContext
public class GenerateOtpControllerTest {

  @MockBean
  OtpService otpService;

  @Autowired
  GenerateOtpController generateOtpController;

  @Test
  void testOtpsAreCreated() {
    int numberOfInvocations = 10;
    int validityInHours = 4;

    when(otpService.createOtp(any(), validityInHours))
        .thenReturn(ZonedDateTime.now(ZoneOffset.UTC).plusHours(validityInHours));

    List<OtpTestGenerationResponse> responses = generateOtpController.generateOtp(numberOfInvocations, validityInHours)
        .getBody();

    verify(otpService, times(numberOfInvocations)).createOtp(any(), any());

    assert responses != null;
    for (OtpTestGenerationResponse response : responses) {
      assertThat(ZonedDateTime.now(ZoneOffset.UTC).plusHours(validityInHours))
          .isEqualToIgnoringSeconds(response.getExpirationDate());
    }
  }
}
