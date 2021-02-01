package app.coronawarn.datadonation.services.edus.otp;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatExceptionOfType;
import static org.mockito.AdditionalAnswers.returnsFirstArg;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.reset;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.when;

import app.coronawarn.datadonation.common.persistence.domain.OneTimePassword;
import app.coronawarn.datadonation.common.persistence.repository.OneTimePasswordRepository;
import app.coronawarn.datadonation.services.edus.utils.TimeUtils;
import java.time.Instant;
import java.util.Optional;
import java.util.UUID;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.annotation.DirtiesContext;

@SpringBootTest
@DirtiesContext
public class OtpServiceTest {

  @Autowired
  private OtpService otpService;

  @MockBean
  private OneTimePasswordRepository otpRepository;

  @AfterEach
  void resetMocks() {
    reset(otpRepository);
  }

  @Test
  void testCreateOtp() {
    when(otpRepository.save(any(OneTimePassword.class))).then(returnsFirstArg());
    OneTimePassword otp = otpService.createOtp();
    assertThat(otp).isNotNull();
  }

  @Test
  void testValidityCheckTimestampIsUpdated() {
    OneTimePassword otp = generateValidOTP();
    OneTimePassword otpSpy = Mockito.spy(otp);
    when(otpRepository.findById(otpSpy.getPassword())).thenReturn(Optional.of(otpSpy));
    when(otpRepository.save(any(OneTimePassword.class))).then(returnsFirstArg());

    /*
    setLastValidityCheckTimestamp is called twice.
    The first time to determine whether the opt is valid.
    The second time to determine the state that shall be returned.
     */
    OtpState state = otpService.redeemOtp(otpSpy.getPassword()).getState();
    assertThat(state.equals(OtpState.REDEEMED));
    Mockito.verify(otpSpy, times(2)).
        setLastValidityCheckTimestamp(any());

    state = otpService.redeemOtp(otpSpy.getPassword()).getState();
    assertThat(state.equals(OtpState.REDEEMED));
    Mockito.verify(otpSpy, times(3)).
        setLastValidityCheckTimestamp(any());
  }

  @Test
  void testThrowsExceptionIfOtpNotFound() {
    when(otpRepository.findById(any())).thenReturn(Optional.empty());
    assertThatExceptionOfType(OtpNotFoundException.class).isThrownBy(
        () -> otpService.redeemOtp("Invalid OTP")
    );

  }

  @Nested
  @DisplayName("testGetOtpStatus")
  class GetOtpStatus {

    long twoHoursAgo = Instant.now().minusSeconds(60 * 120).toEpochMilli();

    @Test
    void testExpiredNotRedeemed() {
      OneTimePassword otp = generateValidOTP();
      otp.setCreationTimestamp(twoHoursAgo);

      OtpState state = otpService.getOtpStatus(otp);
      assertThat(state.equals(OtpState.EXPIRED));
    }

    @Test
    void testExpiredRedeemed() {
      OneTimePassword otp = generateValidOTP();
      otp.setCreationTimestamp(twoHoursAgo);
      otp.setRedemptionTimestamp(twoHoursAgo);

      OtpState state = otpService.getOtpStatus(otp);
      assertThat(state.equals(OtpState.REDEEMED));
    }

    @Test
    void testNotExpiredRedeemed() {
      OneTimePassword otp = generateValidOTP();
      otp.setRedemptionTimestamp(TimeUtils.getEpochSecondsForNow());

      OtpState state = otpService.getOtpStatus(otp);
      assertThat(state.equals(OtpState.REDEEMED));
    }

    @Test
    void testNotExpiredNotRedeemed() {
      OneTimePassword otp = generateValidOTP();

      OtpState state = otpService.getOtpStatus(otp);
      assertThat(state.equals(OtpState.VALID));
    }

  }

  @Nested
  @DisplayName("testRedeemOtp")
  class RedeemOtp {

    @Test
    void testRedeemValid() {
      when(otpRepository.save(any(OneTimePassword.class))).then(returnsFirstArg());
      OneTimePassword otp = otpService.createOtp();
      when(otpRepository.findById(otp.getPassword())).thenReturn(Optional.of(otp));

      OtpState state = otpService.redeemOtp(otp.getPassword()).getState();
      assertThat(state.equals(OtpState.VALID));
    }

    @Test
    void testRedeemExpired() {
      OneTimePassword otp = generateValidOTP();
      long twoHoursAgo = Instant.now().minusSeconds(60 * 120).toEpochMilli();
      otp.setCreationTimestamp(twoHoursAgo);
      when(otpRepository.findById(otp.getPassword())).thenReturn(Optional.of(otp));

      OtpState state = otpService.redeemOtp(otp.getPassword()).getState();
      assertThat(state.equals(OtpState.EXPIRED));
    }

    @Test
    void testRedeemRedeemed() {
      OneTimePassword otp = generateValidOTP();
      otp.setRedemptionTimestamp(TimeUtils.getEpochSecondsForNow());
      when(otpRepository.findById(otp.getPassword())).thenReturn(Optional.of(otp));

      OtpState state = otpService.redeemOtp(otp.getPassword()).getState();
      assertThat(state.equals(OtpState.REDEEMED));
    }
  }

  OneTimePassword generateValidOTP() {
    return new OneTimePassword(
        UUID.randomUUID().toString(), TimeUtils.getEpochSecondsForNow());
  }

}
