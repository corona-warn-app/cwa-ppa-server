package app.coronawarn.datadonation.services.ppac.config;

import static org.junit.jupiter.api.Assertions.assertEquals;
import org.junit.jupiter.api.Test;

class UrlConstantsTest {

  @Test
  void testIOSAPIPaths() {
    assertEquals("/version/v1", UrlConstants.V1);
    assertEquals("/version/v1/ios", UrlConstants.IOS);
    assertEquals("/version/v1/ios/data", UrlConstants.IOS + UrlConstants.DATA);
    assertEquals("/version/v1/ios/otp", UrlConstants.IOS + UrlConstants.OTP);
  }

  @Test
  void testAndroidAPIPaths() {
    assertEquals("/version/v1", UrlConstants.V1);
    assertEquals("/version/v1/android", UrlConstants.ANDROID);
    assertEquals("/version/v1/android/data", UrlConstants.ANDROID + UrlConstants.DATA);
    assertEquals("/version/v1/android/otp", UrlConstants.ANDROID + UrlConstants.OTP);
  }

}
