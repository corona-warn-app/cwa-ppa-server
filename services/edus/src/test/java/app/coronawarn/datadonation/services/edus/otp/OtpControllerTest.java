package app.coronawarn.datadonation.services.edus.otp;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

import app.coronawarn.datadonation.common.persistence.domain.OneTimePassword;
import org.junit.jupiter.api.Test;

final class OtpControllerTest {

  @Test
  void testCalculateStrongClientIntegrityCheckInvalid() {
    final OneTimePassword invalid = new OneTimePassword(null, null, null, true, null, null, null);
    assertFalse(OtpController.calculateStrongClientIntegrityCheck(invalid));
  }

  @Test
  void testCalculateStrongClientIntegrityCheckValidAndroid() {
    final OneTimePassword valid = new OneTimePassword(null, null, null, true, true, true, true);
    assertTrue(OtpController.calculateStrongClientIntegrityCheck(valid));
  }

  @Test
  void testCalculateStrongClientIntegrityCheckValidIos() {
    final OneTimePassword valid = new OneTimePassword();
    assertTrue(OtpController.calculateStrongClientIntegrityCheck(valid));
  }

  @Test
  void testIsOtpFromIosDevice() {
    final OneTimePassword valid = new OneTimePassword();
    assertTrue(OtpController.isOtpFromIosDevice(valid));
  }

  @Test
  void testIsOtpFromValidAndroidDevice() {
    final OneTimePassword valid = new OneTimePassword(null, null, null, true, true, true, true);
    assertTrue(OtpController.isOtpFromValidAndroidDevice(valid));
  }

  @Test
  void testOtpController() {
    OtpController otpController = new OtpController(null);
    assertNotNull(otpController.toString());
  }
}
