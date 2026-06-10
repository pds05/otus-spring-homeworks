package ru.otus.hw.exceptions;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

@RequiredArgsConstructor
@ControllerAdvice
@Slf4j
public class GlobalExceptionHandler {

    @ExceptionHandler(EntityNotFoundException.class)
    public ErrorDto handeNotFoundException(EntityNotFoundException ex) {
        log.error("Custom error: {}", ex.getMessage(), ex);
        return new ErrorDto("custom-code", "Resource not found");
    }
}
