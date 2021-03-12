package app.coronawarn.datadonation.services.ppac.android.controller.validation;

import app.coronawarn.datadonation.common.protocols.internal.ppdd.EDUSOneTimePasswordRequestAndroid;
import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;
import org.springframework.stereotype.Component;

@Component
public class EdusOneTimePasswordRequestAndroidValidator extends UuidConstraintValidator implements
    ConstraintValidator<ValidEdusOneTimePasswordRequestAndroid, EDUSOneTimePasswordRequestAndroid> {

  @Override
  public boolean isValid(EDUSOneTimePasswordRequestAndroid requestBody,
      ConstraintValidatorContext context) {
    return super.isValid(requestBody.getPayload().getOtp(), context);
  }
}
