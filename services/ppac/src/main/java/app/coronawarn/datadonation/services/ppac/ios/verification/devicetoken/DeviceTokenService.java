package app.coronawarn.datadonation.services.ppac.ios.verification.devicetoken;

import app.coronawarn.datadonation.common.persistence.domain.DeviceToken;
import app.coronawarn.datadonation.common.persistence.repository.DeviceTokenRepository;
import app.coronawarn.datadonation.common.utils.TimeUtils;
import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.relational.core.conversion.DbActionExecutionException;
import org.springframework.stereotype.Service;

@Service
public class DeviceTokenService {

  private static final Logger logger = LoggerFactory.getLogger(DeviceTokenService.class);
  private final DeviceTokenRepository deviceTokenRepository;
  private final DeviceTokenRedemptionStrategy redemptionStrategy;

  public DeviceTokenService(DeviceTokenRepository deviceTokenRepository,
      DeviceTokenRedemptionStrategy redemptionStrategy) {
    this.deviceTokenRepository = deviceTokenRepository;
    this.redemptionStrategy = redemptionStrategy;
  }

  /**
   * Hashes a given DeviceToken with 'SHA-256' and stores it together with the current epoch seconds in UTC.
   *
   * @param deviceToken The input DeviceToken.
   */
  public void hashAndStoreDeviceToken(String deviceToken) {
    try {
      MessageDigest digest = MessageDigest.getInstance("SHA-256");
      final byte[] tokenHash = digest.digest(deviceToken.getBytes(StandardCharsets.UTF_8));
      DeviceToken newDeviceToken = new DeviceToken(tokenHash, TimeUtils.getEpochSecondsForNow());
      deviceTokenRepository.save(newDeviceToken);
    } catch (DbActionExecutionException | NoSuchAlgorithmException e) {
      redemptionStrategy.redeem(e);
    }
  }
}
