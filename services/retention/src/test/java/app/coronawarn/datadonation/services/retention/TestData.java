package app.coronawarn.datadonation.services.retention;

import static java.time.Instant.now;
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
import java.nio.charset.StandardCharsets;
import java.time.LocalDate;
import java.time.ZoneOffset;
import java.util.stream.IntStream;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;
import org.springframework.test.context.ActiveProfiles;

@Component
@Order(-1)
@ActiveProfiles("test")
public class TestData implements ApplicationRunner {

  private final Logger logger = LoggerFactory.getLogger(this.getClass());
  @Autowired
  private ApiTokenRepository apiTokenRepository;

  @Autowired
  private DeviceTokenRepository deviceTokenRepository;

  @Autowired
  private OneTimePasswordRepository otpRepository;

  @Autowired
  private SaltRepository saltRepository;

  @Autowired
  private ExposureRiskMetadataRepository exposureRiskMetadataRepository;

  @Autowired
  private ExposureWindowRepository exposureWindowRepository;

  @Autowired
  private KeySubmissionMetadataWithClientMetadataRepository clientMetadataRepository;

  @Autowired
  private KeySubmissionMetadataWithUserMetadataRepository userMetadataRepository;

  @Autowired
  private TestResultMetadataRepository testResultMetadataRepository;

  @Override
  public void run(ApplicationArguments args) {
    logger.info("Generating test data");
    IntStream.range(0, 11)
        .peek(this::insertApiToken)
        .peek(this::insertExposureRiskMetadata)
        .peek(this::insertExposureWindows)
        .peek(this::insertKeySubmissionMetadataWithClient)
        .peek(this::insertKeySubmissionMetadataWithUser)
        .peek(this::insertTestResultMetadata)
        .peek(this::insertDeviceTokens)
        .peek(this::insertOtps)
        .forEach(this::insertSalt);
    logger.info("Finished generating test data");
  }

  private void insertSalt(int i) {
    saltRepository.persist("salt" + i, now().minus(i, DAYS).getEpochSecond());
  }

  private void insertOtps(int i) {
    otpRepository.insert("password" + i,
        now().minus(i, HOURS).getEpochSecond(), now().getEpochSecond(), now().getEpochSecond());
  }

  private void insertDeviceTokens(int i) {
    deviceTokenRepository
        .persist((long) i, ("" + i).getBytes(StandardCharsets.UTF_8), now().minus(i, DAYS).getEpochSecond());
  }

  private void insertTestResultMetadata(int i) {
    testResultMetadataRepository.persist((long) i, 1, 1, 1, 1, 1, 1, 1, 50, LocalDate.now(ZoneOffset.UTC).minusDays(i));
  }

  private void insertKeySubmissionMetadataWithUser(int i) {
    userMetadataRepository
        .persist((long) i, false, false, false, 1, 1, 1, 1, 1, 1, 1, LocalDate.now(ZoneOffset.UTC).minusDays(i));
  }

  private void insertKeySubmissionMetadataWithClient(int i) {
    clientMetadataRepository
        .persist((long) i, true, true, false, false, true, 1, 1, 1, 1, "etag",
            LocalDate.now(ZoneOffset.UTC).minusDays(i));
  }

  private void insertExposureWindows(int i) {
    exposureWindowRepository
        .persist((long) i, LocalDate.now(ZoneOffset.UTC).minusDays(i + 1), 1, 2, 1, 1, 1L, 1, 0, 0, "etag",
            LocalDate.now(ZoneOffset.UTC).minusDays(i));
  }

  private void insertExposureRiskMetadata(int i) {
    exposureRiskMetadataRepository
        .persist((long) i, 1, false, LocalDate.now(ZoneOffset.UTC).minusDays(i), false, 1, 1, 1,
            LocalDate.now(ZoneOffset.UTC).minusDays(i));
  }

  private void insertApiToken(int i) {
    apiTokenRepository.insert("test token" + i,
        now().plus(10, DAYS).getEpochSecond(),
        now().minus(i, DAYS).getEpochSecond(), now().getEpochSecond(), now().getEpochSecond());
  }
}
