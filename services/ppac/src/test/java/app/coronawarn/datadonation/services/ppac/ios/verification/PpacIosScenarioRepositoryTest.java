package app.coronawarn.datadonation.services.ppac.ios.verification;

import app.coronawarn.datadonation.common.persistence.domain.ApiTokenData;
import app.coronawarn.datadonation.common.persistence.repository.ApiTokenRepository;
import app.coronawarn.datadonation.services.ppac.ios.verification.apitoken.ApiTokenBuilder;
import app.coronawarn.datadonation.services.ppac.ios.verification.errors.InternalServerError;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Spy;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.relational.core.conversion.DbActionExecutionException;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class PpacIosScenarioRepositoryTest {

  @InjectMocks
  PpacIosScenarioRepository underTest;

  @Spy
  ApiTokenRepository apiTokenRepository;

  @Test
  void updateForEdus() {
    ArgumentCaptor<ApiTokenData> argumentCaptor = ArgumentCaptor.forClass(ApiTokenData.class);
    ApiTokenData apiTokenData = ApiTokenBuilder.newBuilder().setApiToken("test").build();
    underTest.updateForEdus(apiTokenData);
    verify(apiTokenRepository, times(1)).save(argumentCaptor.capture());

    assertThat(argumentCaptor.getValue().getLastUsedEdus()).isNotNull();
  }

  @Test
  void updateForPpa() {
    ArgumentCaptor<ApiTokenData> argumentCaptor = ArgumentCaptor.forClass(ApiTokenData.class);
    ApiTokenData apiTokenData = ApiTokenBuilder.newBuilder().setApiToken("test").build();
    underTest.updateForPpa(apiTokenData);
    verify(apiTokenRepository, times(1)).save(argumentCaptor.capture());

    assertThat(argumentCaptor.getValue().getLastUsedPpac()).isNotNull();
  }

  @Test
  void updateForSrs() {
    ArgumentCaptor<ApiTokenData> argumentCaptor = ArgumentCaptor.forClass(ApiTokenData.class);
    ApiTokenData apiTokenData = ApiTokenBuilder.newBuilder().setApiToken("test").build();
    underTest.updateForSrs(apiTokenData);
    verify(apiTokenRepository, times(1)).save(argumentCaptor.capture());

    assertThat(argumentCaptor.getValue().getLastUsedSrs()).isNotNull();
  }

  @Test
  void saveForPpaShouldFailInternalErrorThrown() {
    ArgumentCaptor<Long> argumentCaptor = ArgumentCaptor.forClass(Long.class);
    ApiTokenData apiTokenData = ApiTokenBuilder.newBuilder().setApiToken("test").build();
    underTest.saveForPpa(apiTokenData);
    doThrow(DbActionExecutionException.class).when(apiTokenRepository)
        .insert(any(), any(), any(), any(), any(), argumentCaptor.capture());

    assertThatThrownBy(() -> {
      underTest.saveForPpa(apiTokenData);
    }).isExactlyInstanceOf(InternalServerError.class);
  }

  @Test
  void saveForEdusShouldFailInternalErrorThrown() {
    ArgumentCaptor<Long> argumentCaptor = ArgumentCaptor.forClass(Long.class);
    ApiTokenData apiTokenData = ApiTokenBuilder.newBuilder().setApiToken("test").build();
    underTest.saveForPpa(apiTokenData);
    doThrow(DbActionExecutionException.class).when(apiTokenRepository)
        .insert(any(), any(), any(), any(), any(), argumentCaptor.capture());

    assertThatThrownBy(() -> {
      underTest.saveForEdus(apiTokenData);
    }).isExactlyInstanceOf(InternalServerError.class);
  }

  @Test
  void saveForSrsShouldFailInternalErrorThrown() {
    ArgumentCaptor<Long> argumentCaptor = ArgumentCaptor.forClass(Long.class);
    ApiTokenData apiTokenData = ApiTokenBuilder.newBuilder().setApiToken("test").build();
    underTest.saveForSrs(apiTokenData);
    doThrow(DbActionExecutionException.class).when(apiTokenRepository)
            .insert(any(), any(), any(), any(), any(), argumentCaptor.capture());

    assertThatThrownBy(() -> {
      underTest.saveForSrs(apiTokenData);
    }).isExactlyInstanceOf(InternalServerError.class);
  }
}
