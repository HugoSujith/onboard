package com.hugo.metalbroker.exceptions;

public class TokenVersionUpdateFailureException extends RuntimeException {
    private final String value;

    public TokenVersionUpdateFailureException(String value) {
        this.value = value;
    }

    @Override
    public String getMessage() {
        return "Failed to update the token of user: { " + value + " }";
    }
}
