package app.coronawarn.datadonation.services.retention.runner;

import static java.time.temporal.ChronoUnit.DAYS;
import static java.time.temporal.ChronoUnit.HOURS;

import app.coronawarn.datadonation.common.persistence.repository.AnalyticsFloatDataRepository;
import app.coronawarn.datadonation.common.persistence.repository.AnalyticsIntDataRepository;
import app.coronawarn.datadonation.common.persistence.repository.AnalyticsTextDataRepository;
import app.coronawarn.datadonation.common.persistence.repository.ApiTokenRepository;
import app.coronawarn.datadonation.common.persistence.repository.DeviceTokenRepository;
import app.coronawarn.datadonation.common.persistence.repository.OneTimePasswordRepository;
import app.coronawarn.datadonation.common.persistence.repository.android.SaltRepository;
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
  private final AnalyticsIntDataRepository intDataRepository;
  private final AnalyticsFloatDataRepository floatDataRepository;
  private final AnalyticsTextDataRepository textDataRepository;
  private final ApiTokenRepository apiTokenRepository;
  private final DeviceTokenRepository deviceTokenRepository;
  private final OneTimePasswordRepository oneTimePasswordRepository;
  private final RetentionConfiguration retentionConfiguration;
  private final ApplicationContext appContext;
  private final SaltRepository saltRepository;

  /**
   * Creates a new {@link RetentionPolicy}.
   */
  @Autowired
  public RetentionPolicy(
      AnalyticsIntDataRepository intDataRepository,
      AnalyticsFloatDataRepository floatDataRepository,
      AnalyticsTextDataRepository textDataRepository,
      ApiTokenRepository apiTokenRepository,
      DeviceTokenRepository deviceTokenRepository,
      OneTimePasswordRepository oneTimePasswordRepository,
      RetentionConfiguration retentionConfiguration, ApplicationContext appContext,
      SaltRepository saltRepository) {
    this.intDataRepository = intDataRepository;
    this.floatDataRepository = floatDataRepository;
    this.textDataRepository = textDataRepository;
    this.apiTokenRepository = apiTokenRepository;
    this.deviceTokenRepository = deviceTokenRepository;
    this.oneTimePasswordRepository = oneTimePasswordRepository;
    this.retentionConfiguration = retentionConfiguration;
    this.appContext = appContext;
    this.saltRepository = saltRepository;
  }

  @Override
  public void run(ApplicationArguments args) {
    try {
      deleteOutdatedAnalyticsData();
      deleteOutdatedApiTokens();
      deleteOutdatedDeviceTokens();
      deleteOutdatedOneTimePasswords();
      deleteOutdatedSalt();
    } catch (Exception e) {
      logger.error("Apply of retention policy failed.", e);
      Application.killApplication(appContext);
    }
  }

  private void deleteOutdatedSalt() {
    long saltThreshold = getThresholdFromTemporalUnitInSeconds(DAYS, retentionConfiguration.getSaltRetentionDays());
    logDeletion(saltRepository.countOlderThan(saltThreshold), retentionConfiguration.getSaltRetentionDays(), "salts");
    saltRepository.deleteOlderThan(saltThreshold);
  }

  private void deleteOutdatedAnalyticsData() {
    LocalDate intDataThreshold = getAnalyticsDataThreshold(retentionConfiguration.getIntDataRetentionDays());

    logDeletion(intDataRepository.countOlderThan(intDataThreshold),
        retentionConfiguration.getIntDataRetentionDays(),
        " ints data");
    intDataRepository.deleteOlderThan(intDataThreshold);

    LocalDate floatDataThreshold = getAnalyticsDataThreshold(retentionConfiguration.getIntDataRetentionDays());
    logDeletion(floatDataRepository.countOlderThan(floatDataThreshold),
        retentionConfiguration.getFloatDataRetentionDays(),
        " floats data");
    floatDataRepository.deleteOlderThan(floatDataThreshold);

    LocalDate textDataThreshold = getAnalyticsDataThreshold(retentionConfiguration.getIntDataRetentionDays());
    logDeletion(textDataRepository.countOlderThan(textDataThreshold),
        retentionConfiguration.getTextDataRetentionDays(),
        " texts data");
    textDataRepository.deleteOlderThan(textDataThreshold);

  }

  private LocalDate getAnalyticsDataThreshold(Integer retentionDays) {
    return Instant.now().atOffset(ZoneOffset.UTC).toLocalDate()
        .minusDays(retentionDays);
  }

  private void deleteOutdatedOneTimePasswords() {
    long otpThreshold = getThresholdFromTemporalUnitInSeconds(HOURS, retentionConfiguration.getOtpRetentionHours());
    logger.info("Deleting {} one time passwords that are older than {} days ago.",
        oneTimePasswordRepository.countOlderThan(otpThreshold),
        retentionConfiguration.getOtpRetentionHours());

    oneTimePasswordRepository.deleteOlderThan(otpThreshold);
  }

  private void logDeletion(int dataAmount, int retentionDays, String dataName) {
    logger.info("Deleting {} " + dataName + " that are older than {} days ago.", dataAmount, dataName, retentionDays);
  }

  private void deleteOutdatedDeviceTokens() {
    long deviceTokenThreshold = getThresholdFromTemporalUnitInSeconds(DAYS,
        retentionConfiguration.getDeviceTokenRetentionDays());
    logDeletion(deviceTokenRepository.countOlderThan(deviceTokenThreshold),
        retentionConfiguration.getDeviceTokenRetentionDays(), "device tokens");

    deviceTokenRepository.deleteOlderThan(deviceTokenThreshold);
  }

  private void deleteOutdatedApiTokens() {
    long apiTokenThreshold = getThresholdFromTemporalUnitInSeconds(DAYS,
        retentionConfiguration.getApiTokenRetentionDays());

    logDeletion(apiTokenRepository.countOlderThan(apiTokenThreshold), retentionConfiguration.getApiTokenRetentionDays(),
        "API tokens");

    apiTokenRepository.deleteOlderThan(apiTokenThreshold);
  }

  private long getThresholdFromTemporalUnitInSeconds(TemporalUnit temporalUnit, Integer retentionPeriod) {
    return Instant.now().truncatedTo(temporalUnit)
        .minus(retentionPeriod, temporalUnit)
        .getEpochSecond();
  }
}
