package com.example.bankcards.exception;

import com.example.bankcards.dto.ApiError;
import com.example.bankcards.exception.not_found.NotFoundException;
import com.example.bankcards.exception.validation.BadRequestException;
import jakarta.validation.ConstraintViolationException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.dao.DataAccessException;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.AuthenticationException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.MissingServletRequestParameterException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.io.PrintWriter;
import java.io.StringWriter;
import java.time.LocalDateTime;

@RestControllerAdvice
@Slf4j
public class MainServiceHandlerController {
    private final StringWriter sw = new StringWriter();
    private final PrintWriter pw = new PrintWriter(sw);

    @ExceptionHandler({DataAccessException.class, NotFoundException.class})
    @ResponseStatus(HttpStatus.NOT_FOUND)
    public ApiError handleNotFound(RuntimeException e) {
        log.info("404 {}", e.getMessage(), e);
        e.printStackTrace(pw);
        ApiError apiError = new ApiError();
        apiError.setMessage(e.getMessage());
        apiError.setStatus(HttpStatus.NOT_FOUND.toString());
        apiError.setReason("The required object was not found.");
        apiError.setTimestamp(LocalDateTime.now());
        return apiError;
    }

    @ExceptionHandler({ConstraintViolationException.class, MethodArgumentNotValidException.class, DataIntegrityViolationException.class,
            MissingServletRequestParameterException.class, BadRequestException.class})
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public ApiError handleBadRequest(RuntimeException e) {
        log.info("400 {}", e.getMessage(), e);
        e.printStackTrace(pw);
        ApiError apiError = new ApiError();
        apiError.setMessage(e.getMessage());
        apiError.setStatus(HttpStatus.BAD_REQUEST.toString());
        apiError.setReason("Incorrectly made request.");
        apiError.setTimestamp(LocalDateTime.now());
        return apiError;
    }

    @ExceptionHandler({AuthenticationException.class})
    @ResponseStatus(HttpStatus.UNAUTHORIZED)
    public ApiError handleUnauthorized(RuntimeException e) {
        log.info("401 {}", e.getMessage(), e);
        e.printStackTrace(pw);
        ApiError apiError = new ApiError();
        apiError.setMessage(e.getMessage());
        apiError.setStatus(HttpStatus.UNAUTHORIZED.toString());
        apiError.setReason("UNAUTHORIZED");
        apiError.setTimestamp(LocalDateTime.now());
        return apiError;
    }

    @ExceptionHandler(Exception.class)
    @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
    public ApiError handleServerError(final Exception e) {
        log.info("500 {}", e.getMessage(), e);
        e.printStackTrace(pw);
        ApiError apiError = new ApiError();
        apiError.setMessage(e.getMessage());
        apiError.setStatus(HttpStatus.INTERNAL_SERVER_ERROR.toString());
        apiError.setReason("Internal Server Error");
        apiError.setTimestamp(LocalDateTime.now());
        return apiError;
    }
}
