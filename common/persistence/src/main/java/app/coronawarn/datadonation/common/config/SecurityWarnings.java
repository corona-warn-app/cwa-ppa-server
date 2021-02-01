package app.coronawarn.datadonation.common.config;

import java.util.function.BiConsumer;

public interface SecurityWarnings {

  BiConsumer<SecurityLogger, RuntimeException> getLogger();
}
