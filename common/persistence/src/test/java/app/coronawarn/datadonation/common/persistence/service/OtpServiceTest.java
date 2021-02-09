package app.coronawarn.datadonation.common.persistence.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatExceptionOfType;
import static org.mockito.AdditionalAnswers.returnsFirstArg;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.reset;
import static org.mockito.Mockito.when;

import app.coronawarn.datadonation.common.persistence.domain.OneTimePassword;
import app.coronawarn.datadonation.common.persistence.repository.OneTimePasswordRepository;
import app.coronawarn.datadonation.common.util.TimeUtils;
import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.util.Optional;
import java.util.UUID;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
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

  private long twoHoursAgo = Instant.now().minusSeconds(60 * 120).getEpochSecond();

  private int validityInHours = 5;

  @AfterEach
  void resetMocks() {
    reset(otpRepository);
  }

  @Test
  void testCreateOtp() {
    when(otpRepository.save(any(OneTimePassword.class))).then(returnsFirstArg());
    LocalDateTime estimatedExpirationTime = LocalDateTime.now(ZoneOffset.UTC).plusHours(validityInHours);
    LocalDateTime expirationTime = otpService.createOtp(generateValidOtp(), validityInHours);
    assertThat(expirationTime).isEqualToIgnoringSeconds(estimatedExpirationTime);
  }

  @Test
  void testThrowsExceptionIfOtpNotFound() {
    when(otpRepository.findById(any())).thenReturn(Optional.empty());
    assertThatExceptionOfType(OtpNotFoundException.class).isThrownBy(
        () -> otpService.getOtp("Invalid OTP")
    );
  }

  @Nested
  @DisplayName("testGetOtpStatus")
  class GetOtpStatus {

    @Test
    void testExpiredNotRedeemed() {
      OneTimePassword otp = generateValidOtp();
      otp.setExpirationTimestamp(twoHoursAgo);

      OtpState state = otpService.getOtpStatus(otp);
      assertThat(state.equals(OtpState.EXPIRED));
    }

    @Test
    void testExpiredRedeemed() {
      OneTimePassword otp = generateValidOtp();
      otp.setExpirationTimestamp(twoHoursAgo);
      otp.setRedemptionTimestamp(twoHoursAgo);

      OtpState state = otpService.getOtpStatus(otp);
      assertThat(state.equals(OtpState.REDEEMED));
    }

    @Test
    void testNotExpiredRedeemed() {
      OneTimePassword otp = generateValidOtp();
      otp.setRedemptionTimestamp(TimeUtils.getEpochSecondsForNow());

      OtpState state = otpService.getOtpStatus(otp);
      assertThat(state.equals(OtpState.REDEEMED));
    }

    @Test
    void testNotExpiredNotRedeemed() {
      OneTimePassword otp = generateValidOtp();

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

      OneTimePassword otp = generateValidOtp();
      otpService.createOtp(otp, validityInHours);
      when(otpRepository.findById(otp.getPassword())).thenReturn(Optional.of(otp));

      OtpState state = otpService.redeemOtp(otp);
      assertThat(state.equals(OtpState.VALID));
    }

    @Test
    void testRedeemExpired() {
      OneTimePassword otp = generateValidOtp();
      otp.setExpirationTimestamp(twoHoursAgo);
      when(otpRepository.findById(otp.getPassword())).thenReturn(Optional.of(otp));

      OtpState state = otpService.redeemOtp(otp);
      assertThat(state.equals(OtpState.EXPIRED));
    }

    @Test
    void testRedeemRedeemed() {
      OneTimePassword otp = generateValidOtp();
      otp.setRedemptionTimestamp(twoHoursAgo);
      when(otpRepository.findById(otp.getPassword())).thenReturn(Optional.of(otp));

      OtpState state = otpService.redeemOtp(otp);
      assertThat(state.equals(OtpState.REDEEMED));
    }
  }

  private OneTimePassword generateValidOtp() {
    return new OneTimePassword(
        UUID.randomUUID().toString(),
        LocalDateTime.now(ZoneOffset.UTC).plusHours(validityInHours));
  }
}
