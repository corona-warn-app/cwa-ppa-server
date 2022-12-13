package app.coronawarn.datadonation.services.ppac.ios.controller.validation;

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;
import javax.validation.Constraint;
import javax.validation.Payload;

@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.PARAMETER)
@Constraint(validatedBy = {
    EdusOneTimePasswordRequestIosValidator.class,
    ElsOneTimePasswordRequestIosValidator.class,
    SrsOneTimePasswordRequestIosValidator.class })
@Documented
public @interface ValidOneTimePasswordRequestIos {

  /**
   * Validation message.
   */
  String message() default "Invalid payload for otp creation.";

  /**
   * Validation groups.
   */
  Class<?>[] groups() default {};

  /**
   * Payload type.
   */
  Class<? extends Payload>[] payload() default {};
}
