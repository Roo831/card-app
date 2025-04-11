package com.poptsov.core.util;


import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import javax.crypto.Cipher;
import javax.crypto.NoSuchPaddingException;
import javax.crypto.spec.SecretKeySpec;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.util.Base64;

@Component
public class CardNumberEncryptor {
    @Value("${AES-256-key}")
    private static final String SECRET_KEY = "secret";

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


    public String mask(String cardNumber) {
        return "**** **** **** " + cardNumber.substring(15);
    }
}