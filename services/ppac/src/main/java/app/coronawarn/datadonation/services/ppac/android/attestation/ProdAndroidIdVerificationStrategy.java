package app.coronawarn.datadonation.services.ppac.android.attestation;

import static java.lang.Boolean.TRUE;

import app.coronawarn.datadonation.services.ppac.android.attestation.errors.AndroidIdNotValid;
import app.coronawarn.datadonation.services.ppac.android.attestation.errors.MissingMandatoryAuthenticationFields;
import app.coronawarn.datadonation.services.ppac.config.PpacConfiguration;
import com.google.protobuf.ByteString;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Component;
import org.springframework.util.ObjectUtils;

@Component
@Profile("!loadtest")
public class ProdAndroidIdVerificationStrategy implements AndroidIdVerificationStrategy {

  private final PpacConfiguration appParameters;

  /**
   * Just constructs an instance.
   */
  public ProdAndroidIdVerificationStrategy(final PpacConfiguration appParameters) {
    this.appParameters = appParameters;
  }

  /**
   * Verify that the given android id has the correct size.
   *
   * @param androidId to be validated.
   */
  @Override
  public void validateAndroidId(final ByteString androidId) {
    if (ObjectUtils.isEmpty(androidId)) {
      throw new MissingMandatoryAuthenticationFields("No android id received");
    }
    if (TRUE.equals(appParameters.getAndroid().getSrs().getRequireAndroidIdSyntaxCheck())
        && androidId.size() != 8) {
      throw new AndroidIdNotValid();
    }
  }
}
