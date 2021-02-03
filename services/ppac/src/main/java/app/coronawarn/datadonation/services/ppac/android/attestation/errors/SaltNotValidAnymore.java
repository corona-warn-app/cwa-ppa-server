package app.coronawarn.datadonation.services.ppac.android.attestation.errors;

import app.coronawarn.datadonation.common.persistence.domain.ppac.android.Salt;

public class SaltNotValidAnymore extends RuntimeException {

  private static final long serialVersionUID = 1710485742505301467L;

  public SaltNotValidAnymore(Salt salt) {
    super("A salt was sent with an expired validity: " + salt);       
  }
}
