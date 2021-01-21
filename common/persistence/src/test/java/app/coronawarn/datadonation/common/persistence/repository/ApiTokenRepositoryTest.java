package app.coronawarn.datadonation.common.persistence.repository;

import app.coronawarn.datadonation.common.persistence.domain.ApiToken;
import java.time.LocalDate;
import java.time.OffsetDateTime;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

@SpringBootTest
public class ApiTokenRepositoryTest {

  @Autowired
  ApiTokenRepository underTest;

  @BeforeEach
  public void setup() {
    underTest.deleteAll();
  }

  @Test
  public void save() {
    OffsetDateTime now = OffsetDateTime.now();
    long epochSecond = now.toInstant().getEpochSecond();
    LocalDate expirationDate = now.toLocalDate();

    underTest.insert("apiToken", expirationDate, epochSecond,
        epochSecond);
    ApiToken apiToken1 = underTest.findById("apiToken").get();

    Assertions.assertThat(apiToken1).isNotNull();
    Assertions.assertThat(apiToken1.getApiToken()).isEqualTo("apiToken");
    Assertions.assertThat(apiToken1.getExpirationDate()).isEqualTo(expirationDate);
    Assertions.assertThat(apiToken1.getLastUsedEdus()).isEqualTo(epochSecond);
    Assertions.assertThat(apiToken1.getLastUsedPpac()).isEqualTo(epochSecond);
  }


}
