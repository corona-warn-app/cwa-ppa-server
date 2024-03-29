package app.coronawarn.datadonation.common.validation;

import static java.time.LocalDate.EPOCH;
import static java.time.LocalDate.MAX;
import static java.time.LocalDate.MIN;
import static java.time.LocalDate.now;
import static org.junit.jupiter.api.Assertions.assertEquals;

import java.util.Set;
import javax.validation.ConstraintViolation;
import javax.validation.Validation;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.autoconfigure.data.jdbc.DataJdbcTest;

@DataJdbcTest
class DateInRangeValidatorTest {

  static Set<ConstraintViolation<ObjectWithDateMember>> validate(ObjectWithDateMember o) {
    return Validation.buildDefaultValidatorFactory().getValidator().validate(o);
  }

  @Test
  void testNull() {
    final ObjectWithDateMember o = new ObjectWithDateMember();
    Set<ConstraintViolation<ObjectWithDateMember>> violations = validate(o);
    assertEquals(0, violations.size(), "Violations are not empty!");

    o.setDateToBeValidatedNull(now());
    violations = validate(o);
    assertEquals(0, violations.size(), "Violations are not empty!");

    o.setDateToBeValidatedNull(MIN);
    violations = validate(o);
    assertEquals(0, violations.size(), "Violations are not empty!");

    o.setDateToBeValidatedNull(MAX);
    violations = validate(o);
    assertEquals(0, violations.size(), "Violations are not empty!");

    o.setDateToBeValidatedNull(EPOCH);
    violations = validate(o);
    assertEquals(0, violations.size(), "Violations are not empty!");
  }

  @Test
  void testDateInRange() {
    final ObjectWithDateMember o = new ObjectWithDateMember();
    o.setDateToBeValidated(now());
    Set<ConstraintViolation<ObjectWithDateMember>> violations = validate(o);
    assertEquals(1, violations.size(), "Current date should violate the given range!");
    assertEquals("Date must be between 1970-01-01 and 2000-01-01", violations.iterator().next().getMessage());
  }

  @Test
  void testDateInRangeFrom() {
    final ObjectWithDateMember o = new ObjectWithDateMember();
    o.setDateToBeValidatedFrom(MIN);
    Set<ConstraintViolation<ObjectWithDateMember>> violations = validate(o);
    assertEquals(1, violations.size(), MIN + " should violate the given range!");
    assertEquals("Date must be after 1970-01-01", violations.iterator().next().getMessage());
  }

  @Test
  void testDateInRangeTill() {
    final ObjectWithDateMember o = new ObjectWithDateMember();
    o.setDateToBeValidatedTill(MAX);
    Set<ConstraintViolation<ObjectWithDateMember>> violations = validate(o);
    assertEquals(1, violations.size(), MAX + " should violate the given range!");
    assertEquals("Date must be before 2000-01-01", violations.iterator().next().getMessage());
  }
}
