package com.epam.esm.SpringApiAdvanced.exception;

public class DataBaseRuntimeException extends RuntimeException {
    public DataBaseRuntimeException(String message) {
        super(message);
    }
}
