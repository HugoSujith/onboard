package com.hugo.metalbroker.exceptions;

public class RedisKeyNotFoundException extends RuntimeException {
    private final String value;

    public RedisKeyNotFoundException(String value) {
        this.value = value;
    }

    @Override
    public String getMessage() {
        return "Redis key { " + value + " } not found in cache memory!";
    }
}
