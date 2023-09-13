package com.example.coinserver.utils;

import lombok.experimental.UtilityClass;

import java.util.Random;

@UtilityClass
public class RandomTokenGenerator {
    private final String CHARACTERS = "abcdefghijklmnopqrstuvwxyzABCDEFGHIJKLMNOPQRSTUVWXYZ0123456789";

    public static String generateToken(int length) {
        StringBuilder token = new StringBuilder(length);
        Random random = new Random();

        for (int i = 0; i < length; i++) {
            int index = random.nextInt(CHARACTERS.length());
            token.append(CHARACTERS.charAt(index));
        }

        return token.toString();
    }

    public String generateToken() {
        return generateToken(64);
    }
}