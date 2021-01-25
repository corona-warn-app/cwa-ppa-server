package app.coronawarn.datadonation.services.ppac.ios.identification;

import app.coronawarn.datadonation.common.persistence.repository.DeviceTokenRepository;
import app.coronawarn.datadonation.services.ppac.ios.exception.DuplicateDeviceTokenHashException;
import app.coronawarn.datadonation.services.ppac.ios.exception.InternalErrorException;
import app.coronawarn.datadonation.services.ppac.ios.utils.TimeUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.dao.DuplicateKeyException;
import org.springframework.stereotype.Service;

@Service
public class DeviceTokenService {

  private final DeviceTokenRepository deviceTokenRepository;
  private final TimeUtils timeUtils;
  private static final Logger logger = LoggerFactory.getLogger(DeviceTokenService.class);

  public DeviceTokenService(DeviceTokenRepository deviceTokenRepository, TimeUtils timeUtils) {
    this.deviceTokenRepository = deviceTokenRepository;
    this.timeUtils = timeUtils;
  }

  /**
   * This is a comment.
   *
   * @param deviceTokenHash  parameter.
   * @param currentTimeStamp
   */
  public void store(String deviceTokenHash, Long currentTimeStamp) {
    try {
      deviceTokenRepository.save(deviceTokenHash, currentTimeStamp);
    } catch (DuplicateKeyException e) {
      throw new DuplicateDeviceTokenHashException();
    } catch (Exception e) {
      throw new InternalErrorException();
    }
  }
}
