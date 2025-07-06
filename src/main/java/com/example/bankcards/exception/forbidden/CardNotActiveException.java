package com.example.bankcards.exception.forbidden;

public class CardNotActiveException extends ForbiddenException {
    public CardNotActiveException() {
        super();
    }

    public CardNotActiveException(String message) {
        super(message);
    }

    public CardNotActiveException(String message, Throwable cause) {
        super(message, cause);
    }
}
