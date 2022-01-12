package app.coronawarn.datadonation.services.ppac.android.attestation.errors;

import app.coronawarn.datadonation.common.persistence.domain.ppac.android.SaltData;

public class SaltNotValidAnymore extends RuntimeException {

  private static final long serialVersionUID = 1710485742505301467L;

  public SaltNotValidAnymore(SaltData saltData) {
    super("A salt was sent with an expired validity: " + saltData);
  }
}
