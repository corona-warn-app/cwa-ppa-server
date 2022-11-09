package app.coronawarn.datadonation.services.ppac.android.attestation;

import app.coronawarn.datadonation.common.protocols.internal.ppdd.PPACAndroid;
import app.coronawarn.datadonation.services.ppac.android.attestation.salt.SaltVerificationStrategy;
import app.coronawarn.datadonation.services.ppac.android.attestation.signature.SignatureVerificationStrategy;
import app.coronawarn.datadonation.services.ppac.android.attestation.timestamp.NoOpTimestampVerificationStrategy;
import app.coronawarn.datadonation.services.ppac.commons.PpacScenario;
import app.coronawarn.datadonation.services.ppac.config.PpacConfiguration;
import org.apache.http.conn.ssl.DefaultHostnameVerifier;
import org.springframework.stereotype.Component;

/**
 * The implementation of this attestation verifier uses the original {@link DeviceAttestationVerifier} but skips the
 * device time check.
 */
@Component
public class SrsDeviceAttestationVerifier extends DeviceAttestationVerifier {

  private final AndroidIdVerificationStrategy androidIdVerificationStrategy;

  private final SrsRateLimitVerificationStrategy srsRateLimitVerificationStrategy;

  /**
   * Constructs a verifier instance.
   *
   * @param hostnameVerifier                 The host name verifier.
   * @param appParameters                    The configuration
   * @param saltVerificationStrategy         The salt verification strategy
   * @param signatureVerificationStrategy    The signature verification strategy
   * @param integrityValidator               The integrity validator
   * @param androidIdVerificationStrategy    The android id validator
   * @param srsRateLimitVerificationStrategy The rate limit validator
   */
  public SrsDeviceAttestationVerifier(final DefaultHostnameVerifier hostnameVerifier,
      final PpacConfiguration appParameters,
      final SaltVerificationStrategy saltVerificationStrategy,
      final SignatureVerificationStrategy signatureVerificationStrategy,
      final PpacAndroidIntegrityValidator integrityValidator,
      final AndroidIdVerificationStrategy androidIdVerificationStrategy,
      final SrsRateLimitVerificationStrategy srsRateLimitVerificationStrategy) {
    super(hostnameVerifier, appParameters, saltVerificationStrategy, signatureVerificationStrategy,
        new NoOpTimestampVerificationStrategy(), integrityValidator);
    this.androidIdVerificationStrategy = androidIdVerificationStrategy;
    this.srsRateLimitVerificationStrategy = srsRateLimitVerificationStrategy;
  }

  @Override
  public AttestationStatement validate(final PPACAndroid authAndroid, final NonceCalculator nonceCalculator,
      final PpacScenario scenario) {
    androidIdVerificationStrategy.validateAndroidId(authAndroid.getAndroidId());
    // FIXME: get the 16 byte pepper from Vault (do we have to do something additional to the app parameter config
    // here?)
    final String pepper = appParameters.getAndroid().getAndroidIdPepper();
    srsRateLimitVerificationStrategy.validateSrsRateLimit(authAndroid.getAndroidId(), pepper);
    return super.validate(authAndroid, nonceCalculator, scenario);
  }
}
