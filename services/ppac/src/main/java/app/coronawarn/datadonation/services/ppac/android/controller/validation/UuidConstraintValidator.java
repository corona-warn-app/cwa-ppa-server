package app.coronawarn.datadonation.services.ppac.android.controller.validation;

import java.util.UUID;
import javax.validation.ConstraintValidatorContext;

abstract class UuidConstraintValidator {

  protected boolean isValid(String uuid,
      ConstraintValidatorContext context) {
    context.disableDefaultConstraintViolation();
    return checkIsValidUuid(uuid, context);
  }

  private boolean checkIsValidUuid(String uuid,
      ConstraintValidatorContext constraintValidatorContext) {
    boolean isUuid = false;
    try {
      UUID.fromString(uuid);
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
