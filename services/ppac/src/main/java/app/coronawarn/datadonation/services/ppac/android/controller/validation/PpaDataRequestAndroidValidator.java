package app.coronawarn.datadonation.services.ppac.android.controller.validation;

import app.coronawarn.datadonation.common.protocols.internal.ppdd.PPADataAndroid;
import app.coronawarn.datadonation.services.ppac.commons.PpaDataRequestValidationFailed;
import app.coronawarn.datadonation.services.ppac.commons.PpaDataRequestValidator;
import java.util.List;
import org.springframework.stereotype.Component;

@Component
public class PpaDataRequestAndroidValidator extends PpaDataRequestValidator<PPADataAndroid> {

  @Override
  public void validate(PPADataAndroid payload, Integer maxExposureWindowsToRejectSubmission) {
    validateCardinalities(payload.getExposureRiskMetadataSetList(), 1, "Exposure Risk Metadata");
    validateCardinalities(payload.getTestResultMetadataSetList(), 1, "Test Result Metadata");
    validateCardinalities(payload.getKeySubmissionMetadataSetList(), 1, "Key Submission Metadata");
    validateCardinalities(payload.getNewExposureWindowsList(), maxExposureWindowsToRejectSubmission,
        "New Exposure Windows");
  }

  @SuppressWarnings("rawtypes")
  private void validateCardinalities(List exposureRiskMetadataSetList, Integer maxSize,
      String entityInMessage) {
    if (exposureRiskMetadataSetList != null && exposureRiskMetadataSetList.size() > 1) {
      throw new PpaDataRequestValidationFailed(
          entityInMessage + " set contains more than " + maxSize + " element.");
    }
  }
}
