package app.coronawarn.datadonation.services.ppac.android.controller.validation;

import app.coronawarn.datadonation.common.protocols.internal.ppdd.SRSOneTimePasswordRequestAndroid;
import app.coronawarn.datadonation.services.ppac.commons.validation.UuidConstraintValidator;
import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;
import org.springframework.stereotype.Component;

@Component
public class SrsOneTimePasswordRequestAndroidValidator extends UuidConstraintValidator
    implements ConstraintValidator<ValidAndroidOneTimePasswordRequest, SRSOneTimePasswordRequestAndroid> {

  @Override
  public boolean isValid(final SRSOneTimePasswordRequestAndroid requestBody, final ConstraintValidatorContext context) {
    return super.isValid(requestBody.getPayload().getOtp(), context);
  }
}
