package app.coronawarn.datadonation.common.persistence.service;

@SuppressWarnings("serial")
public class OtpNotFoundException extends RuntimeException {

  public OtpNotFoundException(final String otp) {
    // it's save to be logged, since it's coming from OtpRedemptionRequest#otp
    super("OTP '" + otp + "' not found");
  }
}
