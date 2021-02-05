package app.coronawarn.datadonation.services.retention.runner;

import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

import app.coronawarn.datadonation.common.persistence.repository.ApiTokenRepository;
import app.coronawarn.datadonation.common.persistence.repository.DeviceTokenRepository;
import app.coronawarn.datadonation.common.persistence.repository.OneTimePasswordRepository;
import app.coronawarn.datadonation.common.persistence.repository.metrics.ExposureRiskMetadataRepository;
import app.coronawarn.datadonation.common.persistence.repository.metrics.ExposureWindowRepository;
import app.coronawarn.datadonation.common.persistence.repository.metrics.KeySubmissionMetadataWithClientMetadataRepository;
import app.coronawarn.datadonation.common.persistence.repository.metrics.KeySubmissionMetadataWithUserMetadataRepository;
import app.coronawarn.datadonation.common.persistence.repository.metrics.ScanInstanceRepository;
import app.coronawarn.datadonation.common.persistence.repository.metrics.TestResultMetadataRepository;
import app.coronawarn.datadonation.common.persistence.repository.ppac.android.SaltRepository;
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
  ExposureRiskMetadataRepository exposureRiskMetadataRepository;
  @MockBean
  ExposureWindowRepository exposureWindowRepository;
  @MockBean
  KeySubmissionMetadataWithClientMetadataRepository clientMetadataRepository;
  @MockBean
  KeySubmissionMetadataWithUserMetadataRepository userMetadataRepository;
  @MockBean
  TestResultMetadataRepository testResultMetadataRepository;
  @MockBean
  ScanInstanceRepository scanInstanceRepository;
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
  private long daysTimestampThreshold;
  private long hoursTimestampThreshold;
  private LocalDate daysLocalDateThreshold;

  @BeforeEach
  void setUp() {
    daysTimestampThreshold = Instant.now().truncatedTo(ChronoUnit.DAYS)
        .minus(retentionConfiguration.getDeviceTokenRetentionDays(), ChronoUnit.DAYS)
        .getEpochSecond();
    hoursTimestampThreshold = Instant.now().truncatedTo(ChronoUnit.HOURS)
        .minus(retentionConfiguration.getOtpRetentionHours(), ChronoUnit.HOURS)
        .getEpochSecond();
    daysLocalDateThreshold = Instant.now().atOffset(ZoneOffset.UTC).toLocalDate()
        .minusDays(retentionConfiguration.getExposureRiskMetadataRetentionDays());
  }

  @Test
  void testRetentionPolicyRunner() {
    retentionPolicy.run(null);
    verify(apiTokenRepository, times(1)).deleteOlderThan(daysTimestampThreshold);
    verify(deviceTokenRepository, times(1)).deleteOlderThan(daysTimestampThreshold);
    verify(otpRepository, times(1)).deleteOlderThan(hoursTimestampThreshold);
    verify(exposureRiskMetadataRepository, times(1)).deleteOlderThan(daysLocalDateThreshold);
    verify(exposureWindowRepository, times(1)).deleteOlderThan(daysLocalDateThreshold);
    verify(clientMetadataRepository, times(1)).deleteOlderThan(daysLocalDateThreshold);
    verify(userMetadataRepository, times(1)).deleteOlderThan(daysLocalDateThreshold);
    verify(testResultMetadataRepository, times(1)).deleteOlderThan(daysLocalDateThreshold);
    verify(saltRepository, times(1)).deleteOlderThan(daysTimestampThreshold);

  }

}
