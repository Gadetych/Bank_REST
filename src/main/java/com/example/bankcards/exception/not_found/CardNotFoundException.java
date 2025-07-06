package com.example.bankcards.exception.not_found;

public class CardNotFoundException extends NotFoundException {
    public CardNotFoundException(String message) {
        super(message);
    }

    public CardNotFoundException() {
        super();
    }
}
