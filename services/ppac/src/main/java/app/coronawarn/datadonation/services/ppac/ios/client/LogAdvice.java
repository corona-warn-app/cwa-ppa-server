package app.coronawarn.datadonation.services.ppac.ios.client;

import feign.FeignException;
import java.util.Optional;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.ResponseEntity;

@Aspect
public class LogAdvice {

  private Logger logger = LoggerFactory.getLogger(LogAdvice.class);

  @Around("@annotation(app.coronawarn.datadonation.services.ppac.ios.client.Loggable)")
  ResponseEntity<String> logOrError(ProceedingJoinPoint joinPoint) throws Throwable {
    try {
      ResponseEntity response = (ResponseEntity<String>) joinPoint.proceed();
      logger.warn("Request with response: " + response.getStatusCode());
      return response;
      //queryDeviceData
    } catch (FeignException.BadRequest e) {
      logger.warn("Request failed: " + e.request());
    } catch (FeignException e) {
      logger.warn("Request failed: " + e.request());
    }
    return ResponseEntity.of(Optional.empty());
  }
}
