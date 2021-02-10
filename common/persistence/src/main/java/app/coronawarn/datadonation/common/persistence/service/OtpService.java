package app.coronawarn.datadonation.common.persistence.service;

import app.coronawarn.datadonation.common.persistence.domain.OneTimePassword;
import app.coronawarn.datadonation.common.persistence.repository.OneTimePasswordRepository;
import app.coronawarn.datadonation.common.util.TimeUtils;
import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.time.ZonedDateTime;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

@Service
public class OtpService {

  private final Logger logger = LoggerFactory.getLogger(OtpService.class);

  private final OneTimePasswordRepository otpRepository;

  /**
   * Constructs the OtpService.
   *
   * @param otpRepository The OTP Repository.
   */
  public OtpService(OneTimePasswordRepository otpRepository) {
    this.otpRepository = otpRepository;
  }

  /**
   * Save a new OneTimePassword and return the expiration time.
   *
   * @return the expiration time.
   */
  public ZonedDateTime createOtp(OneTimePassword otp, int validityInHours) {
    if (!otp.isNew()) {
      throw new OtpStatusException("OTP to create must be new.");
    }

    ZonedDateTime expirationTime = ZonedDateTime.now(ZoneOffset.UTC).plusHours(validityInHours);
    otp.setExpirationTimestamp(expirationTime.toEpochSecond());

    otpRepository.save(otp);
    return expirationTime;
  }

  /**
   * Redeems the OTP object, if it has state {@link OtpState#VALID}. This means that the redemption
   * timestamp is set to the current timestamp.
   *
   * @param otp The OTP to redeem.
   * @return The {@link OtpState} of the OTP before redemption.
   */
  public OtpState redeemOtp(OneTimePassword otp) {
    OtpState state = getOtpStatus(otp);
    if (state.equals(OtpState.VALID)) {
      otp.setRedemptionTimestamp(TimeUtils.getEpochSecondsForNow());
      otpRepository.save(otp);
      return getOtpStatus(otp);
    }
    return state;
  }

  /**
   * Fetches and returns the {@link OneTimePassword} with the provided password from the
   * repository.
   *
   * @param password The password/ID of the OTP.
   * @return The {@link OneTimePassword} from the repository (if present).
   * @throws OtpNotFoundException if no OTP was found.
   */
  public OneTimePassword getOtp(String password) {
    var otp = otpRepository.findById(password);
    if (otp.isPresent()) {
      return otp.get();
    } else {
      logger.warn("OTP not found.");
      throw new OtpNotFoundException();
    }
  }

  /**
   * Calculates and returns the {@link OtpState} of the provided OTP.
   *
   * @param otp The OTP.
   * @return The {@link OtpState} of the provided OTP.
   */
  public OtpState getOtpStatus(OneTimePassword otp) {
    LocalDateTime expirationTime = TimeUtils.getLocalDateTimeFor(otp.getExpirationTimestamp());
    boolean isExpired = expirationTime.isBefore(LocalDateTime.now(ZoneOffset.UTC));

    if (otp.getRedemptionTimestamp() != null) {
      return OtpState.REDEEMED;
    }
    if (isExpired) {
      return OtpState.EXPIRED;
    }
    return OtpState.VALID;
  }

}
