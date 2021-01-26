package app.coronawarn.datadonation.services.ppac.ios.identification;

import app.coronawarn.datadonation.services.ppac.config.PpacConfiguration;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import java.security.KeyFactory;
import java.security.NoSuchAlgorithmException;
import java.security.PrivateKey;
import java.security.spec.InvalidKeySpecException;
import java.security.spec.PKCS8EncodedKeySpec;
import java.time.Instant;
import java.util.Date;
import java.util.Optional;
import org.bouncycastle.util.encoders.Base64;
import org.springframework.stereotype.Component;

@Component
public class JwtProvider {

  private final PpacConfiguration ppacConfiguration;
  private static final String BEARER_PREFIX = "Bearer ";
  private static final String KEY_ID = "kid";
  private static final String ELLIPTIC_CURVE = "EC";

  public JwtProvider(PpacConfiguration ppacConfiguration) {
    this.ppacConfiguration = ppacConfiguration;
  }

  /**
   * Generate a valid jwt to query the Device API.
   *
   * @return an valid Json Web Token.
   */
  public String generateJwt() {
    String ppacIosJwtKeyId = this.ppacConfiguration.getPpacIosJwtKeyId();
    String ppacIosJwtTeamId = this.ppacConfiguration.getPpacIosJwtTeamId();
    String secretKeyString = this.ppacConfiguration.getPpacSigningKey();

    PrivateKey pk = buildPrivateKey(secretKeyString).orElseThrow(RuntimeException::new);

    return BEARER_PREFIX + Jwts
        .builder()
        .setHeaderParam(KEY_ID, ppacIosJwtKeyId)
        .setIssuer(ppacIosJwtTeamId)
        .setIssuedAt(Date.from(Instant.now()))
        .signWith(pk, SignatureAlgorithm.ES256)
        .compact();
  }

  private Optional<PrivateKey> buildPrivateKey(String pk8) {
    byte[] pk8EncodedBytes = convertToPkcs8(pk8);
    PKCS8EncodedKeySpec keySpec = new PKCS8EncodedKeySpec(pk8EncodedBytes);

    try {
      KeyFactory kf = KeyFactory.getInstance(ELLIPTIC_CURVE);
      return Optional.of(kf.generatePrivate(keySpec));
    } catch (InvalidKeySpecException | NoSuchAlgorithmException e) {
      return Optional.empty();
    }
  }

  private byte[] convertToPkcs8(String pk8) {
    pk8 = pk8.replace("-----BEGIN PRIVATE KEY-----", "");
    pk8 = pk8.replace("-----END PRIVATE KEY-----", "");
    pk8 = pk8.replaceAll("\\s+", "");

    return Base64.decode(pk8);
  }
}
