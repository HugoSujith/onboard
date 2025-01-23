package com.hugo.metalbroker.exceptions;

public class UserNotFoundException extends RuntimeException {
    private final String value;
    
    public UserNotFoundException(String value) {
        this.value = value;
    }
    
    @Override
    public String getMessage() {
        return "User with username: " + value + " not found!";
    }
}
