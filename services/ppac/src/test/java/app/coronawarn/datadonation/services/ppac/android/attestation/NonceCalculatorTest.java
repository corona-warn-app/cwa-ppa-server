package app.coronawarn.datadonation.services.ppac.android.attestation;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;
import org.junit.jupiter.api.Test;
import app.coronawarn.datadonation.services.ppac.android.attestation.errors.NonceCalculationError;

class NonceCalculatorTest {

  @Test
  void shouldThrowExceptionForMissingOrInvalidArguments() {
    NonceCalculationError exception = assertThrows(NonceCalculationError.class, () -> {
      NonceCalculator.of(null);
    });
    assertFalse(exception.getMessage().isEmpty());

    exception = assertThrows(NonceCalculationError.class, () -> {
      NonceCalculator calculator = NonceCalculator.of("payload");
      calculator.calculate(null);
    });
    assertFalse(exception.getMessage().isEmpty());
  }

  @Test
  void shouldComputeCorrectNonce() {
    //test a precomputed salt string
    NonceCalculator calculator = NonceCalculator.of("payload-test-string");
    String saltBase64 = calculator.calculate("test-salt-1234");
    assertEquals("yBYBNsOU06b4TTLj36bs9qGw8kMKN117tUgb9LDnbRE=", saltBase64);
  }

}
