package app.coronawarn.datadonation.services.ppac.android.attestation;

import app.coronawarn.datadonation.common.protocols.internal.ppdd.PPACAndroid;
import app.coronawarn.datadonation.services.ppac.android.attestation.errors.ApkCertificateDigestsNotAllowed;
import app.coronawarn.datadonation.services.ppac.android.attestation.errors.ApkPackageNameNotAllowed;
import app.coronawarn.datadonation.services.ppac.android.attestation.errors.FailedAttestationHostnameValidation;
import app.coronawarn.datadonation.services.ppac.android.attestation.errors.FailedAttestationTimestampValidation;
import app.coronawarn.datadonation.services.ppac.android.attestation.errors.FailedJwsParsing;
import app.coronawarn.datadonation.services.ppac.android.attestation.errors.FailedSignatureVerification;
import app.coronawarn.datadonation.services.ppac.android.attestation.errors.MissingMandatoryAuthenticationFields;
import app.coronawarn.datadonation.services.ppac.android.attestation.errors.NonceCouldNotBeVerified;
import app.coronawarn.datadonation.services.ppac.android.attestation.salt.SaltVerificationStrategy;
import app.coronawarn.datadonation.services.ppac.android.attestation.signature.SignatureVerificationStrategy;
import app.coronawarn.datadonation.services.ppac.android.attestation.timestamp.TimestampVerificationStrategy;
import app.coronawarn.datadonation.services.ppac.commons.PpacScenario;
import app.coronawarn.datadonation.services.ppac.config.PpacConfiguration;
import com.google.api.client.json.gson.GsonFactory;
import com.google.api.client.json.webtoken.JsonWebSignature;
import java.security.GeneralSecurityException;
import java.security.cert.X509Certificate;
import java.util.Arrays;
import java.util.Collection;
import javax.net.ssl.SSLException;
import org.apache.http.conn.ssl.DefaultHostnameVerifier;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;
import org.springframework.util.ObjectUtils;

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

  private static final Logger logger = LoggerFactory.getLogger(DeviceAttestationVerifier.class);

  private DefaultHostnameVerifier hostnameVerifier;
  private PpacConfiguration appParameters;
  private SignatureVerificationStrategy signatureVerificationStrategy;
  private SaltVerificationStrategy saltVerificationStrategy;
  private TimestampVerificationStrategy timestampVerificationStrategy;
  private PpacAndroidIntegrityValidator integrityValidator;

  /**
   * Constructs a verifier instance.
   */
  public DeviceAttestationVerifier(DefaultHostnameVerifier hostnameVerifier, PpacConfiguration appParameters,
      SaltVerificationStrategy saltVerificationStrategy, SignatureVerificationStrategy signatureVerificationStrategy,
      TimestampVerificationStrategy timestampVerificationStrategy,
      PpacAndroidIntegrityValidator integrityValidator) {
    this.hostnameVerifier = hostnameVerifier;
    this.appParameters = appParameters;
    this.saltVerificationStrategy = saltVerificationStrategy;
    this.signatureVerificationStrategy = signatureVerificationStrategy;
    this.timestampVerificationStrategy = timestampVerificationStrategy;
    this.integrityValidator = integrityValidator;
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
  public AttestationStatement validate(PPACAndroid authAndroid, NonceCalculator nonceCalculator,
      PpacScenario scenario) {
    saltVerificationStrategy.validateSalt(authAndroid.getSalt());
    return validateJws(authAndroid.getSafetyNetJws(), authAndroid.getSalt(), nonceCalculator, scenario);
  }

  private AttestationStatement validateJws(String safetyNetJwsResult, String salt, NonceCalculator nonceCalculator,
      PpacScenario scenario) {
    if (ObjectUtils.isEmpty(safetyNetJwsResult)) {
      throw new MissingMandatoryAuthenticationFields("No JWS field received");
    }
    JsonWebSignature jws = parseJws(safetyNetJwsResult);
    validateSignature(jws);
    return validatePayload(jws, salt, nonceCalculator, scenario);
  }

  private AttestationStatement validatePayload(JsonWebSignature jws, String salt, NonceCalculator nonceCalculator,
      PpacScenario scenario) {
    AttestationStatement stmt = (AttestationStatement) jws.getPayload();
    validateNonce(salt, stmt.getNonce(), nonceCalculator);
    timestampVerificationStrategy.validateTimestamp(stmt.getTimestampMs());
    validateApkPackageName(stmt.getApkPackageName());
    validateApkCertificateDigestSha256(stmt.getEncodedApkCertificateDigestSha256());
    scenario.validateIntegrity(integrityValidator, stmt);
    return stmt;
  }

  private void validateNonce(String salt, String receivedNonce, NonceCalculator nonceCalculator) {
    if (ObjectUtils.isEmpty(receivedNonce)) {
      if (appParameters.getAndroid().getDisableNonceCheck()) {
        logger.error("Nonce is null or empty, but we'll ignore this for now...");
      } else {
        throw new MissingMandatoryAuthenticationFields("Nonce has not been received");
      }
    }
    String recalculatedNonce = nonceCalculator.calculate(salt);
    if (!receivedNonce.contentEquals(recalculatedNonce)) {
      if (appParameters.getAndroid().getDisableNonceCheck()) {
        logger.error("Recalculated nonce '{}' does not match the received nonce '{}', but we'll ignore this for now...",
            recalculatedNonce, receivedNonce);
      } else {
        throw new NonceCouldNotBeVerified(
            "Recalculated nonce " + recalculatedNonce + " does not match the received nonce " + receivedNonce);
      }
    } else {
      logger.debug("Recalculated nonce matches the received one");
    }
  }

  private void validateApkCertificateDigestSha256(String[] encodedApkCertDigests) {
    if (ObjectUtils.isEmpty(encodedApkCertDigests)) {
      if (appParameters.getAndroid().getDisableApkCertificateDigestsCheck()) {
        logger.error("no ApkCertificateDigestSha256 received, but we'll ignore this for now...");
        return;
      }
      throw new ApkCertificateDigestsNotAllowed();
    }

    if (encodedApkCertDigests.length != 1) {
      if (appParameters.getAndroid().getDisableApkCertificateDigestsCheck()) {
        logger.error("received multiple ApkCertificateDigestSha256, but we'll ignore this for now...");
        return;
      }
      throw new ApkCertificateDigestsNotAllowed();
    }

    final Collection<String> allowedApkCertificateDigests = Arrays
        .asList(appParameters.getAndroid().getAllowedApkCertificateDigests());

    if (!allowedApkCertificateDigests.contains(encodedApkCertDigests[0])) {
      if (appParameters.getAndroid().getDisableApkCertificateDigestsCheck()) {
        logger.error(
            "received ApkCertificateDigestSha256 '{}' isn't in the allowlist, but we'll ignore this for now...",
            encodedApkCertDigests[0]);
      } else {
        throw new ApkCertificateDigestsNotAllowed();
      }
    }
  }

  private void validateApkPackageName(String apkPackageName) {
    String[] allowedApkPackageNames = appParameters.getAndroid().getAllowedApkPackageNames();
    if (!Arrays.asList(allowedApkPackageNames).contains(apkPackageName)) {
      throw new ApkPackageNameNotAllowed(apkPackageName);
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
          "Error during cryptographic verification of the JWS signature: " + Arrays.toString(jws.getSignatureBytes()),
          e);
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
      return JsonWebSignature.parser(GsonFactory.getDefaultInstance()).setPayloadClass(AttestationStatement.class)
          .parse(signedAttestationStatement);
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
      throw new FailedAttestationHostnameValidation("Hostname verification failed for attestation certificate.", e);
    }
  }
}
