package app.coronawarn.datadonation.services.ppac.android.attestation.signature;

import com.google.api.client.json.webtoken.JsonWebSignature;
import java.security.GeneralSecurityException;
import java.security.cert.X509Certificate;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Component;

@Profile("test-signature")
@Component
public class TestSignatureVerificationStrategy implements SignatureVerificationStrategy {

  private X509Certificate certificate = JwsGenerationUtil.getTestCertificate();

  public TestSignatureVerificationStrategy() {
    // Default constructor is used in tests.
  }

  /**
   * Just return the configured test related certificate as if it were trusted by the platform's TrustManagers.
   */
  @Override
  public X509Certificate verifySignature(JsonWebSignature jws) throws GeneralSecurityException {
    return this.certificate;
  }
}
