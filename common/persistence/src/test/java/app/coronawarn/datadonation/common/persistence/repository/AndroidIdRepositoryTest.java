package app.coronawarn.datadonation.common.persistence.repository;

import static org.assertj.core.api.Assertions.assertThat;

import app.coronawarn.datadonation.common.persistence.domain.AndroidId;
import app.coronawarn.datadonation.common.persistence.domain.ApiTokenData;
import app.coronawarn.datadonation.common.persistence.domain.OneTimePassword;
import app.coronawarn.datadonation.common.utils.TimeUtils;
import java.time.Instant;
import java.time.OffsetDateTime;
import java.time.temporal.ChronoUnit;
import java.util.Optional;
import java.util.UUID;
import org.assertj.core.api.AssertionsForClassTypes;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.data.jdbc.DataJdbcTest;

@DataJdbcTest
final class AndroidIdRepositoryTest {

  @Autowired
  AndroidIdRepository repository;

  @BeforeEach
  void clean() {
    repository.deleteAll();
  }

  @Test
  void testCountOlderThan() {
    long threshold = Instant.now().getEpochSecond();

    AndroidId aboveTreshold = new AndroidId(UUID.randomUUID().toString());
    aboveTreshold.setExpirationDate(TimeUtils.getEpochSecondFor(OffsetDateTime.now().plusDays(1)));

    AndroidId belowTresholdOne = new AndroidId(UUID.randomUUID().toString());
    belowTresholdOne.setExpirationDate(TimeUtils.getEpochSecondFor(OffsetDateTime.now().minusDays(1)));

    AndroidId belowTresholdTwo = new AndroidId(UUID.randomUUID().toString());
    belowTresholdTwo.setExpirationDate(TimeUtils.getEpochSecondFor(OffsetDateTime.now().minusDays(1)));

    AndroidId belowTresholdThree = new AndroidId(UUID.randomUUID().toString());
    belowTresholdThree.setExpirationDate(TimeUtils.getEpochSecondFor(OffsetDateTime.now().minusDays(1)));

    int countEmpty = repository.countOlderThan(threshold);

    repository.insert(aboveTreshold.getId(), aboveTreshold.getExpirationDate(), aboveTreshold.getLastUsedSrs());
    int countInsertAboveThreshold = repository.countOlderThan(threshold);

    repository.insert(belowTresholdOne.getId(), belowTresholdOne.getExpirationDate(), belowTresholdOne.getLastUsedSrs());
    int countInsertBelowThresholdOne = repository.countOlderThan(threshold);

    repository.insert(belowTresholdTwo.getId(), belowTresholdTwo.getExpirationDate(), belowTresholdTwo.getLastUsedSrs());
    int countInsertBelowThresholdTwo = repository.countOlderThan(threshold);

    repository.insert(belowTresholdThree.getId(), belowTresholdThree.getExpirationDate(), belowTresholdThree.getLastUsedSrs());
    int countInsertBelowThresholdThree = repository.countOlderThan(threshold);

    AssertionsForClassTypes.assertThat(countEmpty).isZero();
    AssertionsForClassTypes.assertThat(countInsertAboveThreshold).isZero();
    AssertionsForClassTypes.assertThat(countInsertBelowThresholdOne).isEqualTo(1);
    AssertionsForClassTypes.assertThat(countInsertBelowThresholdTwo).isEqualTo(2);
    AssertionsForClassTypes.assertThat(countInsertBelowThresholdThree).isEqualTo(3);
  }

  @Test
  void testDeleteOlderThan() {
    long threshold = Instant.now().getEpochSecond();

    AndroidId belowTresholdOne = new AndroidId(UUID.randomUUID().toString());
    belowTresholdOne.setExpirationDate(TimeUtils.getEpochSecondFor(OffsetDateTime.now().minusDays(1)));

    AndroidId belowTresholdTwo = new AndroidId(UUID.randomUUID().toString());
    belowTresholdTwo.setExpirationDate(TimeUtils.getEpochSecondFor(OffsetDateTime.now().minusDays(1)));

    AndroidId belowTresholdThree = new AndroidId(UUID.randomUUID().toString());
    belowTresholdThree.setExpirationDate(TimeUtils.getEpochSecondFor(OffsetDateTime.now().minusDays(1)));

    int countEmpty = repository.countOlderThan(threshold);

    repository.insert(belowTresholdOne.getId(), belowTresholdOne.getExpirationDate(), belowTresholdOne.getLastUsedSrs());
    int countInsertBelowThresholdOne = repository.countOlderThan(threshold);

    repository.insert(belowTresholdTwo.getId(), belowTresholdTwo.getExpirationDate(), belowTresholdTwo.getLastUsedSrs());
    int countInsertBelowThresholdTwo = repository.countOlderThan(threshold);

    repository.insert(belowTresholdThree.getId(), belowTresholdThree.getExpirationDate(), belowTresholdThree.getLastUsedSrs());
    int countInsertBelowThresholdThree = repository.countOlderThan(threshold);

    repository.deleteOlderThan(threshold);
    int countAfterDelete = repository.countOlderThan(threshold);

    AssertionsForClassTypes.assertThat(countEmpty).isZero();
    AssertionsForClassTypes.assertThat(countInsertBelowThresholdOne).isEqualTo(1);
    AssertionsForClassTypes.assertThat(countInsertBelowThresholdTwo).isEqualTo(2);
    AssertionsForClassTypes.assertThat(countInsertBelowThresholdThree).isEqualTo(3);
    AssertionsForClassTypes.assertThat(countAfterDelete).isZero();
  }

  @Test
  void testFindById() {
    repository.insert("foo", 42L, 42L);
    assertThat(repository.findById("foo")).isPresent();
  }

  @Test
  void testInsert() {
    final Instant now = Instant.now();
    final long expirationDate = now.getEpochSecond();
    final long lastUsedSrs = now.minus(2, ChronoUnit.DAYS).getEpochSecond();

    repository.insert("androidId", expirationDate, lastUsedSrs);

    AndroidId androidId = null;
    final Optional<AndroidId> optionalAndroidId = repository.findById("androidId");
    if (optionalAndroidId.isPresent()) {
      androidId = optionalAndroidId.get();
    }

    assertThat(androidId).isNotNull();
    assertThat(androidId.getId().equals("androidId"));
    assertThat(androidId.getExpirationDate()).isEqualTo(expirationDate);
    assertThat(androidId.getLastUsedSrs()).isEqualTo(lastUsedSrs);
  }

  @Test
  void testUpdate() {
    final Instant now = Instant.now();
    final long expirationDate = now.getEpochSecond();
    final long lastUsedSrs = now.minus(2, ChronoUnit.DAYS).getEpochSecond();
    final long lastUsedSrsUpdate = now.minus(1, ChronoUnit.DAYS).getEpochSecond();

    repository.insert("androidId", expirationDate, lastUsedSrs);
    repository.update("androidId", expirationDate, lastUsedSrsUpdate);

    AndroidId androidId = null;
    final Optional<AndroidId> optionalAndroidId = repository.findById("androidId");
    if (optionalAndroidId.isPresent()) {
      androidId = optionalAndroidId.get();
    }

    assertThat(androidId.getLastUsedSrs()).isEqualTo(lastUsedSrsUpdate);

  }
}
