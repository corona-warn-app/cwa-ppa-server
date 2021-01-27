package app.coronawarn.datadonation.services.ppac.ios.verification.errors;

public class ApiTokenExpired extends RuntimeException {

  public ApiTokenExpired() {
    super("PPAC failed due to expired api token.");
  }
}
