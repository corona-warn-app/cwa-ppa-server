package app.coronawarn.datadonation.common.config;

import org.slf4j.Marker;
import org.slf4j.MarkerFactory;

public interface SecurityLogger {

  Marker SECURITY = MarkerFactory.getMarker("SECURITY");

  void error(final Exception exception);

  void securityWarn(final Exception exception);

  void successAndroid(final String endpoint);

  void successIos(final String endpoint);
}
