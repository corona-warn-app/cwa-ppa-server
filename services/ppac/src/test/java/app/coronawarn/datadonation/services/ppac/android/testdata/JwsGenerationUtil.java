package app.coronawarn.datadonation.services.ppac.android.testdata;

import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.Serializable;
import java.net.URL;
import java.security.NoSuchProviderException;
import java.security.PrivateKey;
import java.security.Security;
import java.security.cert.CertificateException;
import java.security.cert.CertificateFactory;
import java.security.cert.X509Certificate;
import java.util.List;
import java.util.Map;
import org.bouncycastle.asn1.pkcs.PrivateKeyInfo;
import org.bouncycastle.openssl.PEMParser;
import org.bouncycastle.openssl.jcajce.JcaPEMKeyConverter;
import com.google.api.client.json.gson.GsonFactory;
import com.google.api.client.json.webtoken.JsonWebSignature;
import com.google.api.client.json.webtoken.JsonWebToken;
import com.google.api.client.util.Base64;
import com.google.api.client.util.Lists;
import com.google.api.client.util.StringUtils;

public class JwsGenerationUtil {

  public static JsonWebSignature createJsonWebSignature(Map<String, Serializable> payloadValues) {
    try {
      Security.addProvider(new org.bouncycastle.jce.provider.BouncyCastleProvider());
      JsonWebSignature.Header header = new JsonWebSignature.Header();
      header.setAlgorithm("RS256");
      
      X509Certificate certificate = getTestCertificate();
      List<String> certificates = Lists.newArrayList();
      certificates.add(Base64.encodeBase64String(certificate.getEncoded()));
      header.setX509Certificates(certificates);
      JsonWebToken.Payload payload = new JsonWebToken.Payload();
      payloadValues.forEach((k, v) -> payload.set(k, v));

      //RSAPublicKey publicKey = getPublicKey();
      String signedJWSString = JsonWebSignature.signUsingRsaSha256(getPrivateKey(),
          GsonFactory.getDefaultInstance(), header, payload);

      int firstDot = signedJWSString.indexOf('.');
      int secondDot = signedJWSString.indexOf('.', firstDot + 1);
      byte[] signatureBytes = Base64.decodeBase64(signedJWSString.substring(secondDot + 1));
      byte[] signedContentBytes = StringUtils.getBytesUtf8(signedJWSString.substring(0, secondDot));
      JsonWebSignature signature =
          new JsonWebSignature(header, payload, signatureBytes, signedContentBytes);
      return signature;
    } catch (Exception ex) {
      return null;
    }
  }

  public static String createCompactSerializedJws(Map<String, Serializable> payloadValues) {
    try {
      JsonWebSignature jws = createJsonWebSignature(payloadValues);
      GsonFactory gsonFactory = GsonFactory.getDefaultInstance();
      String content = Base64.encodeBase64URLSafeString(gsonFactory.toByteArray(jws.getHeader()))
          + "." + Base64.encodeBase64URLSafeString(gsonFactory.toByteArray(jws.getPayload()));
      return content + "." + Base64.encodeBase64URLSafeString(jws.getSignatureBytes());
    } catch (Exception ex) {
      return null;
    }
  }
  
  public static X509Certificate getTestCertificate() {
    try {
      Security.addProvider(new org.bouncycastle.jce.provider.BouncyCastleProvider());
      CertificateFactory cf = CertificateFactory.getInstance("X509", "BC");
      InputStream is = JwsGenerationUtil.class.getResourceAsStream("/certificates/test.cert");
      X509Certificate certificate = (X509Certificate) cf.generateCertificate(is);
    
      is.close();
      return certificate;
    } catch (IOException | CertificateException | NoSuchProviderException e) {
      e.printStackTrace();
    }
    return null;
  }
  
  private static PrivateKey getPrivateKey() {
    try {
      URL url = JwsGenerationUtil.class.getResource("/certificates/test.key");
      PEMParser pemParser = new PEMParser(new FileReader(new File(url.getPath())));
      Object object;
      object = pemParser.readObject();
      JcaPEMKeyConverter converter = new JcaPEMKeyConverter().setProvider("BC");

      // Unencrypted key - no password needed
      PrivateKeyInfo pki = (PrivateKeyInfo) object;
      return converter.getPrivateKey(pki);
    } catch (IOException e) {
      e.printStackTrace();
    }
    return null;
  }
}
