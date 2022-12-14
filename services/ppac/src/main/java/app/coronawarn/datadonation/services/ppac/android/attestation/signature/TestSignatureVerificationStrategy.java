package app.coronawarn.datadonation.services.ppac.android.attestation.signature;

import static app.coronawarn.datadonation.services.ppac.android.attestation.signature.JwsGenerationUtil.getTestCertificate;

import com.google.api.client.json.webtoken.JsonWebSignature;
import java.security.GeneralSecurityException;
import java.security.cert.X509Certificate;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Component;

@Profile("test")
@Component
public class TestSignatureVerificationStrategy implements SignatureVerificationStrategy {

  private final X509Certificate certificate;

  public TestSignatureVerificationStrategy() throws Exception {
    // Default constructor is used in tests.
    certificate = getTestCertificate();
  }

  /**
   * Just return the configured test related certificate as if it were trusted by the platform's TrustManagers.
   */
  @Override
  public X509Certificate verifySignature(final JsonWebSignature jws) throws GeneralSecurityException {
    return certificate;
  }
}
