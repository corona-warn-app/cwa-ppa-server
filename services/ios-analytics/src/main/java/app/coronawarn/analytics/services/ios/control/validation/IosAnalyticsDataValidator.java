package app.coronawarn.analytics.services.ios.control.validation;

import app.coronawarn.analytics.common.persistence.domain.AnalyticsData;

import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;


class IosAnalyticsDataValidator implements ConstraintValidator<ValidAnalyticsSubmissionPayload, AnalyticsData> {
    @Override
    public boolean isValid(AnalyticsData value, ConstraintValidatorContext context) {
        // TODO FR
        return false;
    }
}
