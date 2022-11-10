package app.coronawarn.datadonation.services.ppac.android.attestation;

import static java.lang.Boolean.TRUE;

import app.coronawarn.datadonation.common.protocols.internal.ppdd.PPACAndroid;
import app.coronawarn.datadonation.services.ppac.android.attestation.errors.AndroidIdNotValid;
import app.coronawarn.datadonation.services.ppac.android.attestation.errors.MissingMandatoryAuthenticationFields;
import app.coronawarn.datadonation.services.ppac.config.PpacConfiguration;
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
   * @param ppacAndroid to be validated.
   */
  @Override
  public void validateAndroidId(final PPACAndroid ppacAndroid) {
    if (ObjectUtils.isEmpty(ppacAndroid.getAndroidId())) {
      throw new MissingMandatoryAuthenticationFields("No Android ID received");
    }
    if (TRUE.equals(appParameters.getAndroid().getSrs().getRequireAndroidIdSyntaxCheck())
        && ppacAndroid.getAndroidId().size() != 8) {
      throw new AndroidIdNotValid();
    }
  }
}
