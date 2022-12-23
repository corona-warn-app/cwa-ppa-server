package app.coronawarn.datadonation.services.ppac.android.config;

import static org.junit.jupiter.api.Assertions.assertNotNull;

import app.coronawarn.datadonation.services.ppac.config.PpacConfiguration;
import jakarta.validation.ConstraintViolation;
import jakarta.validation.Validation;
import jakarta.validation.Validator;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

//TODO: Convert to full PPAC config test
class AndroidConfigValidationTest {

  private Validator validator;

  @BeforeEach
  void setup() {
    validator = Validation.buildDefaultValidatorFactory().getValidator();
  }

  @Test
  void testNonEmptyStringViolations() {
    PpacConfiguration.Android config = new PpacConfiguration.Android();
    ConstraintViolation<PpacConfiguration.Android> error =
        validator.validate(config).stream().findAny().orElse(null);
    assertNotNull(error);
  }
}
