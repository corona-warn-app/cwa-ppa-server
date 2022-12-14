package app.coronawarn.datadonation.services.srs.otp;

import static app.coronawarn.datadonation.common.config.UrlConstants.SRS;
import static app.coronawarn.datadonation.common.config.UrlConstants.SRS_VERIFY;
import static app.coronawarn.datadonation.services.srs.otp.StringUtils.asJsonString;
import static java.time.LocalDateTime.now;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.mockito.MockitoAnnotations.openMocks;
import static org.springframework.boot.test.context.SpringBootTest.WebEnvironment.RANDOM_PORT;
import static org.springframework.http.MediaType.APPLICATION_JSON;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.springframework.test.web.servlet.setup.MockMvcBuilders.standaloneSetup;

import app.coronawarn.datadonation.common.persistence.domain.SrsOneTimePassword;
import app.coronawarn.datadonation.common.persistence.repository.SrsOneTimePasswordRepository;
import java.time.LocalDateTime;
import java.util.Optional;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.dao.DataAccessResourceFailureException;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.web.servlet.MockMvc;

@SpringBootTest(webEnvironment = RANDOM_PORT)
@DirtiesContext
class OtpRedemptionIntegrationTest {

  private static final String VALID_UUID = "fb954b83-02ff-4cb7-8f07-fae2bcd64363";
  @MockBean
  SrsOneTimePasswordRepository srsOtpRepository;
  @Autowired
  private SrsOtpController srsOtpController;
  private MockMvc mockMvc;

  private SrsOneTimePassword createOtp(final String uuid, final LocalDateTime expirationTime) {
    return createOtp(uuid, expirationTime, null);
  }

  private SrsOneTimePassword createOtp(final String uuid, final LocalDateTime expirationTime,
      final LocalDateTime redemptionTime) {
    final SrsOneTimePassword otp = new SrsOneTimePassword(uuid);
    otp.setExpirationTimestamp(expirationTime);
    otp.setRedemptionTimestamp(redemptionTime);
    return otp;
  }

  @Test
  void databaseExceptionShouldReturnResponseStatusCode500() throws Exception {
    final SrsOtpRedemptionRequest otpRedemptionRequest = new SrsOtpRedemptionRequest();
    otpRedemptionRequest.setOtp(VALID_UUID);

    when(srsOtpRepository.findById(any())).thenThrow(new DataAccessResourceFailureException(""));

    mockMvc.perform(post(SRS_VERIFY + SRS).content(asJsonString(otpRedemptionRequest))
        .contentType(APPLICATION_JSON).accept(APPLICATION_JSON)).andExpect(status().isInternalServerError());
  }

  @BeforeEach
  public void setup() {
    openMocks(this);
    mockMvc = standaloneSetup(srsOtpController).setControllerAdvice(new SrsOtpControllerExceptionHandler()).build();
  }

  @Test
  void testInvalidStrongClientIntegrityCheckForAndroid() throws Exception {
    final SrsOtpRedemptionRequest otpRedemptionRequest = new SrsOtpRedemptionRequest();
    otpRedemptionRequest.setOtp(VALID_UUID);

    final SrsOneTimePassword otpWithInvalidAndroidStrongIntegrityCheck = createOtp(VALID_UUID, now().plusDays(5), null);
    otpWithInvalidAndroidStrongIntegrityCheck.setAndroidPpacBasicIntegrity(true);
    otpWithInvalidAndroidStrongIntegrityCheck.setAndroidPpacCtsProfileMatch(false);
    otpWithInvalidAndroidStrongIntegrityCheck.setAndroidPpacEvaluationTypeHardwareBacked(false);

    when(srsOtpRepository.findById(any())).thenReturn(Optional.of(otpWithInvalidAndroidStrongIntegrityCheck));

    mockMvc
        .perform(post(SRS_VERIFY + SRS).content(asJsonString(otpRedemptionRequest))
            .contentType(APPLICATION_JSON).accept(APPLICATION_JSON))
        .andExpect(status().isOk()).andExpect(jsonPath("$.state").value("valid"))
        .andExpect(jsonPath("$.strongClientIntegrityCheck").value(false));
  }

  @Test
  void testShouldReturnResponseStatusCode200AndStateValidWhenOtpNotRedeemed() throws Exception {
    final SrsOtpRedemptionRequest otpRedemptionRequest = new SrsOtpRedemptionRequest();
    otpRedemptionRequest.setOtp(VALID_UUID);
    final SrsOneTimePassword otp = new SrsOneTimePassword(VALID_UUID);
    otp.setExpirationTimestamp(now().plusDays(5));
    when(srsOtpRepository.findById(any())).thenReturn(Optional.of(otp));

    mockMvc
        .perform(post(SRS_VERIFY + SRS).content(asJsonString(otpRedemptionRequest))
            .contentType(APPLICATION_JSON).accept(APPLICATION_JSON))
        .andExpect(status().isOk()).andExpect(jsonPath("$.state").value("valid"));
  }

  @Test
  void testShouldReturnResponseStatusCode400AndOtpStateExpiredWhenExpiredOtp() throws Exception {
    final SrsOtpRedemptionRequest otpRedemptionRequest = new SrsOtpRedemptionRequest();
    otpRedemptionRequest.setOtp(VALID_UUID);

    when(srsOtpRepository.findById(any())).thenReturn(Optional.of(createOtp(VALID_UUID, now().minusDays(1))));

    mockMvc
        .perform(post(SRS_VERIFY + SRS).content(asJsonString(otpRedemptionRequest))
            .contentType(APPLICATION_JSON).accept(APPLICATION_JSON))
        .andExpect(status().isOk()).andExpect(jsonPath("$.state").value("expired"));
  }

  @Test
  void testShouldReturnResponseStatusCode400AndOtpStateRedeemedWhenAlreadyRedeemed() throws Exception {
    final SrsOtpRedemptionRequest otpRedemptionRequest = new SrsOtpRedemptionRequest();
    otpRedemptionRequest.setOtp(VALID_UUID);

    when(srsOtpRepository.findById(any()))
        .thenReturn(Optional.of(createOtp(VALID_UUID, now().plusDays(5), now().minusDays(1))));

    mockMvc
        .perform(post(SRS_VERIFY + SRS).content(asJsonString(otpRedemptionRequest))
            .contentType(APPLICATION_JSON).accept(APPLICATION_JSON))
        .andExpect(status().isOk()).andExpect(jsonPath("$.state").value("redeemed"));
  }

  @Test
  void testShouldReturnResponseStatusCode400AndOtpStateRedeemedWhenRedeemedAndExpired() throws Exception {
    final SrsOtpRedemptionRequest otpRedemptionRequest = new SrsOtpRedemptionRequest();
    otpRedemptionRequest.setOtp(VALID_UUID);

    when(srsOtpRepository.findById(any()))
        .thenReturn(Optional.of(createOtp(VALID_UUID, now().minusDays(1), now().minusDays(1))));

    mockMvc
        .perform(post(SRS_VERIFY + SRS).content(asJsonString(otpRedemptionRequest))
            .contentType(APPLICATION_JSON).accept(APPLICATION_JSON))
        .andExpect(status().isOk()).andExpect(jsonPath("$.state").value("redeemed"));
  }

  @Test
  void testShouldReturnResponseStatusCode400WhenInvalidRequest() throws Exception {
    final SrsOtpRedemptionRequest otpRedemptionRequest = new SrsOtpRedemptionRequest();
    otpRedemptionRequest.setOtp("invalid_otp_payload");

    when(srsOtpRepository.findById(any())).thenReturn(Optional.of(createOtp(VALID_UUID, now().plusDays(5))));

    mockMvc.perform(post(SRS_VERIFY + SRS).content(asJsonString(otpRedemptionRequest)).contentType(APPLICATION_JSON)
        .accept(APPLICATION_JSON)).andExpect(status().isBadRequest());
  }

  @Test
  void testShouldReturnResponseStatusCode404WhenOtpNotFound() throws Exception {
    final SrsOtpRedemptionRequest otpRedemptionRequest = new SrsOtpRedemptionRequest();
    otpRedemptionRequest.setOtp(VALID_UUID);

    mockMvc
        .perform(post(SRS_VERIFY + SRS).content(asJsonString(otpRedemptionRequest))
            .contentType(APPLICATION_JSON).accept(APPLICATION_JSON))
        .andExpect(status().isNotFound());
  }

  @Test
  void testShouldReturnResponseStatusCodeUuidCaseInsensitive() throws Exception {
    final SrsOtpRedemptionRequest otpRedemptionRequest = new SrsOtpRedemptionRequest();
    otpRedemptionRequest.setOtp(VALID_UUID.toUpperCase());

    when(srsOtpRepository.findById(VALID_UUID.toLowerCase()))
        .thenReturn(Optional.of(createOtp(VALID_UUID.toLowerCase(), now().plusDays(5), null)));

    mockMvc
        .perform(post(SRS_VERIFY + SRS).content(asJsonString(otpRedemptionRequest))
            .contentType(APPLICATION_JSON).accept(APPLICATION_JSON))
        .andExpect(status().isOk()).andExpect(jsonPath("$.state").value("valid"));

    final ArgumentCaptor<SrsOneTimePassword> argument = ArgumentCaptor.forClass(SrsOneTimePassword.class);
    verify(srsOtpRepository, times(1)).save(argument.capture());
    assertEquals(VALID_UUID.toLowerCase(), argument.getValue().getPassword());
  }

  @Test
  void testStrongClientIntegrityCheckForAndroid() throws Exception {
    final SrsOtpRedemptionRequest otpRedemptionRequest = new SrsOtpRedemptionRequest();
    otpRedemptionRequest.setOtp(VALID_UUID);

    final SrsOneTimePassword otpWithValidAndroidStrongIntegrityCheck = createOtp(VALID_UUID, now().plusDays(5), null);
    otpWithValidAndroidStrongIntegrityCheck.setAndroidPpacBasicIntegrity(true);
    otpWithValidAndroidStrongIntegrityCheck.setAndroidPpacCtsProfileMatch(true);
    otpWithValidAndroidStrongIntegrityCheck.setAndroidPpacEvaluationTypeHardwareBacked(true);

    when(srsOtpRepository.findById(any())).thenReturn(Optional.of(otpWithValidAndroidStrongIntegrityCheck));

    mockMvc
        .perform(post(SRS_VERIFY + SRS).content(asJsonString(otpRedemptionRequest)).contentType(APPLICATION_JSON)
            .accept(APPLICATION_JSON))
        .andExpect(status().isOk()).andExpect(jsonPath("$.state").value("valid"))
        .andExpect(jsonPath("$.strongClientIntegrityCheck").value(true));
  }

  @Test
  void testStrongClientIntegrityCheckForIos() throws Exception {
    final SrsOtpRedemptionRequest otpRedemptionRequest = new SrsOtpRedemptionRequest();
    otpRedemptionRequest.setOtp(VALID_UUID);

    final SrsOneTimePassword otpWithValidIosStrongIntegrityCheck = createOtp(VALID_UUID, now().plusDays(5), null);
    otpWithValidIosStrongIntegrityCheck.setAndroidPpacBasicIntegrity(null);
    otpWithValidIosStrongIntegrityCheck.setAndroidPpacCtsProfileMatch(null);
    otpWithValidIosStrongIntegrityCheck.setAndroidPpacEvaluationTypeHardwareBacked(null);
    otpWithValidIosStrongIntegrityCheck.setAndroidPpacEvaluationTypeBasic(null);

    when(srsOtpRepository.findById(any())).thenReturn(Optional.of(otpWithValidIosStrongIntegrityCheck));

    mockMvc
        .perform(post(SRS_VERIFY + SRS).content(asJsonString(otpRedemptionRequest)).contentType(APPLICATION_JSON)
            .accept(APPLICATION_JSON))
        .andExpect(status().isOk()).andExpect(jsonPath("$.state").value("valid"))
        .andExpect(jsonPath("$.strongClientIntegrityCheck").value(true));
  }
}
