package app.coronawarn.datadonation.services.ppac.android.attestation;

import static org.junit.jupiter.api.Assertions.*;
import java.time.Instant;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;
import app.coronawarn.datadonation.common.utils.TimeUtils;

class TimeUtilsTest {

  @ParameterizedTest
  @ValueSource(ints = {0, 1, 200, 5000, 7199, 7200})
  void testWithDatesInRangee(int presentOffset) {
    Instant present = Instant.now();
    Instant upperLimit = present.plusSeconds(7200);
    Instant lowerLimit = present.minusSeconds(7200);

    Instant futureTimestamp = present.plusSeconds(presentOffset);
    assertTrue(TimeUtils.isInRange(futureTimestamp.toEpochMilli(), lowerLimit, upperLimit));

    Instant pastTimestamp = present.plusSeconds(presentOffset);
    assertTrue(TimeUtils.isInRange(pastTimestamp.toEpochMilli(), lowerLimit, upperLimit));
  }
}
