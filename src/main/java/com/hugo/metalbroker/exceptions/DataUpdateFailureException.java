package com.hugo.metalbroker.exceptions;

public class DataUpdateFailureException extends RuntimeException {
    private final String value;

    public DataUpdateFailureException(String value) {
        this.value = value;
    }

    @Override
    public String getMessage() {
        return "Data Updation Failure in class: " + value;
    }
}
