package app.coronawarn.datadonation.services.ppac.ios.validation;

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;
import javax.validation.Constraint;
import javax.validation.Payload;

@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.PARAMETER)
@Constraint(validatedBy = IosSubmissionPayloadValidator.class)
@Documented
public @interface ValidIosSubmissionPayload {

  /**
   * Validation failure message.
   * 
   * @return
   */
  String message() default "Invalid submission payload for ppac.";

  /**
   * Groups.
   * 
   * @return
   */
  Class<?>[] groups() default {};

  /**
   * Payload.
   * 
   * @return
   */
  Class<? extends Payload>[] payload() default {};

}
