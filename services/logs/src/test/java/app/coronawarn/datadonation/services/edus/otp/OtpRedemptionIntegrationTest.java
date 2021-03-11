package app.coronawarn.datadonation.services.edus.otp;

import static app.coronawarn.datadonation.services.edus.utils.StringUtils.asJsonString;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.mockito.MockitoAnnotations.openMocks;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.springframework.test.web.servlet.setup.MockMvcBuilders.standaloneSetup;

import app.coronawarn.datadonation.common.config.UrlConstants;
import app.coronawarn.datadonation.common.persistence.domain.ElsOneTimePassword;
import app.coronawarn.datadonation.common.persistence.domain.OneTimePassword;
import app.coronawarn.datadonation.common.persistence.repository.ElsOneTimePasswordRepository;
import app.coronawarn.datadonation.common.persistence.repository.OneTimePasswordRepository;
import java.time.LocalDateTime;
import java.util.Optional;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.dao.DataAccessResourceFailureException;
import org.springframework.http.MediaType;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@DirtiesContext
public class OtpRedemptionIntegrationTest {

  private static final String VALID_UUID = "fb954b83-02ff-4cb7-8f07-fae2bcd64363";
  private static final String LOG_OTP_REDEEM_URL = UrlConstants.SURVEY + UrlConstants.LOG;
  @MockBean
  ElsOneTimePasswordRepository elsOtpRepository;
  @Autowired
  private OtpController otpController;
  private MockMvc mockMvc;

  @BeforeEach
  public void setup() {
    openMocks(this);
    this.mockMvc = standaloneSetup(otpController)
        .setControllerAdvice(new OtpControllerExceptionHandler()).build();
  }

  @Test
  void testShouldReturnResponseStatusCode200AndStateValidWhenLogOtpNotRedeemed() throws Exception {
    OtpRedemptionRequest validOtpRedemptionRequest = new OtpRedemptionRequest();
    validOtpRedemptionRequest.setOtp(VALID_UUID);
    ElsOneTimePassword otp = new ElsOneTimePassword(VALID_UUID);
    otp.setExpirationTimestamp(LocalDateTime.now().plusDays(5));
    when(elsOtpRepository.findById(any())).thenReturn(Optional.of(otp));

    mockMvc.perform(MockMvcRequestBuilders
        .post(LOG_OTP_REDEEM_URL)
        .content(asJsonString(validOtpRedemptionRequest))
        .contentType(MediaType.APPLICATION_JSON)
        .accept(MediaType.APPLICATION_JSON))
        .andExpect(status().isOk())
        .andExpect(MockMvcResultMatchers.jsonPath("$.state").value("valid"));
  }

  @Test
  void testStrongClientIntegrityCheckForIos() throws Exception {
    OtpRedemptionRequest validOtpRedemptionRequest = new OtpRedemptionRequest();
    validOtpRedemptionRequest.setOtp(VALID_UUID);

    ElsOneTimePassword otpWithValidIosStrongIntegrityCheck = createOtp(VALID_UUID, LocalDateTime.now().plusDays(5), null);
    otpWithValidIosStrongIntegrityCheck.setAndroidPpacBasicIntegrity(null);
    otpWithValidIosStrongIntegrityCheck.setAndroidPpacCtsProfileMatch(null);
    otpWithValidIosStrongIntegrityCheck.setAndroidPpacEvaluationTypeHardwareBacked(null);
    otpWithValidIosStrongIntegrityCheck.setAndroidPpacEvaluationTypeBasic(null);

    when(elsOtpRepository.findById(any())).thenReturn(Optional.of(otpWithValidIosStrongIntegrityCheck));

    mockMvc.perform(MockMvcRequestBuilders
        .post(LOG_OTP_REDEEM_URL)
        .content(asJsonString(validOtpRedemptionRequest))
        .contentType(MediaType.APPLICATION_JSON)
        .accept(MediaType.APPLICATION_JSON))
        .andExpect(status().isOk())
        .andExpect(MockMvcResultMatchers.jsonPath("$.state").value("valid"))
        .andExpect(MockMvcResultMatchers.jsonPath("$.strongClientIntegrityCheck").value(true));
  }

  @Test
  void testStrongClientIntegrityCheckForAndroid() throws Exception {
    OtpRedemptionRequest validOtpRedemptionRequest = new OtpRedemptionRequest();
    validOtpRedemptionRequest.setOtp(VALID_UUID);

    ElsOneTimePassword otpWithValidAndroidStrongIntegrityCheck = createOtp(VALID_UUID, LocalDateTime.now().plusDays(5), null);
    otpWithValidAndroidStrongIntegrityCheck.setAndroidPpacBasicIntegrity(true);
    otpWithValidAndroidStrongIntegrityCheck.setAndroidPpacCtsProfileMatch(true);
    otpWithValidAndroidStrongIntegrityCheck.setAndroidPpacEvaluationTypeHardwareBacked(true);

    when(elsOtpRepository.findById(any())).thenReturn(Optional.of(otpWithValidAndroidStrongIntegrityCheck));

    mockMvc.perform(MockMvcRequestBuilders
        .post(LOG_OTP_REDEEM_URL)
        .content(asJsonString(validOtpRedemptionRequest))
        .contentType(MediaType.APPLICATION_JSON)
        .accept(MediaType.APPLICATION_JSON))
        .andExpect(status().isOk())
        .andExpect(MockMvcResultMatchers.jsonPath("$.state").value("valid"))
        .andExpect(MockMvcResultMatchers.jsonPath("$.strongClientIntegrityCheck").value(true));
  }

  @Test
  void testInvalidStrongClientIntegrityCheckForAndroid() throws Exception {
    OtpRedemptionRequest validOtpRedemptionRequest = new OtpRedemptionRequest();
    validOtpRedemptionRequest.setOtp(VALID_UUID);

    ElsOneTimePassword otpWithInvalidAndroidStrongIntegrityCheck = createOtp(VALID_UUID, LocalDateTime.now().plusDays(5), null);
    otpWithInvalidAndroidStrongIntegrityCheck.setAndroidPpacBasicIntegrity(true);
    otpWithInvalidAndroidStrongIntegrityCheck.setAndroidPpacCtsProfileMatch(false);
    otpWithInvalidAndroidStrongIntegrityCheck.setAndroidPpacEvaluationTypeHardwareBacked(false);

    when(elsOtpRepository.findById(any())).thenReturn(Optional.of(otpWithInvalidAndroidStrongIntegrityCheck));

    mockMvc.perform(MockMvcRequestBuilders
        .post(LOG_OTP_REDEEM_URL)
        .content(asJsonString(validOtpRedemptionRequest))
        .contentType(MediaType.APPLICATION_JSON)
        .accept(MediaType.APPLICATION_JSON))
        .andExpect(status().isOk())
        .andExpect(MockMvcResultMatchers.jsonPath("$.state").value("valid"))
        .andExpect(MockMvcResultMatchers.jsonPath("$.strongClientIntegrityCheck").value(false));
  }

  @Test
  void testShouldReturnResponseStatusCodeUuidCaseInsensitive() throws Exception {
    OtpRedemptionRequest validOtpRedemptionRequest = new OtpRedemptionRequest();
    validOtpRedemptionRequest.setOtp(VALID_UUID.toUpperCase());

    when(elsOtpRepository.findById(VALID_UUID.toLowerCase()))
        .thenReturn(Optional.of(createOtp(VALID_UUID.toLowerCase(),
            LocalDateTime.now().plusDays(5), null)));

    mockMvc.perform(MockMvcRequestBuilders
        .post(LOG_OTP_REDEEM_URL)
        .content(asJsonString(validOtpRedemptionRequest))
        .contentType(MediaType.APPLICATION_JSON)
        .accept(MediaType.APPLICATION_JSON))
        .andExpect(status().isOk())
        .andExpect(MockMvcResultMatchers.jsonPath("$.state").value("valid"));

    ArgumentCaptor<ElsOneTimePassword> argument = ArgumentCaptor.forClass(ElsOneTimePassword.class);
    verify(elsOtpRepository, times(1)).save(argument.capture());
    assertEquals(VALID_UUID.toLowerCase(), argument.getValue().getPassword());
  }

  @Test
  void testShouldReturnResponseStatusCode400WhenInvalidRequest() throws Exception {
    OtpRedemptionRequest otpRedemptionRequest = new OtpRedemptionRequest();
    otpRedemptionRequest.setOtp("invalid_otp_payload");

    when(elsOtpRepository.findById(any())).thenReturn(Optional.of(createOtp(VALID_UUID,
        LocalDateTime.now().plusDays(5))));

    mockMvc.perform(MockMvcRequestBuilders
        .post(LOG_OTP_REDEEM_URL)
        .content(asJsonString(otpRedemptionRequest))
        .contentType(MediaType.APPLICATION_JSON)
        .accept(MediaType.APPLICATION_JSON))
        .andExpect(status().isBadRequest());
  }

  @Test
  void testShouldReturnResponseStatusCode400AndOtpStateExpiredWhenExpiredOtp() throws Exception {
    OtpRedemptionRequest validOtpRedemptionRequest = new OtpRedemptionRequest();
    validOtpRedemptionRequest.setOtp(VALID_UUID);

    when(elsOtpRepository.findById(any())).thenReturn(Optional.of(createOtp(VALID_UUID,
        LocalDateTime.now().minusDays(1))));

    mockMvc.perform(MockMvcRequestBuilders
        .post(LOG_OTP_REDEEM_URL)
        .content(asJsonString(validOtpRedemptionRequest))
        .contentType(MediaType.APPLICATION_JSON)
        .accept(MediaType.APPLICATION_JSON))
        .andExpect(status().isBadRequest())
        .andExpect(MockMvcResultMatchers.jsonPath("$.state").value("expired"));
  }

  @Test
  void testShouldReturnResponseStatusCode400AndOtpStateRedeemedWhenAlreadyRedeemed()
      throws Exception {
    OtpRedemptionRequest validOtpRedemptionRequest = new OtpRedemptionRequest();
    validOtpRedemptionRequest.setOtp(VALID_UUID);

    when(elsOtpRepository.findById(any())).thenReturn(Optional.of(createOtp(VALID_UUID,
        LocalDateTime.now().plusDays(5), LocalDateTime.now().minusDays(1))));

    mockMvc.perform(MockMvcRequestBuilders
        .post(LOG_OTP_REDEEM_URL)
        .content(asJsonString(validOtpRedemptionRequest))
        .contentType(MediaType.APPLICATION_JSON)
        .accept(MediaType.APPLICATION_JSON))
        .andExpect(status().isBadRequest())
        .andExpect(MockMvcResultMatchers.jsonPath("$.state").value("redeemed"));
  }

  @Test
  void testShouldReturnResponseStatusCode400AndOtpStateRedeemedWhenRedeemedAndExpired()
      throws Exception {
    OtpRedemptionRequest validOtpRedemptionRequest = new OtpRedemptionRequest();
    validOtpRedemptionRequest.setOtp(VALID_UUID);

    when(elsOtpRepository.findById(any())).thenReturn(Optional.of(createOtp(VALID_UUID,
        LocalDateTime.now().minusDays(1), LocalDateTime.now().minusDays(1))));

    mockMvc.perform(MockMvcRequestBuilders
        .post(LOG_OTP_REDEEM_URL)
        .content(asJsonString(validOtpRedemptionRequest))
        .contentType(MediaType.APPLICATION_JSON)
        .accept(MediaType.APPLICATION_JSON))
        .andExpect(status().isBadRequest())
        .andExpect(MockMvcResultMatchers.jsonPath("$.state").value("redeemed"));
  }

  @Test
  void testShouldReturnResponseStatusCode404WhenOtpNotFound() throws Exception {
    OtpRedemptionRequest otpRedemptionRequest = new OtpRedemptionRequest();
    otpRedemptionRequest.setOtp(VALID_UUID);

    mockMvc.perform(MockMvcRequestBuilders
        .post(LOG_OTP_REDEEM_URL)
        .content(asJsonString(otpRedemptionRequest))
        .contentType(MediaType.APPLICATION_JSON)
        .accept(MediaType.APPLICATION_JSON))
        .andExpect(status().isNotFound());
  }

  @Test
  void databaseExceptionShouldReturnResponseStatusCode500() throws Exception {
    OtpRedemptionRequest validOtpRedemptionRequest = new OtpRedemptionRequest();
    validOtpRedemptionRequest.setOtp(VALID_UUID);

    when(elsOtpRepository.findById(any())).thenThrow(new DataAccessResourceFailureException(""));

    mockMvc.perform(MockMvcRequestBuilders
        .post(LOG_OTP_REDEEM_URL)
        .content(asJsonString(validOtpRedemptionRequest))
        .contentType(MediaType.APPLICATION_JSON)
        .accept(MediaType.APPLICATION_JSON))
        .andExpect(status().isInternalServerError());
  }

  private ElsOneTimePassword createOtp(String uuid, LocalDateTime expirationTime) {
    return createOtp(uuid, expirationTime, null);
  }

  private ElsOneTimePassword createOtp(String uuid, LocalDateTime expirationTime,
      LocalDateTime redemptionTime) {
    ElsOneTimePassword otp = new ElsOneTimePassword(uuid);
    otp.setExpirationTimestamp(expirationTime);
    otp.setRedemptionTimestamp(redemptionTime);
    return otp;
  }
}
