package app.coronawarn.datadonation.services.ppac.ios.verification;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.any;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

import app.coronawarn.datadonation.common.persistence.domain.ApiToken;
import app.coronawarn.datadonation.common.persistence.repository.ApiTokenRepository;
import app.coronawarn.datadonation.services.ppac.ios.verification.apitoken.ApiTokenBuilder;
import app.coronawarn.datadonation.services.ppac.ios.verification.errors.InternalError;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Spy;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.relational.core.conversion.DbActionExecutionException;

@ExtendWith(MockitoExtension.class)
class PpacIosScenarioRepositoryTest {

  @InjectMocks
  PpacIosScenarioRepository underTest;

  @Spy
  ApiTokenRepository apiTokenRepository;

  @Test
  void updateForEdus() {
    ArgumentCaptor<ApiToken> argumentCaptor = ArgumentCaptor.forClass(ApiToken.class);
    ApiToken apiToken = ApiTokenBuilder.newBuilder().setApiToken("test").build();
    underTest.updateForEdus(apiToken);
    verify(apiTokenRepository, times(1)).save(argumentCaptor.capture());

    assertThat(argumentCaptor.getValue().getLastUsedEdus()).isNotNull();
  }

  @Test
  void updateForPpa() {
    ArgumentCaptor<ApiToken> argumentCaptor = ArgumentCaptor.forClass(ApiToken.class);
    ApiToken apiToken = ApiTokenBuilder.newBuilder().setApiToken("test").build();
    underTest.updateForPpa(apiToken);
    verify(apiTokenRepository, times(1)).save(argumentCaptor.capture());

    assertThat(argumentCaptor.getValue().getLastUsedPpac()).isNotNull();
  }

  @Test
  void saveForPpaShouldFailInternalErrorThrown() {
    ArgumentCaptor<Long> argumentCaptor = ArgumentCaptor.forClass(Long.class);
    ApiToken apiToken = ApiTokenBuilder.newBuilder().setApiToken("test").build();
    underTest.saveForPpa(apiToken);
    doThrow(DbActionExecutionException.class).when(apiTokenRepository)
        .insert(any(), any(), any(), any(), argumentCaptor.capture());

    assertThatThrownBy(() -> {
      underTest.saveForPpa(apiToken);
    }).isExactlyInstanceOf(InternalError.class);
  }

  @Test
  void saveForEdusShouldFailInternalErrorThrown() {
    ArgumentCaptor<Long> argumentCaptor = ArgumentCaptor.forClass(Long.class);
    ApiToken apiToken = ApiTokenBuilder.newBuilder().setApiToken("test").build();
    underTest.saveForPpa(apiToken);
    doThrow(DbActionExecutionException.class).when(apiTokenRepository)
        .insert(any(), any(), any(), any(), argumentCaptor.capture());

    assertThatThrownBy(() -> {
      underTest.saveForEdus(apiToken);
    }).isExactlyInstanceOf(InternalError.class);
  }
}
