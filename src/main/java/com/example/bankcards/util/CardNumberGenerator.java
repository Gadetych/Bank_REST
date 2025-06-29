package com.example.bankcards.util;

import java.util.Random;

public class CardNumberGenerator {
    private static final Random random = new Random();

    public static String generateCardNumber() {
        String bin = "4";

        int length = 16;
        int accountNumberLength = length - bin.length() - 1; // -1 для контрольной цифры

        StringBuilder sb = new StringBuilder(bin);
        for (int i = 0; i < accountNumberLength; i++) {
            sb.append(random.nextInt(10));
        }

        String partialNumber = sb.toString();
        int checkDigit = calculateLuhnCheckDigit(partialNumber);
        sb.append(checkDigit);

        return sb.toString();
    }

    private static int calculateLuhnCheckDigit(String number) {
        int sum = 0;
        boolean alternate = false;

        for (int i = number.length() - 1; i >= 0; i--) {
            int digit = Character.getNumericValue(number.charAt(i));

            if (alternate) {
                digit *= 2;
                if (digit > 9) {
                    digit = (digit % 10) + 1;
                }
            }

            sum += digit;
            alternate = !alternate;
        }

        return (10 - (sum % 10)) % 10;
    }

    public static boolean isValidCardNumber(String cardNumber) {
        if (cardNumber == null || cardNumber.length() < 12 || !cardNumber.matches("\\d+")) {
            return false;
        }

        int sum = 0;
        boolean alternate = false;

        for (int i = cardNumber.length() - 1; i >= 0; i--) {
            int digit = Character.getNumericValue(cardNumber.charAt(i));

            if (alternate) {
                digit *= 2;
                if (digit > 9) {
                    digit = (digit % 10) + 1;
                }
            }

            sum += digit;
            alternate = !alternate;
        }

        return (sum % 10 == 0);
    }
}
