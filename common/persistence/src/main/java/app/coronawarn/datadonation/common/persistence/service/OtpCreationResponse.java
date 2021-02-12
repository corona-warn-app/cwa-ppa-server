package app.coronawarn.datadonation.common.persistence.service;

import com.fasterxml.jackson.annotation.JsonFormat;
import java.time.ZonedDateTime;

public class OtpCreationResponse {

  @JsonFormat(pattern = "yyyy-MM-dd'T'HH:mm:ssXXX")
  private ZonedDateTime expirationTime;

  public OtpCreationResponse() {
    // empty constructor
  }

  public OtpCreationResponse(ZonedDateTime expirationTime) {
    this.expirationTime = expirationTime;
  }

  public ZonedDateTime getExpirationTime() {
    return expirationTime;
  }

  public void setExpirationTime(ZonedDateTime expirationTime) {
    this.expirationTime = expirationTime;
  }
}
