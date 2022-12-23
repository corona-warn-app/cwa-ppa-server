package app.coronawarn.datadonation.services.ppac.ios.controller.validation;

import jakarta.validation.Constraint;
import jakarta.validation.Payload;
import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.PARAMETER)
@Constraint(validatedBy = PpaDataRequestIosPayloadValidator.class)
@Documented
//PPADataRequestIOS
public @interface ValidPpaDataRequestIosPayload {

  /**
   * Validation message.
   */
  String message() default "Invalid submission payload for ppac.";

  /**
   * Validation groups.
   */
  Class<?>[] groups() default {};

  /**
   * Payload type.
   */
  Class<? extends Payload>[] payload() default {};
}
