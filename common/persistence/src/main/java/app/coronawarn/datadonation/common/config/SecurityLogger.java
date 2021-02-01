package app.coronawarn.datadonation.common.config;

public interface SecurityLogger extends Tag {

  void warn(final RuntimeException exception);

  void error(final RuntimeException exception);

  void securityWarn(final RuntimeException exception);

  void securityError(final RuntimeException exception);

}
