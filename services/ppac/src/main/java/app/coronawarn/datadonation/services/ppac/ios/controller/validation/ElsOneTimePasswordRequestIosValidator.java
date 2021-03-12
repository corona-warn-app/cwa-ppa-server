package app.coronawarn.datadonation.services.ppac.ios.controller.validation;

import app.coronawarn.datadonation.common.protocols.internal.ppdd.ELSOneTimePasswordRequestIOS;
import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;
import org.springframework.stereotype.Component;

@Component
public class ElsOneTimePasswordRequestIosValidator extends UuidConstraintValidator implements
    ConstraintValidator<ValidEdusOneTimePasswordRequestIos, ELSOneTimePasswordRequestIOS> {

  @Override
  public boolean isValid(ELSOneTimePasswordRequestIOS requestBody,
      ConstraintValidatorContext context) {
    return super.isValid(requestBody.getPayload().getOtp(), context);
  }

}
