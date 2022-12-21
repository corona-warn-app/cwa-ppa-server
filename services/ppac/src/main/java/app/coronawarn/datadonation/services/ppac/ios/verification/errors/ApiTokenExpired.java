package app.coronawarn.datadonation.services.ppac.ios.verification.errors;

public class ApiTokenExpired extends RuntimeException {

  private static final long serialVersionUID = -8382160416477150383L;

  public ApiTokenExpired() {
    super("PPAC failed due to expired api token.");
  }
}
