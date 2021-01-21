package app.coronawarn.analytics.services.edus.otp;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

import app.coronawarn.analytics.common.persistence.domain.OneTimePassword;
import app.coronawarn.analytics.common.persistence.repository.OneTimePasswordRepository;
import java.time.LocalDate;
import java.util.Objects;
import java.util.Optional;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.test.annotation.DirtiesContext;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@DirtiesContext
public class OneTimePasswordRedemptionTest {

  @Autowired
  private OtpController otpController;

  @MockBean
  OneTimePasswordRepository dataRepository;


  @Test
  void testOtpControllerResponseOkIsValid() {
    when(dataRepository.findById(any())).thenReturn(Optional.of(new OneTimePassword("uuid4string",
        LocalDate.now().plusDays(1), LocalDate.now().plusDays(1), LocalDate.now().plusDays(1))));

    ResponseEntity<OtpResponse> otpData = otpController.redeemOtp(new OtpRequest());

    assertThat(otpData.getStatusCode()).isEqualTo(HttpStatus.OK);
    assertThat(otpData.getBody().getValid()).isTrue();
  }

  @Test
  void testOtpControllerResponseOkIsNotValid() {
    when(dataRepository.findById(any())).thenReturn(Optional.of(new OneTimePassword("uuid4string",
        LocalDate.now().minusDays(1), LocalDate.now().minusDays(1), LocalDate.now().minusDays(1))));
    ;

    ResponseEntity<OtpResponse> otpData = otpController.redeemOtp(new OtpRequest());

    assertThat(otpData.getStatusCode()).isEqualTo(HttpStatus.OK);
    assertThat(Objects.requireNonNull(otpData.getBody()).getValid()).isFalse();
  }

  @Test
  void testNonExistentOTPIsNotValid() {
    when(dataRepository.findById(any())).thenReturn(Optional.empty());

    ResponseEntity<OtpResponse> otpData = otpController.redeemOtp(new OtpRequest());

    assertThat(otpData.getStatusCode()).isEqualTo(HttpStatus.OK);
    assertThat(Objects.requireNonNull(otpData.getBody()).getValid()).isFalse();
  }
}
