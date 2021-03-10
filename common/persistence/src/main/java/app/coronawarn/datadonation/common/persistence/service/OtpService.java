package app.coronawarn.datadonation.common.persistence.service;

import app.coronawarn.datadonation.common.persistence.domain.OneTimePassword;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Service;

@Service
public class OtpService extends AbstractOtpService<OneTimePassword> {

  /**
   * Constructs the OtpService.
   *
   * @param otpRepository The OTP Repository.
   */
  protected OtpService(CrudRepository<OneTimePassword, String> otpRepository) {
    super(otpRepository);
  }
}
