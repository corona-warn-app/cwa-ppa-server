package app.coronawarn.datadonation.services.ppac.commons;

import static app.coronawarn.datadonation.common.protocols.internal.ppdd.PPARiskLevel.RISK_LEVEL_UNKNOWN_VALUE;
import static app.coronawarn.datadonation.common.utils.TimeUtils.getLocalDateFor;

import app.coronawarn.datadonation.common.persistence.domain.metrics.ClientMetadata;
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
import app.coronawarn.datadonation.common.protocols.internal.ppdd.ExposureRiskMetadata;
import app.coronawarn.datadonation.common.protocols.internal.ppdd.PPAExposureWindow;
import app.coronawarn.datadonation.common.protocols.internal.ppdd.PPAExposureWindowScanInstance;
import app.coronawarn.datadonation.common.protocols.internal.ppdd.PPAKeySubmissionMetadata;
import app.coronawarn.datadonation.common.protocols.internal.ppdd.PPANewExposureWindow;
import app.coronawarn.datadonation.common.protocols.internal.ppdd.PPATestResultMetadata;
import app.coronawarn.datadonation.common.protocols.internal.ppdd.PPAUserMetadata;
import app.coronawarn.datadonation.services.ppac.config.PpacConfiguration;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Objects;
import java.util.Set;
import java.util.UUID;
import java.util.stream.Collectors;

public abstract class PpaDataRequestConverter<T, U> {

  protected static Integer ARRAY_SIZE_KEY_SUBMISSION_METADATA = 2;

  protected abstract ClientMetadataDetails convertToClientMetadataDetails(U clientMetadata);

  protected abstract CwaVersionMetadata convertToCwaVersionMetadata(U clientMetadata);

  /**
   * Convert the given proto structure to a domain {@link ClientMetadata} entity.
   */
  protected ClientMetadata convertToClientMetadataEntity(final U clientMetadata,
      final TechnicalMetadata technicalMetadata) {
    return new ClientMetadata(null, convertToClientMetadataDetails(clientMetadata), technicalMetadata);
  }

  /**
   * Convert the given proto structure to a domain {@link ExposureWindowsAtTestRegistration} entity.
   */
  protected Set<ExposureWindowsAtTestRegistration> convertToExposureWindowsAtTestRegistration(
      final List<PPANewExposureWindow> exposureWindows, Boolean afterTestRegistration,
      TechnicalMetadata technicalMetadata) {
    if (!exposureWindows.isEmpty()) {
      return exposureWindows.stream()
          .map(newWindow -> convertToExposureWindowAtTestRegistration(newWindow, afterTestRegistration,
              technicalMetadata))
          .filter(Objects::nonNull)
          .collect(Collectors.toSet());
    }
    return Collections.emptySet();
  }

  protected ExposureWindowsAtTestRegistration convertToExposureWindowAtTestRegistration(
      PPANewExposureWindow newExposureWindow, Boolean afterTestRegistration, TechnicalMetadata technicalMetadata) {
    PPAExposureWindow exposureWindow = newExposureWindow.getExposureWindow();
    Set<ScanInstancesAtTestRegistration> scanInstancesAtTestRegistration =
        convertToScanInstancesAtTestRegistrationEntities(newExposureWindow, technicalMetadata);
    if (exposureWindow != null) {
      return new ExposureWindowsAtTestRegistration(null, null, getLocalDateFor(exposureWindow.getDate()),
          exposureWindow.getReportTypeValue(), exposureWindow.getInfectiousnessValue(),
          exposureWindow.getCalibrationConfidence(), newExposureWindow.getTransmissionRiskLevel(),
          newExposureWindow.getNormalizedTime(), scanInstancesAtTestRegistration, afterTestRegistration,
          technicalMetadata);
    }
    return null;
  }

  protected ExposureWindowTestResult convertToExposureWindowTestResult(PPATestResultMetadata testResult,
      U clientMetadata, TechnicalMetadata technicalMetadata) {
    Set<ExposureWindowsAtTestRegistration> exposureWindowsTestRegistrations =
        convertToExposureWindowsAtTestRegistration(testResult.getExposureWindowsAtTestRegistrationList(), false,
            technicalMetadata);
    exposureWindowsTestRegistrations.addAll(
        convertToExposureWindowsAtTestRegistration(testResult.getExposureWindowsUntilTestResultList(), true,
            technicalMetadata));
    exposureWindowsTestRegistrations.stream().filter(Objects::nonNull).collect(Collectors.toSet());
    return new ExposureWindowTestResult(null, testResult.getTestResultValue(),
        convertToClientMetadataDetails(clientMetadata), technicalMetadata, exposureWindowsTestRegistrations);
  }

  /**
   * Convert exposure risk meta data to the internal format.
   *
   * @param exposureRiskMetadata the collection of exposure risk metadata contained in an request that needs to be
   *                             mapped to the internal data model.
   * @param userMetadata         the corresponding user meta data that is need to build the exposure metrics.
   * @return a new instance of exposure risk meta data.
   */
  protected app.coronawarn.datadonation.common.persistence.domain.metrics.ExposureRiskMetadata convertToExposureMetrics(
      List<ExposureRiskMetadata> exposureRiskMetadata, PPAUserMetadata userMetadata,
      TechnicalMetadata technicalMetadata, U clientMetadata) {
    if (!exposureRiskMetadata.isEmpty()) {
      ExposureRiskMetadata riskElement = exposureRiskMetadata.iterator().next();
      return new app.coronawarn.datadonation.common.persistence.domain.metrics.ExposureRiskMetadata(
          null, riskElement.getRiskLevelValue(),
          riskElement.getRiskLevelChangedComparedToPreviousSubmission(),
          getLocalDateFor(riskElement.getMostRecentDateAtRiskLevel()),
          riskElement.getDateChangedComparedToPreviousSubmission(),
          riskElement.getPtRiskLevelValue(),
          riskElement.getPtRiskLevelValue() != RISK_LEVEL_UNKNOWN_VALUE
              ? riskElement.getPtRiskLevelChangedComparedToPreviousSubmission() : null,
          riskElement.getPtRiskLevelValue() != RISK_LEVEL_UNKNOWN_VALUE
              ? getLocalDateFor(riskElement.getPtMostRecentDateAtRiskLevel()) : null,
          riskElement.getPtRiskLevelValue() != RISK_LEVEL_UNKNOWN_VALUE
              ? riskElement.getPtDateChangedComparedToPreviousSubmission() : null,
          convertToUserMetadataDetails(userMetadata), technicalMetadata, convertToCwaVersionMetadata(clientMetadata)
      );
    }
    return null;

  }

  protected ExposureWindow convertToExposureWindowEntity(final PPANewExposureWindow newExposureWindow,
      final U clientMetadata, final TechnicalMetadata technicalMetadata) {
    PPAExposureWindow exposureWindow = newExposureWindow.getExposureWindow();
    Set<ScanInstance> scanInstances = convertToScanInstancesEntities(newExposureWindow, technicalMetadata);
    return new ExposureWindow(null, getLocalDateFor(exposureWindow.getDate()), exposureWindow.getReportTypeValue(),
        exposureWindow.getInfectiousness().getNumber(), exposureWindow.getCalibrationConfidence(),
        newExposureWindow.getTransmissionRiskLevel(), newExposureWindow.getNormalizedTime(),
        convertToClientMetadataDetails(clientMetadata), technicalMetadata, scanInstances);
  }

  protected List<ExposureWindow> convertToExposureWindowMetrics(final List<PPANewExposureWindow> newExposureWindows,
      final U clientMetadata, final TechnicalMetadata technicalMetadata) {
    if (!newExposureWindows.isEmpty()) {
      return newExposureWindows.stream()
          .map(newWindow -> convertToExposureWindowEntity(newWindow, clientMetadata, technicalMetadata))
          .collect(Collectors.toList());
    }
    return null;
  }

  protected List<SummarizedExposureWindowsWithUserMetadata> convertToSummarizedExposureWindowsWithUserMetadata(
      final List<PPANewExposureWindow> newExposureWindows,
      final PPAUserMetadata userMetadata, final TechnicalMetadata technicalMetadata) {
    final List<SummarizedExposureWindowsWithUserMetadata> summarizedExposureWindowsWithUserMetadataList =
        new ArrayList<>();
    String batchId = UUID.randomUUID().toString();
    if (!newExposureWindows.isEmpty()) {
      newExposureWindows.forEach(newWindow -> summarizedExposureWindowsWithUserMetadataList.add(
          new SummarizedExposureWindowsWithUserMetadata(null,
              getLocalDateFor(newWindow.getExposureWindow().getDate()),
              batchId, newWindow.getTransmissionRiskLevel(),
              newWindow.getNormalizedTime(),
              convertToUserMetadataDetails(userMetadata),
              technicalMetadata
          )
      ));
    }
    return summarizedExposureWindowsWithUserMetadataList.isEmpty()
        ? Collections.emptyList() : summarizedExposureWindowsWithUserMetadataList;
  }

  protected List<KeySubmissionMetadataWithClientMetadata> convertToKeySubmissionWithClientMetadataMetrics(
      List<PPAKeySubmissionMetadata> keySubmissionsMetadata, U clientMetadata, TechnicalMetadata technicalMetadata) {
    final List<KeySubmissionMetadataWithClientMetadata> keySubmissionMetadataWithClientMetadataList =
        new ArrayList<>(ARRAY_SIZE_KEY_SUBMISSION_METADATA);
    if (!keySubmissionsMetadata.isEmpty()) {
      keySubmissionsMetadata.forEach(keySubmissionElement -> keySubmissionMetadataWithClientMetadataList
          .add(new KeySubmissionMetadataWithClientMetadata(null, keySubmissionElement.getSubmitted(),
              keySubmissionElement.getSubmittedInBackground(), keySubmissionElement.getSubmittedAfterCancel(),
              keySubmissionElement.getSubmittedAfterSymptomFlow(), keySubmissionElement.getAdvancedConsentGiven(),
              keySubmissionElement.getLastSubmissionFlowScreenValue(),
              keySubmissionElement.getSubmittedWithCheckIns().getNumber() == 0 ? null
                  : keySubmissionElement.getSubmittedWithCheckIns().getNumber() == 1,
              convertToClientMetadataDetails(clientMetadata), technicalMetadata)));
    }
    return keySubmissionMetadataWithClientMetadataList.isEmpty() ? null : keySubmissionMetadataWithClientMetadataList;
  }

  /**
   * Limit the number of exposure windows to convert for storage based on application configuration.
   */
  protected List<PPANewExposureWindow> sliceExposureWindows(
      List<PPANewExposureWindow> newExposureWindowsList, PpacConfiguration ppacConfiguration) {
    return newExposureWindowsList.stream().limit(ppacConfiguration.getMaxExposureWindowsToStore())
        .collect(Collectors.toList());
  }

  /**
   * Convert user meta data to its internal data format.
   *
   * @param userMetadata user meta data from an incoming requests that will be mapped to the internal data format.
   * @return a newly created instance  of {@link UserMetadataDetails }
   */
  protected UserMetadataDetails convertToUserMetadataDetails(PPAUserMetadata userMetadata) {
    return new UserMetadataDetails(userMetadata.getFederalStateValue(),
        userMetadata.getAdministrativeUnit(), userMetadata.getAgeGroup().getNumber());
  }

  /**
   * Convert the given proto structure to a domain {@link UserMetadata} entity.
   */
  protected UserMetadata convertToUserMetadataEntity(PPAUserMetadata userMetadata,
      TechnicalMetadata technicalMetadata) {
    return new UserMetadata(null,
        new UserMetadataDetails(userMetadata.getFederalState().getNumber(),
            userMetadata.getAdministrativeUnit(), userMetadata.getAgeGroup().getNumber()),
        technicalMetadata);
  }

  /**
   * Convert test result meta data to its internal data format.
   *
   * @param testResults  a collection of key submission meta data that is mapped to the internal data format.
   * @param userMetadata the corresponding user meta data.
   * @return a newly created instance  of {@link TestResultMetadata }
   */
  protected TestResultMetadata convertToTestResultMetrics(
      List<PPATestResultMetadata> testResults, PPAUserMetadata userMetadata,
      TechnicalMetadata technicalMetadata, U clientMetadata) {
    if (!testResults.isEmpty()) {
      PPATestResultMetadata resultElement = testResults.iterator().next();
      return new TestResultMetadata(null, resultElement.getTestResult().getNumber(),
          resultElement.getHoursSinceTestRegistration(),
          resultElement.getRiskLevelAtTestRegistrationValue(),
          resultElement.getDaysSinceMostRecentDateAtRiskLevelAtTestRegistration(),
          resultElement.getHoursSinceHighRiskWarningAtTestRegistration(),
          resultElement.getPtRiskLevelAtTestRegistrationValue(),
          resultElement.getPtDaysSinceMostRecentDateAtRiskLevelAtTestRegistration(),
          resultElement.getPtHoursSinceHighRiskWarningAtTestRegistration(),
          convertToUserMetadataDetails(userMetadata), technicalMetadata, convertToCwaVersionMetadata(clientMetadata));
    }
    return null;
  }

  /**
   * Convert key submission meta data to its internal data format.
   *
   * @param keySubmissionsMetadata a collection of key submission meta data that is mapped to the internal data format.
   * @param userMetadata           the corresponding user meta data.
   * @return a newly created instance of {@link KeySubmissionMetadataWithUserMetadata }
   */
  protected List<KeySubmissionMetadataWithUserMetadata> convertToKeySubmissionWithUserMetadataMetrics(
      List<PPAKeySubmissionMetadata> keySubmissionsMetadata, PPAUserMetadata userMetadata,
      TechnicalMetadata technicalMetadata, U clientMetadata) {
    final List<KeySubmissionMetadataWithUserMetadata> keySubmissionMetadataWithUserMetadataList =
        new ArrayList<>(ARRAY_SIZE_KEY_SUBMISSION_METADATA);
    if (!keySubmissionsMetadata.isEmpty()) {
      keySubmissionsMetadata.forEach(keySubmissionElement ->
          keySubmissionMetadataWithUserMetadataList.add(
              new KeySubmissionMetadataWithUserMetadata(null, keySubmissionElement.getSubmitted(),
                  keySubmissionElement.getSubmittedAfterSymptomFlow(),
                  keySubmissionElement.getSubmittedWithTeleTAN(),
                  keySubmissionElement.getSubmittedAfterRapidAntigenTest(),
                  keySubmissionElement.getHoursSinceTestResult(),
                  keySubmissionElement.getHoursSinceTestRegistration(),
                  keySubmissionElement.getDaysSinceMostRecentDateAtRiskLevelAtTestRegistration(),
                  keySubmissionElement.getHoursSinceHighRiskWarningAtTestRegistration(),
                  keySubmissionElement.getPtDaysSinceMostRecentDateAtRiskLevelAtTestRegistration(),
                  keySubmissionElement.getPtHoursSinceHighRiskWarningAtTestRegistration(),
                  convertToUserMetadataDetails(userMetadata), technicalMetadata,
                  convertToCwaVersionMetadata(clientMetadata))
          )
      );
    }
    return keySubmissionMetadataWithUserMetadataList.isEmpty() ? null : keySubmissionMetadataWithUserMetadataList;
  }

  protected Set<ScanInstance> convertToScanInstancesEntities(
      PPANewExposureWindow newExposureWindow, TechnicalMetadata technicalMetadata) {
    List<PPAExposureWindowScanInstance> scanInstances =
        newExposureWindow.getExposureWindow().getScanInstancesList();
    return scanInstances.stream().map(scanData -> this.convertToScanInstanceEntity(scanData, technicalMetadata))
        .collect(Collectors.toSet());
  }

  protected ScanInstance convertToScanInstanceEntity(PPAExposureWindowScanInstance scanInstanceData,
      TechnicalMetadata technicalMetadata) {
    return new ScanInstance(null, null, scanInstanceData.getTypicalAttenuation(),
        scanInstanceData.getMinAttenuation(), scanInstanceData.getSecondsSinceLastScan(), technicalMetadata);
  }

  protected Set<ScanInstancesAtTestRegistration> convertToScanInstancesAtTestRegistrationEntities(
      PPANewExposureWindow newExposureWindow, TechnicalMetadata technicalMetadata) {
    List<PPAExposureWindowScanInstance> scanInstances =
        newExposureWindow.getExposureWindow().getScanInstancesList();
    if (!scanInstances.isEmpty()) {
      return scanInstances.stream()
          .map(scanData -> this.convertToScanInstanceAtTestRegistrationEntity(scanData, technicalMetadata))
          .collect(Collectors.toSet());
    }
    return Collections.emptySet();
  }

  protected ScanInstancesAtTestRegistration convertToScanInstanceAtTestRegistrationEntity(
      PPAExposureWindowScanInstance scanInstanceData, TechnicalMetadata technicalMetadata) {
    return new ScanInstancesAtTestRegistration(null, null, scanInstanceData.getTypicalAttenuation(),
        scanInstanceData.getMinAttenuation(), scanInstanceData.getSecondsSinceLastScan(), technicalMetadata);
  }
}
