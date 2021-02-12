package app.coronawarn.datadonation.common.persistence.service;

import app.coronawarn.datadonation.common.persistence.domain.metrics.DataDonationMetric;
import app.coronawarn.datadonation.common.persistence.errors.MetricsDataCouldNotBeStored;
import app.coronawarn.datadonation.common.persistence.repository.metrics.ClientMetadataRepository;
import app.coronawarn.datadonation.common.persistence.repository.metrics.ExposureRiskMetadataRepository;
import app.coronawarn.datadonation.common.persistence.repository.metrics.ExposureWindowRepository;
import app.coronawarn.datadonation.common.persistence.repository.metrics.KeySubmissionMetadataWithClientMetadataRepository;
import app.coronawarn.datadonation.common.persistence.repository.metrics.KeySubmissionMetadataWithUserMetadataRepository;
import app.coronawarn.datadonation.common.persistence.repository.metrics.ScanInstanceRepository;
import app.coronawarn.datadonation.common.persistence.repository.metrics.TestResultMetadataRepository;
import app.coronawarn.datadonation.common.persistence.repository.metrics.UserMetadataRepository;
import java.util.Collection;
import java.util.List;
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
  private final ScanInstanceRepository scanInstanceRepo;
  private final TestResultMetadataRepository testResultRepo;
  private final KeySubmissionMetadataWithUserMetadataRepository keySubmissionWithUserMetadataRepo;
  private final KeySubmissionMetadataWithClientMetadataRepository keySubmissionWithClientMetadataRepo;
  private final UserMetadataRepository userMetadataRepo;
  private final ClientMetadataRepository clientMetadataRepo;

  /**
   * Constructs the service bean.
   */
  public PpaDataService(ExposureRiskMetadataRepository exposureRiskMetadataRepo,
      ExposureWindowRepository exposureWindowRepo, ScanInstanceRepository scanInstanceRepo,
      TestResultMetadataRepository testResultRepo,
      KeySubmissionMetadataWithUserMetadataRepository keySubmissionWithUserMetadataRepo,
      KeySubmissionMetadataWithClientMetadataRepository keySubmissionWithClientMetadataRepo, 
      UserMetadataRepository userMetadataRepo, ClientMetadataRepository clientMetadataRepo) {
    this.exposureRiskMetadataRepo = exposureRiskMetadataRepo;
    this.exposureWindowRepo = exposureWindowRepo;
    this.scanInstanceRepo = scanInstanceRepo;
    this.testResultRepo = testResultRepo;
    this.keySubmissionWithUserMetadataRepo = keySubmissionWithUserMetadataRepo;
    this.keySubmissionWithClientMetadataRepo = keySubmissionWithClientMetadataRepo;
    this.userMetadataRepo = userMetadataRepo;
    this.clientMetadataRepo = clientMetadataRepo;
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
      throwIfMetricsNotValid(metrics);
      exposureWindowRepo.save(metrics);
    });
    dataToStore.getTestResultMetric().ifPresent(metrics -> {
      throwIfMetricsNotValid(metrics);
      testResultRepo.save(metrics);
    });
    dataToStore.getKeySubmissionWithUserMetadata().ifPresent(metrics -> {
      throwIfMetricsNotValid(metrics);
      keySubmissionWithUserMetadataRepo.save(metrics);
    });
    dataToStore.getKeySubmissionWithClientMetadata().ifPresent(metrics -> {
      throwIfMetricsNotValid(metrics);
      keySubmissionWithClientMetadataRepo.save(metrics);
    });
    dataToStore.getUserMetadata().ifPresent(metrics -> {
      userMetadataRepo.save(metrics);
    });
    dataToStore.getClientMetadata().ifPresent(metrics -> {
      clientMetadataRepo.save(metrics);
    });
  }

  private void throwIfMetricsNotValid(DataDonationMetric metricData) {
    Collection<ConstraintViolation<DataDonationMetric>> violations = metricData.validate();
    boolean isValid = violations.isEmpty();

    if (!isValid) {
      String violationMessages =
          violations.stream().map(this::convertToMessage).collect(Collectors.joining(","));
      throw new MetricsDataCouldNotBeStored(
          "Validation failed for diagnosis key from database. Violations: " + violationMessages);
    }
  }
  
  public String convertToMessage(ConstraintViolation<DataDonationMetric> v) {
    return v.getPropertyPath().toString() + " " + v.getMessage();
  }
}
