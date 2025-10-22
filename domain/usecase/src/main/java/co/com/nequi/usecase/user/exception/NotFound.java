package co.com.nequi.usecase.user.exception;

public class NotFound extends RuntimeException {
    public NotFound(String message) {
        super(message);
    }
}
