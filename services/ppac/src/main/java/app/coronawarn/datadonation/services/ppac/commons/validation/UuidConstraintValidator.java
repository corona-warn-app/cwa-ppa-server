package app.coronawarn.datadonation.services.ppac.commons.validation;

import java.util.UUID;
import javax.validation.ConstraintValidatorContext;

public abstract class UuidConstraintValidator {

  private void addViolation(final ConstraintValidatorContext validatorContext) {
    validatorContext.buildConstraintViolationWithTemplate("OTP must be a valid UUID v4 String.")
        .addConstraintViolation();
  }

  private boolean checkIsValidUuid(final String uuid, final ConstraintValidatorContext constraintValidatorContext) {
    boolean isUuid = false;
    try {
      UUID.fromString(uuid);
      isUuid = true;
    } catch (final IllegalArgumentException e) {
      addViolation(constraintValidatorContext);
    }
    return isUuid;
  }

  protected boolean isValid(final String uuid, final ConstraintValidatorContext context) {
    context.disableDefaultConstraintViolation();
    return checkIsValidUuid(uuid, context);
  }
}
