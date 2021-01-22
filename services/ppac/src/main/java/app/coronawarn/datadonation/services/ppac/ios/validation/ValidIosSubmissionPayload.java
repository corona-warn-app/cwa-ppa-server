package app.coronawarn.datadonation.services.ppac.ios.validation;

import javax.validation.Constraint;
import javax.validation.Payload;
import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.PARAMETER)
@Constraint(validatedBy = IosSubmissionPayloadValidator.class)
@Documented
public @interface ValidIosSubmissionPayload {

  String message() default "Invalid submission payload for ppac.";
  Class<?>[] groups() default {};
  Class<? extends Payload>[] payload() default {};

}
