package app.coronawarn.datadonation.common.persistence.repository.android;

import static org.junit.Assert.assertEquals;
import java.time.LocalDate;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.data.jdbc.DataJdbcTest;
import app.coronawarn.datadonation.common.persistence.domain.android.Salt;

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
    //TODO: We don't know what the date format will be at the moment
    saltRepository.persist("test-salt", epochDate);
    Salt salt = saltRepository.findAll().iterator().next();
    assertEquals(salt.getSalt(), "test-salt");
    assertEquals(salt.getCreatedAt().longValue(), epochDate);
  }
}
