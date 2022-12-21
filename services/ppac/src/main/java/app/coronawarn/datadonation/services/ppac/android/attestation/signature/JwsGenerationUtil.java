package app.coronawarn.datadonation.services.ppac.android.attestation.signature;

import static com.google.api.client.json.webtoken.JsonWebSignature.signUsingRsaSha256;
import static java.util.Objects.requireNonNull;
import static org.apache.commons.codec.binary.Base64.decodeBase64;
import static org.apache.commons.codec.binary.Base64.encodeBase64String;
import static org.apache.commons.codec.binary.Base64.encodeBase64URLSafeString;

import com.google.api.client.json.gson.GsonFactory;
import com.google.api.client.json.webtoken.JsonWebSignature;
import com.google.api.client.json.webtoken.JsonWebSignature.Header;
import com.google.api.client.json.webtoken.JsonWebToken;
import com.google.api.client.util.StringUtils;
import java.io.File;
import java.io.FileReader;
import java.io.InputStream;
import java.io.Serializable;
import java.net.URL;
import java.security.PrivateKey;
import java.security.Security;
import java.security.cert.CertificateFactory;
import java.security.cert.X509Certificate;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import org.bouncycastle.asn1.pkcs.PrivateKeyInfo;
import org.bouncycastle.openssl.PEMParser;
import org.bouncycastle.openssl.jcajce.JcaPEMKeyConverter;

public class JwsGenerationUtil {

  /**
   * Serialized Jws.
   *
   * @param payloadValues Map with the payload values
   * @return Serialized Jws
   */
  public static String createCompactSerializedJws(final Map<String, Serializable> payloadValues) throws Exception {
    final JsonWebSignature jws = createJsonWebSignature(payloadValues);
    final GsonFactory gsonFactory = GsonFactory.getDefaultInstance();
    return encodeBase64URLSafeString(gsonFactory.toByteArray(requireNonNull(jws).getHeader())) + "."
        + encodeBase64URLSafeString(gsonFactory.toByteArray(jws.getPayload())) + "."
        + encodeBase64URLSafeString(jws.getSignatureBytes());
  }

  /**
   * Creates a Json Web Signature for tests.
   *
   * @param payloadValues values set on the jws payload
   * @return jws for test purpose
   */
  public static JsonWebSignature createJsonWebSignature(final Map<String, Serializable> payloadValues)
      throws Exception {
    Security.addProvider(new org.bouncycastle.jce.provider.BouncyCastleProvider());
    final Header header = new Header();
    header.setAlgorithm("RS256");

    final X509Certificate certificate = getTestCertificate();
    final List<String> certificates = new ArrayList<>();
    certificates.add(encodeBase64String(requireNonNull(certificate).getEncoded()));
    header.setX509Certificates(certificates);
    final JsonWebToken.Payload payload = new JsonWebToken.Payload();
    payloadValues.forEach(payload::set);

    final String signedJwsString = signUsingRsaSha256(getPrivateKey(),
        GsonFactory.getDefaultInstance(), header, payload);

    final int firstDot = signedJwsString.indexOf('.');
    final int secondDot = signedJwsString.indexOf('.', firstDot + 1);
    final byte[] signatureBytes = decodeBase64(signedJwsString.substring(secondDot + 1));
    final byte[] signedContentBytes = StringUtils.getBytesUtf8(signedJwsString.substring(0, secondDot));
    return new JsonWebSignature(header, payload, signatureBytes, signedContentBytes);
  }

  private static PrivateKey getPrivateKey() throws Exception {
    final URL url = JwsGenerationUtil.class.getResource("/certificates/test.key");
    try (final PEMParser pemParser = new PEMParser(new FileReader(new File(url.toURI())))) {
      final PrivateKeyInfo pki = (PrivateKeyInfo) pemParser.readObject();
      final JcaPEMKeyConverter converter = new JcaPEMKeyConverter().setProvider("BC");
      // Unencrypted key - no password needed
      return converter.getPrivateKey(pki);
    }
  }

  /**
   * X509 Certificate loaded for tests.
   *
   * @return self created test certificate
   */
  public static X509Certificate getTestCertificate() throws Exception {
    Security.addProvider(new org.bouncycastle.jce.provider.BouncyCastleProvider());
    final CertificateFactory cf = CertificateFactory.getInstance("X509", "BC");
    final InputStream is = JwsGenerationUtil.class.getResourceAsStream("/certificates/test.cert");
    final X509Certificate certificate = (X509Certificate) cf.generateCertificate(is);

    is.close();
    return certificate;
  }
}
