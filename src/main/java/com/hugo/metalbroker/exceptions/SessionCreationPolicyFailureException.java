package com.hugo.metalbroker.exceptions;

public class SessionCreationPolicyFailureException extends RuntimeException {
    private final String value;

    public SessionCreationPolicyFailureException(String value) {
        this.value = value;
    }

    public String getMessage() {
        return "Session creation policy failed with following error message: " + value;
    }
}
