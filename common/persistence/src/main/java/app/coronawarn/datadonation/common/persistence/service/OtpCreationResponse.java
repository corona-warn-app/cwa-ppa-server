package app.coronawarn.datadonation.common.persistence.service;

import com.fasterxml.jackson.annotation.JsonFormat;
import java.time.ZonedDateTime;

public class OtpCreationResponse {

  @JsonFormat(pattern = "yyyy-MM-dd'T'HH:mm:ssXXX")
  private ZonedDateTime expirationDate;

  public OtpCreationResponse() {
    // empty constructor
  }

  public OtpCreationResponse(ZonedDateTime expirationDate) {
    this.expirationDate = expirationDate;
  }

  public ZonedDateTime getExpirationDate() {
    return expirationDate;
  }

  public void setExpirationDate(ZonedDateTime expirationDate) {
    this.expirationDate = expirationDate;
  }
}
