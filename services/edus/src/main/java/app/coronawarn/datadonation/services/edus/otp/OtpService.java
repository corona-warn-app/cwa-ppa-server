package app.coronawarn.datadonation.services.edus.otp;

import app.coronawarn.datadonation.common.persistence.domain.OneTimePassword;
import app.coronawarn.datadonation.common.persistence.repository.OneTimePasswordRepository;
import app.coronawarn.datadonation.services.edus.config.OtpConfig;
import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.util.Optional;
import org.springframework.stereotype.Service;

@Service
public class OtpService {

  private OneTimePasswordRepository dataRepository;
  private OtpConfig otpConfig;

  public OtpService(
      OneTimePasswordRepository dataRepository,
      OtpConfig otpConfig) {
    this.dataRepository = dataRepository;
    this.otpConfig = otpConfig;
  }

  /**
   * Checks if requested otp exists in the database and is valid.
   *
   * @param otp String unique id
   * @return true if otp exists and not expired
   */
  public boolean checkOtpIsValid(String otpString) {
    Optional<OneTimePassword> otpOptional = dataRepository.findById(otpString);
    OneTimePassword otp = otpOptional.get();
    LocalDateTime expirationTime = otp.getCreationTimestamp().plusHours(otpConfig.getOtpValidityInHours());
    return otp.getRedemptionTimestamp() == null && expirationTime.isAfter(LocalDateTime.now(ZoneOffset.UTC));
  }

  public OtpState redeemOtp(String otp) {
    Optional<OneTimePassword> otpOptional = dataRepository.findById(otp);
    if (otpOptional.isPresent()) {
      return getOtpState(otpOptional.get());
    }
    // TODO: what state if otp not found -> 404
    return OtpState.EXPIRED;
    /*
    boolean isValid = dataRepository.findById(otp).filter(otpData ->
        otpData.getExpirationDate().isAfter(LocalDate.now(ZoneOffset.UTC))).isPresent();
     */
  }

  public OtpState getOtpState(OneTimePassword otp) {
    //boolean isExpired = otp.getRedemptionTime()

    // not redeemed, not expired -> valid
    // not expired, redeemed -> redeemed
    // not redeemed, expired -> expired
    // expired, redeemed -> redeemed
    return OtpState.VALID;
  }
}
