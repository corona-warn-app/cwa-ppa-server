package app.coronawarn.analytics.services.edus.otp;

import app.coronawarn.analytics.common.persistence.domain.OtpData;
import app.coronawarn.analytics.common.persistence.repository.OtpDataRepository;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.test.annotation.DirtiesContext;
import java.util.Calendar;
import java.util.Date;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@DirtiesContext
public class OtpRedemptionTest {

  @Autowired
  private OtpController otpController;

  @MockBean
  OtpDataRepository dataRepository;


  @Test
  void testOtpControllerResponseOkIsValid() {
    Calendar calendar = Calendar.getInstance();
    calendar.add(Calendar.DAY_OF_YEAR, 1);
    Date tomorrow = calendar.getTime();

    when(dataRepository.findById(any())).thenReturn(Optional.of(new OtpData("uuid4string",
        tomorrow, tomorrow, tomorrow)));

    ResponseEntity<OtpResponse> otpData = otpController.redeemOtp(new OtpRequest());

    assertThat(otpData.getStatusCode()).isEqualTo(HttpStatus.OK);
    assertThat(otpData.getBody().getValid()).isTrue();
  }

  @Test
  void testOtpControllerResponseOkIsNotValid() {
    Calendar calendar = Calendar.getInstance();
    calendar.add(Calendar.DAY_OF_YEAR, -1);
    Date yesterday = calendar.getTime();

    when(dataRepository.findById(any())).thenReturn(Optional.of(new OtpData("uuid4string",
        yesterday, yesterday, yesterday)));

    ResponseEntity<OtpResponse> otpData = otpController.redeemOtp(new OtpRequest());

    assertThat(otpData.getStatusCode()).isEqualTo(HttpStatus.OK);
    assertThat(otpData.getBody().getValid()).isFalse();
  }

  @Test
  void testNonExistentOTPIsNotValid() {
    when(dataRepository.findById(any())).thenReturn(Optional.empty());

    ResponseEntity<OtpResponse> otpData = otpController.redeemOtp(new OtpRequest());

    assertThat(otpData.getStatusCode()).isEqualTo(HttpStatus.OK);
    assertThat(otpData.getBody().getValid()).isFalse();
  }
}
