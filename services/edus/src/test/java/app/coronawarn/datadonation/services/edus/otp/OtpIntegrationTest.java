package app.coronawarn.datadonation.services.edus.otp;

import static app.coronawarn.datadonation.services.edus.utils.StringUtils.asJsonString;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.mockito.MockitoAnnotations.openMocks;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.springframework.test.web.servlet.setup.MockMvcBuilders.standaloneSetup;
import app.coronawarn.datadonation.common.config.UrlConstants;
import app.coronawarn.datadonation.common.persistence.domain.OneTimePassword;
import app.coronawarn.datadonation.common.persistence.repository.OneTimePasswordRepository;
import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.util.Optional;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
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
public class OtpIntegrationTest {

  private static final String VALID_OTP_ID = "fb954b83-02ff-4cb7-8f07-fae2bcd64363";
  private static final String OTP_REDEEM_URL = UrlConstants.SURVEY + UrlConstants.OTP;
  @MockBean
  OneTimePasswordRepository otpRepository;
  @Autowired
  private OtpController otpController;
  private MockMvc mockMvc;

  @BeforeEach
  public void setup() {
    openMocks(this);
    this.mockMvc = standaloneSetup(otpController).setControllerAdvice(new OtpControllerExceptionHandler()).build();
  }

  private long convertLocalDateTimeToEpochSecond(LocalDateTime localDateTime) {
    return localDateTime.toInstant(ZoneOffset.UTC).getEpochSecond();
  }

  @Test
  void testShouldReturnResponseStatusCode200AndStateValidWhenNotRedeemed() throws Exception {
    OtpRequest validOtpRequest = new OtpRequest();
    validOtpRequest.setOtp(VALID_UUID);

    when(otpRepository.findById(any())).thenReturn(Optional.of(new OneTimePassword(VALID_UUID,
        convertLocalDateTimeToEpochSecond(LocalDateTime.now().plusDays(1)), null)));

    mockMvc.perform(MockMvcRequestBuilders
        .post(OTP_REDEEM_URL)
        .content(asJsonString(validOtpRequest))
        .contentType(MediaType.APPLICATION_JSON)
        .accept(MediaType.APPLICATION_JSON))
        .andExpect(status().isOk())
        .andExpect(MockMvcResultMatchers.jsonPath("$.state").value("redeemed"));
  }

  @Test
  void testShouldReturnResponseStatusCode400WhenInvalidRequest() throws Exception {
    OtpRequest otpRequest = new OtpRequest();
    otpRequest.setOtp("invalid_otp_payload");

    when(otpRepository.findById(any())).thenReturn(Optional.of(new OneTimePassword(VALID_UUID,
        convertLocalDateTimeToEpochSecond(LocalDateTime.now().plusDays(1)),
        convertLocalDateTimeToEpochSecond(LocalDateTime.now().minusDays(1)))));

    mockMvc.perform(MockMvcRequestBuilders
        .post(OTP_REDEEM_URL)
        .content(asJsonString(otpRequest))
        .contentType(MediaType.APPLICATION_JSON)
        .accept(MediaType.APPLICATION_JSON))
        .andExpect(status().isBadRequest());
  }

  @Test
  void testShouldReturnResponseStatusCode400AndOtpStateExpiredWhenExpiredOtp() throws Exception {
    OtpRequest validOtpRequest = new OtpRequest();
    validOtpRequest.setOtp(VALID_UUID);

    when(otpRepository.findById(any())).thenReturn(Optional.of(new OneTimePassword(VALID_UUID,
        convertLocalDateTimeToEpochSecond(LocalDateTime.now().minusDays(1)), null)));

    mockMvc.perform(MockMvcRequestBuilders
        .post(OTP_REDEEM_URL)
        .content(asJsonString(validOtpRequest))
        .contentType(MediaType.APPLICATION_JSON)
        .accept(MediaType.APPLICATION_JSON))
        .andExpect(status().isBadRequest())
        .andExpect(MockMvcResultMatchers.jsonPath("$.state").value("expired"));
  }

  @Test
  void testShouldReturnResponseStatusCode400AndOtpStateRedeemedWhenAlreadyRedeemed() throws Exception {
    OtpRequest validOtpRequest = new OtpRequest();
    validOtpRequest.setOtp(VALID_UUID);

    when(otpRepository.findById(any())).thenReturn(Optional.of(new OneTimePassword(VALID_UUID,
        convertLocalDateTimeToEpochSecond(LocalDateTime.now().plusDays(1)),
        convertLocalDateTimeToEpochSecond(LocalDateTime.now())
    )));

    mockMvc.perform(MockMvcRequestBuilders
        .post(OTP_REDEEM_URL)
        .content(asJsonString(validOtpRequest))
        .contentType(MediaType.APPLICATION_JSON)
        .accept(MediaType.APPLICATION_JSON))
        .andExpect(status().isBadRequest())
        .andExpect(MockMvcResultMatchers.jsonPath("$.state").value("redeemed"));
  }

  @Test
  void testShouldReturnResponseStatusCode400AndOtpStateRedeemedWhenRedeemedAndExpired() throws Exception {
    OtpRequest validOtpRequest = new OtpRequest();
    validOtpRequest.setOtp(VALID_UUID);

    when(otpRepository.findById(any())).thenReturn(Optional.of(new OneTimePassword(VALID_UUID,
        convertLocalDateTimeToEpochSecond(LocalDateTime.now().minusDays(1)),
        convertLocalDateTimeToEpochSecond(LocalDateTime.now())
    )));

    mockMvc.perform(MockMvcRequestBuilders
        .post(OTP_REDEEM_URL)
        .content(asJsonString(validOtpRequest))
        .contentType(MediaType.APPLICATION_JSON)
        .accept(MediaType.APPLICATION_JSON))
        .andExpect(status().isBadRequest())
        .andExpect(MockMvcResultMatchers.jsonPath("$.state").value("redeemed"));
  }

  @Test
  void testShouldReturnResponseStatusCode404WhenOtpNotFound() throws Exception {
    OtpRequest otpRequest = new OtpRequest();
    otpRequest.setOtp(VALID_UUID);

    mockMvc.perform(MockMvcRequestBuilders
        .post(OTP_REDEEM_URL)
        .content(asJsonString(otpRequest))
        .contentType(MediaType.APPLICATION_JSON)
        .accept(MediaType.APPLICATION_JSON))
        .andExpect(status().isNotFound());
  }

  @Test
  void databaseExceptionShouldReturnResponseStatusCode500() throws Exception {
    OtpRequest validOtpRequest = new OtpRequest();
    validOtpRequest.setOtp(VALID_UUID);

    when(otpRepository.findById(any())).thenThrow(new DataAccessResourceFailureException(""));

    mockMvc.perform(MockMvcRequestBuilders
        .post(OTP_REDEEM_URL)
        .content(asJsonString(validOtpRequest))
        .contentType(MediaType.APPLICATION_JSON)
        .accept(MediaType.APPLICATION_JSON))
        .andExpect(status().isInternalServerError());
  }
}
