package app.coronawarn.datadonation.services.ppac.android.controller.validation;

import app.coronawarn.datadonation.common.protocols.internal.ppdd.ELSOneTimePasswordRequestAndroid;
import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;
import org.springframework.stereotype.Component;

@Component
public class ElsOneTimePasswordRequestAndroidValidator extends UuidConstraintValidator implements
    ConstraintValidator<ValidEdusOneTimePasswordRequestAndroid, ELSOneTimePasswordRequestAndroid> {

  @Override
  public boolean isValid(ELSOneTimePasswordRequestAndroid requestBody,
      ConstraintValidatorContext context) {
    return super.isValid(requestBody.getPayload().getOtp(), context);
  }
}
