package com.example.coinserver.exception;

import com.example.coinserver.common.GenericResponse;
import jakarta.validation.ConstraintViolationException;
import jakarta.validation.ValidationException;
import lombok.NoArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.Ordered;
import org.springframework.core.annotation.Order;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import static org.springframework.http.HttpStatus.OK;

@Slf4j
@NoArgsConstructor
@RestControllerAdvice
@Order(Ordered.HIGHEST_PRECEDENCE)
public class CoinServerExceptionHandler {

    @ExceptionHandler(CoinServerException.class)
    ResponseEntity<GenericResponse> handleCoinServerException(CoinServerException e) {
        log.error("CoinServerException", e);
        int statusCode = e.getStatusCode();
        var response = new GenericResponse<>(statusCode, e.getMessage());
        return new ResponseEntity<>(response, OK);
    }

    @ExceptionHandler({ValidationException.class, MethodArgumentNotValidException.class, ConstraintViolationException.class, DataIntegrityViolationException.class})
    ResponseEntity<GenericResponse> handleException(ValidationException e) {
        log.error("Validation error", e);
        var response = new GenericResponse<>(1200, "VALIDATION_ERROR");
        return new ResponseEntity<>(response, OK);
    }

    @ExceptionHandler(RuntimeException.class)
    ResponseEntity<GenericResponse> handleException(RuntimeException e) {
        log.error("Unknown error", e);
        var response = new GenericResponse<>(1000, "UNKNOWN_SERVER_ERROR");
        return new ResponseEntity<>(response, OK);
    }
}
