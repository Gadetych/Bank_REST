package com.example.bankcards.exception.forbidden;

public class AccessDeniedCardException extends ForbiddenException {
    public AccessDeniedCardException() {
        super();
    }

    public AccessDeniedCardException(String message) {
        super(message);
    }

    public AccessDeniedCardException(String message, Throwable cause) {
        super(message, cause);
    }
}
