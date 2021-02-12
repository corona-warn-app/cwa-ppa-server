package app.coronawarn.datadonation.services.ppac.ios.controller.validation;

import app.coronawarn.datadonation.common.protocols.internal.ppdd.EdusOtpRequestIos.EDUSOneTimePasswordRequestIOS;
import java.util.UUID;
import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;
import org.springframework.stereotype.Component;

@Component
public class EdusOneTimePasswordRequestIosValidator implements
    ConstraintValidator<ValidEdusOneTimePasswordRequestIos, EDUSOneTimePasswordRequestIOS> {

  @Override
  public boolean isValid(EDUSOneTimePasswordRequestIOS requestBody,
      ConstraintValidatorContext context) {
    context.disableDefaultConstraintViolation();
    return checkIsValidUuid(requestBody.getPayload().getOtp(), context);
  }

  private boolean checkIsValidUuid(String string,
      ConstraintValidatorContext constraintValidatorContext) {
    boolean isUuid = false;
    try {
      UUID.fromString(string);
      isUuid = true;
    } catch (Exception e) {
      addViolation(constraintValidatorContext, "OTP must be a valid UUID v4 String.");
    }
    return isUuid;
  }

  private void addViolation(ConstraintValidatorContext validatorContext, String message) {
    validatorContext.buildConstraintViolationWithTemplate(message).addConstraintViolation();
  }
}
