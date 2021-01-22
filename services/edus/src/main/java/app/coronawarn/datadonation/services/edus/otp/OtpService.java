package app.coronawarn.datadonation.services.edus.otp;

import app.coronawarn.datadonation.common.persistence.repository.OneTimePasswordRepository;
import org.springframework.stereotype.Service;
import java.time.LocalDate;
import java.time.ZoneOffset;

@Service
public class OtpService {

  OneTimePasswordRepository dataRepository;

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
    return dataRepository.findById(otp).filter(otpData ->
        otpData.getExpirationDate().isAfter(LocalDate.now(ZoneOffset.UTC))).isPresent();
  }

  public OtpState redeemOtp(String otp) {
    boolean isValid = dataRepository.findById(otp).filter(otpData ->
        otpData.getExpirationDate().isAfter(LocalDate.now(ZoneOffset.UTC))).isPresent();
  }
}
