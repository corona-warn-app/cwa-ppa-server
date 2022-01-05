package app.coronawarn.datadonation.common.persistence.service;

import static org.assertj.core.api.Assertions.assertThat;

import net.minidev.json.JSONValue;
import org.junit.jupiter.api.Test;

class OtpStateTest {

  @Test
  void testStateReturnsValidJson() {
    OtpState valid = OtpState.VALID;
    OtpState expired = OtpState.EXPIRED;
    OtpState redeemed = OtpState.REDEEMED;

    assertThat(JSONValue.isValidJson(valid.state())).isTrue();
    assertThat(JSONValue.isValidJson(expired.state())).isTrue();
    assertThat(JSONValue.isValidJson(redeemed.state())).isTrue();
  }
}
