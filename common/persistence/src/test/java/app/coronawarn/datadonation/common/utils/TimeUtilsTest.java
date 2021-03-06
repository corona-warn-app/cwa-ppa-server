package app.coronawarn.datadonation.common.utils;

import static app.coronawarn.datadonation.common.utils.TimeUtils.getEpochMilliSecondForNow;
import static app.coronawarn.datadonation.common.utils.TimeUtils.getEpochSecondFor;
import static app.coronawarn.datadonation.common.utils.TimeUtils.getEpochSecondsForNow;
import static app.coronawarn.datadonation.common.utils.TimeUtils.getLastDayOfMonthFor;
import static app.coronawarn.datadonation.common.utils.TimeUtils.getLastDayOfMonthForNow;
import static app.coronawarn.datadonation.common.utils.TimeUtils.getLocalDateFor;
import static app.coronawarn.datadonation.common.utils.TimeUtils.getLocalDateForNow;
import static app.coronawarn.datadonation.common.utils.TimeUtils.getZonedDateTimeFor;
import static app.coronawarn.datadonation.common.utils.TimeUtils.isInRange;
import static org.assertj.core.api.Assertions.assertThat;

import java.time.Instant;
import java.time.LocalDate;
import java.time.OffsetDateTime;
import java.time.ZoneOffset;
import java.time.ZonedDateTime;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;

public class TimeUtilsTest {

  @Test
  public void testEpochSecondsRetrieval() {
    // given
    OffsetDateTime time = OffsetDateTime.parse("2021-01-01T10:00:00+01:00");

    Long epochSecond = getEpochSecondFor(time);

    assertThat(epochSecond).isEqualTo(1609491600L);
  }

  @Test
  public void testLastDayOfTheMonthComputation() {
    // given
    OffsetDateTime time = OffsetDateTime.parse("2020-01-01T10:00:00+01:00");

    // when
    Long lastDayOfMonthFor = getLastDayOfMonthFor(time);

    LocalDate result = Instant.ofEpochSecond(lastDayOfMonthFor).atOffset(ZoneOffset.UTC).toLocalDate();
    assertThat(result.getDayOfMonth()).isEqualTo(time.toLocalDate().lengthOfMonth());
    assertThat(result.getMonth()).isEqualTo(time.toLocalDate().getMonth());
  }

  @Test
  public void getLastDayOfMonth() {
    final Long epochSecondForNow = getLastDayOfMonthForNow();
    final Long lastDayOfMonthFor = getLastDayOfMonthFor(OffsetDateTime.now());

    assertThat(epochSecondForNow).isEqualTo(lastDayOfMonthFor);
  }

  @Test
  public void testConversionMethods() {
    long now = getEpochSecondsForNow();
    assertThat(now).isEqualTo(getEpochMilliSecondForNow() / 1000);
    LocalDate localDateToday = Instant.now().atOffset(ZoneOffset.UTC).toLocalDate();
    ZonedDateTime zonedDateTimeToday = Instant.now().atOffset(ZoneOffset.UTC).toZonedDateTime();

    assertThat(zonedDateTimeToday).isEqualToIgnoringSeconds(getZonedDateTimeFor(now));
    assertThat(localDateToday).isEqualTo(getLocalDateFor(now));
    assertThat(localDateToday).isEqualTo(getLocalDateForNow());

  }

  @ParameterizedTest
  @ValueSource(ints = {0, 1, 200, 5000, 7199, 7200})
  void testDateInRangeCalculation(int presentOffset) {
    Instant present = Instant.now();
    Instant upperLimit = present.plusSeconds(7200);
    Instant lowerLimit = present.minusSeconds(7200);

    Instant futureTimestamp = present.plusSeconds(presentOffset);
    assertThat(isInRange(futureTimestamp.toEpochMilli(), lowerLimit, upperLimit)).isTrue();

    Instant pastTimestamp = present.plusSeconds(presentOffset);
    assertThat(isInRange(pastTimestamp.toEpochMilli(), lowerLimit, upperLimit)).isTrue();
  }
}
