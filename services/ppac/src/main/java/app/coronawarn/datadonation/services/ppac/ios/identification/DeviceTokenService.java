package app.coronawarn.datadonation.services.ppac.ios.identification;

import app.coronawarn.datadonation.common.persistence.domain.DeviceToken;
import app.coronawarn.datadonation.common.persistence.repository.DeviceTokenRepository;
import app.coronawarn.datadonation.services.ppac.ios.exception.DuplicateDeviceTokenHashException;
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
   * This is a comment.
   *
   * @param deviceTokenHash  parameter.
   * @param currentTimeStamp parameter.
   */
  public void store(String deviceTokenHash, Long currentTimeStamp) {
    try {
      MessageDigest digest = MessageDigest.getInstance("SHA-256");
      final byte[] tokenHash = digest.digest(deviceTokenHash.getBytes(StandardCharsets.UTF_8));
      DeviceToken deviceToken = new DeviceToken(tokenHash,
          currentTimeStamp);
      deviceTokenRepository.save(deviceToken);
    } catch (Exception e) {
      if (e.getCause() instanceof DuplicateKeyException) {
        throw new DuplicateDeviceTokenHashException();
      }
      throw new InternalErrorException("Saving device token failed");
    }
  }
}
