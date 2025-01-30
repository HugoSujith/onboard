package com.hugo.metalbroker.exceptions;

public class SerializationFailureException extends RuntimeException {
    private final String value;

    public SerializationFailureException(String value) {
        this.value = value;
    }

    @Override
    public String getMessage() {
        return "Serialization failed in {" + value + "} class!";
    }
}
