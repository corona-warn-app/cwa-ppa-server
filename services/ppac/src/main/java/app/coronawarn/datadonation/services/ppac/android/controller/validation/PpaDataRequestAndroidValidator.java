package app.coronawarn.datadonation.services.ppac.android.controller.validation;

import app.coronawarn.datadonation.common.protocols.internal.ppdd.PPADataAndroid;
import app.coronawarn.datadonation.common.protocols.internal.ppdd.PPANewExposureWindow;
import app.coronawarn.datadonation.services.ppac.commons.PpaDataRequestValidationFailed;
import app.coronawarn.datadonation.services.ppac.commons.PpaDataRequestValidator;
import java.util.List;
import org.springframework.stereotype.Component;

@Component
public class PpaDataRequestAndroidValidator extends PpaDataRequestValidator<PPADataAndroid> {

  @Override
  public void validate(PPADataAndroid payload, Integer maxExposureWindowsToRejectSubmission) {
    validateCardinalities(payload.getExposureRiskMetadataSetList(), 0, 1, "Exposure Risk Metadata");
    validateCardinalities(payload.getTestResultMetadataSetList(), 0, 1, "Test Result Metadata");
    validateCardinalities(payload.getKeySubmissionMetadataSetList(), 0, 1,
        "Key Submission Metadata");
    validateCardinalities(payload.getNewExposureWindowsList(), 0,
        maxExposureWindowsToRejectSubmission, "New Exposure Windows");
    validateCardinalitiesOfScanInstanceData(payload.getNewExposureWindowsList());
  }

  private void validateCardinalitiesOfScanInstanceData(
      List<PPANewExposureWindow> newExposureWindowsList) {
    newExposureWindowsList.forEach(expData -> {
      validateCardinalities(expData.getExposureWindow().getScanInstancesList(), 1, 15,
          "Scan Instance");
    });
  }

  @SuppressWarnings("rawtypes")
  private void validateCardinalities(List dataset, Integer minSize, Integer maxSize,
      String entityInMessage) {
    if (dataset != null && dataset.size() < minSize) {
      throw new PpaDataRequestValidationFailed(
          entityInMessage + " set contains less than " + minSize + " element.");
    }
    if (dataset != null && dataset.size() > maxSize) {
      throw new PpaDataRequestValidationFailed(
          entityInMessage + " set contains more than " + maxSize + " element.");
    }
  }
}
