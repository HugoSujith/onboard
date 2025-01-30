package com.hugo.metalbroker.exceptions;

public class TransactionCreationFailureException extends RuntimeException {
    private final String value;

    public TransactionCreationFailureException(String value) {
        this.value = value;
    }

    @Override
    public String getMessage() {
        return "Transaction failed to create for transaction number: {" + value + "}";
    }
}
