package app.coronawarn.datadonation.common.utils;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.time.Instant;
import java.time.LocalDate;
import java.time.OffsetDateTime;
import java.time.ZoneOffset;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;

public class TimeUtilsTest {

  @Test
  public void testEpochSecondsRetrieval() {
    // given
    OffsetDateTime time = OffsetDateTime.parse("2021-01-01T10:00:00+01:00");

    Long epochSecond = TimeUtils.getEpochSecondFor(time);

    assertThat(epochSecond).isEqualTo(1609491600L);
  }

  @Test
  public void testLastDayOfTheMonthComputation() {
    // given
    OffsetDateTime time = OffsetDateTime.parse("2020-01-01T10:00:00+01:00");

    // when
    Long lastDayOfMonthFor = TimeUtils.getLastDayOfMonthFor(time);

    LocalDate result = Instant.ofEpochSecond(lastDayOfMonthFor).atOffset(ZoneOffset.UTC).toLocalDate();
    assertThat(result.getDayOfMonth()).isEqualTo(time.toLocalDate().lengthOfMonth());
    assertThat(result.getMonth()).isEqualTo(time.toLocalDate().getMonth());
  }

  @Test
  public void getLastDayOfMonth() {
    final Long epochSecondForNow = TimeUtils.getLastDayOfMonthForNow();
    final Long lastDayOfMonthFor = TimeUtils.getLastDayOfMonthFor(OffsetDateTime.now());

    Assertions
        .assertThat(epochSecondForNow).isEqualTo(lastDayOfMonthFor);
  }

  @ParameterizedTest
  @ValueSource(ints = {0, 1, 200, 5000, 7199, 7200})
  void testDateInRangeCalculation(int presentOffset) {
    Instant present = Instant.now();
    Instant upperLimit = present.plusSeconds(7200);
    Instant lowerLimit = present.minusSeconds(7200);

    Instant futureTimestamp = present.plusSeconds(presentOffset);
    assertTrue(TimeUtils.isInRange(futureTimestamp.toEpochMilli(), lowerLimit, upperLimit));

    Instant pastTimestamp = present.plusSeconds(presentOffset);
    assertTrue(TimeUtils.isInRange(pastTimestamp.toEpochMilli(), lowerLimit, upperLimit));
  }
}
