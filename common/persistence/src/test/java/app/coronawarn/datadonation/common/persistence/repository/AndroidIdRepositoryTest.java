package app.coronawarn.datadonation.common.persistence.repository;

import static org.assertj.core.api.Assertions.assertThat;

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
    // TODO
  }

  @Test
  void testDeleteOlderThan() {
    // TODO
  }

  @Test
  void testFindById() {
    repository.insert("foo", 42L, 42L);
    assertThat(repository.findById("foo")).isPresent();
  }

  @Test
  void testInsert() {
    // TODO
  }

  @Test
  void testUpdate() {
    // TODO
  }
}
