package app.coronawarn.datadonation.services.ios.domain;

import static org.assertj.core.api.Assertions.assertThat;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.context.junit.jupiter.SpringExtension;

@ExtendWith({SpringExtension.class, MockitoExtension.class})
public class DeviceValidationRequestTest {

  @Test
  public void toJson() throws JsonProcessingException {
    String value = "{\"timestamp\":123456,\"device_token\":\"apiToken\""
        + ",\"transaction_id\":\"transactionId\"}";

    DeviceValidationRequest request = new DeviceValidationRequest("apiToken", "transactionId",
        123456L);
    ObjectMapper mapper = new ObjectMapper();
    String requestJson = mapper.writeValueAsString(request);

    assertThat(requestJson).isEqualTo(value);
  }

  @Test
  public void fromJsonString() throws JsonProcessingException {
    String value = "{\"timestamp\":123456,\"device_token\":\"apiToken\""
        + ",\"transaction_id\":\"transactionId\"}";

    DeviceValidationRequest expected = new DeviceValidationRequest("apiToken", "transactionId",
        123456L);
    ObjectMapper mapper = new ObjectMapper();
    DeviceValidationRequest actual = mapper.readValue(value, DeviceValidationRequest.class);

    assertThat(actual).usingRecursiveComparison().isEqualTo(expected);
  }
}
