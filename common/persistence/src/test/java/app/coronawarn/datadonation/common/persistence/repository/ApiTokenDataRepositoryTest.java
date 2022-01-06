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
    long expirationDate = now.getEpochSecond();
    long createdAt = now.minus(1, ChronoUnit.DAYS).getEpochSecond();

    underTest.insert("apiToken", expirationDate, createdAt,
        createdAt, createdAt);
    ApiTokenData apiTokenData1 = null;
    Optional<ApiTokenData> optionalApiToken = underTest.findById("apiToken");
    if(optionalApiToken.isPresent()) {
      apiTokenData1 = optionalApiToken.get();
    }

    assertThat(apiTokenData1).isNotNull();
    assertThat(apiTokenData1.getApiToken()).isEqualTo("apiToken");
    assertThat(apiTokenData1.getExpirationDate()).isEqualTo(expirationDate);
    assertThat(apiTokenData1.getCreatedAt()).isEqualTo(createdAt);

    Long lastUsedEdus = null;
    Optional<Long> optionalLastUsedEdus = apiTokenData1.getLastUsedEdus();
    if(optionalLastUsedEdus.isPresent()) {
      lastUsedEdus = optionalLastUsedEdus.get();
    }
    assertThat(lastUsedEdus).isEqualTo(createdAt);

    Long lastUsedPpac = null;
    Optional<Long> optionalLastUsedPpac = apiTokenData1.getLastUsedPpac();
    if(optionalLastUsedPpac.isPresent()) {
      lastUsedPpac = optionalLastUsedPpac.get();
    }
    assertThat(lastUsedPpac).isEqualTo(createdAt);
  }
}
