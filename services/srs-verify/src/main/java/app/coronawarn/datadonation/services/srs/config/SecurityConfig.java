package app.coronawarn.datadonation.services.srs.config;

import static app.coronawarn.datadonation.common.config.UrlConstants.HEALTH_ROUTE;
import static app.coronawarn.datadonation.common.config.UrlConstants.LIVENESS_ROUTE;
import static app.coronawarn.datadonation.common.config.UrlConstants.PROMETHEUS_ROUTE;
import static app.coronawarn.datadonation.common.config.UrlConstants.READINESS_ROUTE;
import static app.coronawarn.datadonation.common.config.UrlConstants.SRS;
import static app.coronawarn.datadonation.common.config.UrlConstants.SRS_VERIFY;
import static java.util.Arrays.asList;
import static java.util.Collections.emptyList;
import static org.springframework.http.HttpMethod.GET;
import static org.springframework.http.HttpMethod.POST;

import org.hibernate.validator.messageinterpolation.ParameterMessageInterpolator;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configurers.ExpressionUrlAuthorizationConfigurer;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.firewall.HttpFirewall;
import org.springframework.security.web.firewall.StrictHttpFirewall;
import org.springframework.validation.beanvalidation.LocalValidatorFactoryBean;

@Configuration
public class SecurityConfig {

  /**
   * Validation factory bean is configured here because its message interpolation mechanism is considered a potential
   * threat if enabled.
   */
  @Bean
  public static LocalValidatorFactoryBean defaultValidator() {
    final LocalValidatorFactoryBean localValidatorFactory = new LocalValidatorFactoryBean();
    localValidatorFactory.setMessageInterpolator(new ParameterMessageInterpolator());
    return localValidatorFactory;
  }

  /**
   * Security Filter Chain bean is configured here because it is encouraged a more component-based approach. Before this
   * we used to extend WebSecurityConfigurerAdapter (now deprecated) and Override the configure method.
   *
   * @return newly configured http bean
   */
  @Bean
  public SecurityFilterChain filterChain(final HttpSecurity http) throws Exception {
    final ExpressionUrlAuthorizationConfigurer<HttpSecurity>.ExpressionInterceptUrlRegistry registry = http
        .authorizeRequests();
    registry.mvcMatchers(POST, SRS_VERIFY + SRS).authenticated().and().x509().userDetailsService(userDetailsService());
    registry.mvcMatchers(GET, HEALTH_ROUTE, PROMETHEUS_ROUTE, READINESS_ROUTE, LIVENESS_ROUTE).permitAll();
    registry.anyRequest().denyAll().and().csrf().disable();
    http.headers().contentSecurityPolicy("default-src 'self'");
    return http.build();
  }

  @Bean
  protected HttpFirewall strictFirewall() {
    final StrictHttpFirewall firewall = new StrictHttpFirewall();
    firewall.setAllowedHttpMethods(asList(GET.name(), POST.name()));
    return firewall;
  }

  @Bean
  public UserDetailsService userDetailsService() {
    return username -> new User(username, "", emptyList());
  }
}
