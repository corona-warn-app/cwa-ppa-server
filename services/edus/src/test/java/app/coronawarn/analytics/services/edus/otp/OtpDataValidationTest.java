package app.coronawarn.analytics.services.edus.otp;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

import app.coronawarn.analytics.common.persistence.domain.OtpData;
import app.coronawarn.analytics.common.persistence.repository.OtpDataRepository;
import java.util.Calendar;
import java.util.Date;
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
public class OtpDataValidationTest {

  @Autowired
  private OtpController otpController;

  @MockBean
  OtpDataRepository dataRepository;

  @Test
  void testOtpExpirationDateIsInTheFuture() {
    Calendar calendar = Calendar.getInstance();
    calendar.add(Calendar.DAY_OF_YEAR, 1);
    Date tomorrow = calendar.getTime();

    when(dataRepository.findById(any())).thenReturn(Optional.of(new OtpData("uuid4string",
        tomorrow)));

    assertThat(otpController.checkOtpIsValid("uuid4string")).isTrue();
  }

  @Test
  void testOtpExpirationDateIsInThePast() {
    Calendar calendar = Calendar.getInstance();
    calendar.add(Calendar.DAY_OF_YEAR, -1);
    Date yesterday = calendar.getTime();

    when(dataRepository.findById(any())).thenReturn(Optional.of(new OtpData("uuid4string",
        yesterday)));

    assertThat(otpController.checkOtpIsValid("uuid4string")).isFalse();
  }

  @Test
  void testOtpControllerResponseOkIsValid() {
    Calendar calendar = Calendar.getInstance();
    calendar.add(Calendar.DAY_OF_YEAR, 1);
    Date tomorrow = calendar.getTime();

    when(dataRepository.findById(any())).thenReturn(Optional.of(new OtpData("uuid4string",
        tomorrow)));

    ResponseEntity<OtpResponse> otpData = otpController.submitData(new OtpRequest());

    assertThat(otpData.getStatusCode()).isEqualTo(HttpStatus.OK);
    assertThat(otpData.getBody().getValid()).isTrue();
  }

  @Test
  void testOtpControllerResponseOkIsNotValid() {
    Calendar calendar = Calendar.getInstance();
    calendar.add(Calendar.DAY_OF_YEAR, -1);
    Date yesterday = calendar.getTime();

    when(dataRepository.findById(any())).thenReturn(Optional.of(new OtpData("uuid4string",
        yesterday)));

    ResponseEntity<OtpResponse> otpData = otpController.submitData(new OtpRequest());

    assertThat(otpData.getStatusCode()).isEqualTo(HttpStatus.OK);
    assertThat(otpData.getBody().getValid()).isFalse();
  }

  /*@Test
  void testOtpControllerResponseIs500() {
    Calendar calendar = Calendar.getInstance();
    calendar.add(Calendar.DAY_OF_YEAR, -1);
    Date yesterday = calendar.getTime();

    when(dataRepository.findById(any())).thenThrow(new RuntimeException());

    ResponseEntity<OtpResponse> otpData = otpController.submitData(new OtpRequest());

    assertThat(otpData.getStatusCode()).isEqualTo(HttpStatus.INTERNAL_SERVER_ERROR);
  }*/
}
