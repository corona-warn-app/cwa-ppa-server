

package app.coronawarn.analytics.services.edus.config;

import app.coronawarn.analytics.services.edus.otp.OtpController;
import java.util.Arrays;
import org.hibernate.validator.messageinterpolation.ParameterMessageInterpolator;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.web.firewall.HttpFirewall;
import org.springframework.security.web.firewall.StrictHttpFirewall;
import org.springframework.validation.beanvalidation.LocalValidatorFactoryBean;

@Configuration
@EnableWebSecurity
public class SecurityConfig extends WebSecurityConfigurerAdapter {

  private static final String EDUS_ROUTE =
      "/version/v1" + OtpController.EDUS_ROUTE;
  private static final String REDEMPTION_ROUTE =
      "/version/v1" + OtpController.REDEMPTION_ROUTE;

  @Bean
  protected HttpFirewall strictFirewall() {
    StrictHttpFirewall firewall = new StrictHttpFirewall();
    firewall.setAllowedHttpMethods(Arrays.asList(
        HttpMethod.GET.name(),
        HttpMethod.POST.name()));
    return firewall;
  }

  @Override
  protected void configure(HttpSecurity http) throws Exception {
    http.authorizeRequests()
        .mvcMatchers(HttpMethod.POST, EDUS_ROUTE).permitAll()
        .anyRequest().denyAll()
        .and().csrf().disable();
    http.headers().contentSecurityPolicy("default-src 'self'");

    http.authorizeRequests() // TODO: configure Redemption Endpoint correctly
        .mvcMatchers(HttpMethod.POST, REDEMPTION_ROUTE).permitAll()
        .anyRequest().denyAll()
        .and().csrf().disable();
    http.headers().contentSecurityPolicy("default-src 'self'");
  }

  /**
   * Validation factory bean is configured here because its message interpolation mechanism is considered a potential
   * threat if enabled.
   */
  @Bean
  public static LocalValidatorFactoryBean defaultValidator() {
    LocalValidatorFactoryBean factoryBean = new LocalValidatorFactoryBean();
    factoryBean.setMessageInterpolator(new ParameterMessageInterpolator());
    return factoryBean;
  }
}
