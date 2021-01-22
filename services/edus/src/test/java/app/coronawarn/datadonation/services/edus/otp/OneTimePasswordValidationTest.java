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
import java.time.LocalDate;
import java.util.Objects;
import java.util.Optional;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.dao.DataAccessResourceFailureException;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
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

  private static final String VALID_OTP_ID = "fb954b83-02ff-4cb7-8f07-fae2bcd64363";
  private static final String OTP_URL = "/version/v1/otp/validate";

  @BeforeEach
  public void setup() {
    openMocks(this);
    this.mockMvc = standaloneSetup(otpController).setControllerAdvice(new OtpControllerExceptionHandler()).build();
  }

  @Test
  void testOtpExpirationDateIsInTheFuture() {

    when(dataRepository.findById(any())).thenReturn(Optional.of(new OneTimePassword(VALID_OTP_ID,
        LocalDate.now().plusDays(1), LocalDate.now().plusDays(1), LocalDate.now().plusDays(1))));

    assertThat(otpController.checkOtpIsValid(VALID_OTP_ID)).isTrue();
  }

  @Test
  void testOtpExpirationDateIsInThePast() {

    when(dataRepository.findById(any())).thenReturn(Optional.of(new OneTimePassword(VALID_OTP_ID,
        LocalDate.now().minusDays(1), LocalDate.now().minusDays(1), LocalDate.now().minusDays(1))));

    assertThat(otpController.checkOtpIsValid(VALID_OTP_ID)).isFalse();
  }

  @Test
  void testResponseStatusCodeOkWithValidOtp() {

    when(dataRepository.findById(any())).thenReturn(Optional.of(new OneTimePassword(VALID_OTP_ID,
        LocalDate.now().plusDays(1), LocalDate.now().plusDays(1), LocalDate.now().plusDays(1))));

    ResponseEntity<OtpValidationResponse> otpData = otpController.submitData(new OtpRequest());

    assertThat(otpData.getStatusCode()).isEqualTo(HttpStatus.OK);
    assertThat(Objects.requireNonNull(otpData.getBody()).getValid()).isTrue();
  }

  @Test
  void testResponseStatusCodeOkWithExpiredOtp() {

    when(dataRepository.findById(any())).thenReturn(Optional.of(new OneTimePassword(VALID_OTP_ID,
        LocalDate.now().minusDays(1), LocalDate.now().minusDays(1), LocalDate.now().minusDays(1))));

    ResponseEntity<OtpValidationResponse> otpData = otpController.submitData(new OtpRequest());

    assertThat(otpData.getStatusCode()).isEqualTo(HttpStatus.OK);
    assertThat(Objects.requireNonNull(otpData.getBody()).getValid()).isFalse();
  }

  @Test
  void testWithValidOtpRequestShouldReturnStatusCode200() throws Exception {
    OtpRequest validOtpRequest = new OtpRequest();
    validOtpRequest.setOtp(VALID_OTP_ID);

    when(dataRepository.findById(any())).thenReturn(Optional.of(new OneTimePassword(VALID_OTP_ID,
        LocalDate.now().plusDays(1), LocalDate.now().plusDays(1), LocalDate.now().plusDays(1))));

    mockMvc.perform(MockMvcRequestBuilders
        .post(OTP_URL)
        .content(asJsonString(validOtpRequest))
        .contentType(MediaType.APPLICATION_JSON)
        .accept(MediaType.APPLICATION_JSON))
        .andExpect(status().isOk())
        .andExpect(MockMvcResultMatchers.jsonPath("$.valid").value("true"));
  }

  @Test
  void testWithInvalidOtpRequestShouldReturnStatusCode400() throws Exception {
    OtpRequest validOtpRequest = new OtpRequest();
    validOtpRequest.setOtp("invalid_otp_request");

    mockMvc.perform(MockMvcRequestBuilders
        .post(OTP_URL)
        .content(asJsonString(validOtpRequest))
        .contentType(MediaType.APPLICATION_JSON)
        .accept(MediaType.APPLICATION_JSON))
        .andExpect(status().isBadRequest());
  }


  @Test
  void databaseExceptionShouldReturnStatusCode500() throws Exception {
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
