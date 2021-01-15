package app.coronawarn.analytics.services.ios.control.validation;

import javax.validation.Constraint;
import javax.validation.Payload;
import java.lang.annotation.*;

@Constraint(validatedBy = IosAnalyticsDataValidator.class)
@Target({ElementType.PARAMETER})
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface ValidAnalyticsSubmissionPayload {

    /**
     * Error message.
     *
     * @return the error message
     */
    String message() default "Invalid analytics data submission payload.";

    /**
     * Groups.
     */
    Class<?>[] groups() default {};

    /**
     * Payload.
     */
    Class<? extends Payload>[] payload() default {};
}
