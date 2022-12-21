package app.coronawarn.datadonation.common.persistence.repository;

import static org.assertj.core.api.Assertions.assertThat;

import app.coronawarn.datadonation.common.persistence.domain.ApiTokenData;
import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.Optional;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

@SpringBootTest
class ApiTokenDataRepositoryTest {

  @Autowired
  ApiTokenRepository underTest;

  @BeforeEach
  public void setup() {
    underTest.deleteAll();
  }

  @Test
  void testPersistApiToken() {

    final Instant now = Instant.now();
    final long expirationDate = now.getEpochSecond();
    final long createdAt = now.minus(1, ChronoUnit.DAYS).getEpochSecond();

    underTest.insert("apiToken", expirationDate, createdAt,
        createdAt, createdAt, createdAt);
    ApiTokenData apiTokenData = null;
    final Optional<ApiTokenData> optionalApiToken = underTest.findById("apiToken");
    if (optionalApiToken.isPresent()) {
      apiTokenData = optionalApiToken.get();
    }

    assertThat(apiTokenData).isNotNull();
    assertThat(apiTokenData.getApiToken()).isEqualTo("apiToken");
    assertThat(apiTokenData.getExpirationDate()).isEqualTo(expirationDate);
    assertThat(apiTokenData.getCreatedAt()).isEqualTo(createdAt);

    Long lastUsed = null;
    var optional = apiTokenData.getLastUsedEdus();
    if (optional.isPresent()) {
      lastUsed = optional.get();
    }
    assertThat(lastUsed).isEqualTo(createdAt);

    lastUsed = null;
    optional = apiTokenData.getLastUsedPpac();
    if (optional.isPresent()) {
      lastUsed = optional.get();
    }
    assertThat(lastUsed).isEqualTo(createdAt);

    lastUsed = null;
    optional = apiTokenData.getLastUsedSrs();
    if (optional.isPresent()) {
      lastUsed = optional.get();
    }
    assertThat(lastUsed).isEqualTo(createdAt);
  }
}
