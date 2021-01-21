package app.coronawarn.datadonation.services.ios.domain;

import static org.assertj.core.api.Assertions.assertThat;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.context.junit.jupiter.SpringExtension;

@ExtendWith({SpringExtension.class, MockitoExtension.class})
public class DeviceDataQueryRequestTest {

  @Test
  public void toJsonString() throws JsonProcessingException {
    String value = "{\"timestamp\":123456,\"device_token\":\"apiToken\""
        + ",\"transaction_id\":\"transactionId\"}";

    DeviceDataQueryRequest request = new DeviceDataQueryRequest("apiToken", "transactionId", 123456L);
    ObjectMapper mapper = new ObjectMapper();
    String requestJson = mapper.writeValueAsString(request);

    assertThat(requestJson).isEqualTo(value);
  }

  @Test
  public void fromJsonString() throws JsonProcessingException {
    String value = "{\"timestamp\":123456,\"device_token\":\"apiToken\""
        + ",\"transaction_id\":\"transactionId\"}";

    DeviceDataQueryRequest src = new DeviceDataQueryRequest("apiToken", "transactionId", 123456L);
    ObjectMapper mapper = new ObjectMapper();
    DeviceDataQueryRequest result = mapper.readValue(value, DeviceDataQueryRequest.class);

    assertThat(result).usingRecursiveComparison().isEqualTo(src);
  }

}
