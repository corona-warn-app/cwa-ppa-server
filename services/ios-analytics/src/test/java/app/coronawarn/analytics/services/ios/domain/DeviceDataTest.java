package app.coronawarn.analytics.services.ios.domain;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import static org.assertj.core.api.Assertions.assertThat;

@ExtendWith({SpringExtension.class, MockitoExtension.class})
public class DeviceDataTest {


  @Test
  public void toJson() throws JsonProcessingException {
    String value = "{\"bit0\":false,\"bit1\":true,\"last_update_time\":\"2000-12\"}";

    DeviceData data = new DeviceData(false,
        true, "2000-12");
    ObjectMapper mapper = new ObjectMapper();
    String requestJson = mapper.writeValueAsString(data);

    assertThat(requestJson).isEqualTo(value);
  }

  @Test
  public void fromJsonString() throws JsonProcessingException {
    String value = "{\"bit0\":false,\"bit1\":true,\"last_update_time\":\"2000-12\"}";

    DeviceData expected = new DeviceData(false,
        true, "2000-12");
    ObjectMapper mapper = new ObjectMapper();
    DeviceData actual = mapper.readValue(value, DeviceData.class);

    assertThat(actual).usingRecursiveComparison().isEqualTo(expected);
  }


}
