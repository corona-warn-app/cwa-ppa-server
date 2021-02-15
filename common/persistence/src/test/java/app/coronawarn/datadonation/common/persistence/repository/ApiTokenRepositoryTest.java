package app.coronawarn.datadonation.common.persistence.repository;

import static org.assertj.core.api.Assertions.assertThat;

import app.coronawarn.datadonation.common.persistence.domain.ApiToken;
import java.time.Instant;
import java.time.temporal.ChronoUnit;
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
  public void testPersistApiToken() {

    final Instant now = Instant.now();
    long expirationDate = now.getEpochSecond();
    long createdAt = now.minus(1, ChronoUnit.DAYS).getEpochSecond();

    underTest.insert("apiToken", expirationDate, createdAt,
        createdAt, createdAt);
    ApiToken apiToken1 = underTest.findById("apiToken").get();

    assertThat(apiToken1).isNotNull();
    assertThat(apiToken1.getApiToken()).isEqualTo("apiToken");
    assertThat(apiToken1.getExpirationDate()).isEqualTo(expirationDate);
    assertThat(apiToken1.getCreatedAt()).isEqualTo(createdAt);
    assertThat(apiToken1.getLastUsedEdus().get()).isEqualTo(createdAt);
    assertThat(apiToken1.getLastUsedPpac().get()).isEqualTo(createdAt);
  }
}
