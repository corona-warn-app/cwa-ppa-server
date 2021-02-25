package app.coronawarn.datadonation.services.ppac.android.attestation;

import app.coronawarn.datadonation.services.ppac.android.attestation.errors.NonceCalculationError;
import com.google.api.client.util.Base64;
import java.io.IOException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
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
    } catch (Exception ex) {
      throw new NonceCalculationError(ex);
    }
  }

  private String calculate(String saltBase64, byte[] payload)
      throws IOException, NoSuchAlgorithmException {
    if (ObjectUtils.isEmpty(saltBase64)) {
      throw new NonceCalculationError("Missing salt given to nonce calculation function");
    }
    byte[] saltBytes = Base64.decodeBase64(saltBase64.getBytes());
    byte[] input = new byte[saltBytes.length + payload.length];
    System.arraycopy(saltBytes, 0, input, 0, saltBytes.length);
    System.arraycopy(payload, 0, input, saltBytes.length, payload.length);

    byte[] nonceBytes = MessageDigest.getInstance("SHA-256").digest(input);
    return Base64.encodeBase64String(nonceBytes);
  }

  /**
   * Constructs a calculator instance.
   * @throws NonceCalculationError - in case the given object is null
   */
  public static NonceCalculator of(byte[] payload) {
    if (ObjectUtils.isEmpty(payload)) {
      throw new NonceCalculationError("Missing payload given to nonce calculation function");
    }
    return new NonceCalculator(payload);
  }
}