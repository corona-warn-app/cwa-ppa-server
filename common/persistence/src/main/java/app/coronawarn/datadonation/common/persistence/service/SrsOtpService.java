package app.coronawarn.datadonation.common.persistence.service;

import app.coronawarn.datadonation.common.persistence.domain.SrsOneTimePassword;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Service;

@Service
public class SrsOtpService extends AbstractOtpService<SrsOneTimePassword> {

  /**
   * Constructs the SrsOtpService.
   *
   * @param otpRepository The SRS OTP Repository.
   */
  protected SrsOtpService(final CrudRepository<SrsOneTimePassword, String> otpRepository) {
    super(otpRepository);
  }
}
