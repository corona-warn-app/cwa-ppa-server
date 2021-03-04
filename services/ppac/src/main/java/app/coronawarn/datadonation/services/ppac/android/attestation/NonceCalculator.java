package app.coronawarn.datadonation.services.ppac.android.attestation;

import app.coronawarn.datadonation.services.ppac.android.attestation.errors.NonceCalculationError;
import app.coronawarn.datadonation.services.ppac.android.attestation.errors.NonceCouldNotBeVerified;
import java.io.IOException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Base64;
import org.springframework.util.ObjectUtils;

public class NonceCalculator {

  private final byte[] objectByteArray;

  private NonceCalculator(byte[] payload) {
    this.objectByteArray = payload;
  }

  /**
   * Calculate the Nonce by calculating the SHA-256 hash of the byte array representations of the
   * given salt concatenated with the byte array representation of the scenario-specific payload.
   */
  public String calculate(String saltBase64) {
    try {
      return calculate(saltBase64, objectByteArray);
    } catch (IOException | NoSuchAlgorithmException ex) {
      throw new NonceCalculationError(ex);
    }
  }

  private String calculate(String saltBase64, byte[] payload)
      throws IOException, NoSuchAlgorithmException {
    if (ObjectUtils.isEmpty(saltBase64)) {
      throw new NonceCouldNotBeVerified("Salt is null or empty");
    }
    byte[] saltBytes = Base64.getDecoder().decode(saltBase64);
    byte[] input = new byte[saltBytes.length + payload.length];
    System.arraycopy(saltBytes, 0, input, 0, saltBytes.length);
    System.arraycopy(payload, 0, input, saltBytes.length, payload.length);

    byte[] nonceBytes = MessageDigest.getInstance("SHA-256").digest(input);
    return Base64.getEncoder().encodeToString(nonceBytes);
  }

  /**
   * Constructs a calculator instance.
   * @throws NonceCalculationError - in case the given object is null
   */
  public static NonceCalculator of(byte[] payload) {
    if (ObjectUtils.isEmpty(payload)) {
      throw new NonceCouldNotBeVerified("Payload byte array is null or empty");
    }
    return new NonceCalculator(payload);
  }
}