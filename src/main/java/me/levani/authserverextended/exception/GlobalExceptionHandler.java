package me.levani.authserverextended.exception;

import jakarta.validation.ConstraintViolationException;
import me.levani.authorizationserver.exeption.ExceptionResponseBody;
import me.levani.authorizationserver.exeption.ServerException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.context.request.WebRequest;
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler;

@ControllerAdvice
public class GlobalExceptionHandler extends ResponseEntityExceptionHandler {

    @ExceptionHandler(value = { ServerException.class})
    protected ResponseEntity<ExceptionResponseBody> handleConflict(ServerException ex, WebRequest request) {
        return ResponseEntity
                .status(ex.getStatus())
                .body(new ExceptionResponseBody(ex.getMessage()));
    }

    @ExceptionHandler(value = {ConstraintViolationException.class})
    protected ResponseEntity<ExceptionResponseBody> handleConstraintViolation(ConstraintViolationException ex, WebRequest request) {
        return ResponseEntity
                .status(HttpStatus.BAD_REQUEST)
                .body(new ExceptionResponseBody(ex.getMessage()));
    }

    @ExceptionHandler(value = {RuntimeException.class})
    protected ResponseEntity<Object> handleError(RuntimeException ex, WebRequest request) {
        return ResponseEntity
                .status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(new ExceptionResponseBody(ex.getMessage()));
    }
}

