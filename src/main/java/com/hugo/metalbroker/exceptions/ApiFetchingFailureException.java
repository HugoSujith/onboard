package com.hugo.metalbroker.exceptions;

public class ApiFetchingFailureException extends RuntimeException {
    private final String value;

    public ApiFetchingFailureException(String value) {
        this.value = value;
    }

    @Override
    public String getMessage() {
        return "Data Updation Failure in class: " + value;
    }
}
