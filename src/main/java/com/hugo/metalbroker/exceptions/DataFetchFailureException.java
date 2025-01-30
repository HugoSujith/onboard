package com.hugo.metalbroker.exceptions;

public class DataFetchFailureException extends RuntimeException {
    private final String value;

    public DataFetchFailureException(String value) {
        this.value = value;
    }

    @Override
    public String getMessage() {
        return "Failed to fetch data from database in method: {" + value + "}";
    }
}
