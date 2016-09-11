package sukhesh.accessloganalytics.exception;

/**
 * Created by sukhesh on 11/09/16.
 */
public class InvalidInputException extends Exception {
    private String message;
    public InvalidInputException(String message) {
        super(message);
    }

    @Override
    public String getMessage() {
        return message;
    }
}
