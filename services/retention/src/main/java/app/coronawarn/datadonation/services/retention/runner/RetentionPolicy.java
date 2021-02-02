package app.coronawarn.datadonation.services.retention.runner;

import app.coronawarn.datadonation.common.persistence.domain.AnalyticsIntData;
import app.coronawarn.datadonation.common.persistence.repository.ApiTokenRepository;
import app.coronawarn.datadonation.common.persistence.repository.DeviceTokenRepository;
import app.coronawarn.datadonation.common.persistence.repository.OneTimePasswordRepository;
import app.coronawarn.datadonation.services.retention.Application;
import app.coronawarn.datadonation.services.retention.config.RetentionConfiguration;
import java.time.Instant;
import java.time.temporal.ChronoUnit;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.context.ApplicationContext;
import org.springframework.core.annotation.Order;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Component;

@Component
@Order(1)
public class RetentionPolicy implements ApplicationRunner {

  private final Logger logger = LoggerFactory.getLogger(getClass());
  private final CrudRepository<AnalyticsIntData, Long> analyticsDataRepository;//TODO:: clear analytics data
  private final ApiTokenRepository apiTokenRepository;
  private final DeviceTokenRepository deviceTokenRepository;
  private final OneTimePasswordRepository oneTimePasswordRepository;
  private final RetentionConfiguration configuration;
  private final ApplicationContext appContext;

  @Autowired
  public RetentionPolicy(
      CrudRepository<AnalyticsIntData, Long> analyticsDataRepository,
      ApiTokenRepository apiTokenRepository,
      DeviceTokenRepository deviceTokenRepository,
      OneTimePasswordRepository oneTimePasswordRepository,
      RetentionConfiguration configuration, ApplicationContext appContext) {
    this.analyticsDataRepository = analyticsDataRepository;
    this.apiTokenRepository = apiTokenRepository;
    this.deviceTokenRepository = deviceTokenRepository;
    this.oneTimePasswordRepository = oneTimePasswordRepository;
    this.configuration = configuration;
    this.appContext = appContext;
  }

  @Override
  public void run(ApplicationArguments args) {
    try {
      deleteOutdatedApiTokens();
      deleteOutdatedDeviceTokens();
      deleteOutdatedOneTimePasswords();
    } catch (Exception e) {
      logger.error("Apply of retention policy failed.", e);
      Application.killApplication(appContext);
    }
  }

  private void deleteOutdatedOneTimePasswords() {
    long threshold = getThresholdInSeconds(configuration.getDeviceTokenRetentionDays());
    logger.info("Deleting {} one time passwords that are older than {} days ago.",
        oneTimePasswordRepository.countOlderThan(threshold),
        configuration.getDeviceTokenRetentionDays());

    oneTimePasswordRepository.deleteOlderThan(threshold);
  }

  private void deleteOutdatedDeviceTokens() {
    long threshold = getThresholdInSeconds(configuration.getDeviceTokenRetentionDays());
    logger.info("Deleting {} device tokens that are older than {} days ago.",
        deviceTokenRepository.countOlderThan(threshold),
        configuration.getDeviceTokenRetentionDays());

    deviceTokenRepository.deleteOlderThan(threshold);
  }

  private void deleteOutdatedApiTokens() {
    long thresholdInSeconds = getThresholdInSeconds(configuration.getApiTokenRetentionDays());
    logger.info("Deleting {} API tokens that are older than {} days ago.",
        apiTokenRepository.countOlderThan(thresholdInSeconds),
        configuration.getApiTokenRetentionDays());

    apiTokenRepository.deleteOlderThan(thresholdInSeconds);
  }

  private long getThresholdInSeconds(Integer retentionDays) {
    return Instant.now().truncatedTo(ChronoUnit.DAYS)
        .minus(retentionDays, ChronoUnit.DAYS)
        .getEpochSecond();
  }
}
