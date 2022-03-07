package app.coronawarn.datadonation.common.config;

public final class UrlConstants {

  private UrlConstants() {
  }

  /**
   * {@value}.
   */
  public static final String BASE = "/version";

  /**
   * {@value}.
   */
  public static final String V1 = BASE + "/v1";

  /**
   * {@value}.
   */
  public static final String SURVEY = BASE + "/v1";

  /**
   * {@value} - Error Log Sharing passcode redemption base URL.
   */
  public static final String ELS = BASE + "/v1";

  /**
   * {@value} - DATa Donation.
   */
  public static final String DATA = "/dat";

  /**
   * {@value} - One Time Participantcode/Password.
   */
  public static final String OTP = "/otp";

  /**
   * {@value} - Error Log Sharing.
   */
  public static final String LOG = "/els";

  /**
   * {@value}.
   */
  public static final String ANDROID = V1 + "/android";

  /**
   * {@value}.
   */
  public static final String IOS = V1 + "/ios";

  /**
   * {@value}.
   */
  public static final String ACTUATOR_ROUTE = "/actuator";

  /**
   * {@value}.
   */
  public static final String HEALTH_ROUTE = ACTUATOR_ROUTE + "/health";

  /**
   * {@value}.
   */
  public static final String PROMETHEUS_ROUTE = ACTUATOR_ROUTE + "/prometheus";

  /**
   * {@value}.
   */
  public static final String READINESS_ROUTE = HEALTH_ROUTE + "/readiness";

  /**
   * {@value}.
   */
  public static final String LIVENESS_ROUTE = HEALTH_ROUTE + "/liveness";

  /**
   * {@value}.
   */
  public static final String GENERATE_OTP_ROUTE = SURVEY + OTP + "/{number}/{validity}";

  /**
   * {@value}.
   */
  public static final String GENERATE_ELS_ROUTE = ELS + LOG + "/{number}/{validity}";

  public static final String DELETE_SALT = "/delete/{salt}";

}
