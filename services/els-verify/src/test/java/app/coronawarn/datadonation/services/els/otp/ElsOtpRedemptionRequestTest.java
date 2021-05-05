package app.coronawarn.datadonation.services.els.otp;

import javax.validation.Validation;
import javax.validation.Validator;
import org.junit.jupiter.api.Test;
import org.springframework.validation.BeanPropertyBindingResult;
import org.springframework.validation.Errors;
import org.springframework.validation.beanvalidation.SpringValidatorAdapter;

public class ElsOtpRedemptionRequestTest {

  @Test
  void shouldAcceptValidOTPs(){
    validate("E423A67E-711E-46DB-852D-E8C78CF2A954");
    validate("028DE05A-217B-4135-8088-8D6D6EFB4B5A");
    validate("040B59D2-A3EF-484D-8625-2304FB17F157");
    validate("9EB188EE-E555-4A9F-A92D-02A7369DD90F");
    validate("37015CF6-C1BB-4E08-BFE3-5426DEB741EC");
    validate("C3826BC6-5037-4172-A62E-19DF95FE713F");
    validate("AEFE3B85-250A-4664-AE08-D78B67177E72");
    validate("85AC3C15-92D6-42E8-9349-7D40FD488555");
    validate("196805D4-5659-46ED-AA02-01E31331537A");
    validate("9DF452E5-36D4-48F3-8BDC-8F09FE007461");
  }

  private final Validator javaxValidator = Validation.buildDefaultValidatorFactory().getValidator();
  private final SpringValidatorAdapter validator = new SpringValidatorAdapter(javaxValidator);

  private void validate(String otp) {
    ElsOtpRedemptionRequest toValidate = new ElsOtpRedemptionRequest();
    toValidate.setOtp(otp);
    Errors errors = new BeanPropertyBindingResult(toValidate, toValidate.getClass().getName());
    validator.validate(toValidate, errors);
    if (errors != null && !errors.getAllErrors().isEmpty())
      throw new RuntimeException(errors.getAllErrors().toString() + errors);
  }
}
