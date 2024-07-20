package online.awet.actions.lib.exceptions;

public class InvalidActionArgumentsException extends ActionException {
    public InvalidActionArgumentsException(String message) {
        super(message);
    }

    public InvalidActionArgumentsException(String message, Throwable cause) {
        super(message, cause);
    }
}