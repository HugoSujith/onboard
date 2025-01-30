package com.hugo.metalbroker.exceptions;

public class WalletIdVerificationException extends RuntimeException {
    private final String value;

    public WalletIdVerificationException(String value) {
        this.value = value;
    }

    @Override
    public String getMessage() {
        return "Failed to check if the user's wallet id {" + value + "} is valid or not.";
    }
}
