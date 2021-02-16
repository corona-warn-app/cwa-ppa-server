package app.coronawarn.datadonation.services.ppac.android.attestation;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;
import org.junit.jupiter.api.Test;
import com.google.api.client.util.Base64;
import com.google.protobuf.InvalidProtocolBufferException;
import app.coronawarn.datadonation.common.protocols.internal.ppdd.EdusOtp.EDUSOneTimePassword;
import app.coronawarn.datadonation.services.ppac.android.attestation.errors.NonceCalculationError;

class NonceCalculatorTest {

  @Test
  void shouldThrowExceptionForMissingOrInvalidArguments() {
    NonceCalculationError exception = assertThrows(NonceCalculationError.class, () -> {
      NonceCalculator.of(null);
    });
    assertFalse(exception.getMessage().isEmpty());

    exception = assertThrows(NonceCalculationError.class, () -> {
      NonceCalculator calculator = NonceCalculator.of("payload".getBytes());
      calculator.calculate(null);
    });
    assertFalse(exception.getMessage().isEmpty());
  }

  @Test
  void shouldComputeCorrectNonce() {
    //test a precomputed salt string
    NonceCalculator calculator = NonceCalculator.of("payload-test-string".getBytes());
    String saltBase64 = calculator.calculate("test-salt-1234");
    assertEquals("M2EqczgxveKiptESiBNRmKqxYv5raTdzyeSZyzsCvjg=", saltBase64);
  }
  
  @Test
  void shouldComputeCorrectNonceForOTp() throws InvalidProtocolBufferException {
    //test a precomputed salt string
    byte[] payload = Base64.decodeBase64("CgtoZWxsby13b3JsZA==");
    EDUSOneTimePassword otpProto = EDUSOneTimePassword.parseFrom(payload);
    
    assertEquals("hello-world", otpProto.getOtp());
    
    NonceCalculator calculator = NonceCalculator.of(payload);
    String saltBase64 = calculator.calculate("Ri0AXC9U+b9hE58VqupI8Q==");
    assertEquals("ANjVoDcS8v8iQdlNrcxehSggE9WZwIp7VNpjoU7cPsg=", saltBase64);
  }
}