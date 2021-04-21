package app.coronawarn.datadonation.services.ppac.android.controller.validation;

import app.coronawarn.datadonation.common.protocols.internal.ppdd.EDUSOneTimePasswordRequestAndroid;
import app.coronawarn.datadonation.services.ppac.commons.validation.UuidConstraintValidator;
import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;
import org.springframework.stereotype.Component;

@Component
public class EdusOneTimePasswordRequestAndroidValidator extends UuidConstraintValidator
    implements ConstraintValidator<ValidEdusOneTimePasswordRequestAndroid, EDUSOneTimePasswordRequestAndroid> {

  @Override
  public boolean isValid(final EDUSOneTimePasswordRequestAndroid requestBody,
      final ConstraintValidatorContext context) {
    return super.isValid(requestBody.getPayload().getOtp(), context);
  }
}
