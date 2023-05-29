package com.inws.cvd.exception.handler;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.inws.cvd.exception.ErrorResponse;
import com.inws.cvd.exception.NoDataAvailable;
import jakarta.validation.ValidationException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.method.annotation.MethodArgumentTypeMismatchException;
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler;

import static org.springframework.http.HttpStatus.*;

@Slf4j
@ControllerAdvice
public class Handler extends ResponseEntityExceptionHandler {

    @ExceptionHandler(ValidationException.class)
    public ResponseEntity<ErrorResponse> handleValidationException(ValidationException e) {
        return new ResponseEntity<>(toErrorResponse(e), BAD_REQUEST);
    }

    @ExceptionHandler(MethodArgumentTypeMismatchException.class)
    public ResponseEntity<ErrorResponse> handleMethodArgumentMismatchException(MethodArgumentTypeMismatchException e) {
        return new ResponseEntity<>(toErrorResponse(e), BAD_REQUEST);
    }

    // This is to make api available even if data is missing due to internal seeding error or caching system unavailability.
    // Response code may be changed to 5xx due additional requirements and other needs.
    @ExceptionHandler(NoDataAvailable.class)
    public ResponseEntity<ErrorResponse> handleNoDataAvailableException(NoDataAvailable e) {
        return new ResponseEntity<>(toErrorResponse(e), NOT_FOUND);
    }

    @ExceptionHandler(JsonProcessingException.class)
    public ResponseEntity<ErrorResponse> handleJsonProcessingException(JsonProcessingException e) {
        return new ResponseEntity<>(toErrorResponse(e), INTERNAL_SERVER_ERROR);
    }

    private ErrorResponse toErrorResponse(Exception e) {
        return new ErrorResponse(e.getMessage());
    }

}
