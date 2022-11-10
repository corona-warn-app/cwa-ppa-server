package app.coronawarn.datadonation.services.ppac.config;

import static app.coronawarn.datadonation.common.config.UrlConstants.ANDROID;
import static app.coronawarn.datadonation.common.config.UrlConstants.DATA;
import static app.coronawarn.datadonation.common.config.UrlConstants.DELETE_SALT;
import static app.coronawarn.datadonation.common.config.UrlConstants.HEALTH_ROUTE;
import static app.coronawarn.datadonation.common.config.UrlConstants.IOS;
import static app.coronawarn.datadonation.common.config.UrlConstants.LIVENESS_ROUTE;
import static app.coronawarn.datadonation.common.config.UrlConstants.LOG;
import static app.coronawarn.datadonation.common.config.UrlConstants.OTP;
import static app.coronawarn.datadonation.common.config.UrlConstants.PROMETHEUS_ROUTE;
import static app.coronawarn.datadonation.common.config.UrlConstants.READINESS_ROUTE;
import static app.coronawarn.datadonation.common.config.UrlConstants.SRS;

import java.util.Arrays;
import org.hibernate.validator.messageinterpolation.ParameterMessageInterpolator;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
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
    final LocalValidatorFactoryBean factoryBean = new LocalValidatorFactoryBean();
    factoryBean.setMessageInterpolator(new ParameterMessageInterpolator());
    return factoryBean;
  }

  /**
   * Security Filter Chain bean is configured here because it is encouraged a more component-based approach.
   * Before this we used to extend WebSecurityConfigurerAdapter (now deprecated) and Override the configure method.
   *
   * @return newly configured http bean
   */
  @Bean
  public SecurityFilterChain filterChain(final HttpSecurity http) throws Exception {
    http.authorizeRequests()
        .mvcMatchers(HttpMethod.GET, HEALTH_ROUTE, PROMETHEUS_ROUTE, READINESS_ROUTE, LIVENESS_ROUTE)
        .permitAll()
        .mvcMatchers(HttpMethod.POST, ANDROID + DATA, ANDROID + OTP, ANDROID + LOG, ANDROID + SRS).permitAll()
        .mvcMatchers(HttpMethod.POST, IOS + DATA, IOS + OTP, IOS + LOG, IOS + SRS).permitAll()
        .mvcMatchers(HttpMethod.DELETE, DELETE_SALT).permitAll()
        .anyRequest().denyAll().and().csrf().disable();
    http.headers().contentSecurityPolicy("default-src 'self'");
    return http.build();
  }

  @Bean
  protected HttpFirewall strictFirewall() {
    final StrictHttpFirewall firewall = new StrictHttpFirewall();
    firewall.setAllowedHttpMethods(Arrays.asList(HttpMethod.GET.name(), HttpMethod.POST.name(),
        HttpMethod.DELETE.name()));
    return firewall;
  }
}
