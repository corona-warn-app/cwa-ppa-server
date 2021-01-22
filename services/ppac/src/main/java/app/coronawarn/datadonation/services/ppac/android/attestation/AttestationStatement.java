package app.coronawarn.datadonation.services.ppac.android.attestation;

import com.google.api.client.json.webtoken.JsonWebSignature;
import com.google.api.client.util.Base64;
import com.google.api.client.util.Key;

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

  public byte[] getNonce() {
    return Base64.decodeBase64(nonce);
  }

  public long getTimestampMs() {
    return timestampMs;
  }

  public String getApkPackageName() {
    return apkPackageName;
  }

  public byte[] getApkDigestSha256() {
    return Base64.decodeBase64(apkDigestSha256);
  }

  /**
   * Returns Apk Certificate Digests encoded as Base64 string.
   */
  public String[] getEncodedApkCertificateDigestSha256() {
    return apkCertificateDigestSha256;
  }
  
  /**
   * Returns Apk Certificate Digests decoded from Base64.
   */
  public byte[][] getApkCertificateDigestSha256() {
    byte[][] certs = new byte[apkCertificateDigestSha256.length][];
    for (int i = 0; i < apkCertificateDigestSha256.length; i++) {
      certs[i] = Base64.decodeBase64(apkCertificateDigestSha256[i]);
    }
    return certs;
  }

  public boolean isCtsProfileMatch() {
    return ctsProfileMatch;
  }

  public boolean hasBasicIntegrity() {
    return basicIntegrity;
  }
}
