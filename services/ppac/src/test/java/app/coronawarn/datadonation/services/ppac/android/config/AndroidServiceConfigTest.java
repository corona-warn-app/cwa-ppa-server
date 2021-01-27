package app.coronawarn.datadonation.services.ppac.android.config;

import static org.junit.Assert.assertNotNull;
import javax.validation.ConstraintViolation;
import javax.validation.Validation;
import javax.validation.Validator;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import app.coronawarn.datadonation.services.ppac.config.PpacConfiguration;

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
