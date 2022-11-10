package app.coronawarn.datadonation.services.ppac.android.controller.validation;

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;
import javax.validation.Constraint;
import javax.validation.Payload;

@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.PARAMETER)
@Constraint(validatedBy = { EdusOneTimePasswordRequestAndroidValidator.class,
    ElsOneTimePasswordRequestAndroidValidator.class, SrsOneTimePasswordRequestAndroidValidator.class })
@Documented
public @interface ValidAndroidOneTimePasswordRequest {

  /**
   * Validation message.
   */
  String message() default "Invalid payload for OTP creation.";

  /**
   * Validation groups.
   */
  Class<?>[] groups() default {};

  /**
   * Payload type.
   */
  Class<? extends Payload>[] payload() default {};
}
