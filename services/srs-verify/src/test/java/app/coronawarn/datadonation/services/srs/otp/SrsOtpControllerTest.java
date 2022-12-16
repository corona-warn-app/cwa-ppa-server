package app.coronawarn.datadonation.services.srs.otp;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.jupiter.api.Test;

final class SrsOtpControllerTest {

  @Test
  void testOtpController() {
    final SrsOtpController controller = new SrsOtpController(null);
    assertThat(controller).isNotNull();
  }
}
