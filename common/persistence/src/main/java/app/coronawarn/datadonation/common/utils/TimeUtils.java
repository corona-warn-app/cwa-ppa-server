package app.coronawarn.datadonation.common.utils;

import static java.time.ZoneOffset.UTC;

import java.time.DateTimeException;
import java.time.Instant;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.OffsetDateTime;
import java.time.ZoneOffset;
import java.time.ZonedDateTime;
import java.time.temporal.TemporalAdjusters;

/**
 * Time related business logic. All times are handled in UTC time
 */
public class TimeUtils {

  private static Instant now;

  private TimeUtils() {
  }

  /**
   * get epoch seconds for the last day in the month provided by offsetdatetime in UTC.
   *
   * @param offsetDateTime the reference time point
   * @return the epoch seconds for the last day in the provided month
   */
  public static Long getLastDayOfMonthFor(OffsetDateTime offsetDateTime) {
    return offsetDateTime.withOffsetSameInstant(UTC).with(TemporalAdjusters.lastDayOfMonth())
        .toEpochSecond();
  }

  /**
   * returns the epoch seconds for the last day in the current month in UTC.
   *
   * @return the epoch seconds of the current month in UTC.
   */
  public static Long getLastDayOfMonthForNow() {
    return OffsetDateTime.now().withOffsetSameInstant(UTC).with(TemporalAdjusters.lastDayOfMonth())
        .toEpochSecond();
  }

  /**
   * returns the epoch seconds for the provided time in UTC.
   *
   * @param time the reference time point.
   * @return the epoch seconds of the provided time in UTC.
   */
  public static Long getEpochSecondFor(OffsetDateTime time) {
    return time.withOffsetSameInstant(UTC).toEpochSecond();
  }

  /**
   * Calculate the LocalDate based on epoch seconds in UTC.
   *
   * @param epochSecond the epoch seconds as reference point.
   * @return a LocalDate representing the provided epoch seconds.
   * @throws DateTimeException if epochSecond > {@link Instant#MAX} or epochSecond < {@link Instant#MIN}
   */
  public static LocalDate getLocalDateFor(Long epochSecond) {
    return Instant.ofEpochSecond(epochSecond).atOffset(UTC).toLocalDate();
  }

  /**
   * Calculate the ZonedDateTime based on epoch seconds in UTC.
   *
   * @param epochSeconds the epoch seconds as reference point.
   * @return a ZonedDateTime representing the provided epoch seconds.
   */
  public static ZonedDateTime getZonedDateTimeFor(Long epochSeconds) {
    return Instant.ofEpochSecond(epochSeconds).atOffset(UTC).toZonedDateTime();
  }

  /**
   * Returns true if the given timestamp represents a point in time that falls in the time range constructed from the
   * following parameters of type {@link Instant}.
   */
  public static boolean isInRange(long timestamp, Instant rangeLowerLimit, Instant rangeUpperLimit) {
    Instant testedTimeAsInstant = Instant.ofEpochMilli(timestamp);
    return rangeLowerLimit.isBefore(testedTimeAsInstant) && rangeUpperLimit.isAfter(testedTimeAsInstant);
  }

  /**
   * Calculate the LocalData of the current Timestamp in UTC.
   *
   * @return the parsed LocalDate.
   */
  public static LocalDate getLocalDateForNow() {
    return getNow().atOffset(ZoneOffset.UTC).toLocalDate();
  }

  /**
   * Calculate the LocalDateTime of the current Timestamp in UTC.
   *
   * @return the parsed LocalDateTime.
   */
  public static LocalDateTime getLocalDateTimeForNow() {
    return getNow().atOffset(ZoneOffset.UTC).toLocalDateTime();
  }

  /**
   * Calculates the epoch milli seconds in UTC (important for Apple's DeviceCheck).
   *
   * @return the epoch milli seconds of the current Timestamp.
   */
  public static Long getEpochMilliSecondForNow() {
    return getNow().toEpochMilli();
  }

  /**
   * Calculates the epoch seconds in UTC.
   *
   * @return the epoch seconds of the current Timestamp.
   */
  public static Long getEpochSecondsForNow() {
    return getNow().getEpochSecond();
  }

  /**
   * Returns the UTC {@link Instant} time or creates a new instance if called the first time.
   *
   * @return current Instant
   */
  public static Instant getNow() {
    if (now == null) {
      now = Instant.now();
    }
    return now;
  }

  /**
   * Injects UTC instant time value.
   *
   * @param instant an {@link Instant} object.
   */
  public static void setNow(Instant instant) {
    now = instant;
  }
}
