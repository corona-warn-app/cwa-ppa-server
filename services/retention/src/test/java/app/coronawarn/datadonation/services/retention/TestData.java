package app.coronawarn.datadonation.services.retention;

import static java.time.Instant.now;
import static java.time.temporal.ChronoUnit.DAYS;
import static java.time.temporal.ChronoUnit.HOURS;

import app.coronawarn.datadonation.common.persistence.domain.metrics.ClientMetadata;
import app.coronawarn.datadonation.common.persistence.domain.metrics.ExposureRiskMetadata;
import app.coronawarn.datadonation.common.persistence.domain.metrics.ExposureWindow;
import app.coronawarn.datadonation.common.persistence.domain.metrics.ExposureWindowTestResult;
import app.coronawarn.datadonation.common.persistence.domain.metrics.ExposureWindowsAtTestRegistration;
import app.coronawarn.datadonation.common.persistence.domain.metrics.KeySubmissionMetadataWithClientMetadata;
import app.coronawarn.datadonation.common.persistence.domain.metrics.KeySubmissionMetadataWithUserMetadata;
import app.coronawarn.datadonation.common.persistence.domain.metrics.ScanInstance;
import app.coronawarn.datadonation.common.persistence.domain.metrics.ScanInstancesAtTestRegistration;
import app.coronawarn.datadonation.common.persistence.domain.metrics.SummarizedExposureWindowsWithUserMetadata;
import app.coronawarn.datadonation.common.persistence.domain.metrics.TechnicalMetadata;
import app.coronawarn.datadonation.common.persistence.domain.metrics.TestResultMetadata;
import app.coronawarn.datadonation.common.persistence.domain.metrics.UserMetadata;
import app.coronawarn.datadonation.common.persistence.domain.metrics.embeddable.ClientMetadataDetails;
import app.coronawarn.datadonation.common.persistence.domain.metrics.embeddable.CwaVersionMetadata;
import app.coronawarn.datadonation.common.persistence.domain.metrics.embeddable.UserMetadataDetails;
import app.coronawarn.datadonation.common.persistence.repository.ApiTokenRepository;
import app.coronawarn.datadonation.common.persistence.repository.DeviceTokenRepository;
import app.coronawarn.datadonation.common.persistence.repository.ElsOneTimePasswordRepository;
import app.coronawarn.datadonation.common.persistence.repository.OneTimePasswordRepository;
import app.coronawarn.datadonation.common.persistence.repository.metrics.ClientMetadataRepository;
import app.coronawarn.datadonation.common.persistence.repository.metrics.ExposureRiskMetadataRepository;
import app.coronawarn.datadonation.common.persistence.repository.metrics.ExposureWindowRepository;
import app.coronawarn.datadonation.common.persistence.repository.metrics.ExposureWindowTestResultsRepository;
import app.coronawarn.datadonation.common.persistence.repository.metrics.ExposureWindowsAtTestRegistrationRepository;
import app.coronawarn.datadonation.common.persistence.repository.metrics.KeySubmissionMetadataWithClientMetadataRepository;
import app.coronawarn.datadonation.common.persistence.repository.metrics.KeySubmissionMetadataWithUserMetadataRepository;
import app.coronawarn.datadonation.common.persistence.repository.metrics.ScanInstancesAtTestRegistrationRepository;
import app.coronawarn.datadonation.common.persistence.repository.metrics.SummarizedExposureWindowsWithUserMetadataRepository;
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

  @Autowired
  private ExposureWindowsAtTestRegistrationRepository exposureWindowsAtTestRegistrationRepository;

  @Autowired
  private ExposureWindowTestResultsRepository exposureWindowTestResultsRepository;

  @Autowired
  private SummarizedExposureWindowsWithUserMetadataRepository summarizedExposureWindowsWithUserMetadataRepository;

  @Autowired
  private ScanInstancesAtTestRegistrationRepository scanInstancesAtTestRegistrationRepository;

  @Override
  public void run(ApplicationArguments args) {
    logger.info("Generating test data");
    IntStream.range(0, 35)
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
        .peek(this::insertExposureWindowsAtTestRegistration)
        .peek(this::insertExposureWindowTestResult)
        .peek(this::insertSummarizedExposureWindowsWithUserMetadata)
        .peek(this::insertScanInstancesAtTestRegistration)
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
        new ClientMetadataDetails(new CwaVersionMetadata(1, 0, 0), "etag", 1, 0, 0, 1l, 1l),
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
        new TechnicalMetadata(LocalDate.now(ZoneOffset.UTC).minusDays(i), false, false, false, false),
        new CwaVersionMetadata(1, 1, 1));
    testResultMetadataRepository.save(trm);
  }

  private void insertKeySubmissionMetadataWithUser(int i) {
    KeySubmissionMetadataWithUserMetadata UserMetadataDetails = new KeySubmissionMetadataWithUserMetadata(null, true,
        false, false, false, 1, 1, 1, 1,
        null, null,
        new app.coronawarn.datadonation.common.persistence.domain.metrics.embeddable.UserMetadataDetails(1, 1, 1),
        new TechnicalMetadata(LocalDate.now(ZoneOffset.UTC).minusDays(i), false, false, false, false),
        new CwaVersionMetadata(1, 1, 1));
    keySubmissionWithUserMetadataDetailsRepository.save(UserMetadataDetails);
  }

  private void insertKeySubmissionMetadataWithClient(int i) {
    KeySubmissionMetadataWithClientMetadata clientMetadata = new KeySubmissionMetadataWithClientMetadata(null, true,
        true, false, false, false, 1, false,
        new ClientMetadataDetails(new CwaVersionMetadata(1, 1, 1), "etag", 1, 0, 0, 1l, 1l),
        new TechnicalMetadata(LocalDate.now(ZoneOffset.UTC).minusDays(i), false, false, false, false));
    keySubmissionWithClientMetadataRepository.save(clientMetadata);
  }

  private void insertExposureWindows(int i) {
    ExposureWindow ew = new ExposureWindow(null, LocalDate.now(ZoneOffset.UTC).minusDays(i + 1), 1, 2, 1, 1, 1.0,
        new ClientMetadataDetails(new CwaVersionMetadata(1, 1, 1), "etag", 1, 0, 0, 1l, 1l),
        new TechnicalMetadata(LocalDate.now(ZoneOffset.UTC).minusDays(i), false, false, false, false),
        Set.of(new ScanInstance(null, null, 1, 2, 3, null), new ScanInstance(null, null, 3, 3, 3, null)));
    exposureWindowRepository.save(ew);
  }

  private void insertExposureWindowsAtTestRegistration(int i) {
    ExposureWindowsAtTestRegistration ewTestRegistration = new ExposureWindowsAtTestRegistration(null, 1,
        LocalDate.now(ZoneOffset.UTC), 1, 2, 1, 1, 1.0,
        Set.of(new ScanInstancesAtTestRegistration(null, null, 1, 2, 3,
            new TechnicalMetadata(LocalDate.now(ZoneOffset.UTC).minusDays(i), false, false, false, false))), false,
        new TechnicalMetadata(LocalDate.now(ZoneOffset.UTC).minusDays(i), false, false, false, false));
    exposureWindowsAtTestRegistrationRepository.save(ewTestRegistration);
  }

  private void insertExposureWindowTestResult(int i) {
    ExposureWindowTestResult ewTestResult = new ExposureWindowTestResult(null, 1,
        new ClientMetadataDetails(new CwaVersionMetadata(1, 1, 1), "etag", 1, 0, 0, 1l, 1l),
        new TechnicalMetadata(LocalDate.now(ZoneOffset.UTC).minusDays(i), false, false, false, false), Set.of(
        new ExposureWindowsAtTestRegistration(null, 1, LocalDate.now(ZoneOffset.UTC), 1, 2, 1, 1, 1.0,
            Set.of(new ScanInstancesAtTestRegistration(null, null, 1, 2, 3,
                new TechnicalMetadata(LocalDate.now(ZoneOffset.UTC).minusDays(i), false, false, false, false))), false,
            new TechnicalMetadata(LocalDate.now(ZoneOffset.UTC).minusDays(i), false, false, false, false))));
    exposureWindowTestResultsRepository.save(ewTestResult);
  }

  private void insertSummarizedExposureWindowsWithUserMetadata(int i) {
    SummarizedExposureWindowsWithUserMetadata summarizedExposureWindowsWithUserMetadata = new SummarizedExposureWindowsWithUserMetadata(
        null, LocalDate.now(ZoneOffset.UTC), "", 1, 1.0, new UserMetadataDetails(1, 1, 1),
        new TechnicalMetadata(LocalDate.now(ZoneOffset.UTC).minusDays(i), false, false, false, false));
    summarizedExposureWindowsWithUserMetadataRepository.save(summarizedExposureWindowsWithUserMetadata);
  }

  private void insertScanInstancesAtTestRegistration(int i) {
    ScanInstancesAtTestRegistration scanInstancesAtTestRegistration = new ScanInstancesAtTestRegistration(null, 1, 1, 1,
        1, new TechnicalMetadata((LocalDate.now(ZoneOffset.UTC).minusDays(i)), false, false, false, false));
    scanInstancesAtTestRegistrationRepository.save(scanInstancesAtTestRegistration);
  }

  private void insertExposureRiskMetadata(int i) {
    TechnicalMetadata tm = new TechnicalMetadata(LocalDate.now(ZoneOffset.UTC).minusDays(i), false, false, false,
        false);
    UserMetadataDetails um = new UserMetadataDetails(1, 1, 1);
    ExposureRiskMetadata erm = new ExposureRiskMetadata(null, 1, false,
        LocalDate.now(ZoneOffset.UTC).minusDays(i),
        false, 1, false, LocalDate.now(ZoneOffset.UTC).minusDays(i), false, um, tm,
        new CwaVersionMetadata(1, 1, 1));
    exposureRiskMetadataRepository.save(erm);
  }

  private void insertApiToken(int i) {
    long theNewNormal = now().minus(i, DAYS).getEpochSecond();
    apiTokenRepository.insert("test token" + i, theNewNormal, theNewNormal, theNewNormal, theNewNormal, theNewNormal);
  }
}
