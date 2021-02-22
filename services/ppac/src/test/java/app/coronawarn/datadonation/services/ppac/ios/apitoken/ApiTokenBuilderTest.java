package app.coronawarn.datadonation.services.ppac.ios.apitoken;

import static org.assertj.core.api.Assertions.assertThat;

import app.coronawarn.datadonation.common.persistence.domain.ApiToken;
import app.coronawarn.datadonation.common.utils.TimeUtils;
import app.coronawarn.datadonation.services.ppac.ios.verification.apitoken.ApiTokenBuilder;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
public class ApiTokenBuilderTest {

  @Test
  public void buildApiToken() {
    String apiToken = "apitoken";

    final Long now = TimeUtils.getEpochSecondForNow();

    final ApiToken newApiToken = ApiTokenBuilder.newBuilder()
        .setApiToken(apiToken)
        .setCreatedAt(now)
        .setExpirationDate(now)
        .setLastUsedEdus(now)
        .setLastUsedPpac(now).build();

    assertThat(newApiToken.getLastUsedEdus()).isPresent();
    assertThat(newApiToken.getLastUsedPpac()).isPresent();

    assertThat(newApiToken.getLastUsedPpac().get()).isEqualTo(now);
    assertThat(newApiToken.getLastUsedEdus().get()).isEqualTo(now);
    assertThat(newApiToken.getExpirationDate()).isEqualTo(now);
    assertThat(newApiToken.getCreatedAt()).isEqualTo(now);
    assertThat(newApiToken.getApiToken()).isEqualTo(apiToken);
  }
}
