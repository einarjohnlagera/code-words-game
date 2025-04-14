package com.cctalents.code_words.exception;

import com.cctalents.code_words.dto.ErrorResponse;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler;

@ControllerAdvice
public class GlobalExceptionHandler extends ResponseEntityExceptionHandler {

    @ExceptionHandler(value = NoGameFoundException.class)
    public ResponseEntity<ErrorResponse> handleNotFoundException(Exception ex) {
        HttpStatus status = HttpStatus.NOT_FOUND;
        return new ResponseEntity<>(new ErrorResponse(status.value(), ex.getMessage()), status);
    }

    @ExceptionHandler(value = {MultipleGuessLetterNotAllowedException.class,
            GameAlreadyFinishedException.class})
    public ResponseEntity<ErrorResponse> handleBadRequests(Exception ex) {
        HttpStatus status = HttpStatus.BAD_REQUEST;
        return new ResponseEntity<>(new ErrorResponse(status.value(), ex.getMessage()), status);

    }
}
