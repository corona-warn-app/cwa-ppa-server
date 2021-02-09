package app.coronawarn.datadonation.common.utils;

import java.time.Instant;
import java.time.LocalDate;
import java.time.OffsetDateTime;
import java.time.ZoneOffset;
import java.time.temporal.TemporalAdjusters;

/**
 * Time related business logic. All times are handled in UTC time
 */
public final class TimeUtils {

  private TimeUtils() {
  }

  /**
   * get epoch seconds for the last day in the month provided by offsetdatetime in UTC.
   *
   * @param offsetDateTime the reference time point
   * @return the epoch seconds for the last day in the provided month
   */
  public static Long getLastDayOfMonthFor(OffsetDateTime offsetDateTime) {
    return offsetDateTime
        .withOffsetSameInstant(ZoneOffset.UTC)
        .with(TemporalAdjusters.lastDayOfMonth()).toEpochSecond();
  }

  /**
   * returns the epoch seconds for the last day in the current month in UTC.
   *
   * @return the epoch seconds of the current month in UTC.
   */
  public static Long getLastDayOfMonthForNow() {
    return OffsetDateTime.now().withOffsetSameInstant(ZoneOffset.UTC).with(TemporalAdjusters.lastDayOfMonth())
        .toEpochSecond();
  }

  /**
   * returns the epoch seconds for the provided time in UTC.
   *
   * @param time the reference time point.
   * @return the epoch seconds of the provided time in UTC.
   */
  public static Long getEpochSecondFor(OffsetDateTime time) {
    return time.withOffsetSameInstant(ZoneOffset.UTC).toEpochSecond();
  }

  /**
   * Calculates the epoch seconds of the current timestamp.
   *
   * @return {@code Instant.now().getEpochSecond()}
   */
  public static Long getEpochSecondForNow() {
    return Instant.now().getEpochSecond();
  }

  /**
   * Calculate the LocalDate based on epoch seconds in UTC.
   *
   * @param epochSecond the epoch seconds as reference point.
   * @return a LocalDate representing the provided epoch seconds.
   */
  public static LocalDate getLocalDateFor(Long epochSecond) {
    return Instant.ofEpochSecond(epochSecond).atOffset(ZoneOffset.UTC).toLocalDate();
  }

  /**
   * Returns true if the given timestamp represents a point in time that falls in the time range constructed from the
   * following parameters of type {@link Instant}.
   */
  public static boolean isInRange(long timestamp, Instant rangeLowerLimit,
      Instant rangeUpperLimit) {
    Instant testedTimeAsInstant = Instant.ofEpochMilli(timestamp);
    return rangeLowerLimit.isBefore(testedTimeAsInstant)
        && rangeUpperLimit.isAfter(testedTimeAsInstant);
  }

  /**
   * Calculate the LocalData of the current Timestamp in UTC.
   *
   * @return the parsed LocalDate.
   */
  public static LocalDate getLocalDateForNow() {
    return Instant.now().atOffset(ZoneOffset.UTC).toLocalDate();
  }

  /**
   * Calculates the epcoch milli seconds in UTC (important for Apple's DeviceCheck).
   *
   * @return the epoch milli seconds of the current Timestamp.
   */
  public static Long getEpochMilliSecondForNow() {
    return Instant.now().toEpochMilli();
  }


}
