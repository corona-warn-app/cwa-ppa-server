package app.coronawarn.datadonation.common.persistence.service;

import app.coronawarn.datadonation.common.persistence.domain.ElsOneTimePassword;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Service;

@Service
public class ElsOtpService extends AbstractOtpService<ElsOneTimePassword> {

  /**
   * Constructs the ElsOtpService.
   *
   * @param otpRepository The OTP Repository.
   */
  protected ElsOtpService(CrudRepository<ElsOneTimePassword, String> otpRepository) {
    super(otpRepository);
  }
}
