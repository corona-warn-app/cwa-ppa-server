package app.coronawarn.datadonation.services.ppac.android.controller;

import static org.assertj.core.api.Assertions.assertThat;

import app.coronawarn.datadonation.common.persistence.domain.ppac.android.SaltData;
import app.coronawarn.datadonation.common.persistence.repository.ppac.android.SaltRepository;
import app.coronawarn.datadonation.services.ppac.config.TestBeanConfig;
import java.time.LocalDate;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Import;
import org.springframework.test.context.junit.jupiter.SpringExtension;

@ExtendWith(SpringExtension.class)
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@Import(TestBeanConfig.class)
public class DeleteSaltControllerTest {

  @Autowired
  private SaltService saltService;

  @Autowired
  private SaltRepository saltRepository;

  @BeforeEach
  void setup() {
    long epochDate = LocalDate.now().toEpochDay();
    saltRepository.persist("test-salt-data", epochDate);
  }

  @Test
  void checkDeleteSaltDataIsSuccessful() {
    SaltData expectedSaltData = new SaltData("test-salt-data", LocalDate.now().toEpochDay());

    assertThat(saltRepository.findById("test-salt-data").get().toString()).isEqualTo(expectedSaltData.toString());
    saltService.deleteSalt("test-salt-data");
    assertThat(saltRepository.findById("test-salt-data")).isEmpty();
  }


}
