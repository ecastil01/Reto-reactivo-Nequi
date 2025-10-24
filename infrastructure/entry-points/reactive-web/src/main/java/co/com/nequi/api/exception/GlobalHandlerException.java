package co.com.nequi.api.exception;

import co.com.nequi.model.exception.BusinessException;
import co.com.nequi.model.exception.PocketException;
import co.com.nequi.model.exception.TechnicalException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.reactive.function.client.WebClientResponseException;
import org.springframework.web.server.ResponseStatusException;
import org.springframework.web.server.ServerWebInputException;

@RestControllerAdvice
public class GlobalHandlerException {

    @ExceptionHandler(WebClientResponseException.class)
    public ResponseEntity<String> handleWebClientException(WebClientResponseException ex) {
        return ResponseEntity.status(ex.getStatusCode())
                .body("{\"error\":\"User not found in external API\"}");
    }

    @ExceptionHandler(BusinessException.class)
    public ResponseEntity<String> handleBusinessException(BusinessException ex) {
        return ResponseEntity.status(HttpStatus.NOT_FOUND)
                .body("{\"error\":\"" + ex.getTechnicalMessage().getMessage() + "\"}");
    }

    @ExceptionHandler(TechnicalException.class)
    public ResponseEntity<String> handleTechnicalException(TechnicalException ex) {
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body("{\"error\":\"" + ex.getTechnicalMessage().getMessage() + "\"}");
    }

    @ExceptionHandler(PocketException.class)
    public ResponseEntity<String> handlePocketException(PocketException ex) {
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body("{\"error\":\"" + ex.getTechnicalMessage().getMessage() + "\"}");
    }

    @ExceptionHandler(NumberFormatException.class)
    public ResponseEntity<String> handleNumberFormatException(NumberFormatException ex) {
        return ResponseEntity.badRequest()
                .body("{\"error\":\"Invalid ID format\"}");
    }

    @ExceptionHandler(ResponseStatusException.class)
    public ResponseEntity<String> handleResponseStatusException(ResponseStatusException ex) {
        return ResponseEntity.status(ex.getStatusCode())
                .body("{\"error\":\"" + ex.getReason() + "\"}");
    }

    @ExceptionHandler(ServerWebInputException.class)
    public ResponseEntity<String> handleServerWebInputException(ServerWebInputException ex) {
        return ResponseEntity.badRequest()
                .body("{\"error\":\"Invalid request format\"}");
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<String> handleGenericException(Exception ex) {
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body("{\"error\":\"Internal server error\"}");
    }
}
