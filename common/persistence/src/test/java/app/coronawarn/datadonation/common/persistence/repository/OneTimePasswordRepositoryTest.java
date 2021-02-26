package app.coronawarn.datadonation.common.persistence.repository;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;

import app.coronawarn.datadonation.common.persistence.domain.OneTimePassword;
import app.coronawarn.datadonation.common.utils.TimeUtils;
import java.time.Instant;
import java.time.OffsetDateTime;
import java.util.UUID;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.data.jdbc.DataJdbcTest;

@DataJdbcTest
public class OneTimePasswordRepositoryTest {

  @Autowired
  OneTimePasswordRepository oneTimePasswordRepository;

  @BeforeEach
  void clean() {
    oneTimePasswordRepository.deleteAll();
  }

  @Test
  void testInsert() {
    String password = UUID.randomUUID().toString();
    OneTimePassword otp = new OneTimePassword(password);
    otp.setRedemptionTimestamp(Instant.now().getEpochSecond());
    otp.setExpirationTimestamp(Instant.now().getEpochSecond());

    oneTimePasswordRepository.insert(otp.getPassword(), otp.getRedemptionTimestamp(), otp.getExpirationTimestamp());

    OneTimePassword storedOtp =
        oneTimePasswordRepository.findById(password).isPresent() ? oneTimePasswordRepository.findById(password).get()
            : null;

    assert storedOtp != null;
    assertThat(storedOtp.getPassword()).isEqualTo(otp.getPassword());
    assertThat(storedOtp.getExpirationTimestamp()).isEqualTo(otp.getExpirationTimestamp());
    assertThat(storedOtp.getRedemptionTimestamp()).isEqualTo(otp.getRedemptionTimestamp());
  }

  @Test
  void testDeleteOlderThan() {

    long threshold = Instant.now().getEpochSecond();

    OneTimePassword belowTresholdOne = new OneTimePassword(UUID.randomUUID().toString());
    belowTresholdOne.setRedemptionTimestamp(TimeUtils.getEpochSecondFor(OffsetDateTime.now().minusDays(1)));
    belowTresholdOne.setExpirationTimestamp(TimeUtils.getEpochSecondFor(OffsetDateTime.now().minusDays(1)));

    OneTimePassword belowTresholdTwo = new OneTimePassword(UUID.randomUUID().toString());
    belowTresholdTwo.setRedemptionTimestamp(TimeUtils.getEpochSecondFor(OffsetDateTime.now().minusDays(1)));
    belowTresholdTwo.setExpirationTimestamp(TimeUtils.getEpochSecondFor(OffsetDateTime.now().plusDays(1)));

    OneTimePassword belowTresholdThree = new OneTimePassword(UUID.randomUUID().toString());
    belowTresholdThree.setRedemptionTimestamp(TimeUtils.getEpochSecondFor(OffsetDateTime.now().plusDays(1)));
    belowTresholdThree.setExpirationTimestamp(TimeUtils.getEpochSecondFor(OffsetDateTime.now().minusDays(1)));

    int countEmpty = oneTimePasswordRepository.countOlderThan(threshold);

    oneTimePasswordRepository.save(belowTresholdOne);
    int countInsertBelowThresholdOne = oneTimePasswordRepository.countOlderThan(threshold);

    oneTimePasswordRepository.save(belowTresholdTwo);
    int countInsertBelowThresholdTwo = oneTimePasswordRepository.countOlderThan(threshold);

    oneTimePasswordRepository.save(belowTresholdThree);
    int countInsertBelowThresholdThree = oneTimePasswordRepository.countOlderThan(threshold);

    oneTimePasswordRepository.deleteOlderThan(threshold);
    int countAfterDelete = oneTimePasswordRepository.countOlderThan(threshold);

    assertThat(countEmpty).isEqualTo(0);
    assertThat(countInsertBelowThresholdOne).isEqualTo(1);
    assertThat(countInsertBelowThresholdTwo).isEqualTo(2);
    assertThat(countInsertBelowThresholdThree).isEqualTo(3);
    assertThat(countAfterDelete).isEqualTo(0);
  }

  @Test
  void testCountOlderThan() {

    long threshold = Instant.now().getEpochSecond();

    OneTimePassword aboveTreshold = new OneTimePassword(UUID.randomUUID().toString());
    aboveTreshold.setRedemptionTimestamp(TimeUtils.getEpochSecondFor(OffsetDateTime.now().plusDays(1)));
    aboveTreshold.setExpirationTimestamp(TimeUtils.getEpochSecondFor(OffsetDateTime.now().plusDays(1)));

    OneTimePassword belowTresholdOne = new OneTimePassword(UUID.randomUUID().toString());
    belowTresholdOne.setRedemptionTimestamp(TimeUtils.getEpochSecondFor(OffsetDateTime.now().minusDays(1)));
    belowTresholdOne.setExpirationTimestamp(TimeUtils.getEpochSecondFor(OffsetDateTime.now().minusDays(1)));

    OneTimePassword belowTresholdTwo = new OneTimePassword(UUID.randomUUID().toString());
    belowTresholdTwo.setRedemptionTimestamp(TimeUtils.getEpochSecondFor(OffsetDateTime.now().minusDays(1)));
    belowTresholdTwo.setExpirationTimestamp(TimeUtils.getEpochSecondFor(OffsetDateTime.now().plusDays(1)));

    OneTimePassword belowTresholdThree = new OneTimePassword(UUID.randomUUID().toString());
    belowTresholdThree.setRedemptionTimestamp(TimeUtils.getEpochSecondFor(OffsetDateTime.now().plusDays(1)));
    belowTresholdThree.setExpirationTimestamp(TimeUtils.getEpochSecondFor(OffsetDateTime.now().minusDays(1)));

    int countEmpty = oneTimePasswordRepository.countOlderThan(threshold);

    oneTimePasswordRepository.save(aboveTreshold);
    int countInsertAboveThreshold = oneTimePasswordRepository.countOlderThan(threshold);

    oneTimePasswordRepository.save(belowTresholdOne);
    int countInsertBelowThresholdOne = oneTimePasswordRepository.countOlderThan(threshold);

    oneTimePasswordRepository.save(belowTresholdTwo);
    int countInsertBelowThresholdTwo = oneTimePasswordRepository.countOlderThan(threshold);

    oneTimePasswordRepository.save(belowTresholdThree);
    int countInsertBelowThresholdThree = oneTimePasswordRepository.countOlderThan(threshold);

    assertThat(countEmpty).isEqualTo(0);
    assertThat(countInsertAboveThreshold).isEqualTo(0);
    assertThat(countInsertBelowThresholdOne).isEqualTo(1);
    assertThat(countInsertBelowThresholdTwo).isEqualTo(2);
    assertThat(countInsertBelowThresholdThree).isEqualTo(3);
  }
}
