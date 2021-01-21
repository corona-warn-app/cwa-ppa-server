package app.coronawarn.analytics.common.persistence.repository;

import app.coronawarn.analytics.common.persistence.domain.ApiToken;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.data.jdbc.DataJdbcTest;
import org.springframework.boot.test.context.SpringBootTest;
import java.time.LocalDate;
import java.time.OffsetDateTime;

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
