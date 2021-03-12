package app.coronawarn.datadonation.services.ppac.ios.controller.validation;

import app.coronawarn.datadonation.common.protocols.internal.ppdd.EDUSOneTimePasswordRequestIOS;
import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;
import org.springframework.stereotype.Component;

@Component
public class EdusOneTimePasswordRequestIosValidator extends UuidConstraintValidator implements
    ConstraintValidator<ValidEdusOneTimePasswordRequestIos, EDUSOneTimePasswordRequestIOS> {

  @Override
  public boolean isValid(EDUSOneTimePasswordRequestIOS requestBody,
      ConstraintValidatorContext context) {
    return super.isValid(requestBody.getPayload().getOtp(), context);
  }

}
