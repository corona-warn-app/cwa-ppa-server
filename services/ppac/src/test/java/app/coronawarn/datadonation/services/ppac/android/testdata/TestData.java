package app.coronawarn.datadonation.services.ppac.android.testdata;

import java.io.IOException;
import java.io.InputStream;
import java.io.Serializable;
import java.time.Instant;
import java.util.HashMap;
import java.util.Map;
import java.util.Properties;

public class TestData {

  public static String loadJwsWithExpiredCertificates() throws IOException {
    InputStream fileStream = TestData.class.getResourceAsStream("/jwsSamples/invalid_samples.properties");
    Properties properties = new Properties();
    properties.load(fileStream);
    return (String) properties.get("expiredCertificates");
  }

  public static String getJwsPayloadValues() throws IOException {
    Map<String, Serializable> payloadValues = getJwsPayloadDefaultValue();
    String encodedJws = JwsGenerationUtil.createCompactSerializedJws(payloadValues);
    return encodedJws;
  }

  public static String getJwsPayloadWrongApkPackageName() throws IOException {
    Map<String, Serializable> payloadValues = new HashMap<>(getJwsPayloadDefaultValue());
    payloadValues.put("apkPackageName", "de.rki.wrong.test");
    String encodedJws = JwsGenerationUtil.createCompactSerializedJws(payloadValues);
    return encodedJws;
  }

  public static String getJwsPayloadAttestationValidityExpired() throws IOException {
    Map<String, Serializable> payloadValues = new HashMap<>(getJwsPayloadDefaultValue());
    payloadValues.put("timestampMs", String.valueOf(Instant.now().minusSeconds(8000).toEpochMilli()));
    String encodedJws = JwsGenerationUtil.createCompactSerializedJws(payloadValues);
    return encodedJws;
  }

  public static String getJwsPayloadWithUnacceptedApkCertificateDigestHash() throws IOException {
    Map<String, Serializable> payloadValues = new HashMap<>(getJwsPayloadDefaultValue());
    payloadValues.put("apkCertificateDigestSha256", new String[]{""});
    String encodedJws = JwsGenerationUtil.createCompactSerializedJws(payloadValues);
    return encodedJws;
  }

  public static String getJwsPayloadWithNonce(String nonce) throws IOException {
    Map<String, Serializable> payloadValues = new HashMap<>(getJwsPayloadDefaultValue());
    payloadValues.put("nonce", nonce);
    String encodedJws = JwsGenerationUtil.createCompactSerializedJws(payloadValues);
    return encodedJws;
  }

  private static Map<String, Serializable> getJwsPayloadDefaultValue() throws IOException {
    return Map.of(
        "nonce", "AAAAAAAAAAAAAAAAAAAAAA==",
        "timestampMs", String.valueOf(Instant.now().minusSeconds(500).toEpochMilli()),
        "apkPackageName", "de.rki.coronawarnapp.test", "apkDigestSha256",
        "9oiqOMQAZfBgCnI0jyN7TgPAQNSSxWrjh14f0eXpB3U=", "ctsProfileMatch", "false",
        "apkCertificateDigestSha256", new String[]{"9VLvUGV0Gkx24etruEBYikvAtqSQ9iY6rYuKhG+xwKE="},
        "basicIntegrity", "false", "advice", "RESTORE_TO_FACTORY_ROM,LOCK_BOOTLOADER",
        "evaluationType", "BASIC");
  }
}
