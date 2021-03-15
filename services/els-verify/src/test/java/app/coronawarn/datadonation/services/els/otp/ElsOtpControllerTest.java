package app.coronawarn.datadonation.services.els.otp;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import app.coronawarn.datadonation.common.persistence.domain.OneTimePassword;
import org.junit.jupiter.api.Test;

final class ElsOtpControllerTest {

  @Test
  void testCalculateStrongClientIntegrityCheckInvalid() {
    final OneTimePassword invalid = new OneTimePassword(null, null, null, true, null, null, null);
    assertFalse(ElsOtpController.calculateStrongClientIntegrityCheck(invalid));
  }

  @Test
  void testCalculateStrongClientIntegrityCheckValidAndroid() {
    final OneTimePassword valid = new OneTimePassword(null, null, null, true, true, true, true);
    assertTrue(ElsOtpController.calculateStrongClientIntegrityCheck(valid));
  }

  @Test
  void testCalculateStrongClientIntegrityCheckValidIos() {
    final OneTimePassword valid = new OneTimePassword();
    assertTrue(ElsOtpController.calculateStrongClientIntegrityCheck(valid));
  }

  @Test
  void testIsOtpFromIosDevice() {
    final OneTimePassword valid = new OneTimePassword();
    assertTrue(ElsOtpController.isOtpFromIosDevice(valid));
  }

  @Test
  void testIsOtpFromValidAndroidDevice() {
    final OneTimePassword valid = new OneTimePassword(null, null, null, true, true, true, true);
    assertTrue(ElsOtpController.isOtpFromValidAndroidDevice(valid));
  }

  @Test
  void testOtpController() {
    new ElsOtpController(null);
  }
}
