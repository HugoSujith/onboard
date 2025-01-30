package com.hugo.metalbroker.exceptions;

public class UserBalanceFetchingFailure extends RuntimeException {
    private final String value;

    public UserBalanceFetchingFailure(String value) {
        this.value = value;
    }

    @Override
    public String getMessage() {
        return "Failed to fetch user balance for user with username: {" + value + "}";
    }
}
