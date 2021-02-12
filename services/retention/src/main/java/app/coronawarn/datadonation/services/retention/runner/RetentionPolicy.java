package app.coronawarn.datadonation.services.retention.runner;

import static java.time.temporal.ChronoUnit.DAYS;
import static java.time.temporal.ChronoUnit.HOURS;

import app.coronawarn.datadonation.common.persistence.repository.ApiTokenRepository;
import app.coronawarn.datadonation.common.persistence.repository.DeviceTokenRepository;
import app.coronawarn.datadonation.common.persistence.repository.OneTimePasswordRepository;
import app.coronawarn.datadonation.common.persistence.repository.metrics.ExposureRiskMetadataRepository;
import app.coronawarn.datadonation.common.persistence.repository.metrics.ExposureWindowRepository;
import app.coronawarn.datadonation.common.persistence.repository.metrics.KeySubmissionMetadataWithClientMetadataRepository;
import app.coronawarn.datadonation.common.persistence.repository.metrics.KeySubmissionMetadataWithUserMetadataRepository;
import app.coronawarn.datadonation.common.persistence.repository.metrics.TestResultMetadataRepository;
import app.coronawarn.datadonation.common.persistence.repository.ppac.android.SaltRepository;
import app.coronawarn.datadonation.services.retention.Application;
import app.coronawarn.datadonation.services.retention.config.RetentionConfiguration;
import java.time.Instant;
import java.time.LocalDate;
import java.time.ZoneOffset;
import java.time.temporal.TemporalUnit;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.context.ApplicationContext;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;

@Component
@Order(1)
public class RetentionPolicy implements ApplicationRunner {

  private final Logger logger = LoggerFactory.getLogger(getClass());
  private final ExposureRiskMetadataRepository exposureRiskMetadataRepository;
  private final ExposureWindowRepository exposureWindowRepository;
  private final KeySubmissionMetadataWithClientMetadataRepository keySubmissionMetadataWithClientMetadataRepository;
  private final KeySubmissionMetadataWithUserMetadataRepository keySubmissionMetadataWithUserMetadataRepository;
  private final TestResultMetadataRepository testResultMetadataRepository;
  private final DeviceTokenRepository deviceTokenRepository;
  private final OneTimePasswordRepository oneTimePasswordRepository;
  private final RetentionConfiguration retentionConfiguration;
  private final ApplicationContext appContext;
  private final SaltRepository saltRepository;
  private final ApiTokenRepository apiTokenRepository;

  /**
   * Creates a new {@link RetentionPolicy}.
   */
  @Autowired
  public RetentionPolicy(
      ApiTokenRepository apiTokenRepository,
      ExposureRiskMetadataRepository exposureRiskMetadataRepository,
      ExposureWindowRepository exposureWindowRepository,
      KeySubmissionMetadataWithClientMetadataRepository keySubmissionMetadataWithClientMetadataRepository,
      KeySubmissionMetadataWithUserMetadataRepository keySubmissionMetadataWithUserMetadataRepository,
      TestResultMetadataRepository testResultMetadataRepository,
      DeviceTokenRepository deviceTokenRepository,
      OneTimePasswordRepository oneTimePasswordRepository,
      RetentionConfiguration retentionConfiguration, ApplicationContext appContext,
      SaltRepository saltRepository) {
    this.exposureRiskMetadataRepository = exposureRiskMetadataRepository;
    this.exposureWindowRepository = exposureWindowRepository;
    this.keySubmissionMetadataWithClientMetadataRepository = keySubmissionMetadataWithClientMetadataRepository;
    this.keySubmissionMetadataWithUserMetadataRepository = keySubmissionMetadataWithUserMetadataRepository;
    this.testResultMetadataRepository = testResultMetadataRepository;
    this.deviceTokenRepository = deviceTokenRepository;
    this.oneTimePasswordRepository = oneTimePasswordRepository;
    this.retentionConfiguration = retentionConfiguration;
    this.appContext = appContext;
    this.saltRepository = saltRepository;
    this.apiTokenRepository = apiTokenRepository;
  }

  @Override
  public void run(ApplicationArguments args) {
    try {
      deleteOutdatedExposureRiskMetadata();
      deleteOutdatedExposureWindows();
      deleteKeySubmissionMetadataWithClient();
      deleteKeySubmissionMetadataWithUser();
      deleteTestResultsMetadata();
      deleteOutdatedApiTokens();
      deleteOutdatedOneTimePasswords();
      deleteOutdatedDeviceTokens();
      deleteOutdatedSalt();
    } catch (Exception e) {
      logger.error("Apply of retention policy failed.", e);
      Application.killApplication(appContext);
    }
  }

  private void deleteTestResultsMetadata() {
    LocalDate testResultsMetadataThreshold = subtractRetentionDaysFromNowToLocalDate(
        retentionConfiguration.getTestResultMetadataRetentionDays());

    logDeletionInDays(testResultMetadataRepository.countOlderThan(testResultsMetadataThreshold),
        retentionConfiguration.getKeyMetadataWithClientRetentionDays(),
        "test results metadata");
    testResultMetadataRepository.deleteOlderThan(testResultsMetadataThreshold);
  }

  private void deleteKeySubmissionMetadataWithUser() {
    LocalDate userThreshold = subtractRetentionDaysFromNowToLocalDate(
        retentionConfiguration.getKeyMetadataWithUserRetentionDays());

    logDeletionInDays(keySubmissionMetadataWithUserMetadataRepository.countOlderThan(userThreshold),
        retentionConfiguration.getKeyMetadataWithClientRetentionDays(),
        "key submission metadata with user");
    keySubmissionMetadataWithUserMetadataRepository.deleteOlderThan(userThreshold);
  }

  private void deleteKeySubmissionMetadataWithClient() {
    LocalDate clientsThreshold = subtractRetentionDaysFromNowToLocalDate(
        retentionConfiguration.getKeyMetadataWithClientRetentionDays());

    logDeletionInDays(keySubmissionMetadataWithClientMetadataRepository.countOlderThan(clientsThreshold),
        retentionConfiguration.getKeyMetadataWithClientRetentionDays(),
        "key submission metadata with client");
    keySubmissionMetadataWithClientMetadataRepository.deleteOlderThan(clientsThreshold);
  }

  private void deleteOutdatedExposureWindows() {
    LocalDate exposureWindowThreshold = subtractRetentionDaysFromNowToLocalDate(
        retentionConfiguration.getExposureWindowRetentionDays());

    logDeletionInDays(exposureWindowRepository.countOlderThan(exposureWindowThreshold),
        retentionConfiguration.getExposureRiskMetadataRetentionDays(),
        "exposure windows");
    exposureWindowRepository.deleteOlderThan(exposureWindowThreshold);
  }

  private void deleteOutdatedSalt() {
    long saltThreshold = subtractRetentionPeriodFromNowToSeconds(HOURS,
        retentionConfiguration.getSaltRetentionHours());
    logDeletionInHours(saltRepository.countOlderThan(saltThreshold),
        retentionConfiguration.getSaltRetentionHours(), "salts");
    saltRepository.deleteOlderThan(saltThreshold);
  }

  private void deleteOutdatedExposureRiskMetadata() {
    LocalDate exposureRiskMetadataThreshold = subtractRetentionDaysFromNowToLocalDate(
        retentionConfiguration.getExposureRiskMetadataRetentionDays());

    logDeletionInDays(exposureRiskMetadataRepository.countOlderThan(exposureRiskMetadataThreshold),
        retentionConfiguration.getExposureRiskMetadataRetentionDays(),
        "exposure risk metadata");
    exposureRiskMetadataRepository.deleteOlderThan(exposureRiskMetadataThreshold);

  }

  private LocalDate subtractRetentionDaysFromNowToLocalDate(Integer retentionDays) {
    return Instant.now().atOffset(ZoneOffset.UTC).toLocalDate()
        .minusDays(retentionDays);
  }

  private void logDeletionInDays(int dataAmount, int retentionDays, String dataName) {
    logger.info("Deleting {} " + dataName + " that are older than {} day(s) ago.", dataAmount, retentionDays);
  }

  private void logDeletionInHours(int dataAmount, int retentionDays, String dataName) {
    logger.info("Deleting {} " + dataName + " that are older than {} hour(s) ago.", dataAmount, retentionDays);
  }

  private void deleteOutdatedOneTimePasswords() {
    long otpThreshold = subtractRetentionPeriodFromNowToSeconds(DAYS, retentionConfiguration.getOtpRetentionDays());
    logDeletionInDays(oneTimePasswordRepository.countOlderThan(otpThreshold), retentionConfiguration.getOtpRetentionDays(),
        "one time passwords");
    oneTimePasswordRepository.deleteOlderThan(otpThreshold);
  }

  private void deleteOutdatedDeviceTokens() {
    long deviceTokenThreshold = subtractRetentionPeriodFromNowToSeconds(HOURS,
        retentionConfiguration.getDeviceTokenRetentionHours());
    logDeletionInHours(deviceTokenRepository.countOlderThan(deviceTokenThreshold),
        retentionConfiguration.getDeviceTokenRetentionHours(), "device tokens");

    deviceTokenRepository.deleteOlderThan(deviceTokenThreshold);
  }

  private void deleteOutdatedApiTokens() {
    long apiTokenThreshold = subtractRetentionPeriodFromNowToSeconds(DAYS,
        retentionConfiguration.getApiTokenRetentionDays());
    logDeletionInDays(apiTokenRepository.countOlderThan(apiTokenThreshold),
        retentionConfiguration.getApiTokenRetentionDays(), "API tokens");
    apiTokenRepository.deleteOlderThan(apiTokenThreshold);
  }

  private long subtractRetentionPeriodFromNowToSeconds(TemporalUnit temporalUnit, Integer retentionPeriod) {
    return Instant.now().truncatedTo(temporalUnit)
        .minus(retentionPeriod, temporalUnit)
        .getEpochSecond();
  }
}
