package app.coronawarn.datadonation.common.persistence.service;

import java.time.LocalDateTime;
import java.time.ZonedDateTime;

public class OtpCreationResponse {

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
