package app.coronawarn.datadonation.services.els;

public class JsonParsingException extends RuntimeException {

  public JsonParsingException(String message) {
    super("Invalid request body:" + message);
  }
}
