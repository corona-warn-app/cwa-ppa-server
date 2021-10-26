package app.coronawarn.datadonation.common.persistence.service;

import app.coronawarn.datadonation.common.persistence.domain.metrics.DataDonationMetric;
import app.coronawarn.datadonation.common.persistence.errors.MetricsDataCouldNotBeStored;
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
import java.util.Collection;
import java.util.stream.Collectors;
import javax.validation.ConstraintViolation;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

/**
 * Encapsulates logic regarding storing, retrieval and transactional manipulation of the PPA data model.
 */
@Service
public class PpaDataService {

  private final ExposureRiskMetadataRepository exposureRiskMetadataRepo;
  private final ExposureWindowRepository exposureWindowRepo;
  private final TestResultMetadataRepository testResultRepo;
  private final KeySubmissionMetadataWithUserMetadataRepository keySubmissionWithUserMetadataRepo;
  private final KeySubmissionMetadataWithClientMetadataRepository keySubmissionWithClientMetadataRepo;
  private final UserMetadataRepository userMetadataRepo;
  private final ClientMetadataRepository clientMetadataRepo;
  private final ExposureWindowsAtTestRegistrationRepository exposureWindowsAtTestRegistrationRepo;
  private final ExposureWindowTestResultsRepository exposureWindowTestResultsRepo;
  private final ScanInstancesAtTestRegistrationRepository scanInstancesAtTestRegistrationRepo;
  private final SummarizedExposureWindowsWithUserMetadataRepository summarizedExposureWindowsWithUserMetadataRepo;

  /**
   * Constructs the service bean.
   */
  public PpaDataService(ExposureRiskMetadataRepository exposureRiskMetadataRepo,
      ExposureWindowRepository exposureWindowRepo,
      TestResultMetadataRepository testResultRepo,
      KeySubmissionMetadataWithUserMetadataRepository keySubmissionWithUserMetadataRepo,
      KeySubmissionMetadataWithClientMetadataRepository keySubmissionWithClientMetadataRepo,
      UserMetadataRepository userMetadataRepo, ClientMetadataRepository clientMetadataRepo,
      ExposureWindowsAtTestRegistrationRepository exposureWindowsAtTestRegistrationRepo,
      ExposureWindowTestResultsRepository exposureWindowTestResultsRepo,
      ScanInstancesAtTestRegistrationRepository scanInstancesAtTestRegistrationRepo,
      SummarizedExposureWindowsWithUserMetadataRepository summarizedExposureWindowsWithUserMetadataRepo) {
    this.exposureRiskMetadataRepo = exposureRiskMetadataRepo;
    this.exposureWindowRepo = exposureWindowRepo;
    this.testResultRepo = testResultRepo;
    this.keySubmissionWithUserMetadataRepo = keySubmissionWithUserMetadataRepo;
    this.keySubmissionWithClientMetadataRepo = keySubmissionWithClientMetadataRepo;
    this.userMetadataRepo = userMetadataRepo;
    this.clientMetadataRepo = clientMetadataRepo;
    this.exposureWindowsAtTestRegistrationRepo = exposureWindowsAtTestRegistrationRepo;
    this.exposureWindowTestResultsRepo = exposureWindowTestResultsRepo;
    this.scanInstancesAtTestRegistrationRepo = scanInstancesAtTestRegistrationRepo;
    this.summarizedExposureWindowsWithUserMetadataRepo = summarizedExposureWindowsWithUserMetadataRepo;
  }

  /**
   * Store any metrics that have been provided via the storage request container object.
   */
  @Transactional(propagation = Propagation.REQUIRES_NEW, timeout = 20)
  public void store(PpaDataStorageRequest dataToStore) {
    dataToStore.getExposureRiskMetric().ifPresent(metrics -> {
      throwIfMetricsNotValid(metrics);
      exposureRiskMetadataRepo.save(metrics);
    });
    dataToStore.getExposureWindowsMetric().ifPresent(metrics -> {
      metrics.forEach(this::throwIfMetricsNotValid);
      exposureWindowRepo.saveAll(metrics);
    });
    dataToStore.getTestResultMetric().ifPresent(metrics -> {
      throwIfMetricsNotValid(metrics);
      testResultRepo.save(metrics);
    });
    dataToStore.getKeySubmissionWithUserMetadata().ifPresent(metrics -> {
      metrics.forEach(this::throwIfMetricsNotValid);
      keySubmissionWithUserMetadataRepo.saveAll(metrics);
    });
    dataToStore.getKeySubmissionWithClientMetadata().ifPresent(metrics -> {
      metrics.forEach(this::throwIfMetricsNotValid);
      keySubmissionWithClientMetadataRepo.saveAll(metrics);
    });

    dataToStore.getUserMetadata().ifPresent(metrics -> {
      throwIfMetricsNotValid(metrics);
      userMetadataRepo.save(metrics);
    });
    dataToStore.getClientMetadata().ifPresent(metrics -> {
      throwIfMetricsNotValid(metrics);
      clientMetadataRepo.save(metrics);
    });
    dataToStore.getExposureWindowsAtTestRegistration().ifPresent(metrics -> {
      metrics.forEach(this::throwIfMetricsNotValid);
      exposureWindowsAtTestRegistrationRepo.saveAll(metrics);
    });

  }

  private void throwIfMetricsNotValid(DataDonationMetric metricData) {
    Collection<ConstraintViolation<DataDonationMetric>> violations = metricData.validate();
    boolean isValid = violations.isEmpty();

    if (!isValid) {
      String violationMessages =
          violations.stream().map(this::convertToMessage).collect(Collectors.joining(","));
      throw new MetricsDataCouldNotBeStored(
          "Validation failed for PPA metrics. Violations: " + violationMessages);
    }
  }

  public String convertToMessage(ConstraintViolation<DataDonationMetric> v) {
    return v.getPropertyPath().toString() + " " + v.getMessage();
  }
}
