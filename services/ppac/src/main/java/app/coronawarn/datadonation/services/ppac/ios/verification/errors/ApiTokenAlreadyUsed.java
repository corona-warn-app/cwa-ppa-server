package app.coronawarn.datadonation.services.ppac.ios.verification.errors;

public class ApiTokenAlreadyUsed extends RuntimeException {

  private static final long serialVersionUID = 6565450244587356280L;

  public ApiTokenAlreadyUsed(String perDeviceDataLastUpdated) {
    super("PPAC failed due to API Token already issued this month: " + perDeviceDataLastUpdated);
  }
}
