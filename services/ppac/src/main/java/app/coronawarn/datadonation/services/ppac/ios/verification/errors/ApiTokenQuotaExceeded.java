package app.coronawarn.datadonation.services.ppac.ios.verification.errors;

public class ApiTokenQuotaExceeded extends RuntimeException {

  public ApiTokenQuotaExceeded() {
    super("PPAC failed due to Api Token quota exceeded");
  }
}
