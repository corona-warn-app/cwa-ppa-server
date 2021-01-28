package app.coronawarn.datadonation.services.ppac.ios.client.domain;

import static org.assertj.core.api.Assertions.assertThat;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.context.junit.jupiter.SpringExtension;

@ExtendWith({SpringExtension.class, MockitoExtension.class})
public class PerDeviceValidationRequestTest {

  @Test
  public void testWriteObjectAsJsonString() throws JsonProcessingException {
    String value = "{\"timestamp\":123456,\"device_token\":\"apiToken\""
        + ",\"transaction_id\":\"transactionId\"}";

    PerDeviceValidationRequest request = new PerDeviceValidationRequest("apiToken", "transactionId",
        123456L);
    ObjectMapper mapper = new ObjectMapper();
    String requestJson = mapper.writeValueAsString(request);

    assertThat(requestJson).isEqualTo(value);
  }

  @Test
  public void testReadObjectFromJsonString() throws JsonProcessingException {
    String value = "{\"timestamp\":123456,\"device_token\":\"apiToken\""
        + ",\"transaction_id\":\"transactionId\"}";

    PerDeviceValidationRequest expected = new PerDeviceValidationRequest("apiToken", "transactionId",
        123456L);
    ObjectMapper mapper = new ObjectMapper();
    PerDeviceValidationRequest actual = mapper.readValue(value, PerDeviceValidationRequest.class);

    assertThat(actual).usingRecursiveComparison().isEqualTo(expected);
  }
}
