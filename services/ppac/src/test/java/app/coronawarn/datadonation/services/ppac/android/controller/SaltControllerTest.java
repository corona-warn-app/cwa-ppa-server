package app.coronawarn.datadonation.services.ppac.android.controller;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doThrow;
import static org.springframework.http.HttpStatus.INTERNAL_SERVER_ERROR;

import app.coronawarn.datadonation.common.persistence.repository.ppac.android.SaltRepository;
import app.coronawarn.datadonation.common.persistence.service.SaltService;
import app.coronawarn.datadonation.services.ppac.config.TestBeanConfig;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Import;
import org.springframework.http.ResponseEntity;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit.jupiter.SpringExtension;

@ExtendWith(SpringExtension.class)
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@Import(TestBeanConfig.class)
@ActiveProfiles("test")
public class SaltControllerTest {

  @MockBean
  private SaltRepository saltRepository;

  @Autowired
  private SaltService saltService;

  @Autowired
  private RequestExecutor executor;

  @Test
  void checkResponseInternalServerError() {
    doThrow(new RuntimeException()).when(saltRepository).deleteSalt(any());
    ResponseEntity<String> actResponse = executor.executeDelete("test-salt-data");
    assertThat(actResponse.getStatusCode()).isEqualTo(INTERNAL_SERVER_ERROR);
  }
}
