package com.hugo.metalbroker.exceptions;

public class WalletIdPresenceFailureException extends RuntimeException {
    private final String value;

    public WalletIdPresenceFailureException(String value) {
        this.value = value;
    }

    @Override
    public String getMessage() {
        return "Failed to check if the user with username + {" + value + "} has a wallet or not.";
    }
}
