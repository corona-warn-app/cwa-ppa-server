package app.coronawarn.analytics.services.ios.exception;

public class ApiTokenAlreadyUsedException extends RuntimeException {

    public ApiTokenAlreadyUsedException(String message) {
        super(message);
    }
}
