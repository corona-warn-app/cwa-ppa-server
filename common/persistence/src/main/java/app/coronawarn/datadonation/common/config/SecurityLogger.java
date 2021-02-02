package app.coronawarn.datadonation.common.config;

import org.slf4j.Marker;
import org.slf4j.MarkerFactory;

public interface SecurityLogger {

  Marker SECURITY = MarkerFactory.getMarker("SECURITY");

  void warn(final RuntimeException exception);

  void error(final RuntimeException exception);

  void securityWarn(final RuntimeException exception);

}
