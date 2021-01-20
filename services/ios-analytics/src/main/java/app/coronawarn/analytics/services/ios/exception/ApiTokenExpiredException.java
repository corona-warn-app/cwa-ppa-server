package app.coronawarn.analytics.services.ios.exception;

public class ApiTokenExpiredException extends RuntimeException {
    
    public ApiTokenExpiredException(String message) {
        super(message);
    }
}
