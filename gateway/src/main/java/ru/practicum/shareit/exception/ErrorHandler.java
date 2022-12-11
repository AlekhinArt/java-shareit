package ru.practicum.shareit.exception;

import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.method.annotation.MethodArgumentTypeMismatchException;

import javax.validation.ConstraintViolationException;

@RestControllerAdvice
@Slf4j
public class ErrorHandler {

    @ExceptionHandler
    @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
    public ResponseEntity<String> throwable(final Throwable e) {
        log.info("Throwable : " + e.getMessage());
        return new ResponseEntity<>("Throwable " +
                e.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR);
    }

    @ExceptionHandler
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public ResponseEntity<String> methodArgumentNotValidException(final MethodArgumentNotValidException e) {
        log.info("Argument not valid exception: " + e.getMessage());
        return new ResponseEntity<>("Argument not valid exception " +
                e.getMessage(), HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public ResponseEntity<String> constraintViolationException(final ConstraintViolationException e) {
        log.info("Constraint violation exception : " + e.getMessage());
        return new ResponseEntity<>("Constraint violation exception " +
                e.getMessage(), HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public ErrorResponse unsupportedStatusException(final MethodArgumentTypeMismatchException e) {
        return new ErrorResponse("Unknown state: " +
                e.getValue());
    }

}
