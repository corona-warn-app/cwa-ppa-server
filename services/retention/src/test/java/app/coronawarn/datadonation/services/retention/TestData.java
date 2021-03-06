package app.coronawarn.datadonation.services.retention;

import static java.time.Instant.now;
import static java.time.temporal.ChronoUnit.DAYS;
import static java.time.temporal.ChronoUnit.HOURS;

import app.coronawarn.datadonation.common.persistence.domain.metrics.ClientMetadata;
import app.coronawarn.datadonation.common.persistence.domain.metrics.ExposureRiskMetadata;
import app.coronawarn.datadonation.common.persistence.domain.metrics.ExposureWindow;
import app.coronawarn.datadonation.common.persistence.domain.metrics.KeySubmissionMetadataWithClientMetadata;
import app.coronawarn.datadonation.common.persistence.domain.metrics.KeySubmissionMetadataWithUserMetadata;
import app.coronawarn.datadonation.common.persistence.domain.metrics.ScanInstance;
import app.coronawarn.datadonation.common.persistence.domain.metrics.TechnicalMetadata;
import app.coronawarn.datadonation.common.persistence.domain.metrics.TestResultMetadata;
import app.coronawarn.datadonation.common.persistence.domain.metrics.UserMetadata;
import app.coronawarn.datadonation.common.persistence.domain.metrics.embeddable.ClientMetadataDetails;
import app.coronawarn.datadonation.common.persistence.domain.metrics.embeddable.UserMetadataDetails;
import app.coronawarn.datadonation.common.persistence.repository.ApiTokenRepository;
import app.coronawarn.datadonation.common.persistence.repository.DeviceTokenRepository;
import app.coronawarn.datadonation.common.persistence.repository.ElsOneTimePasswordRepository;
import app.coronawarn.datadonation.common.persistence.repository.OneTimePasswordRepository;
import app.coronawarn.datadonation.common.persistence.repository.metrics.ClientMetadataRepository;
import app.coronawarn.datadonation.common.persistence.repository.metrics.ExposureRiskMetadataRepository;
import app.coronawarn.datadonation.common.persistence.repository.metrics.ExposureWindowRepository;
import app.coronawarn.datadonation.common.persistence.repository.metrics.KeySubmissionMetadataWithClientMetadataRepository;
import app.coronawarn.datadonation.common.persistence.repository.metrics.KeySubmissionMetadataWithUserMetadataRepository;
import app.coronawarn.datadonation.common.persistence.repository.metrics.TestResultMetadataRepository;
import app.coronawarn.datadonation.common.persistence.repository.metrics.UserMetadataRepository;
import app.coronawarn.datadonation.common.persistence.repository.ppac.android.SaltRepository;
import java.nio.charset.StandardCharsets;
import java.time.LocalDate;
import java.time.ZoneOffset;
import java.util.Set;
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
  private ElsOneTimePasswordRepository elsOtpRepository;

  @Autowired
  private SaltRepository saltRepository;

  @Autowired
  private ExposureRiskMetadataRepository exposureRiskMetadataRepository;

  @Autowired
  private ExposureWindowRepository exposureWindowRepository;

  @Autowired
  private KeySubmissionMetadataWithClientMetadataRepository keySubmissionWithClientMetadataRepository;

  @Autowired
  private KeySubmissionMetadataWithUserMetadataRepository keySubmissionWithUserMetadataDetailsRepository;

  @Autowired
  private TestResultMetadataRepository testResultMetadataRepository;

  @Autowired
  private ClientMetadataRepository clientMetadataRepository;

  @Autowired
  private UserMetadataRepository userMetadataRepository;

  @Override
  public void run(ApplicationArguments args) {
    logger.info("Generating test data");
    IntStream.range(0, 12)
        .peek(this::insertApiToken)
        .peek(this::insertExposureRiskMetadata)
        .peek(this::insertExposureWindows)
        .peek(this::insertKeySubmissionMetadataWithClient)
        .peek(this::insertKeySubmissionMetadataWithUser)
        .peek(this::insertTestResultMetadata)
        .peek(this::insertDeviceTokens)
        .peek(this::insertOtps)
        .peek(this::insertElsOtps)
        .peek(this::insertClientMetadata)
        .peek(this::insertSalt)
        .forEach(this::insertUserMetadata);
    logger.info("Finished generating test data");
  }

  private void insertUserMetadata(int i) {
    UserMetadata userMetadata = new UserMetadata(null,
        new UserMetadataDetails(1, 1, 1),
        new TechnicalMetadata(LocalDate.now(ZoneOffset.UTC).minusDays(i), false, false, false, false));
    userMetadataRepository.save(userMetadata);
  }

  private void insertClientMetadata(int i) {
    ClientMetadata clientMetadata = new ClientMetadata(null,
        new ClientMetadataDetails(1, 0, 0, "etag", 1, 0, 0, 1l, 1l),
        new TechnicalMetadata(LocalDate.now(ZoneOffset.UTC).minusDays(i), false, false, false, false));
    clientMetadataRepository.save(clientMetadata);
  }

  private void insertSalt(int i) {
    saltRepository.persist("salt" + i, now().minus(i, HOURS).toEpochMilli());
  }

  private void insertOtps(int i) {
    otpRepository.insert("passwordA" + i, now().minus(i, DAYS).getEpochSecond(), now().getEpochSecond());
    otpRepository.insert("passwordB" + i, now().getEpochSecond(), now().minus(i, DAYS).getEpochSecond());
  }

  private void insertElsOtps(int i) {
    elsOtpRepository.insert("ELSPassword" + i,
        now().minus(i, DAYS).getEpochSecond(),
        now().getEpochSecond());
  }

  private void insertDeviceTokens(int i) {
    deviceTokenRepository.persist((long) i, ("" + i).getBytes(StandardCharsets.UTF_8),
        now().minus(i, HOURS).toEpochMilli());
  }

  private void insertTestResultMetadata(int i) {
    TestResultMetadata trm = new TestResultMetadata(null, 1, 1, 1, 1, 1, 1,
        1, 1,
        new UserMetadataDetails(1, 1, 1),
        new TechnicalMetadata(LocalDate.now(ZoneOffset.UTC).minusDays(i), false, false, false, false));
    testResultMetadataRepository.save(trm);
  }

  private void insertKeySubmissionMetadataWithUser(int i) {
    KeySubmissionMetadataWithUserMetadata UserMetadataDetails = new KeySubmissionMetadataWithUserMetadata(null, true,
        false, false, false, 1, 1, 1, 1,
        null, null,
        new app.coronawarn.datadonation.common.persistence.domain.metrics.embeddable.UserMetadataDetails(1, 1, 1),
        new TechnicalMetadata(LocalDate.now(ZoneOffset.UTC).minusDays(i), false, false, false, false));
    keySubmissionWithUserMetadataDetailsRepository.save(UserMetadataDetails);
  }

  private void insertKeySubmissionMetadataWithClient(int i) {
    KeySubmissionMetadataWithClientMetadata clientMetadata = new KeySubmissionMetadataWithClientMetadata(null, true,
        true, false, false, false, 1, false, new ClientMetadataDetails(1, 0, 0, "etag", 1, 0, 0, 1l, 1l),
        new TechnicalMetadata(LocalDate.now(ZoneOffset.UTC).minusDays(i), false, false, false, false));
    keySubmissionWithClientMetadataRepository.save(clientMetadata);
  }

  private void insertExposureWindows(int i) {
    ExposureWindow ew = new ExposureWindow(null, LocalDate.now(ZoneOffset.UTC).minusDays(i + 1), 1, 2, 1, 1, 1.0,
        new ClientMetadataDetails(1, 0, 0, "etag", 1, 0, 0, 1l, 1l),
        new TechnicalMetadata(LocalDate.now(ZoneOffset.UTC).minusDays(i), false, false, false, false),
        Set.of(new ScanInstance(null, null, 1, 2, 3), new ScanInstance(null, null, 3, 3, 3)));
    exposureWindowRepository.save(ew);
  }

  private void insertExposureRiskMetadata(int i) {
    TechnicalMetadata tm = new TechnicalMetadata(LocalDate.now(ZoneOffset.UTC).minusDays(i), false, false, false,
        false);
    UserMetadataDetails um = new UserMetadataDetails(1, 1, 1);
    ExposureRiskMetadata erm = new ExposureRiskMetadata(null, 1, false,
        LocalDate.now(ZoneOffset.UTC).minusDays(i),
        false, 1, false, LocalDate.now(ZoneOffset.UTC).minusDays(i), false, um, tm);
    exposureRiskMetadataRepository.save(erm);
  }

  private void insertApiToken(int i) {
    apiTokenRepository.insert("test token" + i, now().plus(10, DAYS).getEpochSecond(),
        now().minus(i, DAYS).getEpochSecond(), now().getEpochSecond(), now().getEpochSecond());
  }
}
