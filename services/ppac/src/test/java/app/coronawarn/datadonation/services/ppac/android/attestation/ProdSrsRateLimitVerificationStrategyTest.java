package app.coronawarn.datadonation.services.ppac.android.attestation;

import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

import app.coronawarn.datadonation.common.persistence.domain.AndroidId;
import app.coronawarn.datadonation.common.persistence.service.AndroidIdService;
import app.coronawarn.datadonation.services.ppac.android.attestation.errors.DeviceQuotaExceeded;
import app.coronawarn.datadonation.services.ppac.config.AndroidTestBeanConfig;
import java.time.Instant;
import java.util.Optional;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Import;
import org.springframework.test.context.junit.jupiter.SpringExtension;

@ExtendWith(SpringExtension.class)
@Import(AndroidTestBeanConfig.class)
final class ProdSrsRateLimitVerificationStrategyTest {

  @MockBean
  AndroidIdService androidIdService;

  @Autowired
  ProdSrsRateLimitVerificationStrategy prodSrsRateLimitVerificationStrategy;

  @Test
  void testInvalidSrsRateLimit() {
    final AndroidId androidId = new AndroidId();
    // set last used timestamp to -24 hours
    androidId.setLastUsedSrs(Instant.now().minusSeconds(24 * 3600).toEpochMilli());
    when(androidIdService.getAndroidIdByPrimaryKey(any())).thenReturn(Optional.of(androidId));
    assertThrows(DeviceQuotaExceeded.class,
        () -> prodSrsRateLimitVerificationStrategy.validateSrsRateLimit(new byte[8]));
  }

  @Test
  void testValidSrsRateLimit() {
    final AndroidId androidId = new AndroidId();
    // set last used timestamp to -91 days
    androidId.setLastUsedSrs(Instant.now().minusSeconds(24 * 3600 * 91).toEpochMilli());
    when(androidIdService.getAndroidIdByPrimaryKey(any())).thenReturn(Optional.of(androidId));
    prodSrsRateLimitVerificationStrategy.validateSrsRateLimit(new byte[8]);
  }
}
