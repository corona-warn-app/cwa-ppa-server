package app.coronawarn.datadonation.services.ppac.android.attestation;

import com.google.api.client.json.webtoken.JsonWebSignature;
import app.coronawarn.datadonation.services.ppac.android.attestation.signature.SignatureVerificationStrategy;
import java.security.GeneralSecurityException;
import java.security.cert.X509Certificate;

public class TestSignatureVerificationStrategy implements SignatureVerificationStrategy {

  private X509Certificate certificate;

  public TestSignatureVerificationStrategy(X509Certificate certificate) {
    this.certificate = certificate;
  }

  /**
   * Just return the configured test related certificate as if it were trusted by the platform's
   * TrustManagers.
   */
  @Override
  public X509Certificate verifySignature(JsonWebSignature jws) throws GeneralSecurityException {
    return this.certificate;
  }
}
