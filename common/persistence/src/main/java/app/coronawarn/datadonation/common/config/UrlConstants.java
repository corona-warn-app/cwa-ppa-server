package app.coronawarn.datadonation.common.config;

public interface UrlConstants {

  /**
   * {@value}.
   */
  static final String BASE = "/version";

  /**
   * {@value}.
   */
  static final String V1 = BASE + "/v1";

  /**
   * {@value}.
   */
  static final String SURVEY = BASE + "/v1";

  /**
   * {@value} - Error Log Sharing passcode redemption base URL.
   */
  static final String ELS = BASE + "/v1";

  /**
   * {@value} - DATa Donation.
   */
  static final String DATA = "/dat";

  /**
   * {@value} - One Time Participantcode/Password.
   */
  static final String OTP = "/otp";

  /**
   * {@value} - Error Log Sharing.
   */
  static final String LOG = "/els";

  /**
   * {@value}.
   */
  static final String ANDROID = V1 + "/android";

  /**
   * {@value}.
   */
  static final String IOS = V1 + "/ios";

  /**
   * {@value}.
   */
  static final String ACTUATOR_ROUTE = "/actuator";

  /**
   * {@value}.
   */
  static final String HEALTH_ROUTE = ACTUATOR_ROUTE + "/health";

  /**
   * {@value}.
   */
  static final String PROMETHEUS_ROUTE = ACTUATOR_ROUTE + "/prometheus";

  /**
   * {@value}.
   */
  static final String READINESS_ROUTE = HEALTH_ROUTE + "/readiness";

  /**
   * {@value}.
   */
  static final String LIVENESS_ROUTE = HEALTH_ROUTE + "/liveness";

  /**
   * {@value}.
   */
  static final String GENERATE_OTP_ROUTE = SURVEY + OTP + "/{number}/{validity}";
}
