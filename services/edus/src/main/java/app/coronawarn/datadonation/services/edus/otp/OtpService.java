package app.coronawarn.datadonation.services.edus.otp;

import app.coronawarn.datadonation.common.persistence.domain.OneTimePassword;
import app.coronawarn.datadonation.common.persistence.repository.OneTimePasswordRepository;
import app.coronawarn.datadonation.services.edus.config.OtpConfig;
import app.coronawarn.datadonation.services.edus.utils.TimeUtils;
import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.util.UUID;
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
   * @param otpRepository The OTP Repository.
   * @param otpConfig     The OTP configuration.
   */
  public OtpService(
      OneTimePasswordRepository otpRepository,
      OtpConfig otpConfig) {
    this.otpRepository = otpRepository;
    this.otpConfig = otpConfig;
  }

  /**
   * Create and return a new OneTimePassword.
   *
   * @return the generated OneTimePassword.
   */
  public OneTimePassword createOtp() {
    String uuid = UUID.randomUUID().toString();
    return otpRepository.save(new OneTimePassword(uuid, TimeUtils.getEpochSecondsForNow()));
  }

  /**
   * Redeems the OTP object, if it has state {@link OtpState#VALID}. This means that the redemption timestamp is set to
   * the current timestamp.
   *
   * @param otp The OTP to redeem.
   * @return The {@link OtpState} of the OTP before redemption.
   */
  public OtpState redeemOtp(String otp) {
    OtpState state = getOtpStatus(otp);
    if (state.equals(OtpState.VALID)) {
      var otpData = otpRepository.findById(otp).get();
      otpData.setRedemptionTimestamp(TimeUtils.getEpochSecondsForNow());
      otpRepository.save(otpData);
      return getOtpStatus(otpData);
    }
    return state;
  }

  private OtpState getOtpStatus(String otp) {
    return otpRepository.findById(otp)
        .map(this::getOtpStatus)
        .orElseThrow(() -> {
          logger.warn("OTP not found.");
          return new OtpNotFoundException();
        });
  }

  protected OtpState getOtpStatus(OneTimePassword otp) {
    LocalDateTime expirationTime = TimeUtils.getLocalDateTimeFor(otp.getCreationTimestamp())
        .plusHours(otpConfig.getOtpValidityInHours());
    boolean isExpired = !expirationTime.isAfter(LocalDateTime.now(ZoneOffset.UTC));
    boolean isRedeemed = otp.getRedemptionTimestamp() != null;

    updateOtpLastValidityCheckTimestamp(otp);

    if (!isRedeemed && !isExpired) {
      return OtpState.VALID;
    } else if (isExpired && !isRedeemed) {
      return OtpState.EXPIRED;
    } else {
      return OtpState.REDEEMED;
    }
  }

  private void updateOtpLastValidityCheckTimestamp(OneTimePassword otp) {
    otp.setLastValidityCheckTimestamp(TimeUtils.getEpochSecondsForNow());
    otpRepository.save(otp);
  }
}
