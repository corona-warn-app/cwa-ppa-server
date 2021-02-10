package app.coronawarn.datadonation.common.util;

import java.time.Instant;
import java.time.ZoneOffset;
import java.time.ZonedDateTime;

public class TimeUtils {

  public static Long getEpochSecondsForNow() {
    return Instant.now().getEpochSecond();
  }

  public static ZonedDateTime getLocalDateTimeFor(Long epochSeconds) {
    return Instant.ofEpochSecond(epochSeconds).atOffset(ZoneOffset.UTC).toZonedDateTime();
  }
}
