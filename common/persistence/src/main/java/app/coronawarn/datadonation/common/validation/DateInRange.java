package app.coronawarn.datadonation.common.validation;

import static java.lang.annotation.ElementType.ANNOTATION_TYPE;
import static java.lang.annotation.ElementType.CONSTRUCTOR;
import static java.lang.annotation.ElementType.FIELD;
import static java.lang.annotation.ElementType.METHOD;
import static java.lang.annotation.ElementType.PARAMETER;
import static java.lang.annotation.ElementType.TYPE_USE;
import static java.lang.annotation.RetentionPolicy.RUNTIME;

import java.lang.annotation.Documented;
import java.lang.annotation.Retention;
import java.lang.annotation.Target;
import java.time.LocalDate;
import javax.validation.Constraint;
import javax.validation.Payload;

@Target({ METHOD, FIELD, ANNOTATION_TYPE, CONSTRUCTOR, PARAMETER, TYPE_USE })
@Retention(RUNTIME)
@Documented
@Constraint(validatedBy = { DateInRangeValidator.class })
public @interface DateInRange {
  
  /**
   * Groups where potential violations should be kept together.
   */
  Class<?>[] groups() default { };

  /**
   * Payload.
   */
  Class<? extends Payload>[] payload() default { };

  /**
   * Date must be between {from} and {till}.
   */
  String message() default "Date must be between {from} and {till}";

  /**
   * Default if not set: {@link LocalDate#MIN}.
   */
  String from() default "";

  /**
   * Default if not set: {@link LocalDate#MAX}.
   */
  String till() default "";
}
