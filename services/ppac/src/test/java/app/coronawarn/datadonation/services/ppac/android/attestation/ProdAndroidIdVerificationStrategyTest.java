package app.coronawarn.datadonation.services.ppac.android.attestation;

import app.coronawarn.datadonation.services.ppac.android.attestation.errors.AndroidIdNotValid;
import app.coronawarn.datadonation.services.ppac.config.PpacConfiguration;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.*;
import static org.junit.jupiter.api.Assertions.assertThrows;

public class ProdAndroidIdVerificationStrategyTest {

    ProdAndroidIdVerificationStrategy idVerificationStrategy;

    @BeforeEach
    public void setup() {
        PpacConfiguration ppacConfiguration = new PpacConfiguration();
        PpacConfiguration.Android androidParameters = new PpacConfiguration.Android();
        androidParameters.setSrs(new PpacConfiguration.Android.Srs());
        ppacConfiguration.setAndroid(androidParameters);
        ppacConfiguration.getAndroid().getSrs().setRequireAndroidIdSyntaxCheck(true);
        this.idVerificationStrategy = new ProdAndroidIdVerificationStrategy(ppacConfiguration);
    }

    @Test
    void testValidateAndroidIdShouldFailDueToInvalidId() {
        byte[] testId = new byte[7];
        AndroidIdNotValid exception = assertThrows(AndroidIdNotValid.class, () ->
                idVerificationStrategy.validateAndroidId(testId));
        assertThat(exception.getMessage(), is(not(emptyOrNullString())));
    }

    @Test
    void testValidateAndroidIdShouldPass() {
        byte[] testId = new byte[8];
        idVerificationStrategy.validateAndroidId(testId);
    }
}
