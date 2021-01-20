package app.coronawarn.analytics.services.ios;

import app.coronawarn.analytics.services.ios.control.TimeUtils;
import java.time.OffsetDateTime;
import java.time.ZoneOffset;
import java.time.temporal.TemporalAdjusters;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import static org.assertj.core.api.Assertions.*;

@ExtendWith(value = {MockitoExtension.class, SpringExtension.class})
public class TimeUtilsTest {

  @InjectMocks
  private TimeUtils underTest;

  @Test
  public void getLastDayOfMonthFor() {
    // given
    OffsetDateTime time = OffsetDateTime.parse("2020-01-01T10:00:00+01:00");

    // when
    OffsetDateTime lastDayOfMonthFor = underTest.getLastDayOfMonthFor(time, ZoneOffset.UTC);

    assertThat(lastDayOfMonthFor.getDayOfMonth()).isEqualTo(time.toLocalDate().lengthOfMonth());
    assertThat(lastDayOfMonthFor.getMonth()).isEqualTo(time.toLocalDate().getMonth());
    assertThat(lastDayOfMonthFor.getOffset()).isEqualTo(ZoneOffset.UTC);
  }

  @Test
  public void getCurrentTimeFor() {
    // when
    String time = underTest.getCurrentTimeFor(ZoneOffset.UTC, "yyyy-MM");

    // then
    assertThat(time).isNotNull();
  }

  @Test
  public void getCurrentTimeFor_illegalArgument() {
    assertThatThrownBy(() -> underTest.getCurrentTimeFor(ZoneOffset.UTC, "adbc"))
        .isInstanceOf(IllegalArgumentException.class);
  }
}
