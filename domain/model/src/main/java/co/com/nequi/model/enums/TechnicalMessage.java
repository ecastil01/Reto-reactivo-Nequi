package co.com.nequi.model.enums;

public enum TechnicalMessage {
    USER_NOT_FOUND("User not found");

    private final String message;

    TechnicalMessage(String message) {
        this.message = message;
    }

    public String getMessage() {
        return message;
    }
}