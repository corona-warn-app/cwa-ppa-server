package app.coronawarn.datadonation.common.utils;

import static java.time.ZoneOffset.UTC;
import static java.util.concurrent.TimeUnit.DAYS;
import static java.util.concurrent.TimeUnit.HOURS;
import static java.util.concurrent.TimeUnit.MINUTES;
import static java.util.concurrent.TimeUnit.SECONDS;

import java.time.Clock;
import java.time.DateTimeException;
import java.time.Instant;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.OffsetDateTime;
import java.time.YearMonth;
import java.time.ZonedDateTime;
import java.time.temporal.TemporalAdjusters;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Time related business logic. All times are handled in UTC time
 */
public class TimeUtils {

  private static final Logger LOGGER = LoggerFactory.getLogger(TimeUtils.class);

  private static Clock clock = Clock.systemUTC();

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
    return OffsetDateTime.now(clock).withOffsetSameInstant(UTC).with(TemporalAdjusters.lastDayOfMonth())
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
    return Instant.now(clock).atOffset(UTC).toLocalDate();
  }

  /**
   * Calculate the LocalData of the current Timestamp in UTC.
   *
   * @return the parsed LocalDate.
   */
  public static LocalDateTime getLocalDateTimeForNow() {
    return Instant.now(clock).atOffset(UTC).toLocalDateTime();
  }

  /**
   * Get the YearMonth for now.
   *
   * @return The YearMonth by using the local clock.
   */
  public static YearMonth getYearMonthNow() {
    return YearMonth.now(clock);
  }

  /**
   * Calculates the epoch milli seconds in UTC (important for Apple's DeviceCheck).
   *
   * @return the epoch milli seconds of the current Timestamp.
   */
  public static Long getEpochMilliSecondForNow() {
    return Instant.now(clock).toEpochMilli();
  }

  /**
   * Calculates the epoch seconds in UTC.
   *
   * @return the epoch seconds of the current Timestamp.
   */
  public static Long getEpochSecondsForNow() {
    return Instant.now(clock).getEpochSecond();
  }

  /**
   * Format the given seconds to a human-readable format, something like hh:MM:ss, e.g. 07:18:14. <br />
   * Note: A negative input will result in something like -16:-41:-44 ... on purpose.
   *
   * @param seconds The seconds to format to a String.
   * @return A String displaying the seconds in human-readable format.
   */
  public static String formatToHours(long seconds) {
    long hours = SECONDS.toHours(seconds);
    long remainderMinutes = SECONDS.toMinutes(seconds - HOURS.toSeconds(hours));
    long remainderSeconds = seconds - (MINUTES.toSeconds(remainderMinutes) + HOURS.toSeconds(hours));
    return String.format("%02d:%02d:%02d", hours, remainderMinutes, remainderSeconds);
  }

  /**
   * Format the given seconds to a human-readable format, something like "dd days, hh:MM:ss",
   * e.g. "5 days, 07:18:14". <br />
   * Note: A negative input will result in something like -16:-41:-44 ... on purpose.
   *
   * @param seconds The seconds to format to a String.
   * @return A String displaying the seconds in human-readable format.
   */
  public static String formatToDays(long seconds) {
    long days = SECONDS.toDays(seconds);
    long remainderHours = SECONDS.toHours(seconds - DAYS.toSeconds(days));
    long remainderMinutes = SECONDS.toMinutes(seconds - (HOURS.toSeconds(remainderHours) + DAYS.toSeconds(days)));
    long remainderSeconds = seconds
        - (MINUTES.toSeconds(remainderMinutes) + HOURS.toSeconds(remainderHours) + DAYS.toSeconds(days));
    return String.format("%02d days, %02d:%02d:%02d", days, remainderHours, remainderMinutes, remainderSeconds);
  }

  /**
   * Returns the UTC {@link Instant} time or creates a new instance if called the first time.
   *
   * @return current Instant
   */
  public static Instant getNow() {
    return Instant.now(clock);
  }

  /**
   * Injects UTC instant time value.<br />
   *
   * <strong>NOTE: THIS IS ONLY FOR TESTING PURPOSES!</strong>
   *
   * @param instant an {@link Instant} as a fixed time to set.
   */
  public static void setNow(Instant instant) {
    if (instant == null) {
      clock = Clock.systemUTC();
      return;
    }
    LOGGER.warn("Setting the clock to a fixed time. THIS SHOULD NEVER BE USED IN PRODUCTION!");
    clock = Clock.fixed(instant, UTC);
  }
}
