package app.coronawarn.datadonation.common.validation;

import static org.springframework.util.ObjectUtils.isEmpty;

import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;
import java.time.LocalDate;

public class DateInRangeValidator implements ConstraintValidator<DateInRange, LocalDate> {

  protected LocalDate from = LocalDate.MIN;

  protected LocalDate till = LocalDate.MAX;

  @Override
  public void initialize(final DateInRange annotation) {
    if (!isEmpty(annotation.from())) {
      from = LocalDate.parse(annotation.from());
    }
    if (!isEmpty(annotation.till())) {
      till = LocalDate.parse(annotation.till());
    }

    assert from == null || till == null || from.isBefore(till) : "{from: " + from + "} is not before {till: " + till
        + "}";
  }

  @Override
  public boolean isValid(final LocalDate date, final ConstraintValidatorContext context) {
    // null values are valid
    if (date == null) {
      return true;
    }

    return (from.isBefore(date) || from.isEqual(date)) && (till.isEqual(date) || till.isAfter(date));
  }
}
