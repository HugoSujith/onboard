package com.hugo.metalbroker.utils;

import java.time.Instant;
import java.util.Random;

public class UIDGenerator {

    public String generateUID() {
        long timestamp = Instant.now().toEpochMilli();
        String prefix = Long.toString(timestamp);
        String suffix = generateSuffix(24 - prefix.length() - 1);
        return prefix + "-" + suffix;
    }

    private String generateSuffix(int suffixLength) {
        String chars = "ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz0123456789";
        Random random = new Random(suffixLength);
        StringBuilder suffix = new StringBuilder();

        for (int i = 0; i < suffixLength; i++) {
            int index = random.nextInt(chars.length());
            suffix.append(chars.charAt(index));
        }

        return suffix.toString();
    }
}
