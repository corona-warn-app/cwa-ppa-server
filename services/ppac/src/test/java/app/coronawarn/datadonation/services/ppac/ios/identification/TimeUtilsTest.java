package app.coronawarn.datadonation.services.ppac.ios.identification;

import app.coronawarn.datadonation.services.ppac.ios.utils.TimeUtils;
import java.time.OffsetDateTime;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.context.junit.jupiter.SpringExtension;

@ExtendWith(value = {MockitoExtension.class, SpringExtension.class})
public class TimeUtilsTest {

  @Test
  public void getLastDayOfMonth() {
    final Long epochSecondForNow = TimeUtils.getLastDayOfMonthForNow();
    final Long lastDayOfMonthFor = TimeUtils.getLastDayOfMonthFor(OffsetDateTime.now());

    Assertions
        .assertThat(epochSecondForNow).isEqualTo(lastDayOfMonthFor);
  }
}
