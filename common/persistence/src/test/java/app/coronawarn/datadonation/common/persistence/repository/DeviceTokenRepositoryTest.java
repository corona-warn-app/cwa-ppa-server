package app.coronawarn.datadonation.common.persistence.repository;

import app.coronawarn.datadonation.common.persistence.domain.DeviceToken;
import java.time.Instant;
import java.util.Optional;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.data.jdbc.DataJdbcTest;

@DataJdbcTest
public class DeviceTokenRepositoryTest {

  @Autowired
  private DeviceTokenRepository underTest;

  @Test
  void save() {
    Long timestamp = Instant.now().getEpochSecond();
    String deviceTokenHash = "deviceTokenHash";

    underTest.save(deviceTokenHash, timestamp);

    Optional<DeviceToken> deviceTokenOptional = underTest.findById(deviceTokenHash);

    Assertions.assertThat(deviceTokenOptional.isPresent()).isTrue();
    Assertions.assertThat(deviceTokenOptional.get())
        .extracting(DeviceToken::getDeviceTokenHash, DeviceToken::getCreatedAt)
        .containsExactly(deviceTokenHash, timestamp);
  }
}
