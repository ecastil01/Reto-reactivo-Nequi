package co.com.nequi.model.exception;

import co.com.nequi.model.enums.TechnicalMessage;
import lombok.Getter;

@Getter
public class BusinessException extends PocketException {

    public BusinessException(TechnicalMessage technicalMessage) {
        super(technicalMessage);
    }

    public BusinessException(String message, TechnicalMessage technicalMessage) {
        super(message, technicalMessage);
    }
}