package com.hugo.metalbroker.exceptions;

public class DeserializationFailureException extends RuntimeException {
    private final String value;

    public DeserializationFailureException(String value) {
        this.value = value;
    }

    @Override
    public String getMessage() {
        return "Deserialization failed in " + value + " class!";
    }
}
