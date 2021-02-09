package app.coronawarn.datadonation.common.persistence.service;

import app.coronawarn.datadonation.common.persistence.repository.metrics.ExposureRiskMetadataRepository;
import app.coronawarn.datadonation.common.persistence.repository.metrics.ExposureWindowRepository;
import app.coronawarn.datadonation.common.persistence.repository.metrics.KeySubmissionMetadataWithClientMetadataRepository;
import app.coronawarn.datadonation.common.persistence.repository.metrics.KeySubmissionMetadataWithUserMetadataRepository;
import app.coronawarn.datadonation.common.persistence.repository.metrics.ScanInstanceRepository;
import app.coronawarn.datadonation.common.persistence.repository.metrics.TestResultMetadataRepository;
import app.coronawarn.datadonation.common.protocols.internal.ppdd.PpaDataRequestAndroid.PPADataRequestAndroid;
import app.coronawarn.datadonation.common.protocols.internal.ppdd.PpaDataRequestIos.PPADataRequestIOS;
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
  private final PpaDataRequestIosConverter ppaDataRequestIosConverter;
  private final PpaDataRequestAndroidConverter ppaDataRequestAndroidConverter;

  /**
   * Constructs the service bean.
   */
  public PpaDataService(ExposureRiskMetadataRepository exposureRiskMetadataRepo,
      ExposureWindowRepository exposureWindowRepo, ScanInstanceRepository scanInstanceRepo,
      TestResultMetadataRepository testResultRepo,
      KeySubmissionMetadataWithUserMetadataRepository keySubmissionWithUserMetadataRepo,
      KeySubmissionMetadataWithClientMetadataRepository keySubmissionWithClientMetadataRepo,
      PpaDataRequestIosConverter ppaDataRequestIosConverter,
      PpaDataRequestAndroidConverter ppaDataRequestAndroidConverter) {
    this.exposureRiskMetadataRepo = exposureRiskMetadataRepo;
    this.exposureWindowRepo = exposureWindowRepo;
    this.scanInstanceRepo = scanInstanceRepo;
    this.testResultRepo = testResultRepo;
    this.keySubmissionWithUserMetadataRepo = keySubmissionWithUserMetadataRepo;
    this.keySubmissionWithClientMetadataRepo = keySubmissionWithClientMetadataRepo;
    this.ppaDataRequestIosConverter = ppaDataRequestIosConverter;
    this.ppaDataRequestAndroidConverter = ppaDataRequestAndroidConverter;
  }

  /**
   * Triggers the process of converting an incoming request to the internal data format if the client was an iOS
   * device.
   *
   * @param iosRequest the incoming request from an iOS client.
   */
  @Transactional(propagation = Propagation.REQUIRES_NEW, timeout = 20)
  public void storeForIos(PPADataRequestIOS iosRequest) {
    final PpaDataStorageRequest ppaDataStorageRequest = ppaDataRequestIosConverter.convertToStorageRequest(iosRequest);
    this.store(ppaDataStorageRequest);
  }

  /**
   * Triggers the process of converting an incoming request to the internal data format if the client was an android
   * device.
   *
   * @param androidRequest the incoming request from an android client.
   */
  @Transactional(propagation = Propagation.REQUIRES_NEW, timeout = 20)
  public void storeForAndroid(PPADataRequestAndroid androidRequest) {
    final PpaDataStorageRequest ppaDataStorageRequest = ppaDataRequestAndroidConverter
        .convertToStorageRequest(androidRequest);
    this.store(ppaDataStorageRequest);
  }

  /**
   * Store any metrics that have been provided via the storage request container object.
   */
  private void store(PpaDataStorageRequest dataToStore) {
    dataToStore.getExposureRiskMetric().ifPresent(exposureRiskMetadataRepo::save);
    dataToStore.getExposureWindowsMetric().ifPresent(exposureWindowRepo::save);
    dataToStore.getTestResultMetric().ifPresent(testResultRepo::save);
    dataToStore.getKeySubmissionWithUserMetadata().ifPresent(keySubmissionWithUserMetadataRepo::save);
    dataToStore.getKeySubmissionWithClientMetadata().ifPresent(keySubmissionWithClientMetadataRepo::save);
  }
}
