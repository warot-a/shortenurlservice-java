package com.warota.shorturlservice.util;

public class Base62Encoder {
    private static final String ALPHABET = "abcdefghijklmnopqrstuvwxyzABCDEFGHIJKLMNOPQRSTUVWXYZ0123456789";
    private static final int BASE = 62;

    public static String encode(long num) {
        if (num < 0) {
            throw new IllegalArgumentException("Input number must be non-negative.");
        }
        if (num == 0) {
            return ALPHABET.substring(0, 1);
        }

        var sb = new StringBuilder();
        while (num > 0) {
            sb.insert(0, ALPHABET.charAt((int) (num % BASE)));
            num /= BASE;
        }
        return sb.toString();
    }
}
