package app.coronawarn.datadonation.common.persistence.service;

import com.fasterxml.jackson.annotation.JsonFormat;
import java.time.ZonedDateTime;
import org.springframework.context.annotation.Profile;

@Profile("test-otp")
public class OtpTestGenerationResponse {

  @JsonFormat(pattern = "yyyy-MM-dd'T'HH:mm:ssXXX")
  private ZonedDateTime expirationDate;

  private String otp;

  public OtpTestGenerationResponse(ZonedDateTime expirationDate, String otp) {
    this.expirationDate = expirationDate;
    this.otp = otp;
  }

  public ZonedDateTime getExpirationDate() {
    return expirationDate;
  }

  public void setExpirationDate(ZonedDateTime expirationDate) {
    this.expirationDate = expirationDate;
  }

  public String getOtp() {
    return otp;
  }

  public void setOtp(String otp) {
    this.otp = otp;
  }
}
