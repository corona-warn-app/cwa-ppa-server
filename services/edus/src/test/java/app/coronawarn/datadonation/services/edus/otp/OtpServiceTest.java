package app.coronawarn.datadonation.services.edus.otp;

import app.coronawarn.datadonation.common.persistence.domain.OneTimePassword;
import app.coronawarn.datadonation.common.persistence.repository.OneTimePasswordRepository;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.context.SpringBootTest;

@SpringBootTest
@AutoConfigureTestDatabase
public class OtpServiceTest {

  @Autowired
  private OtpService otpService;

  @Autowired
  private OneTimePasswordRepository otpRepository;

  @AfterEach
  public void tearDown() {
    otpRepository.deleteAll();
  }

  @Test
  void testRetrievalForEmptyDB() {
    OneTimePassword otp = otpService.createOtp();
  }

}
