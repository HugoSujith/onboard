package com.hugo.metalbroker.exceptions;

public class WalletIdFetchingFailureException extends RuntimeException {
    private final String value;

    public WalletIdFetchingFailureException(String value) {
        this.value = value;
    }

    @Override
    public String getMessage() {
        return "Failed to fetch the wallet id of user with username: {" + value + "}";
    }
}
