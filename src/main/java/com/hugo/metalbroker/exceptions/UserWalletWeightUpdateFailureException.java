package com.hugo.metalbroker.exceptions;

public class UserWalletWeightUpdateFailureException extends RuntimeException {
    private final String value;

    public UserWalletWeightUpdateFailureException(String value) {
        this.value = value;
    }

    @Override
    public String getMessage() {
        return "The user with wallet id {" + value + "} failed to update their asset quantity.";
    }
}
