package app.coronawarn.datadonation.services.ppac.android.attestation;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.emptyOrNullString;
import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.not;
import static org.junit.jupiter.api.Assertions.assertThrows;

import app.coronawarn.datadonation.services.ppac.android.attestation.errors.AndroidIdNotValid;
import app.coronawarn.datadonation.services.ppac.config.PpacConfiguration;
import java.security.SecureRandom;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

final class ProdAndroidIdVerificationStrategyTest {

  ProdAndroidIdVerificationStrategy idVerificationStrategy;

  @BeforeEach
  public void setup() {
    final PpacConfiguration ppacConfiguration = new PpacConfiguration();
    final PpacConfiguration.Android androidParameters = new PpacConfiguration.Android();
    androidParameters.setSrs(new PpacConfiguration.Android.Srs());
    ppacConfiguration.setAndroid(androidParameters);
    ppacConfiguration.getAndroid().getSrs().setRequireAndroidIdSyntaxCheck(true);
    idVerificationStrategy = new ProdAndroidIdVerificationStrategy(ppacConfiguration);
  }

  @Test
  void testValidateAndroidIdShouldFailDueToInvalidId() {
    AndroidIdNotValid exception = assertThrows(AndroidIdNotValid.class,
        () -> idVerificationStrategy.validateAndroidId(new byte[7])); // too small
    assertThat(exception.getMessage(), is(not(emptyOrNullString())));

    exception = assertThrows(AndroidIdNotValid.class,
        () -> idVerificationStrategy.validateAndroidId(new byte[Long.BYTES * 2 + 1])); // too large
    assertThat(exception.getMessage(), is(not(emptyOrNullString())));
  }

  @Test
  void testValidateAndroidIdShouldPass() throws Exception {
    byte[] testId = new byte[8];
    SecureRandom.getInstanceStrong().nextBytes(testId);
    idVerificationStrategy.validateAndroidId(testId);
    testId = new byte[12];
    SecureRandom.getInstanceStrong().nextBytes(testId);
    idVerificationStrategy.validateAndroidId(testId);
    testId = new byte[16];
    SecureRandom.getInstanceStrong().nextBytes(testId);
    idVerificationStrategy.validateAndroidId(testId);
  }
}
