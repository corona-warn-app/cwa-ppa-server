package app.coronawarn.analytics.services.ios.exception;

public class BlockedDeviceException extends RuntimeException {

    public BlockedDeviceException() {
    }

    public BlockedDeviceException(String message) {
        super(message);
    }

    public BlockedDeviceException(String message, Throwable cause) {
        super(message, cause);
    }

    public BlockedDeviceException(Throwable cause) {
        super(cause);
    }
}
