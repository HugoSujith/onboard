package com.hugo.metalbroker.exceptions;

public class AssetQuantityFetchException extends RuntimeException {
    private final String value;

    public AssetQuantityFetchException(String value) {
        this.value = value;
    }

    @Override
    public String getMessage() {
        return "Failed to fetch asset quantity of the user with wallet id: {" + value + "}";
    }
}
