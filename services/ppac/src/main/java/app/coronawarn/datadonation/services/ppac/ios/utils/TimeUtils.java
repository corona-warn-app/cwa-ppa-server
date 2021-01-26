package app.coronawarn.datadonation.services.ppac.ios.utils;

import app.coronawarn.datadonation.common.persistence.domain.ApiToken;
import java.time.Instant;
import java.time.LocalDate;
import java.time.OffsetDateTime;
import java.time.ZoneOffset;
import java.time.temporal.TemporalAdjusters;

public final class TimeUtils {

  /**
   * The {@link ApiToken} expects its expiration date to be the last day of the month.
   *
   * @param offsetDateTime the time that is used as basis to find the last day of the month
   * @return a time that is equal to the last day of the month.
   */
  public static Long getLastDayOfMonthFor(OffsetDateTime offsetDateTime) {
    return offsetDateTime
        .withOffsetSameInstant(ZoneOffset.UTC)
        .with(TemporalAdjusters.lastDayOfMonth()).toEpochSecond();
  }

  public static Long getLastDayOfMonthForNow() {
    return OffsetDateTime.now().withOffsetSameInstant(ZoneOffset.UTC).with(TemporalAdjusters.lastDayOfMonth())
        .toEpochSecond();
  }

  public static Long getEpochSecondFor(OffsetDateTime time) {
    return time.withOffsetSameInstant(ZoneOffset.UTC).toEpochSecond();
  }

  public static Long getEpochSecondForNow() {
    return Instant.now().getEpochSecond();
  }

  public static LocalDate getLocalDateFor(Long epochSecond) {
    return Instant.ofEpochSecond(epochSecond).atOffset(ZoneOffset.UTC).toLocalDate();
  }

  public static LocalDate getLocalDateForNow() {
    return Instant.now().atOffset(ZoneOffset.UTC).toLocalDate();
  }

  public static Long getEpochMilliSecondForNow() {
    return Instant.now().toEpochMilli();
  }


}
