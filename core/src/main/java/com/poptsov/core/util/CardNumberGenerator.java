package com.poptsov.core.util;

import org.springframework.stereotype.Component;

import java.util.Random;

@Component
public class CardNumberGenerator {
    public String generate() {
        Random random = new Random();
        return String.format(
                "%04d %04d %04d %04d",
                random.nextInt(10000),
                random.nextInt(10000),
                random.nextInt(10000),
                random.nextInt(10000)
        );
    }
}