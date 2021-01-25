package app.coronawarn.datadonation.services.edus.otp;

import static app.coronawarn.datadonation.services.edus.utils.StringUtils.asJsonString;
import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.mockito.MockitoAnnotations.openMocks;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.springframework.test.web.servlet.setup.MockMvcBuilders.standaloneSetup;

import app.coronawarn.datadonation.common.persistence.domain.OneTimePassword;
import app.coronawarn.datadonation.common.persistence.repository.OneTimePasswordRepository;
import app.coronawarn.datadonation.services.edus.ServerApplication;
import java.time.LocalDateTime;
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
import org.springframework.web.context.WebApplicationContext;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT,
    classes = {ServerApplication.class})
@DirtiesContext
public class OneTimePasswordValidationTest {

  @MockBean
  OneTimePasswordRepository dataRepository;

  private MockMvc mockMvc;

  @Autowired
  private WebApplicationContext context;

  @Autowired
  private OtpController otpController;

  @Autowired
  private OtpService otpService;

  private static final String VALID_OTP_ID = "fb954b83-02ff-4cb7-8f07-fae2bcd64363";
  private static final String OTP_URL = "/version/v1/otp/validate";

  @BeforeEach
  public void setup() {
    openMocks(this);
    this.mockMvc = standaloneSetup(otpController).setControllerAdvice(new OtpControllerExceptionHandler()).build();
  }

  @Test
  void testOtpStateIsValid() {

    when(dataRepository.findById(any())).thenReturn(Optional.of(new OneTimePassword(VALID_OTP_ID,
        LocalDateTime.now().plusDays(1), null, null)));

    assertThat(otpService.checkOtpIsValid(VALID_OTP_ID)).isEqualTo(OtpState.VALID);
  }

  @Test
  void testOtpStateIsExpired() {

    when(dataRepository.findById(any())).thenReturn(Optional.of(new OneTimePassword(VALID_OTP_ID,
        LocalDateTime.now().minusDays(1), null, null)));

    assertThat(otpService.checkOtpIsValid(VALID_OTP_ID)).isEqualTo(OtpState.EXPIRED);
  }

  @Test
  void testValidOtpShouldReturnResponseStatusCode200() throws Exception {
    OtpRequest validOtpRequest = new OtpRequest();
    validOtpRequest.setOtp(VALID_OTP_ID);

    when(dataRepository.findById(any())).thenReturn(Optional.of(new OneTimePassword(VALID_OTP_ID,
        LocalDateTime.now().plusDays(1), null, null)));

    mockMvc.perform(MockMvcRequestBuilders
        .post(OTP_URL)
        .content(asJsonString(validOtpRequest))
        .contentType(MediaType.APPLICATION_JSON)
        .accept(MediaType.APPLICATION_JSON))
        .andExpect(status().isOk())
        .andExpect(MockMvcResultMatchers.jsonPath("$.state").value("valid"));
  }

  @Test
  void testInvalidOtpRequestShouldReturnResponseStatusCode400() throws Exception {
    OtpRequest otpRequest = new OtpRequest();
    otpRequest.setOtp("invalid_otp_request");

    mockMvc.perform(MockMvcRequestBuilders
        .post(OTP_URL)
        .content(asJsonString(otpRequest))
        .contentType(MediaType.APPLICATION_JSON)
        .accept(MediaType.APPLICATION_JSON))
        .andExpect(status().isBadRequest());
  }

  @Test
  void testMissingOtpInDbShouldReturnResponseStatusCode404() throws Exception {
    OtpRequest otpRequest = new OtpRequest();
    otpRequest.setOtp(VALID_OTP_ID);

    mockMvc.perform(MockMvcRequestBuilders
        .post(OTP_URL)
        .content(asJsonString(otpRequest))
        .contentType(MediaType.APPLICATION_JSON)
        .accept(MediaType.APPLICATION_JSON))
        .andExpect(status().isNotFound());
  }

  @Test
  void testOtpShouldReturnResponseStatusCode400WithStateRedeemWhenOtpWasRedeemed() throws Exception {
    OtpRequest otpRequest = new OtpRequest();
    otpRequest.setOtp(VALID_OTP_ID);

    when(dataRepository.findById(any())).thenReturn(Optional.of(new OneTimePassword(VALID_OTP_ID,
        LocalDateTime.now().plusDays(1), LocalDateTime.now().minusDays(1), null)));

    mockMvc.perform(MockMvcRequestBuilders
        .post(OTP_URL)
        .content(asJsonString(otpRequest))
        .contentType(MediaType.APPLICATION_JSON)
        .accept(MediaType.APPLICATION_JSON))
        .andExpect(status().isBadRequest())
        .andExpect(MockMvcResultMatchers.jsonPath("$.state").value("redeemed"));
  }

  @Test
  void testOtpShouldReturnResponseStatusCode400WithStateRedeemWhenOtpExpiredAndRedeemed() throws Exception {
    OtpRequest otpRequest = new OtpRequest();
    otpRequest.setOtp(VALID_OTP_ID);

    when(dataRepository.findById(any())).thenReturn(Optional.of(new OneTimePassword(VALID_OTP_ID,
        LocalDateTime.now().minusDays(1), LocalDateTime.now(), null)));

    mockMvc.perform(MockMvcRequestBuilders
        .post(OTP_URL)
        .content(asJsonString(otpRequest))
        .contentType(MediaType.APPLICATION_JSON)
        .accept(MediaType.APPLICATION_JSON))
        .andExpect(status().isBadRequest())
        .andExpect(MockMvcResultMatchers.jsonPath("$.state").value("redeemed"));
  }

  @Test
  void testOtpShouldReturnResponseStatusCode400WithStateExpiredWhenOtpExpired() throws Exception {
    OtpRequest otpRequest = new OtpRequest();
    otpRequest.setOtp(VALID_OTP_ID);

    when(dataRepository.findById(any())).thenReturn(Optional.of(new OneTimePassword(VALID_OTP_ID,
        LocalDateTime.now().minusDays(1), null, null)));

    mockMvc.perform(MockMvcRequestBuilders
        .post(OTP_URL)
        .content(asJsonString(otpRequest))
        .contentType(MediaType.APPLICATION_JSON)
        .accept(MediaType.APPLICATION_JSON))
        .andExpect(status().isBadRequest())
        .andExpect(MockMvcResultMatchers.jsonPath("$.state").value("expired"));
  }

  @Test
  void databaseExceptionShouldResponseReturnStatusCode500() throws Exception {
    OtpRequest validOtpRequest = new OtpRequest();
    validOtpRequest.setOtp(VALID_OTP_ID);

    when(dataRepository.findById(any())).thenThrow(new DataAccessResourceFailureException(""));

    mockMvc.perform(MockMvcRequestBuilders
        .post(OTP_URL)
        .content(asJsonString(validOtpRequest))
        .contentType(MediaType.APPLICATION_JSON)
        .accept(MediaType.APPLICATION_JSON))
        .andExpect(status().isInternalServerError());
  }
}
