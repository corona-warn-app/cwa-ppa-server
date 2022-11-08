package app.coronawarn.datadonation.services.srs.otp;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import app.coronawarn.datadonation.common.persistence.domain.OneTimePassword;
import org.junit.jupiter.api.Test;

final class SrsOtpControllerTest {

  @Test
  void testCalculateStrongClientIntegrityCheckInvalid() {
    final OneTimePassword invalid = new OneTimePassword(null, null, null, true, null, null, null);
    assertFalse(SrsOtpController.calculateStrongClientIntegrityCheck(invalid));
  }

  @Test
  void testCalculateStrongClientIntegrityCheckValidAndroid() {
    final OneTimePassword valid = new OneTimePassword(null, null, null, true, true, true, true);
    assertTrue(SrsOtpController.calculateStrongClientIntegrityCheck(valid));
  }

  @Test
  void testCalculateStrongClientIntegrityCheckValidIos() {
    final OneTimePassword valid = new OneTimePassword();
    assertTrue(SrsOtpController.calculateStrongClientIntegrityCheck(valid));
  }

  @Test
  void testIsOtpFromIosDevice() {
    final OneTimePassword valid = new OneTimePassword();
    assertTrue(SrsOtpController.isOtpFromIosDevice(valid));
  }

  @Test
  void testIsOtpFromValidAndroidDevice() {
    final OneTimePassword valid = new OneTimePassword(null, null, null, true, true, true, true);
    assertTrue(SrsOtpController.isOtpFromValidAndroidDevice(valid));
  }

  @Test
  void testOtpController() {
    final SrsOtpController controller = new SrsOtpController(null);
    assertThat(controller).isNotNull();
  }
}
