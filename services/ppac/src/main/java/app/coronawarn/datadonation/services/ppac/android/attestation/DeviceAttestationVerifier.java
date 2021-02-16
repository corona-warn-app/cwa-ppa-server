package app.coronawarn.datadonation.services.ppac.android.attestation;

import static app.coronawarn.datadonation.common.utils.TimeUtils.isInRange;

import app.coronawarn.datadonation.common.protocols.internal.ppdd.PpacAndroid.PPACAndroid;
import app.coronawarn.datadonation.services.ppac.android.attestation.AttestationStatement.EvaluationType;
import app.coronawarn.datadonation.services.ppac.android.attestation.errors.ApkCertificateDigestsNotAllowed;
import app.coronawarn.datadonation.services.ppac.android.attestation.errors.ApkPackageNameNotAllowed;
import app.coronawarn.datadonation.services.ppac.android.attestation.errors.BasicEvaluationTypeNotPresent;
import app.coronawarn.datadonation.services.ppac.android.attestation.errors.BasicIntegrityIsRequired;
import app.coronawarn.datadonation.services.ppac.android.attestation.errors.CtsProfileMatchRequired;
import app.coronawarn.datadonation.services.ppac.android.attestation.errors.FailedAttestationHostnameValidation;
import app.coronawarn.datadonation.services.ppac.android.attestation.errors.FailedAttestationTimestampValidation;
import app.coronawarn.datadonation.services.ppac.android.attestation.errors.FailedJwsParsing;
import app.coronawarn.datadonation.services.ppac.android.attestation.errors.FailedSignatureVerification;
import app.coronawarn.datadonation.services.ppac.android.attestation.errors.HardwareBackedEvaluationTypeNotPresent;
import app.coronawarn.datadonation.services.ppac.android.attestation.errors.MissingMandatoryAuthenticationFields;
import app.coronawarn.datadonation.services.ppac.android.attestation.errors.NonceCouldNotBeVerified;
import app.coronawarn.datadonation.services.ppac.android.attestation.salt.SaltVerificationStrategy;
import app.coronawarn.datadonation.services.ppac.android.attestation.signature.SignatureVerificationStrategy;
import app.coronawarn.datadonation.services.ppac.config.PpacConfiguration;
import com.google.api.client.json.gson.GsonFactory;
import com.google.api.client.json.webtoken.JsonWebSignature;
import com.google.common.base.Strings;
import java.security.GeneralSecurityException;
import java.security.cert.X509Certificate;
import java.time.Instant;
import java.util.Arrays;
import javax.net.ssl.SSLException;
import org.apache.http.conn.ssl.DefaultHostnameVerifier;
import org.springframework.stereotype.Component;

/**
 * For security purposes, each Android mobile device that participates in data donation gathering will send an
 * attestation statement (JWS) that helps with ensuring the client is running on a genuine Android device. After
 * assessing the device integrity, its OS issues the attestation statement which must be checked by the data donation
 * server before storing any metrics data. This class is used to perform this validation.
 *
 * @see <a href="https://developer.ppac.android.com/training/safetynet/attestation">SafetyNet API</a>
 * @see <a href= "https://github.com/googlesamples/android-play-safetynet/tree/e291afcacf6e25809cc666cc79711a9438a9b4a6/server">Sample
 * verification</a>
 */
@Component
public class DeviceAttestationVerifier {

  private DefaultHostnameVerifier hostnameVerifier;
  private PpacConfiguration appParameters;
  private SignatureVerificationStrategy signatureVerificationStrategy;
  private SaltVerificationStrategy saltVerificationStrategy;

  /**
   * Constructs a verifier instance.
   */
  public DeviceAttestationVerifier(DefaultHostnameVerifier hostnameVerifier,
      PpacConfiguration appParameters, SaltVerificationStrategy saltVerificationStrategy,
      SignatureVerificationStrategy signatureVerificationStrategy) {
    this.hostnameVerifier = hostnameVerifier;
    this.appParameters = appParameters;
    this.saltVerificationStrategy = saltVerificationStrategy;
    this.signatureVerificationStrategy = signatureVerificationStrategy;
  }

  /**
   * Perform several validations on the given signed attestation statement. In case of validation problems specific
   * runtime exceptions are thrown.
   *
   * @throws MissingMandatoryAuthenticationFields - in case of fields which are expected are null
   * @throws FailedJwsParsing                     - in case of unparsable jws format
   * @throws FailedAttestationTimestampValidation - in case the timestamp in the JWS payload is expired
   * @throws FailedSignatureVerification          - in case the signature can not be verified / trusted
   * @throws ApkPackageNameNotAllowed             - in case contained apk package name is not part of the globally
   *                                              configured apk allowed list
   */
  public AttestationStatement validate(PPACAndroid authAndroid, NonceCalculator nonceCalculator) {
    saltVerificationStrategy.validateSalt(authAndroid.getSalt());
    return validateJws(authAndroid.getSafetyNetJws(), authAndroid.getSalt(), nonceCalculator);
  }

  private AttestationStatement validateJws(String safetyNetJwsResult, String salt,
      NonceCalculator nonceCalculator) {
    if (Strings.isNullOrEmpty(safetyNetJwsResult)) {
      throw new MissingMandatoryAuthenticationFields("No JWS field received");
    }
    JsonWebSignature jws = parseJws(safetyNetJwsResult);
    validateSignature(jws);
    return validatePayload(jws, salt, nonceCalculator);
  }

  private AttestationStatement validatePayload(JsonWebSignature jws, String salt,
      NonceCalculator nonceCalculator) {
    AttestationStatement stmt = (AttestationStatement) jws.getPayload();
    validateNonce(salt, stmt.getNonce(), nonceCalculator);
    validateTimestamp(stmt.getTimestampMs());
    validateApkPackageName(stmt.getApkPackageName());
    validateApkCertificateDigestSha256(stmt.getEncodedApkCertificateDigestSha256());
    validateIntegrity(stmt);
    return stmt;
  }

  private void validateIntegrity(AttestationStatement stmt) {
    if (appParameters.getAndroid().getRequireBasicIntegrity() && !stmt.isBasicIntegrity()) {
      throw new BasicIntegrityIsRequired();
    }
    if (appParameters.getAndroid().getRequireCtsProfileMatch() && !stmt.isCtsProfileMatch()) {
      throw new CtsProfileMatchRequired();
    }
    if (appParameters.getAndroid().getRequireEvaluationTypeBasic()
        && !stmt.isEvaluationTypeEqualTo(EvaluationType.BASIC)) {
      throw new BasicEvaluationTypeNotPresent();
    }
    if (appParameters.getAndroid().getRequireEvaluationTypeHardwareBacked()
        && !stmt.isEvaluationTypeEqualTo(EvaluationType.HARDWARE_BACKED)) {
      throw new HardwareBackedEvaluationTypeNotPresent();
    }
  }

  private void validateNonce(String salt, String receivedNonce, NonceCalculator nonceCalculator) {
    if (Strings.isNullOrEmpty(receivedNonce)) {
      throw new MissingMandatoryAuthenticationFields("Nonce has not been received");
    }
    String recalculatedNonce = nonceCalculator.calculate(salt);
    if (!receivedNonce.contentEquals(recalculatedNonce)) {
      throw new NonceCouldNotBeVerified("Recalculated nonce " + recalculatedNonce
          + " does not match the received nonce " + receivedNonce);
    }
  }

  private void validateApkCertificateDigestSha256(String[] encodedApkCertDigests) {
    String[] allowedApkCertificateDigests =
        appParameters.getAndroid().getAllowedApkCertificateDigests();

    if (!(encodedApkCertDigests.length == 1
        && Arrays.asList(allowedApkCertificateDigests).contains(encodedApkCertDigests[0]))) {
      throw new ApkCertificateDigestsNotAllowed();
    }
  }

  private void validateApkPackageName(String apkPackageName) {
    String[] allowedApkPackageNames = appParameters.getAndroid().getAllowedApkPackageNames();
    if (!Arrays.asList(allowedApkPackageNames).contains(apkPackageName)) {
      throw new ApkPackageNameNotAllowed(apkPackageName);
    }
  }

  private void validateTimestamp(long timestampMs) {
    Integer attestationValidity = appParameters.getAndroid().getAttestationValidity();
    Instant present = Instant.now();
    Instant upperLimit = present.plusSeconds(attestationValidity);
    Instant lowerLimit = present.minusSeconds(attestationValidity);
    if (!isInRange(timestampMs, lowerLimit, upperLimit)) {
      throw new FailedAttestationTimestampValidation();
    }
  }

  private void validateSignature(JsonWebSignature jws) {
    X509Certificate signatureCertificate = parseSignatureCertificate(jws);
    verifyHostname(appParameters.getAndroid().getCertificateHostname(), signatureCertificate);
  }

  /**
   * Use the underlying strategy to verify the JWS certificate chain and return the leaf in case valid.
   *
   * @see SignatureVerificationStrategy#verifySignature(JsonWebSignature)
   */
  private X509Certificate parseSignatureCertificate(JsonWebSignature jws) {
    try {
      X509Certificate cert = signatureVerificationStrategy.verifySignature(jws);
      if (cert == null) {
        throw new FailedSignatureVerification(
            "Certificate missing - Error during cryptographic verification of the JWS signature: "
                + Arrays.toString(jws.getSignatureBytes()));
      }
      return cert;
    } catch (GeneralSecurityException e) {
      throw new FailedSignatureVerification(
          "Error during cryptographic verification of the JWS signature: "
              + Arrays.toString(jws.getSignatureBytes()), e);
    }
  }

  /**
   * Parses the signed attestation statement to JsonWebSignature.
   *
   * @param signedAttestationStatement The signed attestation statement that shall be parsed.
   * @return JsonWebSignature representation of the signed attestation statement.
   */
  public JsonWebSignature parseJws(String signedAttestationStatement) {
    try {
      return JsonWebSignature.parser(GsonFactory.getDefaultInstance())
          .setPayloadClass(AttestationStatement.class).parse(signedAttestationStatement);
    } catch (Exception e) {
      throw new FailedJwsParsing(e);
    }
  }

  /**
   * Verifies that the certificate matches the specified hostname. Uses the {@link DefaultHostnameVerifier} from the
   * Apache HttpClient library to confirm that the hostname matches the certificate.
   */
  private void verifyHostname(String hostname, X509Certificate leafCert) {
    try {
      hostnameVerifier.verify(hostname, leafCert);
    } catch (SSLException e) {
      throw new FailedAttestationHostnameValidation(
          "Hostname verification failed for attestation certificate.", e);
    }
  }
}
