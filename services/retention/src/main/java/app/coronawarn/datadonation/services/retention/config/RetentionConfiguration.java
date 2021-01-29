package app.coronawarn.datadonation.services.retention.config;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.validation.annotation.Validated;

@ConfigurationProperties(prefix = "retention")
@Validated
public class RetentionConfiguration {

}
