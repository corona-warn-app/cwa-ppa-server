package app.coronawarn.datadonation.services.ppac.android.attestation;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;

import app.coronawarn.datadonation.common.protocols.internal.ppdd.EDUSOneTimePassword;
import app.coronawarn.datadonation.common.protocols.internal.ppdd.ExposureRiskMetadata;
import app.coronawarn.datadonation.common.protocols.internal.ppdd.PPADataAndroid;
import app.coronawarn.datadonation.services.ppac.android.attestation.errors.NonceCouldNotBeVerified;
import com.google.protobuf.InvalidProtocolBufferException;
import java.util.Base64;
import java.util.List;
import org.junit.jupiter.api.Test;

class NonceCalculatorTest {

  @Test
  void shouldThrowExceptionForMissingOrInvalidArguments() {
    NonceCouldNotBeVerified exception = assertThrows(NonceCouldNotBeVerified.class, () -> {
      NonceCalculator.of(null);
    });
    assertFalse(exception.getMessage().isEmpty());

    exception = assertThrows(NonceCouldNotBeVerified.class, () -> {
      NonceCalculator calculator = NonceCalculator.of("payload".getBytes());
      calculator.calculate(null);
    });
    assertFalse(exception.getMessage().isEmpty());
  }

  @Test
  void shouldComputeCorrectNonceForOTp() throws InvalidProtocolBufferException {
    // test a precomputed salt string
    byte[] payload = Base64.getDecoder().decode("CgtoZWxsby13b3JsZA==");
    EDUSOneTimePassword otpProto = EDUSOneTimePassword.parseFrom(payload);

    assertEquals("hello-world", otpProto.getOtp());

    NonceCalculator calculator = NonceCalculator.of(payload);
    String saltBase64 = calculator.calculate("Ri0AXC9U+b9hE58VqupI8Q==");
    assertEquals("ANjVoDcS8v8iQdlNrcxehSggE9WZwIp7VNpjoU7cPsg=", saltBase64);
  }

  @Test
  void shouldComputeCorrectNonceForPpa() throws InvalidProtocolBufferException {
    // test a precomputed salt string
    byte[] payload = Base64.getDecoder().decode("Eg0IAxABGMGFyOT6LiABOgkIBBDdj6AFGAI=");
    PPADataAndroid dataProto = PPADataAndroid.parseFrom(payload);

    List<ExposureRiskMetadata> exposureRiskMetadata = dataProto.getExposureRiskMetadataSetList();
    assertEquals(exposureRiskMetadata.get(0).getRiskLevelValue(), 3);

    NonceCalculator calculator = NonceCalculator.of(payload);
    String saltBase64 = calculator.calculate("Ri0AXC9U+b9hE58VqupI8Q==");
    assertEquals("bd6kMfLKby3pzEqW8go1ZgmHN/bU1p/4KG6+1GeB288=", saltBase64);
  }
}
