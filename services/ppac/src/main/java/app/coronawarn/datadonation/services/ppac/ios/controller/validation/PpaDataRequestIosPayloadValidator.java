package app.coronawarn.datadonation.services.ppac.ios.controller.validation;

import app.coronawarn.datadonation.common.protocols.internal.ppdd.PPADataRequestIOS;
import app.coronawarn.datadonation.services.ppac.config.PpacConfiguration;
import java.util.Base64;
import java.util.UUID;
import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;
import org.springframework.stereotype.Component;

@Component
public class PpaDataRequestIosPayloadValidator implements
    ConstraintValidator<ValidPpaDataRequestIosPayload, PPADataRequestIOS> {

  private Integer minDeviceTokenLength;
  private Integer maxDeviceTokenLength;

  /**
   * Constructs a validator instance.
   */
  public PpaDataRequestIosPayloadValidator(PpacConfiguration ppacConfiguration) {
    this.minDeviceTokenLength = ppacConfiguration.getIos().getMinDeviceTokenLength();
    this.maxDeviceTokenLength = ppacConfiguration.getIos().getMaxDeviceTokenLength();
  }

  @Override
  public boolean isValid(PPADataRequestIOS value, ConstraintValidatorContext context) {
    context.disableDefaultConstraintViolation();
    String deviceToken = value.getAuthentication().getDeviceToken();
    String apiToken = value.getAuthentication().getApiToken();
    return checkDeviceTokenIsBase64(deviceToken, context)
        && checkDeviceTokenLength(deviceToken, context)
        && checkApiTokenUuid(apiToken, context);

  }

  private boolean checkApiTokenUuid(String apiToken,
      ConstraintValidatorContext constraintValidatorContext) {
    try {
      UUID.fromString(apiToken);
      return true;
    } catch (Exception e) {
      addViolation(constraintValidatorContext, "Api Token must a valid UUID v4 String");
      return false;
    }
  }

  private boolean checkDeviceTokenLength(String deviceToken,
      ConstraintValidatorContext constraintValidatorContext) {
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

  private boolean checkDeviceTokenIsBase64(String deviceToken,
      ConstraintValidatorContext constraintValidatorContext) {
    try {
      Base64.getDecoder().decode(deviceToken);
      return true;
    } catch (Exception e) {
      addViolation(constraintValidatorContext, "Device Token must a valid base64 encoded String");
      return false;
    }
  }

  private void addViolation(ConstraintValidatorContext validatorContext, String message) {
    validatorContext.buildConstraintViolationWithTemplate(message).addConstraintViolation();
  }
}
