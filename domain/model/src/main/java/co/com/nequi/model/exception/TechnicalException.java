package co.com.nequi.model.exception;

import co.com.nequi.model.enums.TechnicalMessage;
import lombok.Getter;

@Getter
public class TechnicalException extends PocketException {

    public TechnicalException(TechnicalMessage technicalMessage) {
        super(technicalMessage);
    }

    public TechnicalException(String message, TechnicalMessage technicalMessage) {
        super(message, technicalMessage);
    }

    public TechnicalException(Throwable throwable, TechnicalMessage technicalMessage) {
        super(throwable, technicalMessage);
    }
}