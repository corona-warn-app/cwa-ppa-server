package app.coronawarn.datadonation.services.retention.runner;

import static java.time.temporal.ChronoUnit.DAYS;
import static java.time.temporal.ChronoUnit.HOURS;

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

  static long subtractRetentionPeriodFromNowToEpochMilli(final TemporalUnit temporalUnit,
      final Integer retentionPeriod) {
    return Instant.now().truncatedTo(temporalUnit).minus(retentionPeriod, temporalUnit).toEpochMilli();
  }

  static long subtractRetentionPeriodFromNowToSeconds(final TemporalUnit temporalUnit, final Integer retentionPeriod) {
    return Instant.now().truncatedTo(temporalUnit).minus(retentionPeriod, temporalUnit).getEpochSecond();
  }

  /**
   * Calculates the date to be used for the deletion.
   *
   * @param retentionDays how many days back in time you want to travel?
   * @return TODAY - retentionDays
   */
  static LocalDate threshold(final long retentionDays) {
    return Instant.now().atOffset(ZoneOffset.UTC).toLocalDate().minusDays(retentionDays);
  }

  private final Logger logger = LoggerFactory.getLogger(getClass());
  @Autowired
  private ExposureRiskMetadataRepository exposureRiskMetadataRepository;
  @Autowired
  private ExposureWindowRepository exposureWindowRepository;
  @Autowired
  private ScanInstanceRepository scanInstanceRepository;
  @Autowired
  private KeySubmissionMetadataWithClientMetadataRepository keySubmissionMetadataWithClientMetadataRepository;
  @Autowired
  private KeySubmissionMetadataWithUserMetadataRepository keySubmissionMetadataWithUserMetadataRepository;
  @Autowired
  private TestResultMetadataRepository testResultMetadataRepository;
  @Autowired
  private DeviceTokenRepository deviceTokenRepository;
  @Autowired
  private OneTimePasswordRepository oneTimePasswordRepository;
  @Autowired
  private ElsOneTimePasswordRepository elsOneTimePasswordRepository;
  @Autowired
  private SrsOneTimePasswordRepository srsOneTimePasswordRepository;
  @Autowired
  private RetentionConfiguration retentionConfiguration;
  @Autowired
  private ApplicationContext appContext;
  @Autowired
  private SaltRepository saltRepository;
  @Autowired
  private ApiTokenRepository apiTokenRepository;
  @Autowired
  private ClientMetadataRepository clientMetadataRepository;
  @Autowired
  private UserMetadataRepository userMetadataRepository;
  @Autowired
  private SummarizedExposureWindowsWithUserMetadataRepository summarizedExposureWindowsWithUserMetadataRepo;
  @Autowired
  private ExposureWindowTestResultsRepository exposureWindowTestResultsRepository;
  @Autowired
  private ScanInstancesAtTestRegistrationRepository scanInstancesAtTestRegistrationRepository;
  @Autowired
  private ExposureWindowsAtTestRegistrationRepository exposureWindowsAtTestRegistrationRepository;

  private void deleteClientMetadata() {
    final LocalDate date = threshold(retentionConfiguration.getClientMetadataRetentionDays());
    logDeletionInDays(clientMetadataRepository.countOlderThan(date),
        retentionConfiguration.getClientMetadataRetentionDays(), "client metadata");
    clientMetadataRepository.deleteOlderThan(date);
  }

  private void deleteKeySubmissionMetadataWithClient() {
    final LocalDate date = threshold(retentionConfiguration.getKeyMetadataWithClientRetentionDays());
    logDeletionInDays(keySubmissionMetadataWithClientMetadataRepository.countOlderThan(date),
        retentionConfiguration.getKeyMetadataWithClientRetentionDays(), "key submission metadata with client");
    keySubmissionMetadataWithClientMetadataRepository.deleteOlderThan(date);
  }

  private void deleteKeySubmissionMetadataWithUser() {
    final LocalDate date = threshold(retentionConfiguration.getKeyMetadataWithUserRetentionDays());
    logDeletionInDays(keySubmissionMetadataWithUserMetadataRepository.countOlderThan(date),
        retentionConfiguration.getKeyMetadataWithUserRetentionDays(), "key submission metadata with user");
    keySubmissionMetadataWithUserMetadataRepository.deleteOlderThan(date);
  }

  private void deleteOutdatedApiTokens() {
    final long apiTokenThreshold = subtractRetentionPeriodFromNowToSeconds(DAYS,
        retentionConfiguration.getApiTokenRetentionDays());
    logDeletionInDays(apiTokenRepository.countOlderThan(apiTokenThreshold),
        retentionConfiguration.getApiTokenRetentionDays(), "API tokens");
    apiTokenRepository.deleteOlderThan(apiTokenThreshold);
  }

  private void deleteOutdatedDeviceTokens() {
    final long deviceTokenThreshold = subtractRetentionPeriodFromNowToEpochMilli(HOURS,
        retentionConfiguration.getDeviceTokenRetentionHours());
    logDeletionInHours(deviceTokenRepository.countOlderThan(deviceTokenThreshold),
        retentionConfiguration.getDeviceTokenRetentionHours(), "device tokens");
    deviceTokenRepository.deleteOlderThan(deviceTokenThreshold);
  }

  private void deleteOutdatedElsTokens() {
    final long elsOtpThreshold = subtractRetentionPeriodFromNowToSeconds(DAYS,
        retentionConfiguration.getElsOtpRetentionDays());
    logDeletionInDays(elsOneTimePasswordRepository.countOlderThan(elsOtpThreshold),
        retentionConfiguration.getElsOtpRetentionDays(), "els-verify tokens");
    elsOneTimePasswordRepository.deleteOlderThan(elsOtpThreshold);
  }

  private void deleteOutdatedSrsTokens() {
    final long srsOtpThreshold = subtractRetentionPeriodFromNowToSeconds(DAYS,
        retentionConfiguration.getSrsOtpRetentionDays());
    logDeletionInDays(srsOneTimePasswordRepository.countOlderThan(srsOtpThreshold),
        retentionConfiguration.getSrsOtpRetentionDays(), "srs-verify tokens");
    srsOneTimePasswordRepository.deleteOlderThan(srsOtpThreshold);
  }

  private void deleteOutdatedExposureRiskMetadata() {
    final LocalDate date = threshold(retentionConfiguration.getExposureRiskMetadataRetentionDays());
    logDeletionInDays(exposureRiskMetadataRepository.countOlderThan(date),
        retentionConfiguration.getExposureRiskMetadataRetentionDays(), "exposure risk metadata");
    exposureRiskMetadataRepository.deleteOlderThan(date);
  }

  private void deleteOutdatedExposureWindows() {
    final LocalDate date = threshold(retentionConfiguration.getExposureWindowRetentionDays());
    logDeletionInDays(exposureWindowRepository.countOlderThan(date),
        retentionConfiguration.getExposureWindowRetentionDays(), "exposure windows");
    exposureWindowRepository.deleteOlderThan(date);
  }

  private void deleteOutdatedOneTimePasswords() {
    final long otpThreshold = subtractRetentionPeriodFromNowToSeconds(DAYS,
        retentionConfiguration.getOtpRetentionDays());
    logDeletionInDays(oneTimePasswordRepository.countOlderThan(otpThreshold),
        retentionConfiguration.getOtpRetentionDays(), "one time passwords");
    oneTimePasswordRepository.deleteOlderThan(otpThreshold);
  }

  private void deleteOutdatedSalt() {
    final long saltThreshold = subtractRetentionPeriodFromNowToEpochMilli(HOURS,
        retentionConfiguration.getSaltRetentionHours());
    logDeletionInHours(saltRepository.countOlderThan(saltThreshold),
        retentionConfiguration.getSaltRetentionHours(), "salts");
    saltRepository.deleteOlderThan(saltThreshold);
  }

  private void deleteOutdatedScanInstance() {
    final LocalDate date = threshold(retentionConfiguration.getExposureWindowRetentionDays());
    logDeletionInDays(scanInstanceRepository.countOlderThan(date),
        retentionConfiguration.getExposureWindowRetentionDays(), "scan instance");
    scanInstanceRepository.deleteOlderThan(date);
  }

  private void deleteTestResultsMetadata() {
    final LocalDate date = threshold(retentionConfiguration.getTestResultMetadataRetentionDays());
    logDeletionInDays(testResultMetadataRepository.countOlderThan(date),
        retentionConfiguration.getTestResultMetadataRetentionDays(), "test results metadata");
    testResultMetadataRepository.deleteOlderThan(date);
  }

  private void deleteUserMetaData() {
    final LocalDate date = threshold(retentionConfiguration.getUserMetadataRetentionDays());
    logDeletionInDays(userMetadataRepository.countOlderThan(date),
        retentionConfiguration.getUserMetadataRetentionDays(), "user metadata");
    userMetadataRepository.deleteOlderThan(date);
  }

  private void deleteSummarizedExposureWindowsWithUserMetadata() {
    final LocalDate date = threshold(retentionConfiguration.getSummarizedExposureWindowRetentionDays());
    logDeletionInDays(summarizedExposureWindowsWithUserMetadataRepo.countOlderThan(date),
        retentionConfiguration.getSummarizedExposureWindowRetentionDays(), "summarized exposure windows");
    summarizedExposureWindowsWithUserMetadataRepo.deleteOlderThan(date);
  }

  private void deleteExposureWindowsTestResult() {
    final LocalDate date = threshold(retentionConfiguration.getExposureWindowTestResultRetentionDays());
    logDeletionInDays(exposureWindowTestResultsRepository.countOlderThan(date),
        retentionConfiguration.getExposureWindowTestResultRetentionDays(), "exposure window test result");
    exposureWindowTestResultsRepository.deleteOlderThan(date);
  }

  private void deleteExposureWindowAtTestRegistration() {
    final LocalDate date = threshold(retentionConfiguration.getExposureWindowAtTestRegistrationRetentionDays());
    logDeletionInDays(exposureWindowsAtTestRegistrationRepository.countOlderThan(date),
        retentionConfiguration.getExposureWindowAtTestRegistrationRetentionDays(),
        "exposure window at test registration");
    exposureWindowsAtTestRegistrationRepository.deleteOlderThan(date);
  }

  private void deleteScanInstanceAtTestRegistration() {
    final LocalDate date = threshold(retentionConfiguration.getScanInstanceAtTestRegistrationRetentionDays());
    logDeletionInDays(scanInstancesAtTestRegistrationRepository.countOlderThan(date),
        retentionConfiguration.getScanInstanceAtTestRegistrationRetentionDays(),
        "scan instance at test registration");
    scanInstancesAtTestRegistrationRepository.deleteOlderThan(date);
  }

  private void logDeletionInDays(final int dataAmount, final int retentionDays, final String dataName) {
    logger.info("Deleting {} {} that are older than {} day(s) ago.", dataAmount, dataName, retentionDays);
  }

  private void logDeletionInHours(final int dataAmount, final int retentionHours, final String dataName) {
    logger.info("Deleting {} {} that are older than {} hour(s) ago.", dataAmount, dataName, retentionHours);
  }

  @Override
  public void run(final ApplicationArguments args) {
    try {
      deleteClientMetadata();
      deleteKeySubmissionMetadataWithClient();
      deleteKeySubmissionMetadataWithUser();
      deleteOutdatedApiTokens();
      deleteOutdatedDeviceTokens();
      deleteOutdatedElsTokens();
      deleteOutdatedSrsTokens();
      deleteOutdatedExposureRiskMetadata();
      deleteOutdatedOneTimePasswords();
      deleteOutdatedSalt();
      deleteTestResultsMetadata();
      deleteUserMetaData();
      deleteOutdatedScanInstance();
      deleteOutdatedExposureWindows();
      deleteSummarizedExposureWindowsWithUserMetadata();
      deleteExposureWindowsTestResult();
      deleteExposureWindowAtTestRegistration();
      deleteScanInstanceAtTestRegistration();
    } catch (final Exception e) {
      logger.error("Apply of retention policy failed.", e);
      Application.killApplication(appContext);
    }
  }
}
