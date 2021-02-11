package app.coronawarn.datadonation.services.ppac.android.controller.validation;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import app.coronawarn.datadonation.common.protocols.internal.ppdd.EdusOtp.EDUSOneTimePassword;
import app.coronawarn.datadonation.common.protocols.internal.ppdd.EdusOtpRequestAndroid.EDUSOneTimePasswordRequestAndroid;
import java.util.UUID;
import javax.validation.ConstraintValidatorContext;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;

@SpringBootTest
public class EdusOneTimePasswordRequestAndroidValidatorTest {

  @Autowired
  private EdusOneTimePasswordRequestAndroidValidator validator;

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
  public void testValidatePayloadSuccessFully() {
    EDUSOneTimePasswordRequestAndroid payload = buildRequestWithOtp(UUID.randomUUID().toString());
    assertThat(validator.isValid(payload, context)).isTrue();
  }

  @Test
  public void testValidatePayloadInvalidOtp() {
    EDUSOneTimePasswordRequestAndroid payload = buildRequestWithOtp("invalid-uuid");
    assertThat(validator.isValid(payload, context)).isFalse();
  }

  private EDUSOneTimePasswordRequestAndroid buildRequestWithOtp(String otp) {
    return EDUSOneTimePasswordRequestAndroid.newBuilder()
        .setPayload(EDUSOneTimePassword.newBuilder().setOtp(otp)).build();
  }
}
