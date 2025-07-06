package com.example.bankcards.exception.validation;

public class InsufficientFundsException extends BadRequestException {
    public InsufficientFundsException(String message) {
        super(message);
    }
}
