package app.coronawarn.analytics.services.ios.exception;

public class BadDeviceTokenException extends RuntimeException {
    
    public BadDeviceTokenException(String message) {
        super(message);
    }
}
