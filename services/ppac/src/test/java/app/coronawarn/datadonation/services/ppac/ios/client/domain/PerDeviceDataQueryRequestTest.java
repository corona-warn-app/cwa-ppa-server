package app.coronawarn.datadonation.services.ppac.ios.client.domain;

import static org.assertj.core.api.Assertions.assertThat;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.context.junit.jupiter.SpringExtension;

@ExtendWith({SpringExtension.class, MockitoExtension.class})
public class PerDeviceDataQueryRequestTest {

  @Test
  public void testWriteRequestAsJsonString() throws JsonProcessingException {
    String value = "{\"timestamp\":123456,\"device_token\":\"apiToken\""
        + ",\"transaction_id\":\"transactionId\"}";

    PerDeviceDataQueryRequest request = new PerDeviceDataQueryRequest("apiToken", "transactionId", 123456L);
    ObjectMapper mapper = new ObjectMapper();
    String requestJson = mapper.writeValueAsString(request);

    assertThat(requestJson).isEqualTo(value);
  }

  @Test
  public void testReadRequestFromJsonString() throws JsonProcessingException {
    String value = "{\"timestamp\":123456,\"device_token\":\"apiToken\""
        + ",\"transaction_id\":\"transactionId\"}";

    PerDeviceDataQueryRequest src = new PerDeviceDataQueryRequest("apiToken", "transactionId", 123456L);
    ObjectMapper mapper = new ObjectMapper();
    PerDeviceDataQueryRequest result = mapper.readValue(value, PerDeviceDataQueryRequest.class);

    assertThat(result).usingRecursiveComparison().isEqualTo(src);
  }

}
