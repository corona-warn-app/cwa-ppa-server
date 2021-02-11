package app.coronawarn.datadonation.common.persistence.service;

import static org.assertj.core.api.Assertions.assertThat;

import net.minidev.json.JSONValue;
import org.junit.jupiter.api.Test;

public class OtpStateTest {

  @Test
  void testStateReturnsValidJson() {
    OtpState valid = OtpState.VALID;
    OtpState expired = OtpState.EXPIRED;
    OtpState redeemed = OtpState.REDEEMED;

    assertThat(JSONValue.isValidJson(valid.state()));
    assertThat(JSONValue.isValidJson(expired.state()));
    assertThat(JSONValue.isValidJson(redeemed.state()));
  }

}
