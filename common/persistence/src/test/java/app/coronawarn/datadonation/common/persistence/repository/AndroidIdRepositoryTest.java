package app.coronawarn.datadonation.common.persistence.repository;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertEquals;

import app.coronawarn.datadonation.common.persistence.domain.AndroidId;
import java.time.ZonedDateTime;
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
}
