package app.coronawarn.datadonation.common.persistence.repository.android;

import static org.junit.jupiter.api.Assertions.assertEquals;

import app.coronawarn.datadonation.common.persistence.domain.ppac.android.Salt;
import app.coronawarn.datadonation.common.persistence.repository.ppac.android.SaltRepository;
import java.time.LocalDate;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.data.jdbc.DataJdbcTest;

@DataJdbcTest
class SaltRepositoryTest {

  @Autowired
  private SaltRepository saltRepository;

  @AfterEach
  void tearDown() {
    saltRepository.deleteAll();
  }

  @Test
  void testSaltIsPersisted() {
    long epochDate = LocalDate.now().toEpochDay();
    saltRepository.persist("test-salt", epochDate);
    Salt salt = saltRepository.findAll().iterator().next();
    assertEquals("test-salt", salt.getSalt());
    assertEquals(salt.getCreatedAt().longValue(), epochDate);
  }
}
