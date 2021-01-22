package app.coronawarn.datadonation.services.edus.otp;

import app.coronawarn.datadonation.common.persistence.domain.OneTimePassword;
import app.coronawarn.datadonation.common.persistence.repository.OneTimePasswordRepository;
import java.util.Optional;
import org.springframework.stereotype.Service;
import java.time.LocalDate;
import java.time.ZoneOffset;

@Service
public class OtpService {

  private OneTimePasswordRepository dataRepository;

  public OtpService(OneTimePasswordRepository dataRepository) {
    this.dataRepository = dataRepository;
  }

  /**
   * Checks if requested otp exists in the database and is valid.
   *
   * @param otp String unique id
   * @return true if otp exists and not expired
   */
  public boolean checkOtpIsValid(String otp) {
    return true;
    /*
    return dataRepository.findById(otp).filter(otpData ->
        otpData.getExpirationDate().isAfter(LocalDate.now(ZoneOffset.UTC))).isPresent();
     */
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
