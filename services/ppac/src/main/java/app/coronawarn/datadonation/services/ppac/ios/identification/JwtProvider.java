package app.coronawarn.datadonation.services.ppac.ios.identification;

import app.coronawarn.datadonation.services.ppac.config.PpacConfiguration;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.security.Keys;
import java.time.Instant;
import java.util.Date;
import javax.crypto.SecretKey;
import org.springframework.stereotype.Component;

@Component
public class JwtProvider {

  private final PpacConfiguration ppacConfiguration;

  public JwtProvider(PpacConfiguration ppacConfiguration) {
    this.ppacConfiguration = ppacConfiguration;
  }

  /**
   * Generate a valid jwt to query the Device API.
   *
   * @return an valid Json Web Token.
   */
  public String generateJwt() {
    String ppacIosJwtKeyId = this.ppacConfiguration.getIos().getPpacIosJwtKeyId();
    String ppacIosJwtTeamId = this.ppacConfiguration.getIos().getPpacIosJwtTeamId();

    // TODO FR load private key file
    SecretKey key = Keys.secretKeyFor(SignatureAlgorithm.HS384);
    Instant instant = Instant.now();
    return "Bearer " + Jwts
        .builder()
        .setHeaderParam("kid", ppacIosJwtKeyId)
        .setIssuer(ppacIosJwtTeamId)
        .setIssuedAt(Date.from(instant))
        .signWith(key)
        .compact();
  }
}
