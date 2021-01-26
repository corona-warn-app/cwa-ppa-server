package app.coronawarn.datadonation.services.edus.otp;

import app.coronawarn.datadonation.common.persistence.domain.OneTimePassword;
import app.coronawarn.datadonation.common.persistence.repository.OneTimePasswordRepository;
import app.coronawarn.datadonation.services.edus.config.OtpConfig;
import java.time.LocalDateTime;
import java.time.ZoneOffset;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

@Service
public class OtpService {

  private Logger logger = LoggerFactory.getLogger(OtpService.class);

  private OneTimePasswordRepository otpRepository;
  private OtpConfig otpConfig;

  /**
   * Constructs the OtpService.
   *
   * @param otpRepository a
   * @param otpConfig      b
   */
  public OtpService(
      OneTimePasswordRepository otpRepository,
      OtpConfig otpConfig) {
    this.otpRepository = otpRepository;
    this.otpConfig = otpConfig;
  }

  /**
   * Checks if requested otp exists in the database and is valid.
   *
   * @param otp String unique id
   * @return true if otp exists and not expired
   */
  public OtpState getOtpStatus(String otp) {
    return otpRepository.findById(otp)
        .map(this::getOtpStatus)
        .orElseThrow(() -> {
          logger.warn("OTP not found.");
          return new OtpNotFoundException();
        });
  }

  private OtpState getOtpStatus(OneTimePassword otp) {
    LocalDateTime expirationTime = otp.getCreationTimestamp().plusHours(otpConfig.getOtpValidityInHours());
    boolean isExpired = !expirationTime.isAfter(LocalDateTime.now(ZoneOffset.UTC));
    boolean isRedeemed = otp.getRedemptionTimestamp() != null;

    setLastValidityCheckTimestamp(otp);

    if (!isRedeemed && !isExpired) {
      return OtpState.VALID;
    } else if (!isExpired && isRedeemed) {
      return OtpState.REDEEMED;
    } else if (isExpired && !isRedeemed) {
      return OtpState.EXPIRED;
    } else {
      return OtpState.REDEEMED;
    }
  }

  private void setLastValidityCheckTimestamp(OneTimePassword otp) {
    otp.setLastValidityCheckTimestamp(LocalDateTime.now(ZoneOffset.UTC));
    otpRepository.save(otp);
  }

  /**
   * Redeems the otp object.
   *
   * @param otp Otp data id
   * @return OtpStateEnum value
   */
  public OtpState redeemOtp(String otp) {
    OtpState state = getOtpStatus(otp);
    if (state.equals(OtpState.VALID)) {
      var otpData = otpRepository.findById(otp).get();
      otpData.setRedemptionTimestamp(LocalDateTime.now(ZoneOffset.UTC));
      otpRepository.save(otpData);
      return OtpState.VALID;
    }
    return state;
  }
}
