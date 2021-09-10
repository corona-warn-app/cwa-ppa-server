package app.coronawarn.datadonation.services.ppac.ios.verification.scenario.ratelimit;

import static java.time.ZoneOffset.UTC;
import static org.assertj.core.api.Assertions.assertThatNoException;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import app.coronawarn.datadonation.common.persistence.domain.ApiToken;
import app.coronawarn.datadonation.common.utils.TimeUtils;
import app.coronawarn.datadonation.services.ppac.config.PpacConfiguration;
import app.coronawarn.datadonation.services.ppac.config.PpacConfiguration.Ios;
import app.coronawarn.datadonation.services.ppac.ios.verification.errors.ApiTokenQuotaExceeded;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.stream.Stream;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import org.junit.jupiter.params.provider.ValueSource;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
public class ProdPpacIosRateLimitStrategyTest {

  ProdPpacIosRateLimitStrategy underTest;

  PpacConfiguration configuration;

  @BeforeEach
  public void setup() {
    configuration = new PpacConfiguration();
    Ios ios = new Ios();
    configuration.setIos(ios);
    ios.setApiTokenRateLimitSeconds(86100);
    underTest = new ProdPpacIosRateLimitStrategy(configuration);
  }

  @Test
  void shouldThrowExceptionWhenValidateForEdusIsNotOnTheSameMonth() {
    // given
    long now = TimeUtils.getEpochSecondsForNow();
    long expirationDate = TimeUtils.getLastDayOfMonthForNow();
    long lastUsedForEdus = LocalDateTime.now().minusMonths(0).toEpochSecond(UTC);
    ApiToken apiToken = new ApiToken("apiToken", expirationDate, now, lastUsedForEdus, null);

    // when - then
    assertThatThrownBy(() -> {
      underTest.validateForEdus(apiToken);
    }).isExactlyInstanceOf(ApiTokenQuotaExceeded.class);
  }

  @Test
  void shouldNotThrowExceptionWhenValidateForEdusIsNotOnTheSameMonth() {
    // given
    long now = TimeUtils.getEpochSecondsForNow();
    long expirationDate = TimeUtils.getLastDayOfMonthForNow();
    long lastUsedForEdus = LocalDateTime.now().minusMonths(1).toEpochSecond(UTC);
    ApiToken apiToken = new ApiToken("apiToken", expirationDate, now, lastUsedForEdus, null);

    // when - then
    assertThatNoException().isThrownBy(() -> {
      underTest.validateForEdus(apiToken);
    });
  }

  @ParameterizedTest
  @ValueSource(ints = {1, 7})
  void shouldNotThrowExceptionWhenOnlyUpdateDayIsTheSame(int i) {
    // given
    long now = TimeUtils.getEpochSecondsForNow();
    long expirationDate = TimeUtils.getLastDayOfMonthForNow();
    long lastUsedForPpa = LocalDateTime.now(UTC).minusDays(i).toEpochSecond(UTC);
    ApiToken apiToken = new ApiToken("apiToken", expirationDate, now, null, lastUsedForPpa);

    // when - then
    assertThatNoException().isThrownBy(() -> {
      underTest.validateForPpa(apiToken);
    });
  }

  @Test
  void shouldNotThrowExceptionWhenValidateForPpaIsMoreThan23HoursSameDay() {
    LocalDateTime three2Twelve = LocalDateTime.now(UTC).withHour(23).withMinute(57);
    TimeUtils.setNow(three2Twelve.toInstant(UTC));
    long expirationDate = TimeUtils.getLastDayOfMonthForNow();
    long lastUsedForPpa = three2Twelve.minusHours(23).minusMinutes(56).toEpochSecond(UTC);
    ApiToken apiToken = new ApiToken("apiToken", expirationDate, three2Twelve.toEpochSecond(UTC), null, lastUsedForPpa);

    // when - then
    assertThatNoException().isThrownBy(() -> {
      underTest.validateForPpa(apiToken);
    });
    TimeUtils.setNow(null);
  }

  @ParameterizedTest
  @MethodSource("generateLastUsedForPpa")
  public void shouldThrowApiTokenQuotaExceededWhenUpdatingOnTheSameDay(long lastUsedForPpa) {
    // given
    long now = TimeUtils.getEpochSecondsForNow();
    long expirationDate = TimeUtils.getLastDayOfMonthForNow();
    ApiToken apiToken = new ApiToken("apiToken", expirationDate, now, null, lastUsedForPpa);

    // when - then
    assertThatThrownBy(() -> {
      underTest.validateForPpa(apiToken);
    }).isExactlyInstanceOf(ApiTokenQuotaExceeded.class);
  }

  private static Stream<Arguments> generateLastUsedForPpa() {
    return Stream.of(
        Arguments.of(TimeUtils.getLocalDateForNow().atTime(LocalTime.MAX).toEpochSecond(UTC)),
        Arguments.of(TimeUtils.getLocalDateForNow().atTime(LocalTime.MIDNIGHT).toEpochSecond(UTC)),
        Arguments.of(TimeUtils.getEpochSecondsForNow()),
        Arguments.of(TimeUtils.getLocalDateForNow().atStartOfDay(UTC).toEpochSecond())
    );
  }

}
