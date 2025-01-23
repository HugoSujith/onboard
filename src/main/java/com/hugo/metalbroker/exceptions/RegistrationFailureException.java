package com.hugo.metalbroker.exceptions;

public class RegistrationFailureException extends RuntimeException {
    private final String value;

    public RegistrationFailureException(String value) {
        this.value = value;
    }

    @Override
    public String getMessage() {
        return "Registration failed for user with username: " + value + "!";
    }
}
