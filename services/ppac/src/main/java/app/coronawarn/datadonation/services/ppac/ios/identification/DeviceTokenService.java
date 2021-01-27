package app.coronawarn.datadonation.services.ppac.ios.identification;

import app.coronawarn.datadonation.common.persistence.domain.DeviceToken;
import app.coronawarn.datadonation.common.persistence.repository.DeviceTokenRepository;
import app.coronawarn.datadonation.services.ppac.ios.exception.DeviceTokenRedeemedException;
import app.coronawarn.datadonation.services.ppac.ios.exception.InternalErrorException;
import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.dao.DuplicateKeyException;
import org.springframework.stereotype.Service;

@Service
public class DeviceTokenService {

  private final DeviceTokenRepository deviceTokenRepository;

  private static final Logger logger = LoggerFactory.getLogger(DeviceTokenService.class);

  public DeviceTokenService(DeviceTokenRepository deviceTokenRepository) {
    this.deviceTokenRepository = deviceTokenRepository;
  }

  /**
   * Hashes a given DeviceToken with 'SHA-256' and stores it together with the current epoch seconds in UTC.
   *
   * @param deviceToken      The input DeviceToken.
   * @param currentTimeStamp The current Timestamp in Epoch Seconds and UTC.
   */
  public void hashAndStoreDeviceToken(String deviceToken, Long currentTimeStamp) {
    try {
      MessageDigest digest = MessageDigest.getInstance("SHA-256");
      final byte[] tokenHash = digest.digest(deviceToken.getBytes(StandardCharsets.UTF_8));
      DeviceToken newDeviceToken = new DeviceToken(tokenHash, currentTimeStamp);
      deviceTokenRepository.save(newDeviceToken);
    } catch (Exception e) {
      if (e.getCause() instanceof DuplicateKeyException) {
        throw new DeviceTokenRedeemedException();
      }
      throw new InternalErrorException("Saving device token failed.");
    }
  }
}
