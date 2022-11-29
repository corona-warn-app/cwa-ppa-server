package app.coronawarn.datadonation.common.persistence.service;

import app.coronawarn.datadonation.common.persistence.domain.OneTimePassword;
import app.coronawarn.datadonation.common.persistence.repository.OneTimePasswordRepository;
import app.coronawarn.datadonation.common.utils.TimeUtils;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.annotation.DirtiesContext;

import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.time.ZonedDateTime;
import java.util.Optional;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatExceptionOfType;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.AdditionalAnswers.returnsFirstArg;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@SpringBootTest
@DirtiesContext
class OtpServiceTest {

  @Autowired
  private OtpService otpService;

  @MockBean
  private OneTimePasswordRepository otpRepository;

  private static final long TWO_HOURS_AGO = Instant.now().minusSeconds(60 * 120).getEpochSecond();

  private static final int VALIDITY_IN_HOURS = 5;
  private static final int VALIDITY_IN_MINUTES = 60;

  @AfterEach
  void resetMocks() {
    reset(otpRepository);
  }

  @Test
  void testCreateOtp() {
    when(otpRepository.save(any(OneTimePassword.class))).then(returnsFirstArg());
    ZonedDateTime estimatedExpirationTime = ZonedDateTime.now(ZoneOffset.UTC)
        .plusHours(VALIDITY_IN_HOURS);
    ZonedDateTime expirationTime = otpService.createOtp(generateValidOtp(), VALIDITY_IN_HOURS);
    assertThat(expirationTime).isEqualToIgnoringSeconds(estimatedExpirationTime);
  }

  @Test
  void testCreateMinuteOtp() {
    when(otpRepository.save(any(OneTimePassword.class))).then(returnsFirstArg());
    ZonedDateTime estimatedExpirationTime = ZonedDateTime.now(ZoneOffset.UTC)
            .plusMinutes(VALIDITY_IN_MINUTES);
    ZonedDateTime expirationTime = otpService.createMinuteOtp(generateValidOtpMinutes(), VALIDITY_IN_MINUTES);
    assertThat(expirationTime).isEqualToIgnoringSeconds(estimatedExpirationTime);
  }

  @Test
  void testGetOtp() {
    when(otpRepository.findById(any())).thenReturn(Optional.of(generateValidOtp()));
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
  class GetOtpStatusTest {

    @Test
    void testExpiredNotRedeemed() {
      OneTimePassword otp = generateValidOtp();
      otp.setExpirationTimestamp(TWO_HOURS_AGO);

      OtpState state = otpService.getOtpStatus(otp);
      assertThat(state).isEqualTo(OtpState.EXPIRED);
    }

    @Test
    void testExpiredRedeemed() {
      OneTimePassword otp = generateValidOtp();
      otp.setExpirationTimestamp(TWO_HOURS_AGO);
      otp.setRedemptionTimestamp(TWO_HOURS_AGO);

      OtpState state = otpService.getOtpStatus(otp);
      assertThat(state).isEqualTo(OtpState.REDEEMED);
    }

    @Test
    void testNotExpiredRedeemed() {
      OneTimePassword otp = generateValidOtp();
      otp.setRedemptionTimestamp(TimeUtils.getEpochSecondsForNow());

      OtpState state = otpService.getOtpStatus(otp);
      assertThat(state).isEqualTo(OtpState.REDEEMED);
    }

    @Test
    void testNotExpiredNotRedeemed() {
      OneTimePassword otp = generateValidOtp();

      OtpState state = otpService.getOtpStatus(otp);
      assertThat(state).isEqualTo(OtpState.VALID);
    }
  }

  @Nested
  @DisplayName("testRedeemOtp")
  class RedeemOtpTest {

    @Test
    void testRedeemValid() {
      when(otpRepository.save(any(OneTimePassword.class))).then(returnsFirstArg());

      OneTimePassword otp = generateValidOtp();
      otpService.createOtp(otp, VALIDITY_IN_HOURS);
      when(otpRepository.findById(otp.getPassword())).thenReturn(Optional.of(otp));

      OtpState state = otpService.redeemOtp(otp);
      assertThat(state).isEqualTo(OtpState.REDEEMED);
    }

    @Test
    void testRedemptionIsCaseInsensitive() {
      OneTimePassword otp = generateValidOtp();
      otp.setPassword(otp.getPassword().toUpperCase());
      when(otpRepository.save(otp)).thenReturn(otp);
      when(otpRepository.findById(otp.getPassword())).thenReturn(Optional.of(otp));

      OtpState state = otpService.redeemOtp(otp);
      assertThat(state).isEqualTo(OtpState.REDEEMED);

      ArgumentCaptor<OneTimePassword> argument = ArgumentCaptor.forClass(OneTimePassword.class);
      verify(otpRepository, times(1)).save(argument.capture());
      assertEquals(otp.getPassword().toLowerCase(), argument.getValue().getPassword());
    }

    @Test
    void testRedeemExpired() {
      OneTimePassword otp = generateValidOtp();
      otp.setExpirationTimestamp(TWO_HOURS_AGO);
      when(otpRepository.findById(otp.getPassword())).thenReturn(Optional.of(otp));

      OtpState state = otpService.redeemOtp(otp);
      assertThat(state).isEqualTo(OtpState.EXPIRED);
    }

    @Test
    void testRedeemRedeemed() {
      OneTimePassword otp = generateValidOtp();
      otp.setRedemptionTimestamp(TWO_HOURS_AGO);
      when(otpRepository.findById(otp.getPassword())).thenReturn(Optional.of(otp));

      OtpState state = otpService.redeemOtp(otp);
      assertThat(state).isEqualTo(OtpState.REDEEMED);
    }
  }

  private OneTimePassword generateValidOtp() {
    OneTimePassword otp = new OneTimePassword(UUID.randomUUID().toString());
    otp.setExpirationTimestamp(LocalDateTime.now(ZoneOffset.UTC).plusHours(VALIDITY_IN_HOURS));
    return otp;
  }

  private OneTimePassword generateValidOtpMinutes() {
    OneTimePassword otp = new OneTimePassword(UUID.randomUUID().toString());
    otp.setExpirationTimestamp(LocalDateTime.now(ZoneOffset.UTC).plusMinutes(VALIDITY_IN_MINUTES));
    return otp;
  }
}
