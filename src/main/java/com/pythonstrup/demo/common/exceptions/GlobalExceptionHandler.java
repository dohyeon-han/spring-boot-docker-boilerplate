package com.pythonstrup.demo.common.exceptions;

import com.pythonstrup.demo.common.dto.ErrorResponse;
import com.pythonstrup.demo.common.utils.message.ExceptionMeassge;
import com.pythonstrup.demo.common.utils.message.ExceptionStatus;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.FieldError;
import org.springframework.web.HttpRequestMethodNotSupportedException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.servlet.NoHandlerFoundException;

import java.util.List;
import java.util.stream.Collectors;

@Slf4j
@RestControllerAdvice
public class GlobalExceptionHandler {

    // Validation
    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ErrorResponse> handleValidationErrors(MethodArgumentNotValidException e) {
        List<String> errors = e.getBindingResult().getFieldErrors()
                .stream().map(FieldError::getDefaultMessage).collect(Collectors.toList());

        ErrorResponse response = ErrorResponse.builder()
                .status(ExceptionStatus.BAD_REQUEST)
                .message(errors.get(0))
                .errors(errors)
                .build();
        return new ResponseEntity<>(response, HttpStatus.BAD_REQUEST);
    }

    // Custom exception
    @ExceptionHandler(CustomException.class)
    public ResponseEntity<ErrorResponse> handleCustomErrors(final CustomException e){
        ErrorResponse response = ErrorResponse.builder()
                .status(e.getStatus())
                .message(e.getMessage())
                .build();
        return new ResponseEntity<>(response, e.getStatusCode());
    }

    // 404
    @ExceptionHandler(NoHandlerFoundException.class)
    public ResponseEntity<ErrorResponse> handleNotFoundApi() {
        ErrorResponse response = ErrorResponse.builder()
                .status(ExceptionStatus.PAGE_NOT_FOUND)
                .message(ExceptionMeassge.PAGE_NOT_FOUND)
                .build();
        return new ResponseEntity<>(response, HttpStatus.NOT_FOUND);
    }

    // 405
    @ExceptionHandler(HttpRequestMethodNotSupportedException.class)
    public ResponseEntity<ErrorResponse> handleMethodNotAllowed(final HttpRequestMethodNotSupportedException e) {
        log.error(ExceptionMeassge.METHOD_NOT_ALLOWED, e);
        ErrorResponse response = ErrorResponse.builder()
                .status(ExceptionStatus.METHOD_NOT_ALLOWED)
                .message(ExceptionMeassge.METHOD_NOT_ALLOWED)
                .build();
        return new ResponseEntity<>(response, HttpStatus.METHOD_NOT_ALLOWED);
    }

    // Custom Exception 에서 처리되지 않은 400
    @ExceptionHandler(RuntimeException.class)
    public ResponseEntity<ErrorResponse> handleRuntimeError(final RuntimeException e){
        log.error("Uncontrolled Exception", e);
        ErrorResponse response = ErrorResponse.builder()
                .status(ExceptionStatus.RUNTIME_ERROR)
                .message(e.getMessage())
                .build();
        return new ResponseEntity<>(response, HttpStatus.BAD_REQUEST);
    }

    // 500
    @ExceptionHandler(Exception.class)
    public ResponseEntity<ErrorResponse> unhandledException(final Exception e){
        log.error("Uncontrolled Exception", e);
        ErrorResponse response = ErrorResponse.builder()
                .status(ExceptionStatus.INTERNAL_SERVER_ERROR)
                .message(e.getMessage())
                .build();
        return new ResponseEntity<>(response, HttpStatus.INTERNAL_SERVER_ERROR);
    }
}
