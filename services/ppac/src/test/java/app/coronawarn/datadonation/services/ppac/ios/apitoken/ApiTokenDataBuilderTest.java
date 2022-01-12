package app.coronawarn.datadonation.services.ppac.ios.apitoken;

import static org.assertj.core.api.Assertions.assertThat;

import app.coronawarn.datadonation.common.persistence.domain.ApiTokenData;
import app.coronawarn.datadonation.common.utils.TimeUtils;
import app.coronawarn.datadonation.services.ppac.ios.verification.apitoken.ApiTokenBuilder;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class ApiTokenDataBuilderTest {

  @Test
  void buildApiToken() {
    String apiToken = "apitoken";

    final Long now = TimeUtils.getEpochSecondsForNow();

    final ApiTokenData newApiTokenData = ApiTokenBuilder.newBuilder()
        .setApiToken(apiToken)
        .setCreatedAt(now)
        .setExpirationDate(now)
        .setLastUsedEdus(now)
        .setLastUsedPpac(now).build();

    assertThat(newApiTokenData.getLastUsedEdus()).isPresent();
    assertThat(newApiTokenData.getLastUsedPpac()).isPresent();

    assertThat(newApiTokenData.getLastUsedPpac().get()).isEqualTo(now);
    assertThat(newApiTokenData.getLastUsedEdus().get()).isEqualTo(now);
    assertThat(newApiTokenData.getExpirationDate()).isEqualTo(now);
    assertThat(newApiTokenData.getCreatedAt()).isEqualTo(now);
    assertThat(newApiTokenData.getApiToken()).isEqualTo(apiToken);
  }
}
