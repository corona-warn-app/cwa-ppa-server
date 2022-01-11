package app.coronawarn.datadonation.common.utils;

import static app.coronawarn.datadonation.common.utils.TimeUtils.formatToHours;
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
import static org.testcontainers.shaded.org.awaitility.Awaitility.await;

import java.time.Duration;
import java.time.Instant;
import java.time.LocalDate;
import java.time.OffsetDateTime;
import java.time.ZoneOffset;
import java.time.ZonedDateTime;
import java.time.temporal.TemporalUnit;
import java.util.concurrent.TimeUnit;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;

class TimeUtilsTest {

  @Test
  void testEpochSecondsRetrieval() {
    // given
    OffsetDateTime time = OffsetDateTime.parse("2021-01-01T10:00:00+01:00");

    Long epochSecond = getEpochSecondFor(time);

    assertThat(epochSecond).isEqualTo(1609491600L);
  }

  @Test
  void testLastDayOfTheMonthComputation() {
    // given
    OffsetDateTime time = OffsetDateTime.parse("2020-01-01T10:00:00+01:00");

    // when
    Long lastDayOfMonthFor = getLastDayOfMonthFor(time);

    LocalDate result = Instant.ofEpochSecond(lastDayOfMonthFor).atOffset(ZoneOffset.UTC).toLocalDate();
    assertThat(result.getDayOfMonth()).isEqualTo(time.toLocalDate().lengthOfMonth());
    assertThat(result.getMonth()).isEqualTo(time.toLocalDate().getMonth());
  }

  @Test
  void getLastDayOfMonth() {
    final Long epochSecondForNow = getLastDayOfMonthForNow();
    final Long lastDayOfMonthFor = getLastDayOfMonthFor(OffsetDateTime.now());

    assertThat(epochSecondForNow).isEqualTo(lastDayOfMonthFor);
  }

  @Test
  void testConversionMethods() {
    Instant now = Instant.now();
    TimeUtils.setNow(now);
    long nowEpochSeconds = getEpochSecondsForNow();
    assertThat(nowEpochSeconds).isEqualTo(getEpochMilliSecondForNow() / 1000);
    LocalDate localDateToday = now.atOffset(ZoneOffset.UTC).toLocalDate();
    ZonedDateTime zonedDateTimeToday = now.atOffset(ZoneOffset.UTC).toZonedDateTime();

    assertThat(zonedDateTimeToday).isEqualToIgnoringSeconds(getZonedDateTimeFor(nowEpochSeconds));
    assertThat(localDateToday)
        .isEqualTo(getLocalDateFor(nowEpochSeconds))
        .isEqualTo(getLocalDateForNow());
    TimeUtils.setNow(null);
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

  @Test
  void testSetNow() {
    Instant now = Instant.now();
    TimeUtils.setNow(now);

    assertThat(TimeUtils.getNow()).isEqualTo(now);
  }

  @Test
  void testSetNowToNullRestoresOrigin() {
    Instant now = Instant.now();
    TimeUtils.setNow(now);

    assertThat(TimeUtils.getNow()).isEqualTo(now);

    TimeUtils.setNow(null);
    await()
        .atLeast(Duration.ofMillis(10))
        .until(() -> {
          assertThat(now).isNotEqualTo(TimeUtils.getNow());
          return true;
        });
  }

  @Test
  void testNowIsUpdated() {
    Instant now = TimeUtils.getNow();
    await()
        .atLeast(Duration.ofMillis(10))
        .until(() -> {
          assertThat(now).isNotEqualTo(Instant.now());
          return true;
        });
  }

  @Test
  void testFormatToHours(){
    assertThat(formatToHours(0)).isEqualTo("00:00:00");
    assertThat(formatToHours(1)).isEqualTo("00:00:01");
    assertThat(formatToHours(-1)).isEqualTo("00:00:-1");
    assertThat(formatToHours(86100)).isEqualTo("23:55:00");
    assertThat(formatToHours(-86100)).isEqualTo("-23:-55:00");
  }
}
