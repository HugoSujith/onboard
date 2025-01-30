package com.hugo.metalbroker.exceptions;

public class CurrencyConversionException extends RuntimeException {
    private final String value;

    public CurrencyConversionException(String value) {
        this.value = value;
    }

    @Override
    public String getMessage() {
        return "Failed to convert value from currency: {" + value + "}";
    }
}
