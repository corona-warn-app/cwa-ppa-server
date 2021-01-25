package app.coronawarn.datadonation.services.edus.otp;

import app.coronawarn.datadonation.common.persistence.domain.OneTimePassword;
import app.coronawarn.datadonation.common.persistence.repository.OneTimePasswordRepository;
import app.coronawarn.datadonation.services.edus.config.OtpConfig;
import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.util.Optional;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

@Service
public class OtpService {

  private Logger logger = LoggerFactory.getLogger(OtpService.class);

  private OneTimePasswordRepository dataRepository;
  private OtpConfig otpConfig;

  /**
   * Constructs the OtpService.
   *
   * @param dataRepository a
   * @param otpConfig      b
   */
  public OtpService(
      OneTimePasswordRepository dataRepository,
      OtpConfig otpConfig) {
    this.dataRepository = dataRepository;
    this.otpConfig = otpConfig;
  }

  /**
   * Checks if requested otp exists in the database and is valid.
   *
   * @param otpString String unique id
   * @return true if otp exists and not expired
   */
  public OtpState checkOtpIsValid(String otpString) {
    Optional<OneTimePassword> otpOptional = dataRepository.findById(otpString);
    if (!otpOptional.isPresent()) {
      logger.warn("Otp not found!");
      throw new OtpNotFoundException();
    }
    OneTimePassword otp = otpOptional.get();

    LocalDateTime expirationTime = otp.getCreationTimestamp().plusHours(otpConfig.getOtpValidityInHours());
    boolean isExpired = !expirationTime.isAfter(LocalDateTime.now(ZoneOffset.UTC));
    boolean isRedeemed = otp.getRedemptionTimestamp() != null;

    if (!isRedeemed && !isExpired) {
      return OtpState.VALID;
    }

    if (!isExpired && isRedeemed) {
      return OtpState.REDEEMED;
    }

    if (isExpired && !isRedeemed) {
      return OtpState.EXPIRED;
    }
    return OtpState.REDEEMED;
  }

  /**
   * Returns the state of the otp object.
   *
   * @param otp Otp data id
   * @return OtpStateEnum value
   */
  public OtpState redeemOtp(String otp) {
    OtpState state = checkOtpIsValid(otp);
    if (state.equals(OtpState.VALID)) {
      var otpData = dataRepository.findById(otp).get();
      otpData.setRedemptionTimestamp(LocalDateTime.now(ZoneOffset.UTC));
      dataRepository.save(otpData);
      return OtpState.VALID;
    }
    return state;
  }
}
