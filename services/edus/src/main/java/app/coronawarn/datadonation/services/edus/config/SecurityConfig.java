package app.coronawarn.datadonation.services.edus.config;

import app.coronawarn.datadonation.services.edus.otp.OtpController;
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
import org.springframework.web.servlet.handler.HandlerMappingIntrospector;

@Configuration
@EnableWebSecurity
public class SecurityConfig extends WebSecurityConfigurerAdapter {

  private static final String REDEMPTION_ROUTE =
      "/version/v1" + OtpController.REDEMPTION_ROUTE;

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

  @Bean(name = "mvcHandlerMappingIntrospector")
  public HandlerMappingIntrospector mvcHandlerMappingIntrospector() {
    return new HandlerMappingIntrospector();
  }

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
        // TODO authentication
        .mvcMatchers(HttpMethod.POST, REDEMPTION_ROUTE).permitAll()
        .anyRequest().denyAll()
        .and().csrf().disable();
    http.headers().contentSecurityPolicy("default-src 'self'");
  }
}
