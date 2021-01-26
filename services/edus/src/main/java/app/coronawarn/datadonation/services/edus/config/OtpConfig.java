package app.coronawarn.datadonation.services.edus.config;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;
import org.springframework.validation.annotation.Validated;

@Component
@ConfigurationProperties(prefix = "services.edus")
@Validated
public class OtpConfig {

  private int otpValidityInHours;

  public int getOtpValidityInHours() {
    return otpValidityInHours;
  }

  public void setOtpValidityInHours(int otpValidityInHours) {
    this.otpValidityInHours = otpValidityInHours;
  }
}
