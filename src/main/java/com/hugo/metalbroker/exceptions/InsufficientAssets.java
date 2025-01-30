package com.hugo.metalbroker.exceptions;

public class InsufficientAssets extends RuntimeException {
    private final String value;

    public InsufficientAssets(String value) {
        this.value = value;
    }

    @Override
    public String getMessage() {
        return "No sufficient assets. Your account has a current asset of {" + value + "gms}";
    }
}
