package app.coronawarn.datadonation.services.retention;

import org.junit.jupiter.api.Test;
import java.time.Instant;
import java.time.temporal.ChronoUnit;

class ApplicationTest {

  @Test
  void testInstantAtStartOfTheDay() {
    System.out.println(Instant.now().truncatedTo(ChronoUnit.DAYS).minus(2,ChronoUnit.DAYS).toString());
  }
}
