package app.coronawarn.analytics.services.ios;

import app.coronawarn.analytics.common.persistence.domain.ApiToken;
import app.coronawarn.analytics.common.persistence.repository.ApiTokenRepository;
import app.coronawarn.analytics.services.ios.controller.AppleDeviceApiClient;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.data.jdbc.DataJdbcTest;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.mock.mockito.MockBean;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.OffsetDateTime;
import java.time.temporal.ChronoUnit;
import java.time.temporal.TemporalAdjusters;
import java.util.Optional;

import static org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase.Replace.NONE;

@DataJdbcTest // use this
@AutoConfigureTestDatabase(replace = NONE)
public class ApiTokenRepositoryIntegrationTest {

    @MockBean
    private AppleDeviceApiClient appleDeviceApiClient;

    @Autowired
    private ApiTokenRepository underTest;

    @Test
    public void test_save() {
        // given
        OffsetDateTime now = OffsetDateTime.now();
        OffsetDateTime endOfMonth = now.with(TemporalAdjusters.lastDayOfMonth()).truncatedTo(ChronoUnit.DAYS);


        // when
        underTest.insert("apiToken", endOfMonth.toLocalDateTime(), now.toLocalDate(), now.toLocalDate());

        ApiToken persistedApiToken = underTest.findById(("apiToken")).get();

        // then
        Assertions.assertThat(persistedApiToken.getApiToken()).isEqualTo("apiToken");
        Assertions.assertThat(persistedApiToken.getExpirationDate()).isEqualTo(endOfMonth.toLocalDateTime());
        Assertions.assertThat(persistedApiToken.getLastUsedEDUS()).isEqualTo(now.toLocalDate());
        Assertions.assertThat(persistedApiToken.getLastUsedPPAC()).isEqualTo(now.toLocalDate());
    }
}
