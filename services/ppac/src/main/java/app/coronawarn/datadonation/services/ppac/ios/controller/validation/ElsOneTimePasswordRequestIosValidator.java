package app.coronawarn.datadonation.services.ppac.ios.controller.validation;

import app.coronawarn.datadonation.common.protocols.internal.ppdd.ELSOneTimePasswordRequestIOS;
import app.coronawarn.datadonation.services.ppac.commons.validation.UuidConstraintValidator;
import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;
import org.springframework.stereotype.Component;

@Component
public class ElsOneTimePasswordRequestIosValidator extends UuidConstraintValidator
    implements ConstraintValidator<ValidOneTimePasswordRequestIos, ELSOneTimePasswordRequestIOS> {

  @Override
  public boolean isValid(final ELSOneTimePasswordRequestIOS requestBody, final ConstraintValidatorContext context) {
    return super.isValid(requestBody.getPayload().getOtp(), context);
  }
}
