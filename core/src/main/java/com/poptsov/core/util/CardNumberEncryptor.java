package com.poptsov.core.util;


import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import javax.crypto.Cipher;
import javax.crypto.spec.SecretKeySpec;
import java.util.Base64;

@Component
public class CardNumberEncryptor {
    @Value("${AES-256-key}")
    private String SECRET_KEY = "secret";

    private static final String ALGORITHM = "AES";

    public String encrypt(String data) {

        SecretKeySpec key = new SecretKeySpec(SECRET_KEY.getBytes(), ALGORITHM);
        Cipher cipher = null;
        byte[] encrypted = null;
        try {
            cipher = Cipher.getInstance(ALGORITHM);
            cipher.init(Cipher.ENCRYPT_MODE, key);
            encrypted = cipher.doFinal(data.getBytes());
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
        return Base64.getEncoder().encodeToString(encrypted);
    }


    public String mask(String encryptedCardNumber) {
        if (encryptedCardNumber == null || encryptedCardNumber.isEmpty()) {
            return "**** **** **** ****";
        }

        try {
            String decrypted = decrypt(encryptedCardNumber);

            String cleanNumber = decrypted.replaceAll("\\s+", "");

            String lastFour = cleanNumber.substring(cleanNumber.length() - 4);

            return "**** **** **** " + lastFour;
        } catch (Exception e) {
            return "**** **** **** ****";
        }
    }

    public String decrypt(String encryptedData) {
        try {
            SecretKeySpec key = new SecretKeySpec(SECRET_KEY.getBytes(), ALGORITHM);
            Cipher cipher = Cipher.getInstance(ALGORITHM);
            cipher.init(Cipher.DECRYPT_MODE, key);
            byte[] decoded = Base64.getDecoder().decode(encryptedData);
            byte[] decrypted = cipher.doFinal(decoded);
            return new String(decrypted);
        } catch (Exception e) {
            throw new RuntimeException("Decryption failed", e);
        }
    }
}