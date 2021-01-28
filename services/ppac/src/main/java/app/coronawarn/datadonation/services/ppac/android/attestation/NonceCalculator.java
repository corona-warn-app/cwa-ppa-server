package app.coronawarn.datadonation.services.ppac.android.attestation;

import app.coronawarn.datadonation.services.ppac.android.attestation.errors.NonceCalculationError;
import com.google.api.client.util.Base64;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.ObjectOutputStream;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

public class NonceCalculator {

  private final Object payload;

  /**
   * Constructs a calculator instance.
   */
  private NonceCalculator(Object payload) {
    this.payload = payload;
  }

  /**
   * Calculate the Nonce by calculating the SHA-256 hash of the byte array representations of the
   * given salt concatenated with the byte array representation of the scenario-specific payload.
   */
  public String calculate(String saltBase64) {
    try {
      return calculate(saltBase64, payload);
    } catch (Exception ex) {
      throw new NonceCalculationError(ex);
    }
  }

  private String calculate(String saltBase64, Object payload)
      throws IOException, NoSuchAlgorithmException {
    byte[] saltBytes = Base64.decodeBase64(saltBase64.getBytes());
    ByteArrayOutputStream bos = new ByteArrayOutputStream();
    ObjectOutputStream oos = new ObjectOutputStream(new ByteArrayOutputStream());
    oos.writeObject(payload);
    oos.flush();
    byte[] payloadBytes = bos.toByteArray();
    byte[] input = new byte[saltBytes.length + payloadBytes.length];
    System.arraycopy(saltBytes, 0, input, 0, saltBytes.length);
    System.arraycopy(payloadBytes, 0, input, saltBytes.length, payloadBytes.length);

    byte[] nonceBytes = MessageDigest.getInstance("SHA-256").digest(input);
    return Base64.encodeBase64String(nonceBytes);
  }

  public static NonceCalculator of(Object payload) {
    return new NonceCalculator(payload);
  }
}
