package app.coronawarn.datadonation.services.ppac.ios.controller.validation;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import app.coronawarn.datadonation.common.protocols.internal.ppdd.PpaDataRequestIos.PPADataRequestIOS;
import app.coronawarn.datadonation.common.protocols.internal.ppdd.PpacIos.PPACIOS;
import app.coronawarn.datadonation.services.ppac.android.controller.RequestExecutor;
import app.coronawarn.datadonation.services.ppac.config.PpacConfiguration;
import java.nio.charset.Charset;
import java.util.Base64;
import java.util.UUID;
import javax.validation.ConstraintValidatorContext;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;

@SpringBootTest
public class PpaDataRequestIosPayloadValidatorTest {

  @Autowired
  private PpaDataRequestIosPayloadValidator underTest;

  @Autowired
  private PpacConfiguration configuration;

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
  public void testValidatePayload_successfulValidation() {
    String base64String = buildBase64String(configuration.getIos().getMinDeviceTokenLength() + 1);

    PPADataRequestIOS payload = buildPPADataRequestIosPayload(base64String, UUID.randomUUID().toString());

    assertThat(underTest.isValid(payload, context)).isTrue();
  }

  @Test
  public void testValidatePayload_invalidDeviceTokenWrongMinLength() {
    String deviceToken = buildBase64String(configuration.getIos().getMinDeviceTokenLength() - 1);
    PPADataRequestIOS payload = buildPPADataRequestIosPayload(deviceToken, UUID.randomUUID().toString());

    assertThat(underTest.isValid(payload, context)).isFalse();
  }

  @Test
  public void testValidatePayload_invalidDeviceTokenWrongMaxLength() {
    String deviceToken = buildBase64String(configuration.getIos().getMaxDeviceTokenLength() + 1);
    PPADataRequestIOS payload = buildPPADataRequestIosPayload(deviceToken, UUID.randomUUID().toString());

    assertThat(underTest.isValid(payload, context)).isFalse();
  }

  @Test
  public void testValidatePayload_invalidDeviceTokenNoBase64() {
    final PPADataRequestIOS payload = buildPPADataRequestIosPayload("notbase64", UUID.randomUUID().toString());

    assertThat(underTest.isValid(payload, context)).isFalse();
  }

  @Test
  public void testValidatePayload_invalidApiToken() {
    String base64String = buildBase64String(configuration.getIos().getMinDeviceTokenLength() + 1);
    PPADataRequestIOS payload = buildPPADataRequestIosPayload(base64String, "apiToken_invalid");

    assertThat(underTest.isValid(payload, context)).isFalse();
  }

  private String buildBase64String(int length) {
    String key = "thisIsAReallyLongDeviceToken";
    return Base64.getEncoder().encodeToString(key.getBytes(Charset.defaultCharset()))
        .substring(key.length() - length, key.length());
  }

  private PPADataRequestIOS buildPPADataRequestIosPayload(String base64String, String apiToken_invalid) {
    PPACIOS authIos = PPACIOS.newBuilder().setApiToken(apiToken_invalid).setDeviceToken(base64String).build();

    return PPADataRequestIOS.newBuilder().setAuthentication(authIos).build();
  }
}
