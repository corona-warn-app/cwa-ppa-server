package app.coronawarn.analytics.services.ios.control;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import java.time.LocalDate;
import java.time.OffsetDateTime;
import java.time.ZoneOffset;

import app.coronawarn.analytics.services.ios.utils.TimeUtils;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.context.junit.jupiter.SpringExtension;

@ExtendWith(value = {MockitoExtension.class, SpringExtension.class})
public class TimeUtilsTest {

  @InjectMocks
  private TimeUtils underTest;

  @Test
  public void getLastDayOfMonthFor() {
    // given
    OffsetDateTime time = OffsetDateTime.parse("2020-01-01T10:00:00+01:00");

    // when
    LocalDate lastDayOfMonthFor = underTest.getLastDayOfMonthFor(time, ZoneOffset.UTC);

    assertThat(lastDayOfMonthFor.getDayOfMonth()).isEqualTo(time.toLocalDate().lengthOfMonth());
    assertThat(lastDayOfMonthFor.getMonth()).isEqualTo(time.toLocalDate().getMonth());
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
