package com.hugo.metalbroker.exceptions;

public class InsufficientBalance extends RuntimeException {
    private final String value;

    public InsufficientBalance(String value) {
        this.value = value;
    }

    @Override
    public String getMessage() {
        return "No sufficient balance. Your account has a current balance of " + value + " /- in your local currency";
    }
}
