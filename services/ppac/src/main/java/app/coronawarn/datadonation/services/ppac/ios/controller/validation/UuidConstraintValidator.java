package app.coronawarn.datadonation.services.ppac.ios.controller.validation;

import java.util.UUID;
import javax.validation.ConstraintValidatorContext;

abstract class UuidConstraintValidator {

  protected boolean isValid(String uuid,
      ConstraintValidatorContext context) {
    context.disableDefaultConstraintViolation();
    return checkIsValidUuid(uuid, context);
  }

  private boolean checkIsValidUuid(String string,
      ConstraintValidatorContext constraintValidatorContext) {
    boolean isUuid = false;
    try {
      UUID.fromString(string);
      isUuid = true;
    } catch (Exception e) {
      addViolation(constraintValidatorContext);
    }
    return isUuid;
  }

  private void addViolation(ConstraintValidatorContext validatorContext) {
    validatorContext.buildConstraintViolationWithTemplate("OTP must be a valid UUID v4 String.")
        .addConstraintViolation();
  }
}
