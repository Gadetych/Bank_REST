package com.example.bankcards.util;

import jakarta.persistence.AttributeConverter;
import jakarta.persistence.Converter;

import javax.crypto.Cipher;
import javax.crypto.spec.SecretKeySpec;
import java.nio.charset.StandardCharsets;
import java.util.Base64;

@Converter
public class CardNumberConverter implements AttributeConverter<String, String> {
    private static final String AES = "AES";
    private static final byte[] SECRET_KEY = "my-secret-key-16".getBytes(StandardCharsets.UTF_8);

    @Override
    public String convertToDatabaseColumn(String cardNumber) {
        if (cardNumber == null) return null;
        try {
            Cipher cipher = Cipher.getInstance(AES);
            cipher.init(Cipher.ENCRYPT_MODE, new SecretKeySpec(SECRET_KEY, AES));
            return Base64.getEncoder().encodeToString(cipher.doFinal(cardNumber.getBytes()));
        } catch (Exception e) {
            throw new RuntimeException("Encryption failed", e);
        }
    }

    @Override
    public String convertToEntityAttribute(String encryptedCard) {
        if (encryptedCard == null) return null;
        try {
            Cipher cipher = Cipher.getInstance(AES);
            cipher.init(Cipher.DECRYPT_MODE, new SecretKeySpec(SECRET_KEY, AES));
            return new String(cipher.doFinal(Base64.getDecoder().decode(encryptedCard)));
        } catch (Exception e) {
            throw new RuntimeException("Decryption failed", e);
        }
    }
}
