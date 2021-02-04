package app.coronawarn.datadonation.services.retention.runner;

import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

import app.coronawarn.datadonation.common.persistence.repository.AnalyticsIntDataRepository;
import app.coronawarn.datadonation.common.persistence.repository.AnalyticsFloatDataRepository;
import app.coronawarn.datadonation.common.persistence.repository.AnalyticsTextDataRepository;
import app.coronawarn.datadonation.common.persistence.repository.ApiTokenRepository;
import app.coronawarn.datadonation.common.persistence.repository.DeviceTokenRepository;
import app.coronawarn.datadonation.common.persistence.repository.OneTimePasswordRepository;
import app.coronawarn.datadonation.common.persistence.repository.android.SaltRepository;
import app.coronawarn.datadonation.services.retention.config.RetentionConfiguration;
import java.time.Instant;
import java.time.LocalDate;
import java.time.ZoneOffset;
import java.time.temporal.ChronoUnit;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.boot.test.context.ConfigDataApplicationContextInitializer;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit.jupiter.SpringExtension;

@EnableConfigurationProperties(value = RetentionConfiguration.class)
@ExtendWith(SpringExtension.class)
@ContextConfiguration(classes = {RetentionPolicy.class}, initializers = ConfigDataApplicationContextInitializer.class)
class RetentionPolicyTest {

  @MockBean
  AnalyticsIntDataRepository intDataRepository;
  @MockBean
  AnalyticsFloatDataRepository floatDataRepository;
  @MockBean
  AnalyticsTextDataRepository textDataRepository;
  @MockBean
  ApiTokenRepository apiTokenRepository;
  @MockBean
  DeviceTokenRepository deviceTokenRepository;
  @MockBean
  OneTimePasswordRepository otpRepository;
  @MockBean
  SaltRepository saltRepository;
  @Autowired
  RetentionConfiguration retentionConfiguration;
  @Autowired
  RetentionPolicy retentionPolicy;
  private long threshold;
  private long otpThreshold;
  private LocalDate dataRepositoryThreshold;

  @BeforeEach
  void setUp() {
    threshold = Instant.now().truncatedTo(ChronoUnit.DAYS)
        .minus(retentionConfiguration.getDeviceTokenRetentionDays(), ChronoUnit.DAYS)
        .getEpochSecond();
    otpThreshold = Instant.now().truncatedTo(ChronoUnit.HOURS)
        .minus(retentionConfiguration.getOtpRetentionHours(), ChronoUnit.HOURS)
        .getEpochSecond();
    dataRepositoryThreshold = Instant.now().atOffset(ZoneOffset.UTC).toLocalDate()
        .minusDays(retentionConfiguration.getIntDataRetentionDays());
  }

  @Test
  void testRetentionPolicyRunner() {
    retentionPolicy.run(null);
    verify(apiTokenRepository, times(1)).deleteOlderThan(threshold);
    verify(deviceTokenRepository, times(1)).deleteOlderThan(threshold);
    verify(otpRepository, times(1)).deleteOlderThan(otpThreshold);
    verify(intDataRepository, times(1)).deleteOlderThan(dataRepositoryThreshold);
    verify(floatDataRepository, times(1)).deleteOlderThan(dataRepositoryThreshold);
    verify(textDataRepository, times(1)).deleteOlderThan(dataRepositoryThreshold);
    verify(saltRepository, times(1)).deleteOlderThan(threshold);

  }

  @Test
  void testHours() {
    System.out.println(Instant.now().truncatedTo(ChronoUnit.HOURS));
  }

}
