package app.coronawarn.datadonation.services.edus.utils;

import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneOffset;

public class TimeUtils {

  public static Long getEpochSecondsForNow() {
    return Instant.now().getEpochSecond();
  }

  public static LocalDateTime getLocalDateTimeFor(Long epochSeconds) {
    return Instant.ofEpochSecond(epochSeconds).atOffset(ZoneOffset.UTC).toLocalDateTime();
  }
}
