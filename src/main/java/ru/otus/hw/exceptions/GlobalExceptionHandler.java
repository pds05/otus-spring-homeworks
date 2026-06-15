package ru.otus.hw.exceptions;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.servlet.ModelAndView;

@RequiredArgsConstructor
@ControllerAdvice
@Slf4j
public class GlobalExceptionHandler {

    @ExceptionHandler(EntityNotFoundException.class)
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public ModelAndView handeNotFoundException(EntityNotFoundException ex) {
        log.error("Custom error: {}", ex.getMessage(), ex);
        return new ModelAndView("errors/customError", "errorText", "Отсутствует запрошенный ресурс");
    }
}
