package com.hugo.metalbroker.exceptions;

public class MetalNotFoundException extends RuntimeException {
    private final String value;

    public MetalNotFoundException(String value) {
        this.value = value;
    }

    @Override
    public String getMessage() {
        return value.substring(0, 1).toUpperCase() + value.substring(1) + " not found";
    }
}
