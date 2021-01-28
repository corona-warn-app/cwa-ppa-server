package app.coronawarn.datadonation.services.ppac.ios.client.domain;

import static org.assertj.core.api.Assertions.assertThat;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.context.junit.jupiter.SpringExtension;

@ExtendWith({SpringExtension.class, MockitoExtension.class})
public class PerDeviceDataResponseTest {

  @Test
  public void testWriteObjectAsJsonString() throws JsonProcessingException {
    String value = "{\"bit0\":false,\"bit1\":true,\"last_update_time\":\"2000-12\"}";

    PerDeviceDataResponse data = new PerDeviceDataResponse(false,
        true, "2000-12");
    ObjectMapper mapper = new ObjectMapper();
    String requestJson = mapper.writeValueAsString(data);

    assertThat(requestJson).isEqualTo(value);
  }

  @Test
  public void testReadObjectFromJsonString() throws JsonProcessingException {
    String value = "{\"bit0\":false,\"bit1\":true,\"last_update_time\":\"2000-12\"}";

    PerDeviceDataResponse expected = new PerDeviceDataResponse(false,
        true, "2000-12");
    ObjectMapper mapper = new ObjectMapper();
    PerDeviceDataResponse actual = mapper.readValue(value, PerDeviceDataResponse.class);

    assertThat(actual).usingRecursiveComparison().isEqualTo(expected);
  }


}
