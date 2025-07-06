package com.example.bankcards.exception.forbidden;

public class AccessDeniedUserException extends ForbiddenException {
    public AccessDeniedUserException() {
        super();
    }

    public AccessDeniedUserException(String message) {
        super(message);
    }

    public AccessDeniedUserException(String message, Throwable cause) {
        super(message, cause);
    }
}
