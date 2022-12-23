package app.coronawarn.datadonation.services.ppac.ios.controller.validation;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import app.coronawarn.datadonation.common.protocols.internal.ppdd.EDUSOneTimePassword;
import app.coronawarn.datadonation.common.protocols.internal.ppdd.EDUSOneTimePasswordRequestIOS;
import jakarta.validation.ConstraintValidatorContext;
import java.util.UUID;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
class EdusOneTimePasswordRequestIosValidatorTest {

  @Autowired
  private EdusOneTimePasswordRequestIosValidator validator;

  @MockBean
  private ConstraintValidatorContext context;

  @MockBean
  private ConstraintValidatorContext.ConstraintViolationBuilder violationBuilder;

  @BeforeEach
  public void setup() {
    context = mock(ConstraintValidatorContext.class);
    violationBuilder = mock(
        ConstraintValidatorContext.ConstraintViolationBuilder.class);
    when(context.buildConstraintViolationWithTemplate(any())).thenReturn(violationBuilder);
  }

  @Test
  void testValidatePayloadSuccessFully() {
    EDUSOneTimePasswordRequestIOS payload = buildRequestWithOtp(UUID.randomUUID().toString());
    assertThat(validator.isValid(payload, context)).isTrue();
  }

  @Test
  void testValidatePayloadInvalidOtp() {
    EDUSOneTimePasswordRequestIOS payload = buildRequestWithOtp("invalid-uuid");
    assertThat(validator.isValid(payload, context)).isFalse();
  }

  private EDUSOneTimePasswordRequestIOS buildRequestWithOtp(String otp) {
    return EDUSOneTimePasswordRequestIOS.newBuilder()
        .setPayload(EDUSOneTimePassword.newBuilder().setOtp(otp)).build();
  }
}
