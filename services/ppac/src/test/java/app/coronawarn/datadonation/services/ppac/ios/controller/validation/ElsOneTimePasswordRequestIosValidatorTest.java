package app.coronawarn.datadonation.services.ppac.ios.controller.validation;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import app.coronawarn.datadonation.common.protocols.internal.ppdd.ELSOneTimePassword;
import app.coronawarn.datadonation.common.protocols.internal.ppdd.ELSOneTimePasswordRequestIOS;
import java.util.UUID;
import java.util.stream.Stream;
import javax.validation.ConstraintValidatorContext;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
class ElsOneTimePasswordRequestIosValidatorTest {

  @Autowired
  private ElsOneTimePasswordRequestIosValidator validator;

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
    assertThat(validator.isValid(buildRequestWithOtp("6F85A5E6-B730-42F3-B0ED-38C3352ACCBE"), context)).isTrue();
    assertThat(validator.isValid(buildRequestWithOtp("08FC37E9-B3D5-407F-9CF6-979DB6892194"), context)).isTrue();
    assertThat(validator.isValid(buildRequestWithOtp("17B86082-E9F6-43B2-A962-09903B150CAA"), context)).isTrue();
    assertThat(validator.isValid(buildRequestWithOtp("FBC85932-65C3-4CB2-889F-74F340A71E1C"), context)).isTrue();
    assertThat(validator.isValid(buildRequestWithOtp("4D2D3597-932F-4AF4-BA31-37EC9D148AF7"), context)).isTrue();
    assertThat(validator.isValid(buildRequestWithOtp("2F89C68A-5058-49C8-BF49-47A3820CB2AE"), context)).isTrue();
    assertThat(validator.isValid(buildRequestWithOtp("95BB7E96-6D8C-4AE3-B114-0AED1E0FE952"), context)).isTrue();
  }

  @ParameterizedTest
  @MethodSource("provideUuidStrings")
  void shouldAcceptUUIDs(String uuid){
    assertThat(validator.isValid(buildRequestWithOtp(uuid), context)).isTrue();
  }

  private static Stream<Arguments> provideUuidStrings() {
    return Stream.generate(() -> Arguments.of(UUID.randomUUID().toString())).limit(10);
  }

  @Test
  void testValidatePayloadInvalidOtp() {
    ELSOneTimePasswordRequestIOS payload = buildRequestWithOtp("invalid-uuid");
    assertThat(validator.isValid(payload, context)).isFalse();
  }

  private ELSOneTimePasswordRequestIOS buildRequestWithOtp(String otp) {
    return ELSOneTimePasswordRequestIOS.newBuilder()
        .setPayload(ELSOneTimePassword.newBuilder().setOtp(otp)).build();
  }
}
