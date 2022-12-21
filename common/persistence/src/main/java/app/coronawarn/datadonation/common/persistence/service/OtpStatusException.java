package app.coronawarn.datadonation.common.persistence.service;

public class OtpStatusException extends RuntimeException {

  private static final long serialVersionUID = -5158809661421626293L;

  public OtpStatusException(final String message) {
    super(message);
  }
}
