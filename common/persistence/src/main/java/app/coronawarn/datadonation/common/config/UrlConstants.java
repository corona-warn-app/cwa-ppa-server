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
   * {@value}.
   */
  static final String DATA = "/dat";

  /**
   * {@value}.
   */
  static final String OTP = "/otp";

  /**
   * {@value}.
   */
  static final String LOG = "/log";

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
}
