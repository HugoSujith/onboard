package com.hugo.metalbroker.exceptions;

public class AuthenticationFailureException extends RuntimeException {
    private final String value;
    
    public AuthenticationFailureException(String value) {
        this.value = value;
    }
    
    @Override
    public String getMessage() {
        return "Authentication failed for user: " + value + "!";
    }
}
