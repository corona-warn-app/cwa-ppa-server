package app.coronawarn.datadonation.common.persistence.service;

import static java.time.format.DateTimeFormatter.ofPattern;

import com.fasterxml.jackson.annotation.JsonFormat;
import java.time.ZonedDateTime;

public class OtpCreationResponse {

  public static final String DATE_PATTERN = "yyyy-MM-dd'T'HH:mm:ssXXX";

  @JsonFormat(pattern = DATE_PATTERN)
  private ZonedDateTime expirationDate;

  public OtpCreationResponse() {
    // empty constructor
  }

  public OtpCreationResponse(final ZonedDateTime expirationDate) {
    this.expirationDate = expirationDate;
  }

  public ZonedDateTime getExpirationDate() {
    return expirationDate;
  }

  public void setExpirationDate(final ZonedDateTime expirationDate) {
    this.expirationDate = expirationDate;
  }

  @Override
  public String toString() {
    return "{\"expirationDate\":\"" + expirationDate.format(ofPattern(DATE_PATTERN)) + "\"}";
  }
}
