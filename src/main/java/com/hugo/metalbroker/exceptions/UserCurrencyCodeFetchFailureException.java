package com.hugo.metalbroker.exceptions;

public class UserCurrencyCodeFetchFailureException extends RuntimeException {
    private final String value;

    public UserCurrencyCodeFetchFailureException(String value) {
        this.value = value;
    }

    @Override
    public String getMessage() {
        return "Failed to update the user currency code for user with wallet id: {" + value + "}";
    }
}