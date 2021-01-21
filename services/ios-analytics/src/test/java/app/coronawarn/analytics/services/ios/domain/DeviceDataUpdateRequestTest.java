package app.coronawarn.analytics.services.ios.domain;

import static org.assertj.core.api.Assertions.assertThat;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.context.junit.jupiter.SpringExtension;

@ExtendWith({SpringExtension.class, MockitoExtension.class})
public class DeviceDataUpdateRequestTest {


  @Test
  public void toJson() throws JsonProcessingException {
    String value = "{\"timestamp\":123456,\"bit0\":false,\"bit1\":true,\"device_token\":\"apiToken\""
        + ",\"transaction_id\":\"transactionId\"}";

    DeviceDataUpdateRequest request = new DeviceDataUpdateRequest("apiToken", "transactionId", 123456L, false,
        true);
    ObjectMapper mapper = new ObjectMapper();
    String requestJson = mapper.writeValueAsString(request);

    assertThat(requestJson).isEqualTo(value);
  }

  @Test
  public void fromJsonString() throws JsonProcessingException {
    String value =  "{\"timestamp\":123456,\"bit0\":false,\"bit1\":true,\"device_token\":\"apiToken\""
        + ",\"transaction_id\":\"transactionId\"}";

    DeviceDataUpdateRequest expected = new DeviceDataUpdateRequest("apiToken", "transactionId", 123456L, false,
        true);
    ObjectMapper mapper = new ObjectMapper();
    DeviceDataUpdateRequest actual = mapper.readValue(value, DeviceDataUpdateRequest.class);

    assertThat(actual).usingRecursiveComparison().isEqualTo(expected);
  }

}
