package app.coronawarn.datadonation.services.ppac.android.attestation;

import com.google.api.client.json.webtoken.JsonWebSignature;
import java.security.GeneralSecurityException;
import java.security.cert.X509Certificate;

public interface SignatureVerificationStrategy {
  
  /**
   * Verify that X509 certificates (chain) which are included in the 'x5c' header of the JWS (and
   * used to validate the JWS Signature) are trusted by the default JVM TrustManager. Parse and
   * return the leaf  {@link X509Certificate} object in the chain in case it is verified.
   */
  public X509Certificate verifySignature(JsonWebSignature jws) throws GeneralSecurityException;
}
