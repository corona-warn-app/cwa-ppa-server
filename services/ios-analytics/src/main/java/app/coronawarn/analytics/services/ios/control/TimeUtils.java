package app.coronawarn.analytics.services.ios.control;

import java.time.OffsetDateTime;
import java.time.ZoneOffset;
import java.time.format.DateTimeFormatter;
import java.time.temporal.TemporalAdjusters;
import org.springframework.stereotype.Component;

@Component
public class TimeUtils {

  /**
   * The {@link app.coronawarn.analytics.common.persistence.domain.ApiToken} expects its expiration date to be the last
   * day of the month.
   *
   * @param offsetDateTime the time that is used as basis to find the last day of the month
   * @param zoneOffset     the zonoffset used for calculation.
   * @return a time that is equal to the last day of the month.
   */
  public OffsetDateTime getLastDayOfMonthFor(OffsetDateTime offsetDateTime, ZoneOffset zoneOffset) {
    return offsetDateTime
        .withOffsetSameLocal(zoneOffset)
        .with(TemporalAdjusters.lastDayOfMonth());
  }

  /**
   * Convert the current time according to a dateformat and a zoneoffset to a string.
   *
   * @param zoneOffset     the zoneoffset to consider.
   * @param dateTimeFormat the datetime format that is used to format the time.
   * @return an offsetdatetime as string in the given format.
   */
  public String getCurrentTimeFor(ZoneOffset zoneOffset, String dateTimeFormat) {
    return OffsetDateTime
        .now()
        .atZoneSameInstant(zoneOffset)
        .format(DateTimeFormatter
            .ofPattern(dateTimeFormat));
  }
}
