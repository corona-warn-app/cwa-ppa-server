package app.coronawarn.datadonation.services.ppac.ios.verification.errors;

public class ApiTokenQuotaExceeded extends RuntimeException {

  private static final long serialVersionUID = 7997709572968906061L;

  public ApiTokenQuotaExceeded() {
    super("PPAC failed due to Api Token quota exceeded");
  }
}
