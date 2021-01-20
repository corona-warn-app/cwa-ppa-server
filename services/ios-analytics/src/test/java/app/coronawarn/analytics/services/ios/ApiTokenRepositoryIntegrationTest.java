package app.coronawarn.analytics.services.ios;

import app.coronawarn.analytics.common.persistence.domain.ApiToken;
import app.coronawarn.analytics.common.persistence.repository.ApiTokenRepository;
import app.coronawarn.analytics.services.ios.control.TimeUtils;
import app.coronawarn.analytics.services.ios.controller.IosDeviceApiClient;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.data.jdbc.DataJdbcTest;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.annotation.DirtiesContext;

import java.time.OffsetDateTime;
import java.time.ZoneOffset;
import java.time.temporal.ChronoUnit;
import java.time.temporal.TemporalAdjusters;

import static org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase.Replace.NONE;

@DataJdbcTest // use this
@AutoConfigureTestDatabase(replace = NONE)
@DirtiesContext
public class ApiTokenRepositoryIntegrationTest {

  @MockBean
  private IosDeviceApiClient iosDeviceApiClient;

  @Autowired
  private ApiTokenRepository underTest;

  @Autowired
  private TimeUtils timeUtils;

  @BeforeEach
  void clearDatabase() {
    underTest.deleteAll();
  }

  @Test
  public void insertApiTokenAndFindById() {
    // given
    OffsetDateTime now = OffsetDateTime.parse("2021-10-01T10:00:00+01:00");
    Long timestamp = now.toInstant().getEpochSecond();
    OffsetDateTime lastDayOfMonthFor = timeUtils.getLastDayOfMonthFor(now, ZoneOffset.UTC).truncatedTo(ChronoUnit.DAYS);

    // when
    underTest.insert("apiToken", lastDayOfMonthFor.toLocalDateTime(), timestamp, timestamp);

    ApiToken persistedApiToken = underTest.findById(("apiToken")).get();

    // then
    Assertions.assertThat(persistedApiToken.getApiToken()).isEqualTo("apiToken");
    Assertions.assertThat(persistedApiToken.getExpirationDate()).isEqualTo(lastDayOfMonthFor.toLocalDateTime());
    Assertions.assertThat(persistedApiToken.getLastUsedEDUS()).isEqualTo(timestamp);
    Assertions.assertThat(persistedApiToken.getLastUsedPPAC()).isEqualTo(timestamp);
  }
}
