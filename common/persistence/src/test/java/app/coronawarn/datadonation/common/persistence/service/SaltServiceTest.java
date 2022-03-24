package app.coronawarn.datadonation.common.persistence.service;

import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doThrow;

import app.coronawarn.datadonation.common.persistence.repository.ppac.android.SaltRepository;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.ActiveProfiles;

@SpringBootTest
@DirtiesContext
@ActiveProfiles("test")
class SaltServiceTest {

  @Autowired
  SaltService saltService;

  @MockBean
  SaltRepository saltRepository;

  @Test
  void testThrownRuntimeExceptionForSaltRepository() {
    doThrow(new RuntimeException()).when(saltRepository).deleteSalt(any());
    assertThrows(DeleteSaltException.class, () -> saltService.deleteSalt("test"));
  }
}
