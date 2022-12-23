package app.coronawarn.datadonation.services.ppac.android.controller.validation;

import jakarta.validation.Constraint;
import jakarta.validation.Payload;
import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

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
