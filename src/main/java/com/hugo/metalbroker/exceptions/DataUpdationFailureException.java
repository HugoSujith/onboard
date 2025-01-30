package com.hugo.metalbroker.exceptions;

public class DataUpdationFailureException extends RuntimeException {
    private final String value;

    public DataUpdationFailureException(String value) {
        this.value = value;
    }

    @Override
    public String getMessage() {
        return "Failed to insert/update the data in the class: {" + value + "}";
    }
}
