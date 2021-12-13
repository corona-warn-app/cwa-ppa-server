package app.coronawarn.datadonation.services.ppac.android.attestation;

import com.google.api.client.json.webtoken.JsonWebSignature;
import com.google.api.client.util.Key;
import java.util.Base64;
import org.springframework.util.ObjectUtils;

/**
 * Simple pojo that reflects the contents of the device attestation (JWS) statement which is sent
 * with the analytics payload.
 * 
 * @see <a href="https://developer.android.com/training/safetynet/attestation">SafetyNet API</a>
 * @see <a href=
 *      "https://github.com/googlesamples/android-play-safetynet/tree/e291afcacf6e25809cc666cc79711a9438a9b4a6/server">Sample
 *      verification</a>
 */
public class AttestationStatement extends JsonWebSignature.Payload {

  public enum EvaluationType {
    BASIC, HARDWARE_BACKED;
  }

  /**
   * Embedded nonce sent as part of the request.
   */
  @Key
  private String nonce;

  /**
   * Timestamp of the request.
   */
  @Key
  private long timestampMs;

  /**
   * Package name of the APK that submitted this request.
   */
  @Key
  private String apkPackageName;

  /**
   * Digest of certificate of the APK that submitted this request.
   */
  @Key
  private String[] apkCertificateDigestSha256;

  /**
   * Digest of the APK that submitted this request.
   */
  @Key
  private String apkDigestSha256;

  /**
   * The device passed CTS and matches a known profile.
   */
  @Key
  private boolean ctsProfileMatch;

  /**
   * The device has passed a basic integrity test, but the CTS profile could not be verified.
   */
  @Key
  private boolean basicIntegrity;

  @Key
  private String advice;

  @Key
  private String evaluationType;

  /**
   * Constructs an instance.
   */
  public AttestationStatement() {
  }
  
  /**
   * Constructs an instance.
   */
  public AttestationStatement(String nonce, long timestampMs, String apkPackageName,
      String[] apkCertificateDigestSha256, String apkDigestSha256, boolean ctsProfileMatch,
      boolean basicIntegrity, String advice, String evaluationType) {
    this.nonce = nonce;
    this.timestampMs = timestampMs;
    this.apkPackageName = apkPackageName;
    this.apkCertificateDigestSha256 = apkCertificateDigestSha256;
    this.apkDigestSha256 = apkDigestSha256;
    this.ctsProfileMatch = ctsProfileMatch;
    this.basicIntegrity = basicIntegrity;
    this.advice = advice;
    this.evaluationType = evaluationType;
  }

  /**
   * Returns the Base64 encoded nonce value.
   */
  public String getNonce() {
    return nonce;
  }

  public boolean isBasicIntegrity() {
    return basicIntegrity;
  }

  public long getTimestampMs() {
    return timestampMs;
  }

  public String getApkPackageName() {
    return apkPackageName;
  }

  public byte[] getApkDigestSha256() {
    return Base64.getDecoder().decode(apkDigestSha256);
  }

  /**
   * Returns Apk Certificate Digests encoded as Base64 string.
   */
  public String[] getEncodedApkCertificateDigestSha256() {
    return apkCertificateDigestSha256;
  }
  
  public boolean isCtsProfileMatch() {
    return ctsProfileMatch;
  }

  public boolean hasBasicIntegrity() {
    return basicIntegrity;
  }

  public String getAdvice() {
    return advice;
  }

  public String getEvaluationType() {
    return evaluationType;
  }
  
  /**
   * Returns true if the given evaluation type is part of the statement.
   * There could be multiple comma separated evaluation types in on attestation statement.
   */
  public boolean isEvaluationTypeEqualTo(EvaluationType evType) {
    if (!ObjectUtils.isEmpty(evaluationType)) {
      return evaluationType.contains(evType.name());
    }
    return false;
  }
}