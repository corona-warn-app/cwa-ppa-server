package app.coronawarn.datadonation.services.ppac.utils;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.junit.jupiter.api.Assertions.assertTrue;
import java.time.Instant;
import java.time.LocalDate;
import java.time.OffsetDateTime;
import java.time.ZoneOffset;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;

public class TimeUtilsTest {

  @Test
  public void testLocalDateRetrieval() {
    // given
    Long time = 1609491600L;
    LocalDate actual = LocalDate.of(2021, 1, 1);

    // when
    LocalDate localDateFor = TimeUtils.getLocalDateFor(time, ZoneOffset.ofHoursMinutes(1, 0));

    // then
    assertThat(localDateFor).isEqualTo(actual);
  }

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
    LocalDate lastDayOfMonthFor = TimeUtils.getLastDayOfMonthFor(time, ZoneOffset.UTC);

    assertThat(lastDayOfMonthFor.getDayOfMonth()).isEqualTo(time.toLocalDate().lengthOfMonth());
    assertThat(lastDayOfMonthFor.getMonth()).isEqualTo(time.toLocalDate().getMonth());
  }

  @Test
  public void testGettingCurrentTime() {
    // when
    String time = TimeUtils.getCurrentTimeFor(ZoneOffset.UTC, "yyyy-MM");

    // then
    assertThat(time).isNotNull();
  }

  @Test
  public void currentTimeRetrievalShouldFailForInvalidArgument() {
    assertThatThrownBy(() -> TimeUtils.getCurrentTimeFor(ZoneOffset.UTC, "adbc"))
        .isInstanceOf(IllegalArgumentException.class);
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
