package com.hugo.metalbroker.exceptions;

public class UserBalanceUpdateFailure extends RuntimeException {
    private final String value;

    public UserBalanceUpdateFailure(String value) {
        this.value = value;
    }

    @Override
    public String getMessage() {
        return "Failed to update the user balance for user with username: {" + value + "}";
    }
}
