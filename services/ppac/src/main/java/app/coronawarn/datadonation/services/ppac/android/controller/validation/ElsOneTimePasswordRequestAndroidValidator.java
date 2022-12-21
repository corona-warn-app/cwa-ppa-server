package app.coronawarn.datadonation.services.ppac.android.controller.validation;

import app.coronawarn.datadonation.common.protocols.internal.ppdd.ELSOneTimePasswordRequestAndroid;
import app.coronawarn.datadonation.services.ppac.commons.validation.UuidConstraintValidator;
import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;
import org.springframework.stereotype.Component;

@Component
public class ElsOneTimePasswordRequestAndroidValidator extends UuidConstraintValidator
    implements ConstraintValidator<ValidAndroidOneTimePasswordRequest, ELSOneTimePasswordRequestAndroid> {

  @Override
  public boolean isValid(final ELSOneTimePasswordRequestAndroid requestBody, final ConstraintValidatorContext context) {
    return super.isValid(requestBody.getPayload().getOtp(), context);
  }
}
