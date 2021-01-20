package app.coronawarn.analytics.services.ios.control;

import org.springframework.stereotype.Component;

import java.time.OffsetDateTime;
import java.time.ZoneOffset;
import java.time.format.DateTimeFormatter;
import java.time.temporal.TemporalAdjusters;

@Component
public class TimeUtils {

    public OffsetDateTime getLastDayOfMonthFor(OffsetDateTime offsetDateTime, ZoneOffset zoneOffset) {
        return offsetDateTime
                .withOffsetSameLocal(zoneOffset)
                .with(TemporalAdjusters.lastDayOfMonth());
    }


    public String getCurrentTimeFor(ZoneOffset zoneOffset, String dateTimeFormat) {
        return OffsetDateTime
                .now()
                .atZoneSameInstant(zoneOffset)
                .format(DateTimeFormatter
                        .ofPattern(dateTimeFormat));
    }
}
