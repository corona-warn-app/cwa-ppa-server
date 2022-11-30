package app.coronawarn.datadonation.services.ppac.android.attestation;

import app.coronawarn.datadonation.common.persistence.domain.AndroidId;
import app.coronawarn.datadonation.common.persistence.service.AndroidIdService;
import app.coronawarn.datadonation.services.ppac.android.attestation.errors.DeviceQuotaExceeded;
import app.coronawarn.datadonation.services.ppac.android.attestation.errors.HardwareBackedEvaluationTypeNotPresent;
import app.coronawarn.datadonation.services.ppac.config.AndroidTestBeanConfig;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Import;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import java.time.Instant;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

@ExtendWith(SpringExtension.class)
@Import(AndroidTestBeanConfig.class)
public class ProdSrsRateLimitVerificationStrategyTest {

    @MockBean
    AndroidIdService androidIdService;

    @Autowired
    ProdSrsRateLimitVerificationStrategy prodSrsRateLimitVerificationStrategy;

    @Test
    void testValidSrsRateLimit() {
        AndroidId androidId = new AndroidId();
        //set last used timestamp to -91 days
        androidId.setLastUsedSrs(Instant.now().minusSeconds(24*3600*91).toEpochMilli());
        when(androidIdService.getAndroidIdByPrimaryKey(any())).thenReturn(Optional.of(androidId));
        prodSrsRateLimitVerificationStrategy.validateSrsRateLimit(new byte[8]);
    }

    @Test
    void testInvalidSrsRateLimit() {
        AndroidId androidId = new AndroidId();
        //set last used timestamp to -24 hours
        androidId.setLastUsedSrs(Instant.now().minusSeconds(24*3600).toEpochMilli());
        when(androidIdService.getAndroidIdByPrimaryKey(any())).thenReturn(Optional.of(androidId));
        assertThrows(DeviceQuotaExceeded.class,
                () -> prodSrsRateLimitVerificationStrategy.validateSrsRateLimit(new byte[8]));
    }
}
