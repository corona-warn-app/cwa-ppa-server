package app.coronawarn.datadonation.services.ppac.ios.controller.validation;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import app.coronawarn.datadonation.common.protocols.AuthIos;
import app.coronawarn.datadonation.common.protocols.SubmissionPayloadIos;
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
public class IosSubmissionPayloadValidatorTest {

  @Autowired
  private IosSubmissionPayloadValidator underTest;

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
  public void validatePayload() {
    String base64String = buildBase64String(configuration.getIos().getMinDeviceTokenLength() + 1);

    SubmissionPayloadIos payload = buildSubmissionPayload(base64String, UUID.randomUUID().toString());

    assertThat(underTest.isValid(payload, context)).isTrue();
  }

  @Test
  public void validatePayload_invalidDeviceTokenWrongMinLength() {
    String deviceToken = buildBase64String(configuration.getIos().getMinDeviceTokenLength() - 1);
    SubmissionPayloadIos payload = buildSubmissionPayload(deviceToken, UUID.randomUUID().toString());

    assertThat(underTest.isValid(payload, context)).isFalse();
  }

  @Test
  public void validatePayload_invalidDeviceTokenWrongMaxLength() {
    String deviceToken = buildBase64String(configuration.getIos().getMaxDeviceTokenLength() + 1);
    SubmissionPayloadIos payload = buildSubmissionPayload(deviceToken, UUID.randomUUID().toString());

    assertThat(underTest.isValid(payload, context)).isFalse();
  }

  @Test
  public void validatePayload_invalidDeviceTokenNoBase64() {
    SubmissionPayloadIos payload = buildSubmissionPayload("notbase64", UUID.randomUUID().toString());

    assertThat(underTest.isValid(payload, context)).isFalse();
  }

  @Test
  public void validatePayload_invalidApiToken() {
    String base64String = buildBase64String(configuration.getIos().getMinDeviceTokenLength() + 1);
    SubmissionPayloadIos payload = buildSubmissionPayload(base64String, "apiToken_invalid");

    assertThat(underTest.isValid(payload, context)).isFalse();
  }

  private String buildBase64String(int length) {
    String key = "thisIsAReallyLongDeviceToken";
    return Base64.getEncoder().encodeToString(key.getBytes(Charset.defaultCharset()))
        .substring(key.length() - length, key.length());
  }

  private SubmissionPayloadIos buildSubmissionPayload(String base64String, String apiToken_invalid) {
    AuthIos authIos = AuthIos.newBuilder().setApiToken(apiToken_invalid).setDeviceToken(base64String).build();

    return SubmissionPayloadIos.newBuilder().setAuthentication(authIos).build();
  }
}
