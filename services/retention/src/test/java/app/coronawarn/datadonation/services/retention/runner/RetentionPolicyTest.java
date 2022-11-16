package app.coronawarn.datadonation.services.retention.runner;

import static java.time.temporal.ChronoUnit.DAYS;
import static java.time.temporal.ChronoUnit.HOURS;
import static org.junit.Assert.assertEquals;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

import app.coronawarn.datadonation.common.persistence.repository.ApiTokenRepository;
import app.coronawarn.datadonation.common.persistence.repository.DeviceTokenRepository;
import app.coronawarn.datadonation.common.persistence.repository.ElsOneTimePasswordRepository;
import app.coronawarn.datadonation.common.persistence.repository.OneTimePasswordRepository;
import app.coronawarn.datadonation.common.persistence.repository.SrsOneTimePasswordRepository;
import app.coronawarn.datadonation.common.persistence.repository.metrics.ClientMetadataRepository;
import app.coronawarn.datadonation.common.persistence.repository.metrics.ExposureRiskMetadataRepository;
import app.coronawarn.datadonation.common.persistence.repository.metrics.ExposureWindowRepository;
import app.coronawarn.datadonation.common.persistence.repository.metrics.ExposureWindowTestResultsRepository;
import app.coronawarn.datadonation.common.persistence.repository.metrics.ExposureWindowsAtTestRegistrationRepository;
import app.coronawarn.datadonation.common.persistence.repository.metrics.KeySubmissionMetadataWithClientMetadataRepository;
import app.coronawarn.datadonation.common.persistence.repository.metrics.KeySubmissionMetadataWithUserMetadataRepository;
import app.coronawarn.datadonation.common.persistence.repository.metrics.ScanInstanceRepository;
import app.coronawarn.datadonation.common.persistence.repository.metrics.ScanInstancesAtTestRegistrationRepository;
import app.coronawarn.datadonation.common.persistence.repository.metrics.SummarizedExposureWindowsWithUserMetadataRepository;
import app.coronawarn.datadonation.common.persistence.repository.metrics.TestResultMetadataRepository;
import app.coronawarn.datadonation.common.persistence.repository.metrics.UserMetadataRepository;
import app.coronawarn.datadonation.common.persistence.repository.ppac.android.SaltRepository;
import app.coronawarn.datadonation.services.retention.config.RetentionConfiguration;
import java.time.Instant;
import java.time.LocalDate;
import java.time.ZoneOffset;
import java.time.temporal.TemporalUnit;
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
  ScanInstanceRepository scanInstanceRepository;
  @MockBean
  KeySubmissionMetadataWithClientMetadataRepository keySubmissionWithClientMetadataRepository;
  @MockBean
  ClientMetadataRepository clientMetadataRepository;
  @MockBean
  KeySubmissionMetadataWithUserMetadataRepository keySubmissionMetadataWithUserMetadataRepository;
  @MockBean
  TestResultMetadataRepository testResultMetadataRepository;
  @MockBean
  ApiTokenRepository apiTokenRepository;
  @MockBean
  DeviceTokenRepository deviceTokenRepository;
  @MockBean
  OneTimePasswordRepository otpRepository;
  @MockBean
  ElsOneTimePasswordRepository elsOtpRepository;
  @MockBean
  SrsOneTimePasswordRepository srsOtpRepository;
  @MockBean
  SaltRepository saltRepository;
  @MockBean
  UserMetadataRepository userMetadataRepository;
  @MockBean
  SummarizedExposureWindowsWithUserMetadataRepository summarizedExposureWindowsWithUserMetadataRepo;
  @MockBean
  ExposureWindowTestResultsRepository exposureWindowTestResultsRepository;
  @MockBean
  ScanInstancesAtTestRegistrationRepository scanInstancesAtTestRegistrationRepository;
  @MockBean
  ExposureWindowsAtTestRegistrationRepository exposureWindowsAtTestRegistrationRepository;
  @Autowired
  RetentionConfiguration retentionConfiguration;
  @Autowired
  RetentionPolicy retentionPolicy;

  @Test
  void testRetentionPolicyRunner() {
    retentionPolicy.run(null);
    verify(apiTokenRepository, times(1))
        .deleteOlderThan(
            subtractRetentionPeriodFromNowToSeconds(DAYS, retentionConfiguration.getApiTokenRetentionDays()));
    verify(deviceTokenRepository, times(1))
        .deleteOlderThan(
            subtractRetentionPeriodFromNowToEpochMilli(HOURS, retentionConfiguration.getDeviceTokenRetentionHours()));
    verify(otpRepository, times(1))
        .deleteOlderThan(subtractRetentionPeriodFromNowToSeconds(DAYS, retentionConfiguration.getOtpRetentionDays()));
    verify(elsOtpRepository, times(1)).deleteOlderThan(
        subtractRetentionPeriodFromNowToSeconds(DAYS, retentionConfiguration.getElsOtpRetentionDays()));
    verify(srsOtpRepository, times(1)).deleteOlderThan(
        subtractRetentionPeriodFromNowToSeconds(DAYS, retentionConfiguration.getSrsOtpRetentionDays()));
    verify(exposureRiskMetadataRepository, times(1))
        .deleteOlderThan(
            subtractRetentionDaysFromNowToLocalDate(retentionConfiguration.getExposureRiskMetadataRetentionDays()));
    verify(exposureWindowRepository, times(1))
        .deleteOlderThan(
            subtractRetentionDaysFromNowToLocalDate(retentionConfiguration.getExposureWindowRetentionDays()));
    verify(keySubmissionWithClientMetadataRepository, times(1))
        .deleteOlderThan(
            subtractRetentionDaysFromNowToLocalDate(retentionConfiguration.getKeyMetadataWithClientRetentionDays()));
    verify(keySubmissionMetadataWithUserMetadataRepository, times(1))
        .deleteOlderThan(
            subtractRetentionDaysFromNowToLocalDate(retentionConfiguration.getKeyMetadataWithUserRetentionDays()));
    verify(testResultMetadataRepository, times(1))
        .deleteOlderThan(
            subtractRetentionDaysFromNowToLocalDate(retentionConfiguration.getTestResultMetadataRetentionDays()));
    verify(saltRepository, times(1))
        .deleteOlderThan(
            subtractRetentionPeriodFromNowToEpochMilli(HOURS, retentionConfiguration.getSaltRetentionHours()));
    verify(clientMetadataRepository, times(1))
        .deleteOlderThan(
            subtractRetentionDaysFromNowToLocalDate(retentionConfiguration.getClientMetadataRetentionDays()));
    verify(scanInstanceRepository, times(1)).deleteOlderThan(
        subtractRetentionDaysFromNowToLocalDate(retentionConfiguration.getExposureWindowRetentionDays()));
    verify(summarizedExposureWindowsWithUserMetadataRepo, times(1)).deleteOlderThan(
        subtractRetentionDaysFromNowToLocalDate(retentionConfiguration.getSummarizedExposureWindowRetentionDays()));
    verify(exposureWindowTestResultsRepository, times(1)).deleteOlderThan(
        subtractRetentionDaysFromNowToLocalDate(
            retentionConfiguration.getScanInstanceAtTestRegistrationRetentionDays()));
    verify(scanInstancesAtTestRegistrationRepository, times(1)).deleteOlderThan(
        subtractRetentionDaysFromNowToLocalDate(
            retentionConfiguration.getScanInstanceAtTestRegistrationRetentionDays()));
    verify(exposureWindowsAtTestRegistrationRepository, times(1)).deleteOlderThan(
        subtractRetentionDaysFromNowToLocalDate(
            retentionConfiguration.getExposureWindowAtTestRegistrationRetentionDays()));
  }

  private LocalDate subtractRetentionDaysFromNowToLocalDate(Integer retentionDays) {
    return Instant.now().atOffset(ZoneOffset.UTC).toLocalDate()
        .minusDays(retentionDays);
  }

  private long subtractRetentionPeriodFromNowToSeconds(TemporalUnit temporalUnit, Integer retentionPeriod) {
    return Instant.now().truncatedTo(temporalUnit)
        .minus(retentionPeriod, temporalUnit)
        .getEpochSecond();
  }

  private long subtractRetentionPeriodFromNowToEpochMilli(TemporalUnit temporalUnit, Integer retentionPeriod) {
    return Instant.now().truncatedTo(temporalUnit).minus(retentionPeriod, temporalUnit).toEpochMilli();
  }

  @Test
  void testSrsOtpRetentionDays() {
    assertEquals(2, retentionConfiguration.getSrsOtpRetentionDays());
  }
}
