package app.coronawarn.datadonation.services.ppac.ios.verification;

import app.coronawarn.datadonation.common.persistence.domain.ApiToken;
import app.coronawarn.datadonation.common.persistence.repository.ApiTokenRepository;
import app.coronawarn.datadonation.services.ppac.ios.verification.errors.InternalError;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Spy;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.relational.core.conversion.DbActionExecutionException;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class PpacIosScenarioRepositoryTest {

  @InjectMocks
  PpacIosScenarioRepository underTest;

  @Spy
  ApiTokenRepository apiTokenRepository;

  @Test
  public void updateForEdus() {
    ArgumentCaptor<ApiToken> argumentCaptor = ArgumentCaptor.forClass(ApiToken.class);
    ApiToken apiToken = ApiToken.build("test");
    underTest.updateForEdus(apiToken);
    verify(apiTokenRepository, times(1)).save(argumentCaptor.capture());

    assertThat(argumentCaptor.getValue().getLastUsedEdus()).isNotNull();
  }

  @Test
  public void updateForPpa() {
    ArgumentCaptor<ApiToken> argumentCaptor = ArgumentCaptor.forClass(ApiToken.class);
    ApiToken apiToken = ApiToken.build("test");
    underTest.updateForPpa(apiToken);
    verify(apiTokenRepository, times(1)).save(argumentCaptor.capture());

    assertThat(argumentCaptor.getValue().getLastUsedPpac()).isNotNull();
  }

  @Test
  public void saveForPpaShouldFailInternalErrorThrown() {
    ArgumentCaptor<Long> argumentCaptor = ArgumentCaptor.forClass(Long.class);
    ApiToken apiToken = ApiToken.build("test");
    underTest.saveForPpa(apiToken);
    doThrow(DbActionExecutionException.class).when(apiTokenRepository)
        .insert(any(), any(), any(), any(), argumentCaptor.capture());

    assertThatThrownBy(() -> {
      underTest.saveForPpa(apiToken);
    }).isExactlyInstanceOf(InternalError.class);
  }

  @Test
  public void saveForEdusShouldFailInternalErrorThrown() {
    ArgumentCaptor<Long> argumentCaptor = ArgumentCaptor.forClass(Long.class);
    ApiToken apiToken = ApiToken.build("test");
    underTest.saveForPpa(apiToken);
    doThrow(DbActionExecutionException.class).when(apiTokenRepository)
        .insert(any(), any(), any(), any(), argumentCaptor.capture());

    assertThatThrownBy(() -> {
      underTest.saveForEdus(apiToken);
    }).isExactlyInstanceOf(InternalError.class);
  }
}
