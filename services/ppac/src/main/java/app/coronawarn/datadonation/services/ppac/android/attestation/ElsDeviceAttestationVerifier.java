package app.coronawarn.datadonation.services.ppac.android.attestation;

import app.coronawarn.datadonation.services.ppac.android.attestation.salt.SaltVerificationStrategy;
import app.coronawarn.datadonation.services.ppac.android.attestation.signature.SignatureVerificationStrategy;
import app.coronawarn.datadonation.services.ppac.android.attestation.timestamp.NoOpTimestampVerificationStrategy;
import app.coronawarn.datadonation.services.ppac.config.PpacConfiguration;
import org.apache.http.conn.ssl.DefaultHostnameVerifier;
import org.springframework.stereotype.Component;

/**
 * The implementation of this attestation verifier uses the original {@link DeviceAttestationVerifier} but skips the
 * device time check.
 */
@Component
public class ElsDeviceAttestationVerifier extends DeviceAttestationVerifier {

  /**
   * Constructs a verifier instance.
   *
   * @param hostnameVerifier              The host name verifier.
   * @param appParameters                 The configuration
   * @param saltVerificationStrategy      The salt verification strategy
   * @param signatureVerificationStrategy The signature verification strategy
   * @param integrityValidator            The integrity validator
   */
  public ElsDeviceAttestationVerifier(DefaultHostnameVerifier hostnameVerifier, PpacConfiguration appParameters,
      SaltVerificationStrategy saltVerificationStrategy, SignatureVerificationStrategy signatureVerificationStrategy,
      PpacAndroidIntegrityValidator integrityValidator) {
    super(hostnameVerifier, appParameters, saltVerificationStrategy, signatureVerificationStrategy,
        new NoOpTimestampVerificationStrategy(), integrityValidator);
  }
}
