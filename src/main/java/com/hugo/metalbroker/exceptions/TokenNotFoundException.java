package com.hugo.metalbroker.exceptions;

public class TokenNotFoundException extends RuntimeException {
    private final String value;

    public TokenNotFoundException(String value) {
        this.value = value;
    }

    @Override
    public String getMessage() {
        return "The token you are trying to find { " + value + " } is not available.";
    }
}
