package app.coronawarn.datadonation.common.persistence.repository;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import app.coronawarn.datadonation.common.persistence.domain.DeviceToken;
import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.time.Instant;
import java.util.Optional;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.data.jdbc.DataJdbcTest;
import org.springframework.dao.DuplicateKeyException;

@DataJdbcTest
public class DeviceTokenRepositoryTest {

  @Autowired
  private DeviceTokenRepository underTest;

  @Test
  void save() throws Exception {
    Long timestamp = Instant.now().getEpochSecond();
    String deviceTokenHash = "deviceTokenHash";

    final DeviceToken persistedDeviceToken = underTest.save(buildDeviceToken(timestamp, deviceTokenHash, false));

    assertThat(persistedDeviceToken.getId()).isNotNull();
    assertThat(persistedDeviceToken)
        .extracting(dt -> new String(dt.getDeviceTokenHash(), StandardCharsets.UTF_8), DeviceToken::getCreatedAt)
        .containsExactly(deviceTokenHash, timestamp);
  }

  @Test
  void save_violateUniqueIndex() throws Exception {
    Long timestamp = Instant.now().getEpochSecond();
    String deviceTokenHash = "deviceTokenHash";

    underTest.save(buildDeviceToken(timestamp, deviceTokenHash, false));

    assertThatThrownBy(
        () -> underTest.save(buildDeviceToken(timestamp, deviceTokenHash, false))).hasCauseExactlyInstanceOf(
        DuplicateKeyException.class);
  }

  @Test
  void findByDeviceTokenHash() throws Exception {
    Long timestamp = Instant.now().getEpochSecond();
    String deviceTokenHash = "deviceTokenHash";

    underTest.save(buildDeviceToken(timestamp, deviceTokenHash, false));

    final Optional<DeviceToken> deviceTokenOptional = underTest
        .findByDeviceTokenHash("deviceTokenHash".getBytes(StandardCharsets.UTF_8));
    assertThat(deviceTokenOptional).isPresent();
  }

  private DeviceToken buildDeviceToken(Long timestamp, String deviceTokenHash, boolean hash) throws Exception {
    if (hash) {
      MessageDigest digest = MessageDigest.getInstance("SHA-256");
      return new DeviceToken(digest.digest(deviceTokenHash.getBytes(StandardCharsets.UTF_8)), timestamp);
    } else {
      return new DeviceToken(deviceTokenHash.getBytes(StandardCharsets.UTF_8), timestamp);
    }
  }
}
