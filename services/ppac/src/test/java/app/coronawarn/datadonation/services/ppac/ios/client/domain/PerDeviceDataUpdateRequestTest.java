package app.coronawarn.datadonation.services.ppac.ios.client.domain;

import static org.assertj.core.api.Assertions.assertThat;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.context.junit.jupiter.SpringExtension;

@ExtendWith({SpringExtension.class, MockitoExtension.class})
public class PerDeviceDataUpdateRequestTest {

  @Test
  public void toJson() throws JsonProcessingException {
    String value = "{\"timestamp\":123456,\"bit0\":false,\"bit1\":true,\"device_token\":\"apiToken\""
        + ",\"transaction_id\":\"transactionId\"}";

    PerDeviceDataUpdateRequest request = new PerDeviceDataUpdateRequest("apiToken", "transactionId", 123456L, false,
        true);
    ObjectMapper mapper = new ObjectMapper();
    String requestJson = mapper.writeValueAsString(request);

    assertThat(requestJson).isEqualTo(value);
  }

  @Test
  public void fromJsonString() throws JsonProcessingException {
    String value = "{\"timestamp\":123456,\"bit0\":false,\"bit1\":true,\"device_token\":\"apiToken\""
        + ",\"transaction_id\":\"transactionId\"}";

    PerDeviceDataUpdateRequest expected = new PerDeviceDataUpdateRequest("apiToken", "transactionId", 123456L, false,
        true);
    ObjectMapper mapper = new ObjectMapper();
    PerDeviceDataUpdateRequest actual = mapper.readValue(value, PerDeviceDataUpdateRequest.class);

    assertThat(actual).usingRecursiveComparison().isEqualTo(expected);
  }

}
