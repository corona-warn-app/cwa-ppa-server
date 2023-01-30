package app.coronawarn.datadonation.common.persistence.repository;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertEquals;

import app.coronawarn.datadonation.common.persistence.domain.AndroidId;
import app.coronawarn.datadonation.common.utils.TimeUtils;
import java.time.Instant;
import java.time.OffsetDateTime;
import java.time.ZonedDateTime;
import java.time.temporal.ChronoUnit;
import java.util.Optional;
import java.util.UUID;
import org.assertj.core.api.AssertionsForClassTypes;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.data.jdbc.DataJdbcTest;

@DataJdbcTest
public final class AndroidIdRepositoryTest {

  public static AndroidId newAndroidId() {
    final AndroidId id = new AndroidId();
    id.setId(UUID.randomUUID().toString());
    return id;
  }

  @Autowired
  AndroidIdRepository repository;

  @BeforeEach
  void clean() {
    repository.deleteAll();
  }

  @Test
  void testAllCrudOperations() {
    final long now = ZonedDateTime.now().toEpochSecond();
    final String id = "foo";
    repository.insert(id, now, now);
    assertThat(repository.findById(id)).isPresent();

    final long epochSecond = ZonedDateTime.now().plusDays(1).toEpochSecond();
    repository.update(id, now, epochSecond);
    final AndroidId updated = repository.findById(id).get();
    assertEquals(epochSecond, updated.getLastUsedSrs());

    assertEquals(1, repository.countOlderThan(epochSecond));

    repository.deleteOlderThan(epochSecond);
    assertEquals(0, repository.countOlderThan(epochSecond));
  }

  @Test
  void testCountOlderThan() {
    final long threshold = Instant.now().getEpochSecond();

    final AndroidId aboveTreshold = newAndroidId();
    aboveTreshold.setExpirationDate(TimeUtils.getEpochSecondFor(OffsetDateTime.now().plusDays(1)));

    final AndroidId belowTresholdOne = newAndroidId();
    belowTresholdOne.setExpirationDate(TimeUtils.getEpochSecondFor(OffsetDateTime.now().minusDays(1)));

    final AndroidId belowTresholdTwo = newAndroidId();
    belowTresholdTwo.setExpirationDate(TimeUtils.getEpochSecondFor(OffsetDateTime.now().minusDays(1)));

    final AndroidId belowTresholdThree = newAndroidId();
    belowTresholdThree.setExpirationDate(TimeUtils.getEpochSecondFor(OffsetDateTime.now().minusDays(1)));

    final int countEmpty = repository.countOlderThan(threshold);

    repository.insert(aboveTreshold.getId(), aboveTreshold.getExpirationDate(), aboveTreshold.getLastUsedSrs());
    final int countInsertAboveThreshold = repository.countOlderThan(threshold);

    repository.insert(belowTresholdOne.getId(), belowTresholdOne.getExpirationDate(),
        belowTresholdOne.getLastUsedSrs());
    final int countInsertBelowThresholdOne = repository.countOlderThan(threshold);

    repository.insert(belowTresholdTwo.getId(), belowTresholdTwo.getExpirationDate(),
        belowTresholdTwo.getLastUsedSrs());
    final int countInsertBelowThresholdTwo = repository.countOlderThan(threshold);

    repository.insert(belowTresholdThree.getId(), belowTresholdThree.getExpirationDate(),
        belowTresholdThree.getLastUsedSrs());
    final int countInsertBelowThresholdThree = repository.countOlderThan(threshold);

    AssertionsForClassTypes.assertThat(countEmpty).isZero();
    AssertionsForClassTypes.assertThat(countInsertAboveThreshold).isZero();
    AssertionsForClassTypes.assertThat(countInsertBelowThresholdOne).isEqualTo(1);
    AssertionsForClassTypes.assertThat(countInsertBelowThresholdTwo).isEqualTo(2);
    AssertionsForClassTypes.assertThat(countInsertBelowThresholdThree).isEqualTo(3);
  }

  @Test
  void testDeleteOlderThan() {
    final long threshold = Instant.now().getEpochSecond();

    final AndroidId belowTresholdOne = newAndroidId();
    belowTresholdOne.setExpirationDate(TimeUtils.getEpochSecondFor(OffsetDateTime.now().minusDays(1)));

    final AndroidId belowTresholdTwo = newAndroidId();
    belowTresholdTwo.setExpirationDate(TimeUtils.getEpochSecondFor(OffsetDateTime.now().minusDays(1)));

    final AndroidId belowTresholdThree = newAndroidId();
    belowTresholdThree.setExpirationDate(TimeUtils.getEpochSecondFor(OffsetDateTime.now().minusDays(1)));

    final int countEmpty = repository.countOlderThan(threshold);

    repository.insert(belowTresholdOne.getId(), belowTresholdOne.getExpirationDate(),
        belowTresholdOne.getLastUsedSrs());
    final int countInsertBelowThresholdOne = repository.countOlderThan(threshold);

    repository.insert(belowTresholdTwo.getId(), belowTresholdTwo.getExpirationDate(),
        belowTresholdTwo.getLastUsedSrs());
    final int countInsertBelowThresholdTwo = repository.countOlderThan(threshold);

    repository.insert(belowTresholdThree.getId(), belowTresholdThree.getExpirationDate(),
        belowTresholdThree.getLastUsedSrs());
    final int countInsertBelowThresholdThree = repository.countOlderThan(threshold);

    repository.deleteOlderThan(threshold);
    final int countAfterDelete = repository.countOlderThan(threshold);

    AssertionsForClassTypes.assertThat(countEmpty).isZero();
    AssertionsForClassTypes.assertThat(countInsertBelowThresholdOne).isEqualTo(1);
    AssertionsForClassTypes.assertThat(countInsertBelowThresholdTwo).isEqualTo(2);
    AssertionsForClassTypes.assertThat(countInsertBelowThresholdThree).isEqualTo(3);
    AssertionsForClassTypes.assertThat(countAfterDelete).isZero();
  }

  @Test
  void testInsert() {
    final Instant now = Instant.now();
    final long expirationDate = now.getEpochSecond();
    final long lastUsedSrs = now.minus(2, ChronoUnit.DAYS).getEpochSecond();

    final String id = "01234567890123456789012345678901234567891234"; // ID is always 44 chars long
    repository.insert(id, expirationDate, lastUsedSrs);

    AndroidId androidId = null;
    final Optional<AndroidId> optionalAndroidId = repository.findById(id);
    if (optionalAndroidId.isPresent()) {
      androidId = optionalAndroidId.get();
    }

    assertThat(androidId).isNotNull();
    assertThat(androidId.getId().length()).isEqualTo(44);
    assertThat(androidId.getId()).isEqualTo(id);
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
