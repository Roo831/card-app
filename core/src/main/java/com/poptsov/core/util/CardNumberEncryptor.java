package com.poptsov.core.util;

import org.springframework.stereotype.Component;

@Component
public class CardNumberEncryptor {
    private static final String SECRET_KEY = "secret"; //TODO: Переделать через Value

    public String encrypt(String cardNumber) {
        return "";
        //TODO: Реализация AES-256
    }

    public String mask(String cardNumber) {
        return "**** **** **** " + cardNumber.substring(15);
    }
}