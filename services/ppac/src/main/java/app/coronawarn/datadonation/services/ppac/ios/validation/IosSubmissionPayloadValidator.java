package app.coronawarn.datadonation.services.ppac.ios.validation;

import app.coronawarn.datadonation.common.protocols.SubmissionPayloadIos;
import app.coronawarn.datadonation.services.ppac.config.PpacConfiguration;
import java.util.Base64;
import java.util.UUID;
import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;
import org.springframework.stereotype.Component;

@Component
public class IosSubmissionPayloadValidator implements
    ConstraintValidator<ValidIosSubmissionPayload, SubmissionPayloadIos> {

  private final PpacConfiguration ppacConfiguration;
  private Integer minDeviceTokenLength;
  private Integer maxDeviceTokenLength;

  /**
   * Validates {@link PpacConfiguration}.
   * 
   * @param ppacConfiguration - configuration to be validated
   */
  public IosSubmissionPayloadValidator(PpacConfiguration ppacConfiguration) {
    this.ppacConfiguration = ppacConfiguration;
    this.minDeviceTokenLength = ppacConfiguration.getMinDeviceTokenLength();
    this.maxDeviceTokenLength = ppacConfiguration.getMaxDeviceTokenLength();
  }

  @Override
  public boolean isValid(SubmissionPayloadIos value, ConstraintValidatorContext context) {
    context.disableDefaultConstraintViolation();
    String deviceToken = value.getAuthentication().getDeviceToken();
    String apiToken = value.getAuthentication().getApiToken();
    return checkDeviceTokenIsBase64(deviceToken, context)
        && checkDeviceTokenLength(deviceToken, context)
        && checkApiTokenUuid(apiToken, context);

  }

  private boolean checkApiTokenUuid(String apiToken, ConstraintValidatorContext constraintValidatorContext) {
    boolean isUuid = false;
    try {
      UUID.fromString(apiToken);
      isUuid = true;
    } catch (Exception e) {
      addViolation(constraintValidatorContext, "Api Token must a valid UUID v4 String");
    }
    return isUuid;
  }

  private boolean checkDeviceTokenLength(String deviceToken, ConstraintValidatorContext constraintValidatorContext) {
    boolean minLengthViolation = deviceToken.length() < this.minDeviceTokenLength;
    boolean maxLengthViolation = deviceToken.length() > this.maxDeviceTokenLength;
    boolean deviceTokenRangeViolation = minLengthViolation || maxLengthViolation;
    if (deviceTokenRangeViolation) {
      addViolation(constraintValidatorContext, String
          .format("Device token length must be in range %s and %s, but is %s", minDeviceTokenLength,
              maxDeviceTokenLength, deviceToken.length()));
      return false;
    }
    return true;
  }

  private boolean checkDeviceTokenIsBase64(String deviceToken, ConstraintValidatorContext constraintValidatorContext) {
    boolean isBase64 = false;
    try {
      Base64.getDecoder().decode(deviceToken);
      isBase64 = true;
    } catch (Exception e) {
      addViolation(constraintValidatorContext, "Device Token must a valid base64 encoded String");
    }
    return isBase64;
  }

  private void addViolation(ConstraintValidatorContext validatorContext, String message) {
    validatorContext.buildConstraintViolationWithTemplate(message).addConstraintViolation();
  }
}
