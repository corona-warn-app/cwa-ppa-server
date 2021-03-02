package app.coronawarn.datadonation.common.persistence.service;

import app.coronawarn.datadonation.common.persistence.domain.OneTimePassword;
import app.coronawarn.datadonation.common.utils.TimeUtils;
import java.time.ZoneOffset;
import java.time.ZonedDateTime;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.repository.CrudRepository;

public abstract class AbstractOtpService<T extends OneTimePassword> {

  private final Logger logger = LoggerFactory.getLogger(AbstractOtpService.class);

  private final CrudRepository<T, String> otpRepository;

  /**
   * Constructs the OtpService.
   *
   * @param otpRepository The OTP Repository.
   */
  protected AbstractOtpService(CrudRepository<T, String> otpRepository) {
    this.otpRepository = otpRepository;
  }

  /**
   * Save a new OneTimePassword or any of it's subtypes and return the expiration time.
   *
   * @return the expiration time.
   */
  public ZonedDateTime createOtp(T otp, int validityInHours) {
    if (!otp.isNew()) {
      throw new OtpStatusException("OTP to create must be new.");
    }

    ZonedDateTime expirationTime = ZonedDateTime.now(ZoneOffset.UTC).plusHours(validityInHours);
    otp.setExpirationTimestamp(expirationTime.toEpochSecond());

    otp.setPassword(otp.getPassword().toLowerCase());
    otpRepository.save(otp);
    return expirationTime;
  }

  /**
   * Redeems the OTP object, if it has state {@link OtpState#VALID}. This means that the redemption timestamp is set to
   * the current timestamp.
   *
   * @param otp The OTP to redeem.
   * @return The {@link OtpState} of the OTP before redemption.
   */
  public OtpState redeemOtp(T otp) {
    OtpState state = getOtpStatus(otp);
    if (state.equals(OtpState.VALID)) {
      otp.setRedemptionTimestamp(TimeUtils.getEpochSecondsForNow());
      otp.setPassword(otp.getPassword().toLowerCase());
      otpRepository.save(otp);
      return getOtpStatus(otp);
    }
    return state;
  }

  /**
   * Fetches and returns the {@link OneTimePassword} or any of it's subtypes with the provided password from the
   * repository.
   *
   * @param password The password/ID of the OTP.
   * @return The {@link OneTimePassword} from the repository (if present) or any of it's subtypes.
   * @throws OtpNotFoundException if no OTP was found.
   */
  public T getOtp(String password) {
    var otp = otpRepository.findById(password.toLowerCase());
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
  public OtpState getOtpStatus(T otp) {
    ZonedDateTime expirationTime = TimeUtils.getZonedDateTimeFor(otp.getExpirationTimestamp());
    boolean isExpired = expirationTime.isBefore(ZonedDateTime.now(ZoneOffset.UTC));

    if (otp.getRedemptionTimestamp() != null) {
      return OtpState.REDEEMED;
    }
    if (isExpired) {
      return OtpState.EXPIRED;
    }
    return OtpState.VALID;
  }

}
