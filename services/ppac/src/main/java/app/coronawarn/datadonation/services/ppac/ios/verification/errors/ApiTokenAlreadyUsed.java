package app.coronawarn.datadonation.services.ppac.ios.verification.errors;

public class ApiTokenAlreadyUsed extends RuntimeException {

  public ApiTokenAlreadyUsed() {
    super("PPAC failed due to API Token already issued this month");
  }
}
