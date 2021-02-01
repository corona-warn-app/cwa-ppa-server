package app.coronawarn.datadonation.common.config;

import static org.junit.jupiter.api.Assertions.assertEquals;
import org.junit.jupiter.api.Test;

class UrlConstantsTest {

  /**
   * Test API version path.
   */
  @Test
  void testAPIVersionPath() {
    assertEquals("/version/v1", UrlConstants.V1);
  }

  /**
   * Relevant for iOS mobile devices.
   */
  @Test
  void testIOSAPIPaths() {
    assertEquals("/version/v1/ios", UrlConstants.IOS); // iOSController - RequestMapping
    assertEquals("/version/v1/ios/dat", UrlConstants.IOS + UrlConstants.DATA); // iOSController - PostMapping
    assertEquals("/version/v1/ios/otp", UrlConstants.IOS + UrlConstants.OTP); // iOSController - PostMapping
    assertEquals("/version/v1/ios/log", UrlConstants.IOS + UrlConstants.LOG); // iOSController - PostMapping
  }

  /**
   * Relevant for Android mobile devices.
   */
  @Test
  void testAndroidAPIPaths() {
    assertEquals("/version/v1/android", UrlConstants.ANDROID); // AndroidController - RequestMapping
    assertEquals("/version/v1/android/dat", UrlConstants.ANDROID + UrlConstants.DATA); // AndroidController - PostMapping
    assertEquals("/version/v1/android/otp", UrlConstants.ANDROID + UrlConstants.OTP); // AndroidController - PostMapping
    assertEquals("/version/v1/android/log", UrlConstants.ANDROID + UrlConstants.LOG); // AndroidController - PostMapping
  }

  /**
   * Relevant for RKI survey system, EDUS hosted under https://survey.data.coronawarn.app
   */
  @Test
  void testOTPAPIPaths() {
    assertEquals("/version/v1", UrlConstants.SURVEY); // OTPController - RequestMapping
    assertEquals("/version/v1/otp", UrlConstants.SURVEY + UrlConstants.OTP); // OTPController - PostMapping
  }

}
