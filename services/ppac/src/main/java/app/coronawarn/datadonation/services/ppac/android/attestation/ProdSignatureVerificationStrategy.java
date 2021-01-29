package app.coronawarn.datadonation.services.ppac.android.attestation;

import com.google.api.client.json.webtoken.JsonWebSignature;
import java.security.GeneralSecurityException;
import java.security.cert.X509Certificate;
import org.springframework.stereotype.Component;

@Component
public class ProdSignatureVerificationStrategy implements SignatureVerificationStrategy {

  /**
   * Verify using the default JVM TrustManager that contains all Root CA certificate chains. This is
   * currently based on the internal implementation from the Google library used.
   */
  @Override
  public X509Certificate verifySignature(JsonWebSignature jws) throws GeneralSecurityException {
    return jws.verifySignature();
  }
}
