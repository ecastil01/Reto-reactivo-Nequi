package co.com.nequi.model.exception;

import co.com.nequi.model.enums.TechnicalMessage;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public class PocketException extends Exception {

    private final TechnicalMessage technicalMessage;

    public PocketException(String message, TechnicalMessage technicalMessage) {
        super(message);
        this.technicalMessage = technicalMessage;
    }

    public PocketException(Throwable throwable, TechnicalMessage technicalMessage) {
        super(throwable);
        this.technicalMessage = technicalMessage;
    }
}